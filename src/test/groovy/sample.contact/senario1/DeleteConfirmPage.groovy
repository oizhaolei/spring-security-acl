/*
 *
 */
package sample.contact.senario1

import geb.Page

/**
 * The home page
 *
 * @author Rob Winch
 */
class DeleteConfirmPage extends Page {
    static at = { assert driver.title == 'Deletion completed'; true }
    static content = {
        manage(to: AdminIndexPage) { $('a', text: 'Manage') }
    }
}