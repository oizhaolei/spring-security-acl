/*
 *
 *
 */
package sample.contact

import geb.spock.GebReportingSpec
import sample.contact.senario1.AdminIndexPage
import sample.contact.senario1.HomePage
import sample.contact.senario1.LoginPage
import spock.lang.Stepwise

/**
 * Tests the CAS sample application using service tickets.
 *
 * @author Rob Winch
 */
@Stepwise
class Senario1Tests extends GebReportingSpec {
    def 'access home page with unauthenticated user success'() {
        when: 'Unauthenticated user accesses the Home Page'
        go HomePage.url
        then: 'The page is displayed'
        at HomePage
        pagename == "index"
    }

    def 'access admin page with unauthenticated user success'() {
        when: 'Unauthenticated user accesses the Home Page'
        go AdminIndexPage.url
        then: 'The login page is displayed'
        at LoginPage
        pagename == "login"
    }

    def 'access manage page with unauthenticated user sends to login page'() {
        go HomePage.url
        when: 'Unauthenticated user accesses the Manage Page'
        then: 'The page is displayed'
        at HomePage
        admin.click(LoginPage)
        then: 'The login page is displayed'
        at LoginPage
    }

    def 'authenticated user is sent to original page'() {
        when: 'user authenticates'
        login()
        then: 'The manage page is displayed'
        at AdminIndexPage
    }

//    def 'add contact link works'() {
//        when: 'user clicks add link'
//        AddMenu.click(AddMenuPage)
//        then: 'The add page is displayed'
//        at AddMenuPage
//    }

//    def 'add menu'() {
//        when: 'add a contact'
//        AddMenu
//        then: 'The add page is displayed'
//        at AdminIndexPage
//        and: 'The new contact is displayed'
//        menus.find { it.path == '/rob/example' }?.name == 'Rob Winch'
//    }
//
//    def 'delete contact'() {
//        when: 'delete a contact'
//        menus.find { it.path == '/rob/example' }.delete()
//        then: 'Delete confirmation displayed'
//        at DeleteConfirmPage
//        when: 'View Manage Page'
//        manage.click()
//        then: 'New contact has been removed'
//        !menus.find { it.path == '/rob/example' }
//    }

    def 'authenticated user logs out'() {
        when: 'user logs out'
        logout.click()
        then: 'the default logout success page is displayed'
        at LoginPage

//        when: 'Unauthenticated user accesses the Manage Page'
//        via AdminIndexPage
//        then: 'The login page is displayed'
//        at LoginPage
    }
}