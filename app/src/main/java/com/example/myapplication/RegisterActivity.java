package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private TextInputLayout usernameInputLayout, emailInputLayout, passwordInputLayout, passwordConfirmInputLayout;
    private Button buttonRegister;
    private TextView textViewLogin;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find views by their respective IDs
        editTextUsername = findViewById(R.id.edit_text_username);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextConfirmPassword = findViewById(R.id.edit_text_password_confirm);
        usernameInputLayout = findViewById(R.id.username_input_layout);
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        passwordConfirmInputLayout = findViewById(R.id.password_confirm_input_layout);
        buttonRegister = findViewById(R.id.button_register);
        db = new DBHelper(this);
        // Register button click listener
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()){
                    return;
                }
                boolean isInserted = db.insertUser(editTextUsername.getText().toString(), editTextPassword.getText().toString(), editTextEmail.getText().toString());
                if (isInserted) {
                    finish();
                }
                else Toast.makeText(RegisterActivity.this, "Failed To Insert User", Toast.LENGTH_SHORT).show();

            }
        });



    }
    public void goToLoginPage(View view) {
        // Navigate to the login page
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean validateForm() {
        // Reset any previous errors
        usernameInputLayout.setError(null);
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
        passwordConfirmInputLayout.setError(null);

        // Get the entered values
        String username = editTextUsername.getText().toString().trim().toLowerCase();
        String email = editTextEmail.getText().toString().trim().toLowerCase();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            usernameInputLayout.setError("Please enter a username");
            editTextUsername.requestFocus();
            return false;
        }
        if (db.checkUsernameExisted(username)) {
            Toast.makeText(RegisterActivity.this, "Username is taken, please choose Another", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Please enter an email address");
            editTextEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return false;
        }

        if (db.checkEmailExisted(email)) {
            Toast.makeText(RegisterActivity.this, "Username is taken, please choose Another", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Please enter a password");
            editTextPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            passwordConfirmInputLayout.setError("Please confirm your password");
            editTextConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            passwordConfirmInputLayout.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return false;
        }
        return true;

    }
}