package com.findmycoach.app.beans.authentication;

import com.findmycoach.app.beans.student.Address;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Data {
    @SerializedName("locations")
    @Expose
    private ArrayList<Address> multipleAddress;

    public ArrayList<Address> getMultipleAddress() {
        return multipleAddress;
    }

    public void setMultipleAddress(ArrayList<Address> multipleAddress) {
        this.multipleAddress = multipleAddress;
    }

    @Expose
    private String id;
    @Expose
    private String email;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @Expose
    private String phonenumber;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @Expose
    private Object address;
    @Expose
    private Object city;
    @Expose
    private Object state;
    @Expose
    private Object zip;
    @Expose
    private Object dob;
    @Expose
    private Object country;
    @Expose
    private String photograph;
    @Expose
    private String rating;
    @Expose
    private String credentials;
    @Expose
    private String experience;
    @Expose
    private String location;
    @SerializedName("availability_yn")
    @Expose
    private String availabilityYn;
    @SerializedName("google_link")
    @Expose
    private String googleLink;
    @SerializedName("facebook_link")
    @Expose
    private String facebookLink;
    @SerializedName("charges_class")
    @Expose
    private String chargesClass;
    @Expose
    private String charges;
    @Expose
    private String accomplishments;
    @Expose
    private String gender;
    @SerializedName("sub_category_name")
    @Expose
    private List<SubCategoryName> subCategoryName;
    @SerializedName("new_user")
    @Expose
    private boolean newUser;

    @SerializedName("unicode")
    @Expose
    private String currencyCode;

    @SerializedName("auth_token")
    @Expose
    private String authToken;

    @SerializedName("age_group_preference")
    @Expose
    private String ageGroupPreferences;

    @SerializedName("all_age_group_preference")
    @Expose
    private List<AgeGroupPreferences> allAgeGroupPreferences;

    @SerializedName("section_1")
    @Expose
    private String section1;

    @SerializedName("section_2")
    @Expose
    private String section2;

    @SerializedName("section_3")
    @Expose
    private String section3;

    @SerializedName("section_4")
    @Expose
    private String section4;

    @SerializedName("section_5")
    @Expose
    private String section5;

    @SerializedName("slot_type")
    @Expose
    private String slotType;

    @SerializedName("multiple_address_flag")
    @Expose
    private String addressFlagMentor;


    public String getAddressFlagMentor() {
        return addressFlagMentor;
    }

    public void setAddressFlagMentor(String addressFlagMentor) {
        this.addressFlagMentor = addressFlagMentor;
    }




    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getSection1() {
        return section1;
    }

    public void setSection1(String section1) {
        this.section1 = section1;
    }

    public String getSection2() {
        return section2;
    }

    public void setSection2(String section2) {
        this.section2 = section2;
    }

    public String getSection3() {
        return section3;
    }

    public void setSection3(String section3) {
        this.section3 = section3;
    }

    public String getSection4() {
        return section4;
    }

    public void setSection4(String section4) {
        this.section4 = section4;
    }

    public String getSection5() {
        return section5;
    }

    public void setSection5(String section5) {
        this.section5 = section5;
    }


    public String getAgeGroupPreferences() {
        return ageGroupPreferences;
    }

    public void setAgeGroupPreferences(String ageGroupPreferences) {
        this.ageGroupPreferences = ageGroupPreferences;
    }

    public List<AgeGroupPreferences> getAllAgeGroupPreferences() {
        return allAgeGroupPreferences;
    }

    public void setAllAgeGroupPreferences(List<AgeGroupPreferences> allAgeGroupPreferences) {
        this.allAgeGroupPreferences = allAgeGroupPreferences;
    }

    public Object getTrainingLocation() {
        return trainingLocation;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @SerializedName("training_location")
    @Expose
    private Object trainingLocation;

    public Object isTrainingLocation() {
        return trainingLocation;
    }

    public void setTrainingLocation(Object trainingLocation) {
        this.trainingLocation = trainingLocation;
    }

    public String getChargesClass() {
        return chargesClass;
    }

    public void setChargesClass(String chargesClass) {
        this.chargesClass = chargesClass;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public List<SubCategoryName> getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(List<SubCategoryName> subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAccomplishments() {
        return accomplishments;
    }

    public void setAccomplishments(String accomplishments) {
        this.accomplishments = accomplishments;
    }

    @Expose
    private String profession;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The phonenumber
     */
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     * @param phonenumber The phonenumber
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * @return The middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName The middle_name
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * @return The address
     */
    public Object getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(Object address) {
        this.address = address;
    }

    /**
     * @return The city
     */
    public Object getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(Object city) {
        this.city = city;
    }

    /**
     * @return The state
     */
    public Object getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(Object state) {
        this.state = state;
    }

    /**
     * @return The zip
     */
    public Object getZip() {
        return zip;
    }

    /**
     * @param zip The zip
     */
    public void setZip(Object zip) {
        this.zip = zip;
    }

    /**
     * @return The dob
     */
    public Object getDob() {
        return dob;
    }

    /**
     * @param dob The dob
     */
    public void setDob(Object dob) {
        this.dob = dob;
    }

    /**
     * @return The country
     */
    public Object getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(Object country) {
        this.country = country;
    }

    /**
     * @return The photograph
     */
    public String getPhotograph() {
        return photograph;
    }

    /**
     * @param photograph The photograph
     */
    public void setPhotograph(String photograph) {
        this.photograph = photograph;
    }

    /**
     * @return The rating
     */
    public String getRating() {
        return rating;
    }

    /**
     * @param rating The rating
     */
    public void setRating(String rating) {
        this.rating = rating;
    }

    /**
     * @return The credentials
     */
    public String getCredentials() {
        return credentials;
    }

    /**
     * @param credentials The credentials
     */
    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    /**
     * @return The experience
     */
    public String getExperience() {
        return experience;
    }

    /**
     * @param experience The experience
     */
    public void setExperience(String experience) {
        this.experience = experience;
    }

    /**
     * @return The location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location The location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return The availabilityYn
     */
    public String getAvailabilityYn() {
        return availabilityYn;
    }

    /**
     * @param availabilityYn The availability_yn
     */
    public void setAvailabilityYn(String availabilityYn) {
        this.availabilityYn = availabilityYn;
    }

    /**
     * @return The googleLink
     */
    public String getGoogleLink() {
        return googleLink;
    }

    /**
     * @param googleLink The google_link
     */
    public void setGoogleLink(String googleLink) {
        this.googleLink = googleLink;
    }

    /**
     * @return The facebookLink
     */
    public String getFacebookLink() {
        return facebookLink;
    }

    /**
     * @param facebookLink The facebook_link
     */
    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    /**
     * @return The charges
     */
    public String getCharges() {
        return charges;
    }

    /**
     * @param charges The charges
     */
    public void setCharges(String charges) {
        this.charges = charges;
    }

}
