package elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static pages.locators.ElementLocators.*;

/**
 * Created by kgr on 10/6/2016.
 */
@FindBy(xpath = ".//*")
public class SectionTitleElement extends SuperTypifiedElement {
    @FindBy(xpath = GENERAL_HEADER)
    private WebElement element;

    @FindBy(xpath = SAVE_BUTTON)
    private WebElement saveBtn;

    @FindBy(xpath = EDIT_BUTTON)
    private WebElement editBtn;

    @FindBy(xpath = REVERT_BUTTON)
    private WebElement revertBtn;

    @FindBy(xpath = TOGGLE_BUTTON)
    private WebElement toggleBtn;

    public SectionTitleElement(WebElement wrappedElement) {
        super(wrappedElement);
    }

    public void clickSave() {
//        saveBtn.click();
        helper.getElement(getWrappedElement(), SAVE_BUTTON).click();
    }

    public void clickEdit() {
//        editBtn.click();
        helper.getElement(getWrappedElement(), EDIT_BUTTON).click();
    }

    public void clickRevert() {
//        revertBtn.click();
        helper.getElement(getWrappedElement(), REVERT_BUTTON).click();
    }

    public void collapseSection() {
        if (!isSectionCollapsed()) {
            helper.getElement(getWrappedElement(), TOGGLE_BUTTON).click();
        }
    }

    public void expandSection() {
        if (isSectionCollapsed()) {
            helper.getElement(getWrappedElement(), TOGGLE_BUTTON).click();
        }
    }

    // not very good way indeed
    public boolean isSectionCollapsed() {
        String clazz = getWrappedElement().getAttribute("class");
        return !clazz.contains("active") & !clazz.contains("expanded");
    }

    public WebElement getToggleBtn() {
        return toggleBtn;
    }
}
