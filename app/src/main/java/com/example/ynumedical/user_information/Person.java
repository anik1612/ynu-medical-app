package com.example.ynumedical.user_information;

import java.io.Serializable;


public abstract class Person implements Serializable {
    /**
     * Abstract class that holds all methods or fields related to a person or user who will use the application
     * @param name a string that holds the full name of a Person
     * @param email a string that holds the person's email. Due to FirebaseAuth, this field is unique to each Person
     * @param password a string that holds each Person's password. This field must be at least 6 characters. This is checked in the sign up activities before creating a Person
     */
    public String name;
    public String email;
    private String password;


    public Person(){};

    public Person(String name){
        this.name = name;
    }

    public Person(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }


    //---------------- Getters and Setters--------------------//
    public void setName(String name){this.name = name;}
    public void setEmail(String email){this.email = email;}
    public void setPassword(String password){this.password = password;}

    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getPassword(){return password;}
    //---------------- Getters and Setters--------------------//


}
