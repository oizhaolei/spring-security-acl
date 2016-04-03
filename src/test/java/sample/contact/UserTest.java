package sample.contact;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import sample.contact.service.UserGroupService;
import sample.contact.service.UserService;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Thiago
 * 
 */
public class UserTest extends AbstractSecurityTest {
	@Autowired
	protected UserService userService;

	/**
	 * 
	 */
	private static final String USER_NAME = "user";
	private static final String ROLE_USER = "ROLE_USER";

	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;
	
	private UserDetails user = null;
	
	@Before
	public void setup() {
		userService.createUserWithAuthority(USER_NAME, ROLE_USER);
		user = jdbcUserDetailsManager.loadUserByUsername(USER_NAME);
		userService.setAuthentication(USER_NAME);
	}
	
	@Test
	public void checkUser() {
		assertNotNull(user);
		GrantedAuthority roleUser = new SimpleGrantedAuthority(ROLE_USER);
		assertTrue(user.getAuthorities().contains(roleUser));
	}
	
	@After
	public void tearDown() {
		jdbcUserDetailsManager.deleteUser(USER_NAME);
	}
}