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
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import sample.contact.model.Menu;
import sample.contact.service.AclManager;
import sample.contact.service.MenuService;
import sample.contact.service.UserGroupService;
import sample.contact.service.UserService;
import sample.contact.service.impl.SecurityTestService;

import static org.junit.Assert.*;

/**
 * @author Thiago
 *
 */
public class SecurityAclTest extends AbstractSecurityTest {

	private static final String USER_ADMIN = "admin";
	private static final String USER_USER = "user";
	@Rule
	public ExpectedException exception = ExpectedException.none();
	@Autowired
	private MenuService menuService;
	@Autowired
	private UserService userManager;
	@Autowired
	private UserGroupService userGroupManager;
	@Autowired
	private SecurityTestService securityTestService;
	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;
	@Autowired
	private AclManager aclManager;
	private Menu menu = null;
	
	@Before
	public void setup() {
		userManager.createUserWithAuthority(USER_ADMIN, USER_ADMIN, "ROLE_ADMIN");
		userManager.createUserWithAuthority(USER_USER, USER_USER, "ROLE_USER");
		
    	Menu p1 = new Menu();
		p1.setName("Menu");
		p1.setPath("/menu");
		menu = menuService.saveOrUpdate(p1);

		userManager.setAuthentication(USER_ADMIN);
		aclManager.addPermission(Menu.class, menu.getId(), new PrincipalSid(USER_ADMIN), BasePermission.ADMINISTRATION);
	}

	@After
	public void tearDown() {
		jdbcUserDetailsManager.deleteUser(USER_ADMIN);
		jdbcUserDetailsManager.deleteUser(USER_USER);
		menuService.deleteAll();
		aclManager.deleteAllGrantedAcl();
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	public void testUserHasNoAccessToMenu() {
		boolean isGranted = aclManager.isPermissionGranted(Menu.class, menu.getId(), new PrincipalSid(USER_USER), BasePermission.READ);
		assertFalse(isGranted);
	}
	
	@Test
	public void testAdminHasNoAccessToMenuAsRead() {
		boolean isGranted = aclManager.isPermissionGranted(Menu.class, menu.getId(), new PrincipalSid(USER_ADMIN), BasePermission.READ);
		assertFalse(isGranted);
	}
	
	@Test
	public void testAdminHasAccessToMenuAsAdministration() {
		boolean isGranted = aclManager.isPermissionGranted(Menu.class, menu.getId(), new PrincipalSid(USER_ADMIN), BasePermission.ADMINISTRATION);
		assertTrue(isGranted);
	}
	
	@Test
	public void testAdminHasAccessToMethodHasRoleAdmin() {
		userManager.setAuthentication(USER_ADMIN);
		assertTrue(securityTestService.testHasRoleAdmin());
	}
	
	@Test
	public void testUserHasNoAccessToMethodHasRoleAdmin() {
		userManager.setAuthentication(USER_USER);
		exception.expect(AccessDeniedException.class);
		securityTestService.testHasRoleAdmin();
	}
	
	@Test
	public void testAdminHasAccessToMethodHasPermissionAdministration() {
		userManager.setAuthentication(USER_ADMIN);
		assertTrue(securityTestService.testHasPermissionAdministrationOnMenu(menu));
	}
	
	@Test
	public void testUserHasNoAccessToMethodHasPermissionAdministration() {
		userManager.setAuthentication(USER_USER);
		exception.expect(AccessDeniedException.class);
		securityTestService.testHasPermissionAdministrationOnMenu(menu);
	}
	
	@Test
	public void testUserHasNoAccessToMethodHasPermissionRead() {
		userManager.setAuthentication(USER_USER);
		exception.expect(AccessDeniedException.class);
		securityTestService.testHasPermissionReadOnMenu(menu);
	}
	
	@Test
	public void testAdminHasNoAccessToMethodPermissionRead() {
		userManager.setAuthentication(USER_ADMIN);
		exception.expect(AccessDeniedException.class);
		securityTestService.testHasPermissionReadOnMenu(menu);
	}
	
	@Test
	public void testUserHasAclPermissionBasedOnRole() {
		aclManager.addPermission(Menu.class, menu.getId(), new GrantedAuthoritySid("ROLE_USER"), BasePermission.READ);
		userManager.setAuthentication(USER_USER);
		assertTrue(securityTestService.testHasPermissionReadOnMenu(menu));
	}
	
	@Test
	public void testRemoveAclPermissionFromUser() {
		aclManager.addPermission(Menu.class, menu.getId(), new GrantedAuthoritySid("ROLE_USER"), BasePermission.READ);
		userManager.setAuthentication(USER_USER);
		assertTrue(securityTestService.testHasPermissionReadOnMenu(menu));
		
		userManager.setAuthentication(USER_ADMIN);
		aclManager.removePermission(Menu.class, menu.getId(), new GrantedAuthoritySid("ROLE_USER"), BasePermission.READ);
		
		userManager.setAuthentication(USER_USER);
		exception.expect(AccessDeniedException.class);
		securityTestService.testHasPermissionReadOnMenu(menu);
	}

	@Test
	public void testFilterList() {

		menuService.deleteAll();
		aclManager.deleteAllGrantedAcl();

		for (int i = 0; i < 5; i++) {
			Menu m = new Menu();
			m.setName("menu " + i);
			m.setPath("/menu/" + i);

			Menu newMenu = menuService.saveOrUpdate(m);

			if (i < 2) {
				aclManager.addPermission(Menu.class, newMenu.getId(), new GrantedAuthoritySid("ROLE_ADMIN"), BasePermission.ADMINISTRATION);
			} else {
				aclManager.addPermission(Menu.class, newMenu.getId(), new GrantedAuthoritySid("ROLE_USER"), BasePermission.READ);
			}
		}

		assertEquals(menuService.findAll().size(),5);

		userManager.setAuthentication(USER_ADMIN);
		assertEquals(menuService.findAllWithReadPermission().size(),2);

//		userManager.setAuthentication(USER_USER);
//		assertThat(menuService.findAllWithReadPermission().size(), is(equalTo(3)));

		userManager.setAuthentication(USER_ADMIN);
		assertEquals(menuService.findAllWithAdminPermission().size(), 2);

		userManager.setAuthentication(USER_USER);
		exception.expect(AccessDeniedException.class);
		menuService.findAllWithAdminPermission();

	}
	
	@Test
	public void encodePassword() {
		StandardPasswordEncoder encoder = new StandardPasswordEncoder("test");
		String result = encoder.encode(USER_ADMIN);
		assertTrue(encoder.matches(USER_ADMIN, result));
	}
}