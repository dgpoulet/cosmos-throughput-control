package org.example;

public class Customer {

    private String id="";
    private String firstName="";
    private String lastName="";
    private Integer customerID;
    private String location="";
    private String randomNotes="";
    public String getRandomNotes() {
        return randomNotes;
    }
    public void setRandomNotes(String randomNotes) {
        this.randomNotes = randomNotes;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public Integer getCustomerID() {
        return customerID;
    }
    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

}
