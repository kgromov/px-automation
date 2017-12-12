package elements;

import org.openqa.selenium.WebDriver;

/**
 * Created by kgr on 3/29/2017.
 */
// TODO: make ElementsHelper singleton itself
public class HelperSingleton {
    private static HelperSingleton helperSingleton;
    private static ElementsHelper helper;

    private HelperSingleton (ElementsHelper helper){
        HelperSingleton.helper = helper;
    }

    public static void setHelper(WebDriver driver){
        helperSingleton = new HelperSingleton(new ElementsHelper(driver));
    }

    public static ElementsHelper getHelper() {
        return helper;
    }
}
