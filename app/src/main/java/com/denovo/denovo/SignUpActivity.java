package com.denovo.denovo;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inputFirstName = (EditText) findViewById(R.id.edit_first_name);
        inputLastName = (EditText) findViewById(R.id.edit_last_name);
        inputEmail = (EditText) findViewById(R.id.edit_email);
        inputPassword = (EditText) findViewById(R.id.edit_password);
        final Button signUpButton = (Button) findViewById(R.id.btn_sign_up);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    return;
                }
                createAccount(inputEmail.getText().toString(), inputPassword.getText().toString());
                signIn(inputEmail.getText().toString(), inputPassword.getText().toString());
                updateProfile();
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
                            Toast.makeText(SignUpActivity.this, "Sign Up failed, restart app and try again.",
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
                            Toast.makeText(SignUpActivity.this, "Login failed, check your " +
                                    "email and password", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(SignUpActivity.this, MainActivity
                                    .class);
                            startActivity(intent);
                            finish();
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

        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(getApplicationContext(), "Enter first name!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(getApplicationContext(), "Enter last name!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void updateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("" + inputFirstName.getText().toString() + " " + inputLastName.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
}
