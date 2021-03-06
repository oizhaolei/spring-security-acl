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

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Interface for the application's services layer.
 *
 * @author Ben Alex
 */
public interface UserService {

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	List<String> findAllUsers();
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	List<String> findAllAuthorities();

	/**
	 * @param username
	 * @param password
	 * @param authority
	 */
	void createUserWithAuthority(String username, String password, String authority);

	/**
	 * Efetua a autenticação de um usuário já existente, buscando-o através do <code>loadUserByUsername</code>.
	 *
	 * @param username Username a ser autenticado.
	 */
	void setAuthentication(String username);

}
