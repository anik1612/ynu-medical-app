package com.example.ynumedical.user_information;

public class Patient extends Person{
    /**
     * Class that extends Person, with additional field and methods related to Patients
     * @param healthInformation a HealthInformation object that stores basic patient information
     */
    private HealthInformation healthInformation;

    public Patient(){}

    public Patient(String name){
        super(name);
    }

    public Patient(String name, String email, String password, HealthInformation healthInformation) {
        super(name, email, password);
        this.healthInformation = healthInformation;
    }



    //---------------- Getters and Setters--------------------//
    public void setHealthInformation(HealthInformation healthInformation){this.healthInformation = healthInformation;}
    public HealthInformation getHealthInformation() { return healthInformation; }
    //---------------- Getters and Setters--------------------//

    @Override
    public String toString() {
        return "{Patient name: " + name + "}";
    }



}
