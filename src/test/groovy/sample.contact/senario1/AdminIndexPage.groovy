/*
 *
 */
package sample.contact.senario1

import geb.Module
import geb.Page

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