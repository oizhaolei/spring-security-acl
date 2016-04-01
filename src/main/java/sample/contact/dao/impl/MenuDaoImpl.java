/*
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.contact.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import sample.contact.dao.MenuDao;
import sample.contact.model.Menu;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Base implementation of {@link MenuDao} that uses Spring's JdbcTemplate.
 *
 * @author Ben Alex
 * @author Luke Taylor
 */
public class MenuDaoImpl extends JdbcDaoSupport implements MenuDao {
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	private void initialize() {
		setDataSource(dataSource);
	}
	// ~ Methods
	// ========================================================================================================

	public Menu create(final Menu menu) {
		getJdbcTemplate().update("insert into menus (menu_name, menu_path)values (?, ?)",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, menu.getName());
						ps.setString(2, menu.getPath());
					}
				});
		long id =  getJdbcTemplate().queryForObject( "select last_insert_id()" , Long.class);
		return getById(id);
	}

	public void delete(final Long menuId) {
		getJdbcTemplate().update("delete from menus where id = ?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setLong(1, menuId);
					}
				});
	}

	public int update(final Menu menu) {
		return getJdbcTemplate().update(
				"update menus set menu_name = ?, menu_path = ? where id = ?",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, menu.getName());
						ps.setString(2, menu.getPath());
						ps.setLong(3, menu.getId());
					}
				});
	}

	@Override
	public Menu save(Menu t) {
		if(t.getId() == null || update(t) == 0) {
			t = create(t);
		}
		return t;
	}

	@Override
	public void deleteAll() {
		getJdbcTemplate().update("delete from menus");
	}

	public List<Menu> findAll() {
		return getJdbcTemplate().query(
				"select id, menu_name, menu_path from menus order by id",
				new RowMapper<Menu>() {
					public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
						return mapMenu(rs);
					}
				});
	}

	public Menu getById(Long id) {
		List<Menu> list = getJdbcTemplate().query(
				"select id, menu_name, menu_path from menus where id = ? order by id",
				new RowMapper<Menu>() {
					public Menu mapRow(ResultSet rs, int rowNum) throws SQLException {
						return mapMenu(rs);
					}
				}, id);

		if (list.size() == 0) {
			return null;
		} else {
			return (Menu) list.get(0);
		}
	}

	private Menu mapMenu(ResultSet rs) throws SQLException {
		Menu menu = new Menu();
		menu.setId(new Long(rs.getLong("id")));
		menu.setName(rs.getString("menu_name"));
		menu.setPath(rs.getString("menu_path"));

		return menu;
	}
}
