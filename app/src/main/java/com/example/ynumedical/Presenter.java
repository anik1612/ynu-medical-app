package com.example.ynumedical;

public class Presenter {

    private Model model;
    private LoginPage view;

    public Presenter(Model model, LoginPage view){
        this.model = model;
        this.view = view;
    }

    public void login(){
       String email = view.getEmail();
       String password = view.getPassword();

        if(email.equals(""))
            view.displayMessage("email cannot be empty");
        else if(password.equals(""))
            view.displayMessage("password cannot be empty");
        else if(password.length() < 6)
            view.displayMessage("The minimum password length is 6 characters");

        else{
            int checker = model.userIsFound(email, password);
            if(checker==0)
                view.displayMessage("invalid login");
            else{
                view.displayMessage("trying to login");
                view.userLogin(email, password);
            }

        }

    }
}
