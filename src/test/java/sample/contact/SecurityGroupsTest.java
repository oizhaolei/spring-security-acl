/**
 * 
 */
package sample.contact;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import sample.contact.service.UserGroupService;
import sample.contact.service.impl.SecurityTestService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Thiago
 *
 */
public class SecurityGroupsTest extends AbstractSecurityTest {
	
	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private SecurityTestService securityTestService;
	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;
	
	private static final String TEST_GROUP = "testGroup";
	private static final String USER_USER = "user";
	
	private UserDetails user = null;
	private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		authorities.add(new SimpleGrantedAuthority("ROLE_GRUPO"));
		jdbcUserDetailsManager.createGroup(TEST_GROUP, authorities);
		userGroupService.createUserWithAuthoriy(USER_USER, "ROLE_USER");
		user = jdbcUserDetailsManager.loadUserByUsername(USER_USER);
	}
	
	@Test
	public void testAddAuthorityToGroup() {
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_TESTE");
		jdbcUserDetailsManager.addGroupAuthority(TEST_GROUP, authority);
		List<GrantedAuthority> foundAuthorities = jdbcUserDetailsManager.findGroupAuthorities(TEST_GROUP);
		
		assertEquals(foundAuthorities.size(), 2);
		
		for (GrantedAuthority grantedAuthority : foundAuthorities) {
			if ("ROLE_TESTE".equals(grantedAuthority.getAuthority())) {
				assertEquals(grantedAuthority, authority);
			}
		}
	}
	
	@Test
	public void testAddMemberToGroup() {
		jdbcUserDetailsManager.addUserToGroup(user.getUsername(), TEST_GROUP);
		List<String> foundUsers = jdbcUserDetailsManager.findUsersInGroup(TEST_GROUP);
		assertTrue(foundUsers.contains(user.getUsername()));
	}
	
	@Test
	public void testUserInGroupHasAccessToRoleUserMethod() {
		jdbcUserDetailsManager.addUserToGroup(user.getUsername(), TEST_GROUP);
		userGroupService.setAuthentication(user.getUsername());
		assertTrue(securityTestService.testHasRoleUser());
	}
	
	@Test
	public void testUserInGroupHasNoAccessToRoleAdminMethod() {
		jdbcUserDetailsManager.addUserToGroup(user.getUsername(), TEST_GROUP);
		userGroupService.setAuthentication(user.getUsername());
		expectedException.expect(AccessDeniedException.class);
		securityTestService.testHasRoleAdmin();
	}
	
	@Test
	public void testUserInGroupHasAccessToRoleUserAndRoleGroup() {
		jdbcUserDetailsManager.addUserToGroup(user.getUsername(), TEST_GROUP);
		userGroupService.setAuthentication(user.getUsername());
		assertTrue(securityTestService.testHasRoleUser());

		UserDetails userT = jdbcUserDetailsManager.loadUserByUsername(user.getUsername());

		assertTrue(securityTestService.testHasRoleGrupo());
	}
	
	@After
	public void tearDown() {
		jdbcUserDetailsManager.deleteUser(USER_USER);
		jdbcUserDetailsManager.deleteGroup(TEST_GROUP);
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}