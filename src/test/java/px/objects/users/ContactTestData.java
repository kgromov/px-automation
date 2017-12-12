package px.objects.users;

import configuration.helpers.DataHelper;
import dto.StateShortNameToFullName;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static configuration.helpers.DataHelper.*;

/**
 * Created by kgr on 1/25/2017.
 */
public class ContactTestData extends UserTestData {
    private String gender;
    private String birthDate;
    private String city;
    private String state;
    private String zipCode;
    private String streetAddress;
    private String streetNR;
    private Date dob;
    // additional fields
    private String middleName;
    private String initials;
    private String mobilePhone;
    private String businessPhone;
    private String internalEmail;
    private String ipAddress;

    public ContactTestData() {
        this(true);
    }

    public ContactTestData(boolean isPositive) {
        super(isPositive);
        if (isPositive) setPositiveData();
        else setNegativeData();
        this.internalEmail = getUnigueEmail();
    }

    @Override
    public ContactTestData setPositiveData() {
        super.setPositiveData();
        // name
        this.middleName = lastName.substring(0, 2) + firstName.substring(0, 2);
        this.initials = firstName.charAt(0) + "." + lastName.charAt(0) + "." + lastName.charAt(0);
        // personal
        this.gender = contact.getGender();
        this.dob = getDateByFormatSimple(BIRTH_DATE_DATA_PATTERN, contact.getBirthDate());
        this.birthDate = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, dob);
        // address
        this.mobilePhone = RandomStringUtils.randomNumeric(10);
        this.businessPhone = contact.getPhoneNumber();
        this.streetAddress = contact.getAddress();
        this.streetNR = remainDigits(streetAddress);
        this.city = contact.getCity();
        this.state = StateShortNameToFullName.getFullStateName(contact.getState());
        this.zipCode = contact.getZipCode();
        this.ipAddress = String.format("%03d.%03d.%03d.%03d", getRandomInt(255),
                getRandomInt(255),getRandomInt(255),getRandomInt(255));
        return this;
    }

    @Override
    public ContactTestData setNegativeData() {
        super.setNegativeData();
        // name
        this.middleName = getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        this.initials = firstName.charAt(firstName.length() - 1) + "."
                + lastName.charAt(lastName.length() - 1) + "." + lastName.charAt(lastName.length() - 1);
        // personal
        this.gender = contact.getGender();
        this.dob = DataHelper.getRandBoolean() ? getDateByYearOffset(getRandomInt(-1000, 1000, 100)) : new Date();
        this.birthDate = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, dob);
        // address
        this.mobilePhone = RandomStringUtils.random(10, true, true);
        this.businessPhone = RandomStringUtils.random(10, true, true);
        this.streetAddress = contact.getAddress() + getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        this.streetNR = RandomStringUtils.random(10, true, true);
        this.city = RandomStringUtils.random(10, true, true);
        this.state = StateShortNameToFullName.getFullStateName(contact.getState());
        this.zipCode = getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 10));
        return this;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getStreetNR() {
        return streetNR;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getInitials() {
        return initials;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public String getInternalEmail() {
        return internalEmail;
    }

    public Date getDob() {
        return dob;
    }

    // setters
    public void setState(String state) {
        this.state = state;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setStreetNR(String streetNR) {
        this.streetNR = streetNR;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public void setInternalEmail(String internalEmail) {
        this.internalEmail = internalEmail;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // filter management
    public Map<String, String> toFilterMap(){
        Map<String, String> map = new HashMap<>();
        map.put("ContactData.FirstName", firstName);
        map.put("ContactData.LastName", lastName);
        map.put("ContactData.Address", streetAddress);
        map.put("ContactData.City", city);
//        map.put("ContactData.State", state);
        map.put("ContactData.ZIPCode", zipCode);
        map.put("ContactData.EmailAddress", email);
        map.put("ContactData.PhoneNumber", remainDigits(businessPhone));
        map.put("ContactData.DayPhoneNumber", mobilePhone);
        map.put("ContactData.MobilePhoneNumber", RandomStringUtils.randomNumeric(10));
        map.put("ContactData.AdditionalPhoneNumber", RandomStringUtils.randomNumeric(10));
        map.put("ContactData.IPAddress", ipAddress);
        map.put("ContactData.MonthsAtResidence", String.valueOf(getRandomInt(11)));
        return map;
    }

    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        map.put("FirstName", firstName);
        map.put("LastName", lastName);
        map.put("Address", streetAddress);
        map.put("City", city);
        map.put("State", state);
        map.put("ZipCode", zipCode);
        map.put("EmailAddress", internalEmail);
        map.put("PhoneNumber", remainDigits(businessPhone));
        map.put("DayPhoneNumber", mobilePhone);
        map.put("MobilePhoneNumber", RandomStringUtils.randomNumeric(10));
        map.put("AdditionalPhoneNumber", RandomStringUtils.randomNumeric(10));
        map.put("IPAddress", ipAddress);
        map.put("MonthsAtResidence", String.valueOf(getRandomInt(11)));
        return map;
    }

    @Override
    public String toString() {
        return super.toString() + "\nContactTestData{" +
                "gender='" + gender + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", streetNR='" + streetNR + '\'' +
                ", middleName='" + middleName + '\'' +
                ", initials='" + initials + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", businessPhone='" + businessPhone + '\'' +
                ", internalEmail='" + internalEmail + '\'' +
                '}';
    }
}