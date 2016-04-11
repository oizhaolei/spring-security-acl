/*
 *
 *
 */
package sample.contact.senario1

import geb.Page

/**
 * The login page.
 *
 * @author Rob Winch
 */
class LoginPage extends Page {
    static url = 'login.html'
    static at = { assert driver.title == 'Login page'; true }
    static content = {
        login(required: false) { user = 'jane', password = 'wombat' ->
            loginForm.username = user
            loginForm.password = password
            submit.click()
        }
        loginForm { $('form') }
        submit { $('input', type: 'submit') }
        pagename { 'login' }
    }
}