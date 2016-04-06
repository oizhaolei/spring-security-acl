/*
 * Copyright 2011 the original author or authors.
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
package sample.contact.pages

import geb.*

/**
 * The home page
 *
 * @author Rob Winch
 */
class AdminIndexPage extends Page {
    static url = 'admin/index.html'
    static at = { assert driver.title == 'Thymeleafexamples - Spring security'; true }
    static content = {
        AddMenu(to: AddMenuPage) { $('a', text: 'Add Menu') }
        menus { moduleList Menu, $("table#menus tr").tail() }
        logout { $("a", text: "Logout") }
    }
}

class Menu extends Module {
    static content = {
        cell { $("td", it) }
        id { cell(0).text().toInteger() }
        name { cell(1).text() }
        path { cell(2).text() }
        delete { cell(3).$('a').click() }
    }
}