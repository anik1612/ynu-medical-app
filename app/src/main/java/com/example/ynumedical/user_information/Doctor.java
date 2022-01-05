package com.example.ynumedical.user_information;

public class Doctor extends Person{
    /**
     * Class that extends Person, with additional fields and methods related to Doctors
     * @param specialty a string that stores the specialty of the doctor
     * @param gender a string that stores the gender of the doctor - either "Male" or "Female"
     */

    String specialty;
    String gender;

    public Doctor(){}

    public Doctor(String name){
        super(name);
    }

    public Doctor(String name, String email, String password){
        super(name, email, password);
    }

    public Doctor(String name, String email, String password, String gender, String specialty){
        super(name, email, password);
        this.gender = gender;
        this.specialty = specialty;
    }



    //---------------- Getters and Setters --------------------//
    public String getGender(){return gender;}
    public void setGender(String gender){this.gender=gender;}
    public String getSpecialty(){return specialty;}
    public void setSpecialty(String specialty){this.specialty = specialty;}

    //---------------- Getters and Setters--------------------//


    @Override
    public String toString() {
        return "{Doctor name: Dr. " + name + "}";
    }
}
