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
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import sample.contact.service.AclManager;
import sample.contact.service.MenuService;
import sample.contact.service.UserGroupService;
import sample.contact.service.UserService;
import sample.contact.web.validator.WebMenuValidator;

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
public class GroupAdminController {
	private static final Logger log = LoggerFactory.getLogger(GroupAdminController.class);
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


	@RequestMapping("/admin/group.html")
	public ModelAndView group(@RequestParam("group") String group) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("group", group);

		List<String> users = jdbcUserDetailsManager.findUsersInGroup(group);
		model.put("users", users);

		return new ModelAndView("admin/group.html", model);
	}


}
