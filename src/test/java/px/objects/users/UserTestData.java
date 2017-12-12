package px.objects.users;

import dto.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static configuration.helpers.DataHelper.*;

/**
 * Created by kgr on 1/13/2017.
 */
public abstract class UserTestData implements TestData {
    protected boolean isPositive;
    protected Contact contact;
    protected LocaleData localeData;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;

    public UserTestData() {
        this(true);
    }

    public UserTestData(boolean isPositive) {
        this.isPositive = isPositive;
        this.contact = ContactFactory.getContact();
        if (isPositive) setPositiveData();
        else setNegativeData();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // update email with unique characters
    protected String getUnigueEmail() {
        String[] emailParts = contact.getEmailAddress().split("@");
        String emailPrefix = emailParts[0];
        emailPrefix += RandomStringUtils.random(9, true, true);
        return emailPrefix + "@" + emailParts[1];
    }

    public void setDuplicatedEmail() {
        LxpDataProvider dataProvider = new LxpDataProvider();
        // created instances
        List<String> userEmailsList = ObjectIdentityData.getAllNames(dataProvider.getCreatedInstancesData("users"));
        this.email = getRandomValueFromList(userEmailsList);
    }

    @Override
    public UserTestData setPositiveData() {
        this.firstName = contact.getFirstName();
        this.lastName = contact.getLastName();
        this.email = getUnigueEmail();
        this.password = RandomStringUtils.random(10, true, true);
        return this;
    }

    @Override
    public UserTestData setNegativeData() {
        this.localeData = new LocaleData();
        this.firstName = contact.getFirstName() + getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        this.lastName = contact.getLastName() + getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        this.email = getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6)) + "_" + contact.getEmailAddress();
        this.password = "";
        return this;
    }

    @Override
    public boolean isPositive() {
        return isPositive;
    }

    @Override
    public String toString() {
        return "UserTestData{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}