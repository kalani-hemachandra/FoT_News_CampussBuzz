package com.fotnews.campusbuzz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // <-- Import for logging
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity"; // <-- TAG for logs

    private TextInputEditText usernameInput, passwordInput;
    private MaterialButton loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            Log.e(TAG, "Error hiding action bar: " + e.getMessage());
        }

        setContentView(R.layout.activity_login);
        Log.d(TAG, "Layout set");

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        TextView registerNow = findViewById(R.id.registerNow);

        if (usernameInput == null || passwordInput == null || loginButton == null || registerNow == null) {
            Log.e(TAG, "One or more views are null - check your activity_login.xml IDs");
        } else {
            Log.d(TAG, "All views initialized");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "FirebaseAuth instance obtained");

        loginButton.setOnClickListener(view -> {
            Log.d(TAG, "Login button clicked");
            loginUser();
        });

        registerNow.setOnClickListener(v -> {
            Log.d(TAG, "RegisterNow clicked");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        Log.d(TAG, "Login attempt with email: " + email);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Email or password is empty");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Login successful");
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e(TAG, "Login failed: " + error);
                        Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
