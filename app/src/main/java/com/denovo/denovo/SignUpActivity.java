package com.denovo.denovo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText editFirstName = (EditText) findViewById(R.id.edit_first_name);
        EditText editLastName = (EditText) findViewById(R.id.edit_last_name);
        EditText editEmail = (EditText) findViewById(R.id.edit_email);
        EditText editPassword = (EditText) findViewById(R.id.edit_password);

        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();


    }
}
