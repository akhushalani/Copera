package com.denovo.denovo;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import static android.R.attr.updatePeriodMillis;
import static android.R.attr.y;
import static android.R.id.input;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText confirmPassword;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitle(null);

        //find views from xml
        inputFirstName = (EditText) findViewById(R.id.edit_first_name);
        inputLastName = (EditText) findViewById(R.id.edit_last_name);
        inputEmail = (EditText) findViewById(R.id.edit_email);
        inputPassword = (EditText) findViewById(R.id.edit_password);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        final Button signUpButton = (Button) findViewById(R.id.btn_sign_up);

        //instantiate database and FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    //disable signUpButton if fields are empty
                    signUpButton.setEnabled(false);
                    return;
                }
                //else create an account and sign in
                createAccount(inputEmail.getText().toString(), inputPassword.getText().toString());
                signIn(inputEmail.getText().toString(), inputPassword.getText().toString());
            }
        });
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Sign Up failed, check that you have a secure internet connection",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                        } else {
                            //if sign in is successful then update the user's profile with their first and last name
                            updateProfile(firstName + " " + lastName,
                                    "" + firstName.charAt(0) + lastName.charAt(0));
                        }
                    }
                });
    }

    /**
     * Method to check if all fields are filled and valid
     *
     * @return valid
     */
    private boolean validateForm() {
        boolean valid = true;

        //get Strings from EditTexts
        firstName = inputFirstName.getText().toString();
        lastName = inputLastName.getText().toString();
        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();
        confirm = confirmPassword.getText().toString();

        //Check to see if field is filled, if not display a toast to the user and set valid to false
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(getApplicationContext(), "Enter first name!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(getApplicationContext(), "Enter last name!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (!password.equals(confirm)) {
            Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    /**
     * Update the new user's profile with name and initials from the EditTexts
     *
     * @param name     is the name of the new user
     * @param initials is the initials of the new user
     */
    private void updateProfile(String name, String initials) {
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        addUserToDb(name, initials, user.getUid());

        //set user display name
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        //when the profile is finished updating, finish SignUpActivity and start MainActivity
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity
                                .class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    /**
     * Write the new User to the database
     *
     * @param name is the name of the new user
     * @param initials is the initials of the new user
     * @param uid is the unique id of the new user
     */
    private void addUserToDb(String name, String initials, String uid) {
        //create empty wishlist for each new user
        ArrayList<String> wishlist = new ArrayList<>();
        //create new User with inputted information
        User newUser = new User(name, uid, initials, randomColor(), wishlist);
        //write user to database's users branch
        mDatabase.child("users").child(uid).setValue(newUser);
    }

    /**
     * Choose a random color for the default user profile pic
     *
     * @return String of the random color
     */
    private String randomColor() {
        Random rand = new Random();
        int n = rand.nextInt(9) + 1;
        switch (n) {
            case 1:
                return "red";
            case 2:
                return "pink";
            case 3:
                return "purple";
            case 4:
                return "blue";
            case 5:
                return "teal";
            case 6:
                return "green";
            case 7:
                return "yellow";
            case 8:
                return "orange";
            case 9:
                return "gray";
            default:
                return "blue";
        }
    }
}
