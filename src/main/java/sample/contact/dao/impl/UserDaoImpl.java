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
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import sample.contact.dao.ContactDao;
import sample.contact.dao.UserDao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

/**
 * Base implementation of {@link ContactDao} that uses Spring's JdbcTemplate.
 *
 * @author Ben Alex
 * @author Luke Taylor
 */
public class UserDaoImpl extends JdbcDaoSupport implements UserDao {
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	private void initialize() {
		setDataSource(dataSource);
	}

	public List<String> findAllUsers() {
		return getJdbcTemplate().queryForList(
				"select username from users order by username", String.class);
	}

	public List<String> findAllAuthorities() {
		return getJdbcTemplate().queryForList(
				"select distinct authority from authorities order by authority",
				String.class);
	}

}
