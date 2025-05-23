package core.entities;

import common.env.Constants;
import common.tools.DataUtils;
import common.tools.DataValidate;
import java.time.LocalDate;

public final class Guest {

    // National ID number: Includes 12 digits and must be unique
    // o Full name: between 2 and 25 characters long and must start with a letter
    // o Birthdate: This value represents a date of birth
    // o Gender: Represents only one of two values indicating male or female gender
    // o Phone number: A 10-digit number matching the formats of mobile carriers in Vietnam.
    // o Desired room ID: Up to 5 characters, starting with a letter followed by digits
    // o Number of rental days: Must be a positive integer
    // o Start date: Must be a future date
    // o Name of co-tenant (optional)
    private String nationalID;
    private String fullName;
    private LocalDate birthdate;
    private String gender;
    private String phoneNumber;
    private String desiredRoomID;
    private int numberOfRentalDays;
    private LocalDate startDate;
    private String nameOfCoTenant;

    public Guest(
            String nationalID, String fullName, LocalDate birthdate, String gender, String phoneNumber,
            String desiredRoomID, int numberOfRentalDays, LocalDate startDate, String nameOfCoTenant
    ) throws Exception {
        setNationalID(nationalID);
        setFullName(fullName);
        setBirthdate(birthdate);
        setGender(gender);
        setPhoneNumber(phoneNumber);
        setDesiredRoomID(desiredRoomID);
        setNumberOfRentalDays(numberOfRentalDays);
        setStartDate(startDate);
        setNameOfCoTenant(nameOfCoTenant);
    }

    // public Guest(
    //         String nationalID, String fullName, LocalDate birthdate, String gender, String phoneNumber,
    //         String desiredRoomID, int numberOfRentalDays, LocalDate startDate
    // ) throws Exception {
    //     setNationalID(nationalID);
    //     setFullName(fullName);
    //     setBirthdate(birthdate);
    //     setGender(gender);
    //     setPhoneNumber(phoneNumber);
    //     setDesiredRoomID(desiredRoomID);
    //     setNumberOfRentalDays(numberOfRentalDays);
    //     setStartDate(startDate);
    //     nameOfCoTenant = "";
    // }
    public String getNationalID() {
        return nationalID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getGender() {
        return gender;
    }

    public String getDesiredRoomID() {
        return desiredRoomID;
    }

    public int getNumberOfRentalDays() {
        return numberOfRentalDays;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getNameOfCoTenant() {
        return nameOfCoTenant;
    }

    public void setNationalID(String nationalID) throws Exception {
        if (!DataValidate.checkStringWithFormat(nationalID, Constants.NATIONAL_ID_PATTERN)) {
            throw new Exception("National ID invalid.");
        }
        this.nationalID = nationalID;
    }

    public void setFullName(String fullName) throws Exception {
        if (!DataValidate.checkStringWithFormat(fullName, Constants.GUEST_NAME_PATTERN)) {
            throw new Exception("Full name invalid.");
        }
        this.fullName = DataUtils.toTitleCase(fullName);
    }

    public void setBirthdate(LocalDate birthdate) throws Exception {
        if (birthdate.isAfter(LocalDate.now())) {
            throw new Exception("Birthdate must be a past date.");
        }
        this.birthdate = birthdate;
    }

    public void setGender(String gender) throws Exception {
        if (gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female")) {
            this.gender = DataUtils.toTitleCase(gender);
        } else {
            throw new Exception("Gender invalid.");
        }
    }

    public void setPhoneNumber(String phoneNumber) throws Exception {
        if (!DataValidate.checkStringWithFormat(phoneNumber, Constants.GUEST_PHONE_PATTERN)) {
            throw new Exception("Phone number invalid.");
        }
        this.phoneNumber = phoneNumber;
    }

    public void setDesiredRoomID(String desiredRoomID) throws Exception {
        if (!DataValidate.checkStringWithFormat(desiredRoomID, Constants.ROOM_ID_PATTERN)) {
            throw new Exception("Desired room ID invalid.");
        }
        this.desiredRoomID = desiredRoomID.toUpperCase();
    }

    public void setNumberOfRentalDays(int numberOfRentalDays) throws Exception {
        if (!DataValidate.checkStringWithFormat(String.valueOf(numberOfRentalDays), Constants.POSITIVE_NUMBER_PATTERN)) {
            throw new Exception("Number of rental days must be a positive integer.");
        }
        this.numberOfRentalDays = numberOfRentalDays;
    }

    public void setStartDate(LocalDate startDate) throws Exception {
        if (startDate.isBefore(LocalDate.now())) {
            throw new Exception("Start date must be a future date.");
        }
        this.startDate = startDate;
    }

    public void setNameOfCoTenant(String nameOfCoTenant) throws Exception {
        if (!nameOfCoTenant.isEmpty() || !DataValidate.checkStringWithFormat(nameOfCoTenant, Constants.GUEST_NAME_PATTERN)) {
            throw new Exception("Name of co-tenant invalid.");
        }
        this.nameOfCoTenant = DataUtils.toTitleCase(nameOfCoTenant);
    }

    @Override
    public String toString() {
        return String.format(
                "%s, %s, %s, %s, %s, %s, %d, %s, %s",
                nationalID, fullName, this.birthdate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                gender, phoneNumber, desiredRoomID, numberOfRentalDays, this.startDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                this.nameOfCoTenant.isEmpty() ? "None" : this.nameOfCoTenant
        );
    }
}
