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
class AddMenuPage extends Page {
    static url = 'add'
    static at = { assert driver.title == 'Add New Menu'; true }
    static content = {
        AddMenu(required: false) { name = 'Rob', path = '/rob/example' ->
            addForm.name = name
            addForm.path = path
            submit.click()
        }
        addForm { $('form') }
        submit { $('input', type: 'submit') }
    }
}