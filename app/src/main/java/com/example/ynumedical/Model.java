package com.example.ynumedical;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Model {
    List<String> emails;
    List<String> passwords;
    private FirebaseAuth auth;
    private int loginSuccess;

    public Model(){
        loginSuccess = 2;
    }

    public int loginSuccess(){return loginSuccess;}


    public int userIsFound(String email, String password) {


        auth = FirebaseAuth.getInstance();


        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginSuccess = 1;

                    //If authentication fails
                } else {
                    loginSuccess = 0;

                }
            }
        });
        return this.loginSuccess;
    }

}
