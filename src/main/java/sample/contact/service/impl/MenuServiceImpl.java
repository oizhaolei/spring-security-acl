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
package sample.contact.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.contact.dao.MenuDao;
import sample.contact.model.Menu;
import sample.contact.service.MenuService;

import java.util.List;
import java.util.Random;

import static sample.contact.Utils.getUsername;

/**
 * Concrete implementation of {@link MenuService}.
 *
 * @author Ben Alex
 */
@Transactional
@Service
public class MenuServiceImpl extends ApplicationObjectSupport implements
		MenuService {
	// ~ Instance fields
	// ================================================================================================

	@Autowired
	private MenuDao menuDao;
	@Autowired
	private MutableAclService mutableAclService;
	private int counter = 1000;


	public void addPermission(Menu menu, Sid recipient, Permission permission) {
		MutableAcl acl;
		ObjectIdentity oid = new ObjectIdentityImpl(Menu.class, menu.getId());

		try {
			acl = (MutableAcl) mutableAclService.readAclById(oid);
		} catch (NotFoundException nfe) {
			acl = mutableAclService.createAcl(oid);
		}

		acl.insertAce(acl.getEntries().size(), permission, recipient, true);
		mutableAclService.updateAcl(acl);

		logger.debug("Added permission " + permission + " for Sid " + recipient
				+ " menu " + menu);
	}

	public void create(Menu menu) {
		// Create the Menu itself
		menu.setId(new Long(counter++));
		menuDao.create(menu);

		// Grant the current principal administrative permission to the menu
		addPermission(menu, new PrincipalSid(getUsername()),
				BasePermission.ADMINISTRATION);

		if (logger.isDebugEnabled()) {
			logger.debug("Created menu " + menu
					+ " and granted admin permission to recipient " + getUsername());
		}
	}

	public void delete(Menu menu) {
		menuDao.delete(menu.getId());

		// Delete the ACL information as well
		ObjectIdentity oid = new ObjectIdentityImpl(Menu.class, menu.getId());
		mutableAclService.deleteAcl(oid, false);

		if (logger.isDebugEnabled()) {
			logger.debug("Deleted menu " + menu + " including ACL permissions");
		}
	}

	public void deletePermission(Menu menu, Sid recipient, Permission permission) {
		ObjectIdentity oid = new ObjectIdentityImpl(Menu.class, menu.getId());
		MutableAcl acl = (MutableAcl) mutableAclService.readAclById(oid);

		// Remove all permissions associated with this particular recipient (string
		// equality to KISS)
		List<AccessControlEntry> entries = acl.getEntries();

		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).getSid().equals(recipient)
					&& entries.get(i).getPermission().equals(permission)) {
				acl.deleteAce(i);
			}
		}

		mutableAclService.updateAcl(acl);

		if (logger.isDebugEnabled()) {
			logger.debug("Deleted menu " + menu + " ACL permissions for recipient "
					+ recipient);
		}
	}

	@Transactional(readOnly = true)
	public List<Menu> getAll() {
		logger.debug("Returning all menus");

		return menuDao.findAll();
	}

	@Transactional(readOnly = true)
	public Menu getById(Long id) {
		if (logger.isDebugEnabled()) {
			logger.debug("Returning menu with id: " + id);
		}

		return menuDao.getById(id);
	}


	public void setMutableAclService(MutableAclService mutableAclService) {
		this.mutableAclService = mutableAclService;
	}

	public void update(Menu menu) {
		menuDao.update(menu);

		logger.debug("Updated menu " + menu);
	}


	@Override
	public List<Menu> findAll() {
		return menuDao.findAll();
	}

	@Override
	public Menu saveOrUpdate(Menu t) {
		return menuDao.save(t);
	}

	@Override
	public void deleteAll() {
		menuDao.deleteAll();
	}

	public List<Menu> testFilterMenu() {
		return menuDao.findAll();
	}

	public List<Menu> testFilterMenuWithReadPermission() {
		return menuDao.findAll();
	}
}
