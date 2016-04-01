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
public class Menu implements Serializable {
	// ~ Instance fields
	// ================================================================================================

	private Long id;
	private String path;
	private String name;

	// ~ Constructors
	// ===================================================================================================

	public Menu(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public Menu() {
	}

	// ~ Methods
	// ========================================================================================================

	/**
	 * @return Returns the path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return Returns the id.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param path The path to set.
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString() + ": ");
		sb.append("Id: " + this.getId() + "; ");
		sb.append("Name: " + this.getName() + "; ");
		sb.append("Path: " + this.getPath());

		return sb.toString();
	}
}
