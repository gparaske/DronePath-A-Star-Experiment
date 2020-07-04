package com.example.dronepath;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Cloud {

    private static FirebaseAuth mAuth;
    public static FirebaseUser user;
    public static FirebaseDatabase database;

    private ChangeListener listener;

    public Cloud(){
        mAuth = FirebaseAuth.getInstance();
    }

    public Boolean createAccount(String email, String password) {
        user = null;
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("Create to Firebase Successful");
                    user = mAuth.getCurrentUser();
                    database = FirebaseDatabase.getInstance();
                    listener.onSuccessfulLogin();
                } else {
                    System.out.println("Create to Firebase Failed");
                }
            }
        });
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }
    public Boolean loginAccount(String email, String password) {
        user = null;
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("Login to Firebase Successful");
                    user = mAuth.getCurrentUser();
                    database = FirebaseDatabase.getInstance();
                    listener.onSuccessfulLogin();
                } else {
                    System.out.println("Login to Firebase Failed");
                }
            }
        });
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onSuccessfulLogin();
    }
}
