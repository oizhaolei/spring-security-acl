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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sample.contact.model.Contact;
import sample.contact.service.ContactService;
import sample.contact.service.UserService;
import sample.contact.web.model.AddPermission;
import sample.contact.web.validator.AddPermissionValidator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Web controller to handle <tt>Permission</tt> administration functions - adding and
 * deleting permissions for contacts.
 *
 * @author Luke Taylor
 * @since 3.0
 */
@Controller
@SessionAttributes("addPermission")
public final class AdminPermissionController implements MessageSourceAware {
	@Autowired
	private AclService aclService;
	@Autowired
	private ContactService contactService;
	@Autowired
	private UserService userService;
	private MessageSourceAccessor messages;
	private final Validator addPermissionValidator = new AddPermissionValidator();
	private final PermissionFactory permissionFactory = new DefaultPermissionFactory();

	/**
	 * Displays the permission admin page for a particular contact.
	 */
	@RequestMapping(value = "/secure/adminPermission.htm", method = RequestMethod.GET)
	public ModelAndView displayAdminPage(@RequestParam("contactId") int contactId) {
		Contact contact = contactService.getById(Long.valueOf(contactId));
		Acl acl = aclService.readAclById(new ObjectIdentityImpl(contact));

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("contact", contact);
		model.put("acl", acl);

		return new ModelAndView("adminPermission", "model", model);
	}

	/**
	 * Displays the "add permission" page for a contact.
	 */
	@RequestMapping(value = "/secure/addPermission.htm", method = RequestMethod.GET)
	public ModelAndView displayAddPermissionPageForContact(
			@RequestParam("contactId") int contactId) {
		Contact contact = contactService.getById(new Long(contactId));

		AddPermission addPermission = new AddPermission();
		addPermission.setContact(contact);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("addPermission", addPermission);
		model.put("recipients", listRecipients());
		model.put("permissions", listPermissions());

		return new ModelAndView("addPermission", model);
	}

	@InitBinder("addPermission")
	public void initBinder(WebDataBinder binder) {
		binder.setAllowedFields("recipient", "permission");
	}

	/**
	 * Handles submission of the "add permission" form.
	 */
	@RequestMapping(value = "/secure/addPermission.htm", method = RequestMethod.POST)
	public String addPermission(AddPermission addPermission, BindingResult result,
								ModelMap model) {
		addPermissionValidator.validate(addPermission, result);

		if (result.hasErrors()) {
			model.put("recipients", listRecipients());
			model.put("permissions", listPermissions());

			return "addPermission";
		}

		PrincipalSid sid = new PrincipalSid(addPermission.getRecipient());
		Permission permission = permissionFactory.buildFromMask(addPermission
				.getPermission());

		try {
			contactService.addPermission(addPermission.getContact(), sid, permission);
		} catch (DataAccessException existingPermission) {
			existingPermission.printStackTrace();
			result.rejectValue("recipient", "err.recipientExistsForContact",
					"Addition failure.");

			model.put("recipients", listRecipients());
			model.put("permissions", listPermissions());
			return "addPermission";
		}

		return "redirect:/secure/index.htm";
	}

	/**
	 * Deletes a permission
	 */
	@RequestMapping(value = "/secure/deletePermission.htm")
	public ModelAndView deletePermission(@RequestParam("contactId") int contactId,
										 @RequestParam("sid") String sid, @RequestParam("permission") int mask) {

		Contact contact = contactService.getById(new Long(contactId));

		Sid sidObject = new PrincipalSid(sid);
		Permission permission = permissionFactory.buildFromMask(mask);

		contactService.deletePermission(contact, sidObject, permission);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("contact", contact);
		model.put("sid", sidObject);
		model.put("permission", permission);

		return new ModelAndView("deletePermission", "model", model);
	}

	private Map<Integer, String> listPermissions() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(Integer.valueOf(BasePermission.ADMINISTRATION.getMask()),
				messages.getMessage("select.administer", "Administer"));
		map.put(Integer.valueOf(BasePermission.READ.getMask()),
				messages.getMessage("select.read", "Read"));
		map.put(Integer.valueOf(BasePermission.DELETE.getMask()),
				messages.getMessage("select.delete", "Delete"));

		return map;
	}

	private Map<String, String> listRecipients() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("", messages.getMessage("select.pleaseSelect", "-- please select --"));

		for (String recipient : userService.getAllRecipients()) {
			map.put(recipient, recipient);
		}

		return map;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}
}
