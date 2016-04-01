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
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.contact.dao.UserDao;
import sample.contact.service.ContactService;
import sample.contact.service.UserService;

import java.util.List;

/**
 * Concrete implementation of {@link ContactService}.
 *
 * @author Ben Alex
 */
@Transactional
public class UserServiceImpl extends ApplicationObjectSupport implements
		UserService {
	// ~ Instance fields
	// ================================================================================================

	@Autowired
	private UserDao userDao;
	@Autowired
	private MutableAclService mutableAclService;
	private int counter = 1000;


	@Transactional(readOnly = true)
	public List<String> getAllRecipients() {
		logger.debug("Returning all recipients");

		return userDao.findAllPrincipals();
	}
}
