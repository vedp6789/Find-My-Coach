package com.findmycoach.app.beans.authentication;

import com.findmycoach.app.beans.Price;
import com.findmycoach.app.beans.mentor.CountryConfig;
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
    /* @Expose
     private Object address;*/
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

    @SerializedName("all_age_group_preferences")
    @Expose
    private List<AgeGroupPreferences> allAgeGroupPreferences;

    @SerializedName("section_1")
    @Expose
    private String myQualification;

    @SerializedName("section_2")
    @Expose
    private String myAccredition;

    @SerializedName("section_3")
    @Expose
    private String myExperience;

    @SerializedName("section_4")
    @Expose
    private String myTeachingMethodology;

    @SerializedName("section_5")
    @Expose
    private String myAwards;

    @SerializedName("slot_type")
    @Expose
    private String slotType;

    @SerializedName("multiple_address_flag")
    @Expose
    private String addressFlagMentor;

    @SerializedName("medium_of_education")
    @Expose
    private String mediumOfEducation;
    @SerializedName("connection_status")
    @Expose
    private String connectionStatus;
    @SerializedName("connection_id")
    @Expose
    private String connectionId;
    @SerializedName("number_of_students")
    @Expose
    private String numberOfStudents;
    @SerializedName("review")
    @Expose
    private List<Review> reviews;

    public String getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(String numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getMediumOfEducation() {
        return mediumOfEducation;
    }

    public void setMediumOfEducation(String mediumOfEducation) {
        this.mediumOfEducation = mediumOfEducation;
    }

    @SerializedName("country_config")
    @Expose
    private ArrayList<CountryConfig> countryConfigArrayList;


    public String getAddressFlagMentor() {
        return addressFlagMentor;
    }

    public void setAddressFlagMentor(String addressFlagMentor) {
        this.addressFlagMentor = addressFlagMentor;
    }

    @SerializedName("address")
    @Expose
    private String physical_address;

    @SerializedName("currency")
    @Expose
    private CurrencyOfUpdatedProfile currencyOfUpdatedProfile;

    public CurrencyOfUpdatedProfile getCurrencyOfUpdatedProfile() {
        return currencyOfUpdatedProfile;
    }

    public void setCurrencyOfUpdatedProfile(CurrencyOfUpdatedProfile currencyOfUpdatedProfile) {
        this.currencyOfUpdatedProfile = currencyOfUpdatedProfile;
    }

    private int flexible_yn;
    private int min_class_duration;
    private int flexibility_window;
    private int max_class_duration;
    private int max_classes_per_week;
    private int fixed_class_duration;
    private List<Price> prices;


    public int getFlexible_yn() {
        return flexible_yn;
    }

    public void setFlexible_yn(int flexible_yn) {
        this.flexible_yn = flexible_yn;
    }

    public int getMin_class_duration() {
        return min_class_duration;
    }

    public void setMin_class_duration(int min_class_duration) {
        this.min_class_duration = min_class_duration;
    }

    public int getFlexibility_window() {
        return flexibility_window;
    }

    public void setFlexibility_window(int flexibility_window) {
        this.flexibility_window = flexibility_window;
    }

    public int getMax_class_duration() {
        return max_class_duration;
    }

    public void setMax_class_duration(int max_class_duration) {
        this.max_class_duration = max_class_duration;
    }

    public int getMax_classes_per_week() {
        return max_classes_per_week;
    }

    public void setMax_classes_per_week(int max_classes_per_week) {
        this.max_classes_per_week = max_classes_per_week;
    }

    public int getFixed_class_duration() {
        return fixed_class_duration;
    }

    public void setFixed_class_duration(int fixed_class_duration) {
        this.fixed_class_duration = fixed_class_duration;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    public ArrayList<CountryConfig> getCountryConfigArrayList() {
        return countryConfigArrayList;
    }

    public String getPhysical_address() {
        return physical_address;
    }

    public void setPhysical_address(String physical_address) {
        this.physical_address = physical_address;
    }

    public void setCountryConfigArrayList(ArrayList<CountryConfig> countryConfigArrayList) {
        this.countryConfigArrayList = countryConfigArrayList;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getMyQualification() {
        return myQualification;
    }

    public void setMyQualification(String myQualification) {
        this.myQualification = myQualification;
    }

    public String getMyAccredition() {
        return myAccredition;
    }

    public void setMyAccredition(String myAccredition) {
        this.myAccredition = myAccredition;
    }

    public String getMyExperience() {
        return myExperience;
    }

    public void setMyExperience(String myExperience) {
        this.myExperience = myExperience;
    }

    public String getMyTeachingMethodology() {
        return myTeachingMethodology;
    }

    public void setMyTeachingMethodology(String myTeachingMethodology) {
        this.myTeachingMethodology = myTeachingMethodology;
    }

    public String getMyAwards() {
        return myAwards;
    }

    public void setMyAwards(String myAwards) {
        this.myAwards = myAwards;
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
     *//*
    public Object getAddress() {
        return address;
    }

    *//**
     * @param address The address
     *//*
    public void setAddress(Object address) {
        this.address = address;
    }
*/

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
