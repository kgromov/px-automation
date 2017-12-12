package px.objects.users.pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import elements.dropdown.TableDropDown;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import pages.groups.Actions;
import px.objects.users.UserRoleTestData;
import px.objects.users.UsersData;
import px.reports.ReportsPage;
import utils.SoftAssertionHamcrest;

import java.util.List;

import static config.Config.userToString;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static pages.locators.AdminPageLocators.USERS_LINK;
import static pages.locators.ElementLocators.DELETE_POPUP;
import static pages.locators.ElementLocators.DROPDOWN_TABLE_CONTAINER;
import static pages.locators.LoginPageLocators.LOGGED_USER_NAME;
import static pages.locators.LoginPageLocators.LOGOUT_BUTTON;
import static px.objects.pixels.PixelPageLocators.DELETE_LINK;
import static px.objects.pixels.PixelPageLocators.LOGIN_AS_LINK;

/**
 * Created by kgr on 8/15/2017.
 */
public class UsersPage extends ReportsPage implements Actions {

    public UsersPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public UsersPage navigateToObjects() {
        setMenu(DashboardMenuEnum.ADMIN);
        helper.click(USERS_LINK);
        super.checkPage();
        return this;
    }

    public EditUserPage editUser(UserRoleTestData testData) {
        filterInstanceInTable(testData.getUser().getName());
        WebElement cell = findCellByValue(tableElement, testData.getUser().getName());
        cell.click();
        if (!testData.isUserDeleted()) {
            setActionByItemText("Details");
        }
        return new EditUserPage(pxDriver);
    }

    public UsersPage deleteUser(String userName) {
        filterInstanceInTable(userName);
        WebElement cell = findCellByValue(tableElement, userName);
        cell.click();
        setActionByItemLink(cell, DELETE_LINK);
        confirm(true);
        return this;
    }

    public AbstractUserPage resetPassword(String userName) {
        helper.pause(2000);
        filterInstanceInTable(userName);
        WebElement cell = findCellByValue(tableElement, userName);
        cell.click();
        setActionByItemText("Reset password");
        helper.waitUntilDisplayed(DELETE_POPUP);
        return new CreateUserPage(pxDriver);
    }

    public UsersPage loginAs(String userName) {
        filterInstanceInTable(userName);
        WebElement cell = findCellByValue(tableElement, userName);
        cell.click();
        setActionByItemLink(cell, LOGIN_AS_LINK);
        ;
        return this;
    }

    public UsersPage checkLoginAs(UserRoleTestData testData, ObjectIdentityData parent) {
        filterInstanceInTable(testData.getEmail());
        WebElement cell = findCellByValue(tableElement, testData.getEmail());
        cell.click();
        helper.waitUntilDisplayed(DROPDOWN_TABLE_CONTAINER);
        TableDropDown tableDropDown = new TableDropDown(helper.getElement(DROPDOWN_TABLE_CONTAINER));
        List<String> items = tableDropDown.getItems();
        // first assert 'Login As' item in actions column
        assertThat(String.format("User %s with status '%s' has %s item 'Login As', items = %s",
                testData.getEmail(), testData.getStatus(), testData.isUserAbleToLogin() ? "no" : "", items),
//                testData.isUserAbleToLogin() ? hasItem("Log In as") : not(hasItem("Log In as")));
                testData.isUserAbleToLogin(), equalTo(helper.isElementPresent(cell, LOGIN_AS_LINK)));
        if (testData.isUserAbleToLogin()) {
            // if exceeded page loading timeout
            try {
                setActionByItemLink(cell, LOGIN_AS_LINK);
            } catch (TimeoutException | StaleElementReferenceException e) {
                waitAllProgressBarsLoaded();
            } finally {
                assertThat("Check logged user full name",
                        helper.getElement(LOGGED_USER_NAME).getText(),
                        both(containsString(testData.getFullName())).and(containsString(parent.getName())));
                // logout to admin user
                log.info("Logout to user " + userToString());
                helper.scrollToElement(LOGOUT_BUTTON);
                helper.click(LOGOUT_BUTTON);
                // wait till progress finished
                waitPageIsLoaded();
            }
        }
        return this;
    }

    public UsersPage checkAllCells(UsersData testData) {
        if (testData.getItemsTotalCount() > 10)
            // set to 100 items per page
            setItemPerPage(100);
        checkCellsData(testData, testData.getAllRowsArray());
        return this;
    }

    public UsersPage checkUserIsDeleted(String userName) {
        log.info(String.format("Check that user '%s' was successfully deleted - status became 'deleted' " +
                "and 'Delete' item is not present in available actions", userName));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
//        filterInstanceInTable(user.getName());
        WebElement cell = findCellByValue(tableElement, userName, "status");
        hamcrest.assertThat("Status of deleted offer pixel with id '%s' verification", cell.getText(), equalToIgnoringCase("deleted"));
        TableDropDown dropDown = new TableDropDown(findCellByValue(tableElement, userName));
//        dropDown.click();
        hamcrest.assertThat("Item 'Delete' is absent after user was deleted", dropDown.getItems(), not(hasItem("Delete")));
        hamcrest.assertAll();
        return this;
    }
}
