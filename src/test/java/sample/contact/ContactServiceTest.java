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
package sample.contact;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sample.contact.model.Contact;
import sample.contact.service.ContactService;
import sample.contact.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests {@link ContactService}.
 *
 * @author David Leal
 * @author Ben Alex
 * @author Luke Taylor
 */
public class ContactServiceTest extends AbstractSecurityTest {
	// ~ Instance fields
	// ================================================================================================

	@Autowired
	protected ContactService contactService;
	@Autowired
	protected UserService userService;

	// ~ Methods
	// ========================================================================================================

	void assertContainsContact(long id, List<Contact> contacts) {
		for (Contact contact : contacts) {
			if (contact.getId().equals(Long.valueOf(id))) {
				return;
			}
		}

		fail("List of contacts should have contained: " + id);
	}

	void assertDoestNotContainContact(long id, List<Contact> contacts) {
		for (Contact contact : contacts) {
			if (contact.getId().equals(Long.valueOf(id))) {
				fail("List of contact should NOT (but did) contain: " + id);
			}
		}
	}

	/**
	 * Locates the first <code>Contact</code> of the exact name specified.
	 * <p>
	 * Uses the {@link ContactService#getAll()} method.
	 *
	 * @param id Identify of the contact to locate (must be an exact match)
	 *
	 * @return the domain or <code>null</code> if not found
	 */
	Contact getContact(String id) {
		for (Contact contact : contactService.getAll()) {
			if (contact.getId().equals(id)) {
				return contact;
			}
		}

		return null;
	}

	private void makeActiveUser(String username) {
		String password = "";

		if ("rod".equals(username)) {
			password = "koala";
		}
		else if ("dianne".equals(username)) {
			password = "emu";
		}
		else if ("scott".equals(username)) {
			password = "wombat";
		}
		else if ("peter".equals(username)) {
			password = "opal";
		}

		Authentication authRequest = new UsernamePasswordAuthenticationToken(username,
				password);
		SecurityContextHolder.getContext().setAuthentication(authRequest);
	}

	@After
	public void clearContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testDianne() {
		makeActiveUser("dianne"); // has ROLE_USER

		List<Contact> contacts = contactService.getAll();
		assertThat(contacts).hasSize(4);

		assertContainsContact(4, contacts);
		assertContainsContact(5, contacts);
		assertContainsContact(6, contacts);
		assertContainsContact(8, contacts);

		assertDoestNotContainContact(1, contacts);
		assertDoestNotContainContact(2, contacts);
		assertDoestNotContainContact(3, contacts);
	}

	@Test
	public void testrod() {
		makeActiveUser("rod"); // has ROLE_ADMIN

		List<Contact> contacts = contactService.getAll();

		assertThat(contacts).hasSize(4);

		assertContainsContact(1, contacts);
		assertContainsContact(2, contacts);
		assertContainsContact(3, contacts);
		assertContainsContact(4, contacts);

		assertDoestNotContainContact(5, contacts);

		Contact c1 = contactService.getById(new Long(4));

		contactService.deletePermission(c1, new PrincipalSid("bob"),
				BasePermission.ADMINISTRATION);
		contactService.addPermission(c1, new PrincipalSid("bob"),
				BasePermission.ADMINISTRATION);
	}

	@Test
	public void testScott() {
		makeActiveUser("scott"); // has ROLE_USER

		List<Contact> contacts = contactService.getAll();

		assertThat(contacts).hasSize(5);

		assertContainsContact(4, contacts);
		assertContainsContact(6, contacts);
		assertContainsContact(7, contacts);
		assertContainsContact(8, contacts);
		assertContainsContact(9, contacts);

		assertDoestNotContainContact(1, contacts);
	}

	@Test
	public void testUserManager() {
		makeActiveUser("rod"); // has ROLE_ADMIN
		List<String> recipients = userService.findAllUsers();
		assertThat(recipients).hasSize(7);

		makeActiveUser("scott"); // has ROLE_USER
		try {
			userService.findAllUsers();
			fail("no permission");
		} catch (Exception e) {
		}

	}
}
