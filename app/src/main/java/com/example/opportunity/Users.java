package com.example.opportunity;

public class Users {

    String userAddress;
    String userBirthDay;
    String userDreams;
    String userEmail;
    String userFileName;
    String userFileUrl;
    String userFirstName;
    String userKey;
    String userLastName;
    String userOfpptFirstComment;
    String userOfpptSecondComment;
    String userPassword;
    String userPhoneNumber;
    String userSpecialty;

    public Users() {
    }

    public Users(String userAddress, String userBirthDay, String userEmail, String userFirstName, String userLastName, String userPassword, String userPhoneNumber, String userSpecialty) {
        this.userAddress = userAddress;
        this.userBirthDay = userBirthDay;
        this.userEmail = userEmail;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userPassword = userPassword;
        this.userPhoneNumber = userPhoneNumber;
        this.userSpecialty = userSpecialty;
    }


    public Users(String userAddress, String userBirthDay, String userDreams, String userEmail, String userFileName, String userFileUrl, String userFirstName, String userLastName, String userOfpptAdvantagesComment, String userOfpptDisadvantagesComment, String userPassword, String userPhoneNumber, String userSpecialty) {
        this.userAddress = userAddress;
        this.userBirthDay = userBirthDay;
        this.userDreams = userDreams;
        this.userEmail = userEmail;
        this.userFileName = userFileName;
        this.userFileUrl = userFileUrl;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userOfpptFirstComment = userOfpptAdvantagesComment;
        this.userOfpptSecondComment = userOfpptDisadvantagesComment;
        this.userPassword = userPassword;
        this.userPhoneNumber = userPhoneNumber;
        this.userSpecialty = userSpecialty;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserBirthDay() {
        return userBirthDay;
    }

    public void setUserBirthDay(String userBirthDay) {
        this.userBirthDay = userBirthDay;
    }

    public String getUserDreams() {
        return userDreams;
    }

    public void setUserDreams(String userDreams) {
        this.userDreams = userDreams;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFileName() {
        return userFileName;
    }

    public void setUserFileName(String userFileName) {
        this.userFileName = userFileName;
    }

    public String getUserFileUrl() {
        return userFileUrl;
    }

    public void setUserFileUrl(String userFileUrl) {
        this.userFileUrl = userFileUrl;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserOfpptFirstComment() {
        return userOfpptFirstComment;
    }

    public void setUserOfpptFirstComment(String userOfpptFirstComment) {
        this.userOfpptFirstComment = userOfpptFirstComment;
    }

    public String getUserOfpptSecondComment() {
        return userOfpptSecondComment;
    }

    public void setUserOfpptSecondComment(String userOfpptSecondComment) {
        this.userOfpptSecondComment = userOfpptSecondComment;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserSpecialty() {
        return userSpecialty;
    }

    public void setUserSpecialty(String userSpecialty) {
        this.userSpecialty = userSpecialty;
    }

    public String getUserFullName() {
        return userLastName.toUpperCase() + " " + userFirstName;
    }

    public String getUserShortFullName() {
        return userLastName.charAt(0) + "" + userFirstName.charAt(0);
    }
}
