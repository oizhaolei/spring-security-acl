/*
 * Copyright 2002-2016 the original author or authors.
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
package sample.contact.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import sample.contact.model.Menu;
import sample.contact.service.AclManager;
import sample.contact.service.MenuService;
import sample.contact.service.UserGroupService;
import sample.contact.service.UserService;
import sample.contact.web.model.WebMenu;
import sample.contact.web.validator.WebMenuValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller which handles simple, single request use cases such as index pages and
 * contact deletion.
 *
 * @author Luke Taylor
 * @since 3.0
 */
@Controller
public class AdminController {
	private static final Logger log = LoggerFactory.getLogger(AdminController.class);
	private final Validator validator = new WebMenuValidator();
	@Autowired
	private AclService aclService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private MenuService menuService;
	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;
	@Autowired
	private AclManager aclManager;

	/**
	 * Administration zone index.
	 */
	@RequestMapping("/admin/index.html")
	public ModelAndView adminIndex() {
		Map<String, Object> model = new HashMap<String, Object>();

		List<String> users = userService.findAllUsers();
		model.put("users", users);
		List<String> groups = userGroupService.listAllGroups();
		model.put("groups", groups);
		List<Menu> menus = menuService.findAllWithAdminPermission();
		model.put("menus", menus);
		List<String> roles = userService.findAllAuthorities();
		model.put("authorities", roles);

		return new ModelAndView ("admin/index.html", model);
	}

	@RequestMapping("/admin/user.html")
	public ModelAndView user(@RequestParam("user") String user) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", user);

		UserDetails userDetails = jdbcUserDetailsManager.loadUserByUsername(user);
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		model.put("authorities", authorities);

		return new ModelAndView("admin/user.html", model);
	}

	@RequestMapping("/admin/menu.html")
	public ModelAndView menu(@RequestParam("menu") Long id) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("menu", id);

		ObjectIdentity identity = new ObjectIdentityImpl(Menu.class.getCanonicalName(), id);
		Acl acl = aclService.readAclById(identity);

		model.put("owner", acl.getOwner());
		model.put("entries", acl.getEntries());

		return new ModelAndView("admin/menu.html", model);
	}

	@RequestMapping("/admin/group.html")
	public ModelAndView group(@RequestParam("group") String group) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("group", group);

		List<String> users = jdbcUserDetailsManager.findUsersInGroup(group);
		model.put("users",users);

		return new ModelAndView("admin/group.html", model);
	}

	/**
	 * Displays the "add contact" form.
	 */
	@RequestMapping(value = "/admin/menu_add.html", method = RequestMethod.GET)
	public ModelAndView addMenuDisplay() {
		return new ModelAndView("admin/menu_add.html", "webMenu", new WebMenu());
	}

	/**
	 * Handles the submission of the contact form, creating a new instance if the username
	 * and email are valid.
	 */
	@RequestMapping(value = "/admin/menu_add.html", method = RequestMethod.POST)
	public String addMenu(WebMenu form, BindingResult result) {
		validator.validate(form, result);

		if (result.hasErrors()) {
			return "admin/menu_add.html";
		}

		Menu menu = new Menu(form.getName(), form.getPath());
		menuService.create(menu);

		return "redirect:/admin/index.html";
	}

	@RequestMapping(value = "/admin/menu_del.html", method = RequestMethod.GET)
	public ModelAndView handleRequest(@RequestParam("menuId") int menuId) {
		Menu menu = menuService.getById(Long.valueOf(menuId));
		menuService.delete(menu);

		return new ModelAndView("admin/menu_add.html", "menu", menu);
	}

}
