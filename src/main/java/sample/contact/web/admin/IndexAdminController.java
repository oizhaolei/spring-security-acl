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
package sample.contact.web.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import sample.contact.model.Menu;
import sample.contact.service.AclManager;
import sample.contact.service.MenuService;
import sample.contact.service.UserGroupService;
import sample.contact.service.UserService;
import sample.contact.web.model.WebUser;
import sample.contact.web.validator.WebUserValidator;

import java.util.List;

/**
 * Controller which handles simple, single request use cases such as index pages and
 * contact deletion.
 *
 * @author Luke Taylor
 * @since 3.0
 */
@Controller
public class IndexAdminController {
	private static final Logger log = LoggerFactory.getLogger(IndexAdminController.class);
	private final Validator validator = new WebUserValidator();
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

	@ModelAttribute("users")
	public List<String> populateUsers() {
		return userService.findAllUsers();
	}

	@ModelAttribute("groups")
	public List<String> populateGroups() {
		return userGroupService.listAllGroups();
	}

	@ModelAttribute("menus")
	public List<Menu> populateMenus() {
		return menuService.findAllWithAdminPermission();
	}

	@ModelAttribute("authorities")
	public List<String> populateAuthorities() {
		return userService.findAllAuthorities();
	}

	/**
	 * Administration zone index.
	 */
	@RequestMapping("/admin/index.html")
	public ModelAndView adminIndex() {

		return new ModelAndView("admin/index.html");
	}

	@RequestMapping(value = "/admin/index.html", params = {"user_add"}, method = RequestMethod.GET)
	public ModelAndView userAdd() {

		return new ModelAndView("admin/index.html", "webUser", new WebUser());
	}

	@RequestMapping(value = "/admin/index.html", params = {"user_save"}, method = RequestMethod.POST)
	public String userSave(WebUser form, BindingResult result) {

		validator.validate(form, result);

		if (result.hasErrors()) {
			return "admin/index.html";
		}


		log.debug("save user: " + form);
		userService.createUserWithAuthority(form.getUsername(), form.getPassword(), "ROLE_USER");
		return "admin/index.html";
	}

}
