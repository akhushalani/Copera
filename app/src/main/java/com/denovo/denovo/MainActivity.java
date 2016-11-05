package com.denovo.denovo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.renderscript.Type;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView denovoTextView = (TextView) findViewById(R.id.denovo_text_view);
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);

        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(),"fonts/JosefinSans-Bold.ttf");
        denovoTextView.setTypeface(typeFaceBold);

        Typeface typeFaceReg = Typeface.createFromAsset(getAssets(), "fonts/JosefinSlab-Regular" +
                ".ttf");
        signInButton.setTypeface(typeFaceReg);
        username.setTypeface(typeFaceReg);
        password.setTypeface(typeFaceReg);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });
    }
}
