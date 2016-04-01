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
package sample.contact.service;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import sample.contact.model.Menu;

import java.util.List;

/**
 * Interface for the application's services layer.
 *
 * @author Ben Alex
 */
public interface MenuService {
	// ~ Methods
	// ========================================================================================================
	@PreAuthorize("hasPermission(#menu, admin)")
	public void addPermission(Menu menu, Sid recipient, Permission permission);

	@PreAuthorize("hasPermission(#menu, admin)")
	public void deletePermission(Menu menu, Sid recipient, Permission permission);

	@PreAuthorize("hasRole('ROLE_USER')")
	public void create(Menu menu);

	@PreAuthorize("hasPermission(#menu, 'delete') or hasPermission(#menu, admin)")
	public void delete(Menu menu);

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostFilter("hasPermission(filterObject, 'read') or hasPermission(filterObject, admin)")
	public List<Menu> getAll();

	@PreAuthorize("hasPermission(#id, 'sample.menu.model.Menu', read) or "
			+ "hasPermission(#id, 'sample.menu.model.Menu', admin)")
	public Menu getById(Long id);


	List<Menu> findAll();
	Menu saveOrUpdate(Menu t);

	void deleteAll();
	@PreAuthorize("hasRole('ROLE_SUPERUSER')")
	@PostFilter("hasPermission(filterObject, 'administration')")
	List<Menu> testFilterMenu();

	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_SUPERUSER')")
	@PostFilter("hasPermission(filterObject, 'read') or hasPermission(filterObject, admin)")
	List<Menu> testFilterMenuWithReadPermission();

}
