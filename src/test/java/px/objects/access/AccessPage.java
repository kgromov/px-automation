package px.objects.access;

import configuration.browser.PXDriver;
import dto.ObjectIdentityData;
import elements.ElementsHelper;
import elements.input.InputElement;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import pages.BasePage;
import utils.SoftAssertionHamcrest;
import utils.TestReporter;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.ElementLocators.PROGRESS_BAR_CHILD;
import static px.objects.publishers.PublishersPageLocators.*;

/**
 * Created by kgr on 1/27/2017.
 */
public class AccessPage extends BasePage{
    private final Logger log = Logger.getLogger(this.getClass());
    private SoftAssertionHamcrest hamcrest;
   /* private final PXDriver pxDriver;
    private final ElementsHelper helper;*/

    public AccessPage(PXDriver pxDriver) {
        super(pxDriver);
       /* this.pxDriver = pxDriver;
        this.helper = new ElementsHelper(pxDriver.getWrappedDriver(), pxDriver.getWait());*/
    }

    public void waitPageLoaded() {
        long start = System.currentTimeMillis();
        try {
            helper.waitUntilAttributeToBe(By.xpath("(." + PROGRESS_BAR_CHILD + ")[1]"), "clientWidth", "0", 20);
            helper.waitUntilAttributeToBe(By.xpath(OFFER_ACCESS_CONTAINER + PROGRESS_BAR_CHILD), "clientWidth", "0", 20);
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("page is not loaded after timeout in '%d' seconds",
                    TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
        } finally {
            log.info(String.format("Loading page duration = '%d' seconds",
                    TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
        }
    }

    private void checkApprovedStatuses(AccessTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ObjectIdentityData parent = testData.getParent();
        log.info(String.format("Check '%s' approved access for '%s' - '%s'", testData.getParentGroup(), parent.getId(), parent.getName()));
        if (testData.hasApprovedItems()) {
            WebElement container = helper.getElement(APPROVED_TEXTAREA);
            // -------------- unapproved items --------------
            String optionLocator = String.format(".//option[@label='%s']", testData.getApprovedToUnApprovedItemName());
            try {
                log.info(String.format("Move item '%s' from Approved to UnApproved group", testData.getApprovedToUnApprovedItemName()));
                // filter by option
                setFilter(testData.getApprovedToUnApprovedItemName());
                // select
                log.info(String.format("Select item by name '%s'", testData.getApprovedToUnApprovedItemName()));
                helper.getElement(container, optionLocator).click();
                // and unapprove
                log.info(String.format("UnApprove item by name '%s'", testData.getApprovedToUnApprovedItemName()));
                helper.getElement(UNAPPROVE_BUTTON).click();
                helper.pause(2000);
                helper.waitUntilToBeInvisible(UNAPPROVE_BUTTON + "//i");
//                helper.waitUntilToBeInvisible(APPROVED_TEXTAREA + optionLocator.substring(1));
                // filter by option
                setFilter(testData.getApprovedToUnApprovedItemName());
                // check that id excluded from approved list
                hamcrest.assertThat(String.format("%s id excluded from approved list", testData.getApprovedToUnApprovedItemName()),
                        helper.isElementPresent(container, optionLocator), equalTo(false));
                // check that id included into unapproved list
                hamcrest.assertThat(String.format("%s id included into unapproved list", testData.getApprovedToUnApprovedItemName()),
                        helper.isElementPresent(UNAPPROVED_TEXTAREA + optionLocator.substring(1)), equalTo(true));
                if (!hamcrest.toString().isEmpty())
                    TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Issues with unapproving offer");
                // reset filter
                resetFilter();
            } catch (NoSuchElementException e) {
                this.hamcrest.assertThat(String.format("The item with Name '%s' is missed in approved access list", testData.getApprovedToUnApprovedItemName()), false);
                TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Offer is missed in approved group");
                resetFilter();
            } catch (TimeoutException ignored) {
            }
            // -------------- block item --------------
            checkBlockedItem(APPROVED_TEXTAREA, testData.getApprovedToBlockedItemName());
        } else {
            log.info("Approved section should be hidden");
            hamcrest.assertThat("Approved section should be hidden", !helper.isElementAccessible(APPROVED_TEXTAREA));
        }
        this.hamcrest.append(hamcrest.toString());
    }

    private void checkUnApprovedStatuses(AccessTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ObjectIdentityData parent = testData.getParent();
        log.info(String.format("Check '%s' approved access for '%s' - '%s'", testData.getParentGroup(), parent.getId(), parent.getName()));
        if (testData.hasUnApprovedItems()) {
            WebElement container = helper.getElement(UNAPPROVED_TEXTAREA);
            // -------------- approve items --------------
            String optionLocator = String.format(".//option[@label='%s']", testData.getUnApprovedToApprovedItemName());
            try {
                log.info(String.format("Move item '%s' from UnApproved to Approved group", testData.getUnApprovedToApprovedItemName()));
                // filter by option
                setFilter(testData.getUnApprovedToApprovedItemName());
                // select
                log.info(String.format("Select item by name '%s'", testData.getUnApprovedToApprovedItemName()));
                helper.getElement(container, optionLocator).click();
                // and approve
                log.info(String.format("Approve item by name '%s'", testData.getUnApprovedToApprovedItemName()));
                helper.getElement(APPROVE_BUTTON).click();
                helper.pause(2000);
                helper.waitUntilToBeInvisible(APPROVE_BUTTON + "//i");
                helper.waitUntilToBeInvisible(UNAPPROVED_TEXTAREA + optionLocator.substring(1));
                // filter by option
                setFilter(testData.getUnApprovedToApprovedItemName());
                // check that id excluded from approved list
                hamcrest.assertThat(String.format("%s id excluded from unapproved list", testData.getUnApprovedToApprovedItemName()),
                        helper.isElementPresent(container, optionLocator), equalTo(false));
                // check that id included into unapproved list
                hamcrest.assertThat(String.format("%s id included into approved list", testData.getUnApprovedToApprovedItemName()),
                        helper.isElementPresent(APPROVED_TEXTAREA + optionLocator.substring(1)), equalTo(true));
                if (!hamcrest.toString().isEmpty())
                    TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Issues with approving offer");
                // reset filter
                resetFilter();
            } catch (NoSuchElementException e) {
                this.hamcrest.assertThat(String.format("The item with Name '%s' is missed in unapproved access list", testData.getUnApprovedToApprovedItemName()), false);
                TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Offer is missed in unapproved group");
                resetFilter();
            } catch (TimeoutException ignored) {
            }
            // -------------- block item --------------
            checkBlockedItem(UNAPPROVED_TEXTAREA, testData.getUnApprovedToBlockedItemName());
        } else {
            log.info("Unapproved section should be hidden");
            hamcrest.assertThat("Unapproved section should be hidden", !helper.isElementAccessible(UNAPPROVED_TEXTAREA));
        }
        this.hamcrest.append(hamcrest.toString());
    }

    private void checkBlockedStatuses(AccessTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ObjectIdentityData parent = testData.getParent();
        log.info(String.format("Check '%s' approved access for '%s' - '%s'", testData.getParentGroup(), parent.getId(), parent.getName()));
        if (testData.hasBlockedItems()) {
            // unblock only
            WebElement container = helper.getElement(BLOCKED_TEXTAREA);
            // -------------- blocked items --------------
            String optionLocator = String.format(".//option[@label='%s']", testData.getUnBlockedItemName());
            try {
                log.info(String.format("Move item '%s' from Blocked to %s group", testData.getUnBlockedItemName(),
                        testData.isUnblockedJoinedApproved() ? "Approved" : "UnApproved"));
                // filter by option
                setFilter(testData.getUnBlockedItemName());
                // select
                log.info(String.format("Select item by name '%s'", testData.getUnBlockedItemName()));
                helper.getElement(container, optionLocator).click();
                // and unblock
                log.info(String.format("UnBlock item by name '%s'", testData.getUnBlockedItemName()));
                helper.getElement(UNBLOCK_BUTTON).click();
                helper.pause(2000);
                helper.waitUntilToBeInvisible(UNBLOCK_BUTTON + "//i");
//                helper.waitUntilToBeInvisible(BLOCKED_TEXTAREA + optionLocator.substring(1));
                // filter by option
                setFilter(testData.getUnBlockedItemName());
                // check that id excluded from approved list
                hamcrest.assertThat(String.format("%s id excluded from blocked list", testData.getUnBlockedItemName()),
                        helper.isElementPresent(container, optionLocator), equalTo(false));
                // check that id included into approved/unapproved list
                String locator = testData.isUnblockedJoinedApproved() ? APPROVED_TEXTAREA : UNAPPROVED_TEXTAREA;
                hamcrest.assertThat(String.format("%s id included into approved/unapproved list", testData.getUnBlockedItemName()),
                        helper.isElementPresent(locator + optionLocator.substring(1)), equalTo(true));
                if (!hamcrest.toString().isEmpty())
                    TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Issues with unblocking offer");
                // reset filter
                resetFilter();
            } catch (NoSuchElementException e) {
                this.hamcrest.assertThat(String.format("The item with Name '%s' is missed in blocked access list", testData.getUnBlockedItemName()), false);
                TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Offer is missed in blocked group");
                resetFilter();
            } catch (TimeoutException ignored) {
            }
        } else {
            log.info("Blocked section should be hidden");
            hamcrest.assertThat("Blocked section should be hidden", !helper.isElementAccessible(BLOCKED_TEXTAREA));
        }
        this.hamcrest.append(hamcrest.toString());
    }

    private void checkBlockedItem(String containerLocator, String blockedName) {
        if (blockedName == null) return;
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        WebElement container = helper.getElement(containerLocator);
        String optionLocator = String.format(".//option[@label='%s']", blockedName);
        try {
            log.info(String.format("Move item '%s' to Blocked group", blockedName));
            // filter by option
            setFilter(blockedName);
            // select
            log.info(String.format("Select item by name '%s'", blockedName));
            helper.getElement(container, optionLocator).click();
            // click 'block' button
            log.info(String.format("Block item by name '%s'", blockedName));
            helper.getElement(container, BLOCK_BUTTON).click();
            helper.pause(2000);
            helper.waitUntilToBeInvisible(containerLocator + BLOCK_BUTTON.substring(1) + "//i");
//            helper.waitUntilToBeInvisible(containerLocator + optionLocator.substring(1));
            // filter by option
            setFilter(blockedName);
            // check that id excluded from approved/unapproved list
            hamcrest.assertThat(String.format("%s id excluded from approved/unapproved list", blockedName),
                    helper.isElementPresent(container, optionLocator), equalTo(false));
            // check that id included into blocked list
            hamcrest.assertThat(String.format("%s id included into blocked list", blockedName),
                    helper.isElementPresent(BLOCKED_TEXTAREA + optionLocator.substring(1)), equalTo(true));
            if (!hamcrest.toString().isEmpty())
                TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Issues with blocking offer");
            // reset filter
            resetFilter();
            this.hamcrest.append(hamcrest.toString());
        } catch (NoSuchElementException e) {
            this.hamcrest.assertThat(String.format("The item with Name '%s' is missed in approved/unapproved access list", blockedName), false);
            TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Offer is missed in approved/unapproved group");
            resetFilter();
        } catch (TimeoutException ignored) {
        }
    }

    private void checkDoubleGroups(AccessTestData testData) {
        // check that approved/blocked offers are visible in blocked
        if (!testData.getBlockedApprovedItemsMap().isEmpty()) {
            testData.getBlockedApprovedItemsMap().values().forEach(offerName -> {
                // filter by option
                setFilter(offerName);
                try {
                    assertThat(String.format("%s offer name is present into blocked list", offerName),
                            helper.isElementPresent(BLOCKED_TEXTAREA + String.format("//option[@label='%s']", offerName)), equalTo(true));
                    assertThat(String.format("%s offer name is missed into approved list", offerName),
                            helper.isElementPresent(APPROVED_TEXTAREA + String.format("//option[@label='%s']", offerName)), equalTo(false));
                } catch (AssertionError e) {
                    hamcrest.append(e.getMessage());
                    TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Incorrect visualisation of approved/blocked offer");
                }
            });
        }
        // check that unapproved/blocked offers are visible in blocked
        if (!testData.getBlockedUnApprovedItemsMap().isEmpty()) {
            testData.getBlockedUnApprovedItemsMap().values().forEach(offerName -> {
                // filter by option
                setFilter(offerName);
                try {
                    assertThat(String.format("%s offer name is present into blocked list", offerName),
                            helper.isElementPresent(BLOCKED_TEXTAREA + String.format("//option[@label='%s']", offerName)), equalTo(true));
                    assertThat(String.format("%s offer name is missed into approved list", offerName),
                            helper.isElementPresent(UNAPPROVED_TEXTAREA + String.format("//option[@label='%s']", offerName)), equalTo(false));
                } catch (AssertionError e) {
                    hamcrest.append(e.getMessage());
                    TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver(), "Incorrect visualisation of unapproved/blocked offer");
                }
            });
        }
        resetFilter();
        // check approved
        hamcrest.assertThat("Approved and Unapproved lists have the same offer IDs" + testData.getApprovedUnApprovedItemsMap(),
                testData.getApprovedUnApprovedItemsMap().isEmpty());
    }

    private void setFilter(String value) {
        InputElement filterByOffer = new InputElement(helper.getElement(OFFER_FILTER_INPUT));
        filterByOffer.setByText(value);
        helper.pause(1000);
    }

    private void resetFilter() {
        new InputElement(helper.getElement(OFFER_FILTER_INPUT)).getInput().clear();
    }

    private void checkAccessGroups(AccessTestData testData) {
        assertThat("At least one group is not empty", testData.hasApprovedItems() || testData.hasUnApprovedItems() || testData.hasBlockedItems());
        hamcrest.assertThat("Approved list contains only unique IDs", testData.getApprovedIDItems().size(), equalTo(new HashSet<>(testData.getApprovedIDItems()).size()));
        hamcrest.assertThat("Unapproved list contains only unique IDs", testData.getUnApprovedIDItems().size(), equalTo(new HashSet<>(testData.getUnApprovedIDItems()).size()));
        hamcrest.assertThat("Blocked list contains only unique IDs", testData.getBlockedIDItems().size(), equalTo(new HashSet<>(testData.getBlockedIDItems()).size()));
        hamcrest.assertThat("Unblocked list contains only unique IDs", testData.getUnBlockedIDItems().size(), equalTo(new HashSet<>(testData.getUnBlockedIDItems()).size()));
    }

    public void checkAccessStatuses(AccessTestData testData) {
        hamcrest = new SoftAssertionHamcrest();
        // check that each statuses group contains unique IDs
        checkAccessGroups(testData);
        // check correct visualization of two groups offer
        checkDoubleGroups(testData);
        // check unapproved offers
        checkUnApprovedStatuses(testData);
        // check approved offers
        checkApprovedStatuses(testData);
        // check blocked offers
        checkBlockedStatuses(testData);
        hamcrest.assertAll();
    }
}
