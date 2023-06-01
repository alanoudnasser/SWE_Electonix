package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    TextInputEditText username, password;
    private TextInputLayout usernameInputLayout,passwordInputLayout;
    DBHelper db;
    private void makeRegisterClickable(TextView registerText) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        };
        SpannableString spannable = new SpannableString(registerText.getText());
        int startIndex = registerText.getText().toString().indexOf("Register");
        int endIndex = startIndex + "Register".length();
        spannable.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerText.setText(spannable);
        registerText.setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (User._username != null) {
            Intent intent = new Intent(LoginActivity.this,HomepageActivity.class);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_login);
        this.makeRegisterClickable( findViewById(R.id.register_text));
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        usernameInputLayout = findViewById(R.id.username_layout);
        passwordInputLayout = findViewById(R.id.password_layout);

        db = new DBHelper(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm())return ;
                if (db.checkUsernameAndPassword(username.getText().toString(), password.getText().toString() )){
                    Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean validateForm() {
        usernameInputLayout.setError(null);
        passwordInputLayout.setError(null);
        String usernameText = username.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        if (TextUtils.isEmpty(usernameText)) {
            usernameInputLayout.setError("Please enter a username");
            username.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(usernameText)) {
            usernameInputLayout.setError("Please enter a Password");
            username.requestFocus();
            return false;
        }
        return true;
    }
}