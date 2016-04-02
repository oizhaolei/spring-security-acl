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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Controller which handles simple, single request use cases such as index pages and
 * contact deletion.
 *
 * @author Luke Taylor
 * @since 3.0
 */
@Controller
public class IndexController {
	@RequestMapping("/")
	public String root(Locale locale) {
		return "redirect:/index.html";
	}

	/**
	 * Home page.
	 */
	@RequestMapping("/index.html")
	public String index() {
		return "index.html";
	}

	/**
	 * User zone index.
	 */
	@RequestMapping("/user/index.html")
	public String userIndex() {
		return "user/index.html";
	}

	/**
	 * Administration zone index.
	 */
	@RequestMapping("/admin/index.html")
	public String adminIndex() {
		return "admin/index.html";
	}

	/**
	 * Shared zone index.
	 */
	@RequestMapping("/shared/index.html")
	public String sharedIndex() {
		return "shared/index.html";
	}

	/**
	 * Login form.
	 */
	@RequestMapping("/login.html")
	public String login() {
		return "login.html";
	}

	/**
	 * Login form with error.
	 */
	@RequestMapping("/login-error.html")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		return "login.html";
	}

	/**
	 * Simulation of an exception.
	 */
	@RequestMapping("/simulateError.html")
	public void simulateError() {
		throw new RuntimeException("This is a simulated error message");
	}

	/**
	 * Error page.
	 */
	@RequestMapping("/error.html")
	public String error(HttpServletRequest request, Model model) {
		model.addAttribute("errorCode", "Error " + request.getAttribute("javax.servlet.error.status_code"));
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append("<ul>");
		while (throwable != null) {
			errorMessage.append("<li>").append(escapeTags(throwable.getMessage())).append("</li>");
			throwable = throwable.getCause();
		}
		errorMessage.append("</ul>");
		model.addAttribute("errorMessage", errorMessage.toString());
		return "error.html";
	}

	/**
	 * Substitute 'less than' and 'greater than' symbols by its HTML entities.
	 */
	private String escapeTags(String text) {
		if (text == null) {
			return null;
		}
		return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
}
