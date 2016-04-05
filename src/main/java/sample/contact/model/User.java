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

package sample.contact.model;

import java.io.Serializable;

/**
 * Represents a contact.
 *
 * @author Ben Alex
 */
public class User implements Serializable {
	private String username;
	private String password;
	private String realname;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString() + ": ");
		sb.append("username: " + this.getUsername() + "; ");
		sb.append("realname: " + this.getRealname());

		return sb.toString();
	}
}
