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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.transaction.annotation.Transactional;
import sample.contact.dao.UserDao;
import sample.contact.service.ContactService;
import sample.contact.service.UserService;

import java.util.ArrayList;
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


	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;

	@Transactional(readOnly = true)
	public List<String> findAllUsers() {

		return userDao.findAllUsers();
	}
	@Transactional(readOnly = true)
	public List<String> findAllAuthorities() {

		return userDao.findAllAuthorities();
	}


	/**
	 * Efetua a autenticação de um usuário já existente, buscando-o através do <code>loadUserByUsername</code>.
	 *
	 * @param username Username a ser autenticado.
	 */
	@Override
	public void setAuthentication(String username) {
		UserDetails user = jdbcUserDetailsManager.loadUserByUsername(username);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	@Override
	public void createUserWithAuthority(String username, String authority) {
		if (!jdbcUserDetailsManager.userExists(username)) {
			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			grantedAuthorities.add(new SimpleGrantedAuthority(authority));
			UserDetails userDetails = new User(username, username, grantedAuthorities);
			jdbcUserDetailsManager.createUser(userDetails);
		}
	}
}
