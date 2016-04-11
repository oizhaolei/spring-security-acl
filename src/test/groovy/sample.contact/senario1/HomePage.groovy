/*
 *
 *
 */
package sample.contact.senario1

import geb.Page

/**
 * The home page
 *
 * @author Rob Winch
 */
class HomePage extends Page {
    static url = 'index.html'
    static at = { assert driver.title == 'Thymeleafexamples - Spring security'; true }
    static content = {
        admin(to: [AdminIndexPage, LoginPage]) { $('a', text: 'administration zone') }
        user { $('a', text: 'user zone').click() }
        shared { $('a', text: 'shared zone').click() }
        pagename { 'index' }
    }
}