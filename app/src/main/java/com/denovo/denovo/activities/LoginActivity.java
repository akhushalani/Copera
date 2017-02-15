package com.denovo.denovo.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denovo.denovo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText inputEmail;
    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //find views from xml
        TextView denovoTextView = (TextView) findViewById(R.id.denovo_text_view);
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        TextView signUp = (TextView) findViewById(R.id.sign_up);

        //set font styles
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/JosefinSans-Bold.ttf");
        denovoTextView.setTypeface(typeFaceBold);

        Typeface typeFaceReg = Typeface.createFromAsset(getAssets(), "fonts/JosefinSlab-Regular" +
                ".ttf");
        signInButton.setTypeface(typeFaceReg);
        inputEmail.setTypeface(typeFaceReg);
        inputPassword.setTypeface(typeFaceReg);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish SignInActivity and start SignUpActivity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get String values from EditTexts
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                //if email field is empty do not sign in
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //if password field is empty do not sign in
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //sign in
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    //if sign in fails, display a toast to the user
                                    Toast.makeText(LoginActivity.this, "Login failed, check your " +
                                            "email and password", Toast.LENGTH_LONG).show();
                                } else {
                                    ///else finish SignInActivity and start MainActivity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity
                                            .class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}
