package com.fotnews.campusbuzz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View; // Import View
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInfoActivity extends BaseActivity {
    private static final String TAG = "UserInfoActivity";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    private TextView tvUsername, tvEmail;
    private Button btnUpdateInfo;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private SearchView searchBar; // Declare the SearchView
    private SharedPreferences sharedPreferences;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String userId;
    private MaterialButton logoutButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);

        initViews();
        setupBottomNavigation();
        setupToolbarWithUsername();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadUserDataFromFirebase(userId);
        }

        // Initialize logoutButton here, before setting its listener
        logoutButton = findViewById(R.id.logout_button);
        if (logoutButton != null) { // Add a null check for robustness
            logoutButton.setOnClickListener(v -> showSignOutDialog());
        } else {
            Log.e(TAG, "Logout button not found in layout!");
        }


        setupClickListeners();
        // Set up the more options menu
        setupMoreOptionsMenu();

        getSupportActionBar().hide();
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tv_username);
        tvEmail = findViewById(R.id.tv_email);
        btnUpdateInfo = findViewById(R.id.btn_update_info);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home_icon);
        searchBar = findViewById(R.id.search_bar); // Initialize the search bar

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void setupMoreOptionsMenu() {
        ImageView moreOptions = findViewById(R.id.more_options);
        moreOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(UserInfoActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menu_user_info) {
                    startActivity(new Intent(this, UserInfoActivity.class));
                    return true;
                } else if (id == R.id.menu_dev_info) {
                    startActivity(new Intent(this, DevInfoActivity.class));
                    return true;
                } else if (id == R.id.menu_add_news) {
                    startActivity(new Intent(this, AddNewsActivity.class));
                    return true;
                } else if (id == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            // Show the popup menu
            popupMenu.show();
        });
    }


    // THIS IS THE EDITED SECTION
    private void showSignOutDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sign_out_confirmation_dialog);
        dialog.setCancelable(true); // Allow dismissing by tapping outside or back button

        // Cast to MaterialButton if you chose Option 2 in XML
        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);
        MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel);

        // Add null checks for buttons from the dialog layout
        if (btnOk == null || btnCancel == null) {
            Log.e(TAG, "Error: btn_ok or btn_cancel not found in sign_out_confirmation_dialog.xml");
            Toast.makeText(this, "Sign-out dialog error: Buttons not found.", Toast.LENGTH_LONG).show();
            return; // Exit if buttons are not found to prevent NullPointerException
        }

        btnOk.setOnClickListener(v -> {
            performSignOut(); // This will now include Firebase signOut()
            dialog.dismiss(); // Dismiss the dialog after action
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss()); // Just dismiss the dialog

        // Show the dialog at the top of the screen
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.y = 500; // Position from the top
            window.setAttributes(layoutParams);
            window.setBackgroundDrawableResource(android.R.color.transparent); // Make background transparent to show custom shape
        }

        dialog.show(); // Display the dialog
    }

    // THIS IS THE EDITED SECTION - IMPORTANT: ADDED FIREBASE SIGN-OUT HERE
    private void performSignOut() {
        // Perform Firebase sign-out first
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

        // Then navigate to the LoginActivity and clear the back stack
        Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish the current UserInfoActivity
    }


    private void setupBottomNavigation() {
        if (bottomNavigationView == null) {
            Log.e(TAG, "bottomNavigationView is null");
            return;
        }

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.search_icon) {
                    // Toggle visibility of the search bar
                    if (searchBar.getVisibility() == View.VISIBLE) {
                        searchBar.setVisibility(View.GONE);
                    } else {
                        searchBar.setVisibility(View.VISIBLE);
                        searchBar.requestFocus(); // Give focus to the search bar
                    }
                    // You might want to handle actual search queries here or in a dedicated method
                    searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            Toast.makeText(UserInfoActivity.this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                            // Here you would typically initiate a search operation
                            searchBar.clearFocus(); // Hide keyboard after search
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            // You can filter results in real-time here
                            return false;
                        }
                    });
                    return true;
                } else if (itemId == R.id.home_icon) {
                    // Navigate to HomeActivity
                    Intent intent = new Intent(UserInfoActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Clear all activities on top and bring HomeActivity to front if it exists
                    startActivity(intent);
                    overridePendingTransition(0, 0); // No animation
                    finish(); // Finish current activity to prevent stack buildup
                    return true;
                } else if (itemId == R.id.back_arrow) {
                    // Navigate back to the previous activity in the stack
                    onBackPressed(); // This will call the system's back behavior
                    overridePendingTransition(0, 0); // No animation
                    return true;
                }
                return false;
            }
        });
    }

    private void loadUserDataFromFirebase(String uid) {
        firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");

                        // Use default values if null
                        String displayUsername = username != null ? username : "Guest User";
                        String displayEmail = email != null ? email : "guest@example.com";

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_USERNAME, displayUsername);
                        editor.putString(KEY_EMAIL, displayEmail);
                        editor.apply();

                        tvUsername.setText(displayUsername);
                        tvEmail.setText(displayEmail);

                        Log.d(TAG, "User data fetched from Firebase: " + username + ", " + email);
                    } else {
                        Log.d(TAG, "No user document found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch user data", e);
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                });
    }


    private void setupClickListeners() {
        btnUpdateInfo.setOnClickListener(v -> showUpdateDialog());
    }

    private void showUpdateDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_user_info);
        dialog.setCancelable(true);

        TextInputLayout tilUsername = dialog.findViewById(R.id.til_username);
        TextInputEditText etUsername = dialog.findViewById(R.id.et_username);

        TextInputLayout tilEmail = dialog.findViewById(R.id.til_email);
        TextInputEditText etEmail = dialog.findViewById(R.id.et_email);

        TextInputLayout tilPassword = dialog.findViewById(R.id.til_password);
        TextInputEditText etPassword = dialog.findViewById(R.id.et_password);

        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        // Pre-fill fields with current data
        etUsername.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        etEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));

        btnSave.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newPassword = etPassword.getText().toString().trim();

            boolean valid = true;

            // Validate username
            if (newUsername.isEmpty()) {
                tilUsername.setError("Username cannot be empty");
                valid = false;
            } else if (newUsername.length() < 3) {
                tilUsername.setError("Username must be at least 3 characters");
                valid = false;
            } else {
                tilUsername.setError(null);
            }

            // Validate email
            if (newEmail.isEmpty()) {
                tilEmail.setError("Email cannot be empty");
                valid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                tilEmail.setError("Invalid email format");
                valid = false;
            } else {
                tilEmail.setError(null);
            }

            // Validate password (optional)
            if (!newPassword.isEmpty() && newPassword.length() < 6) {
                tilPassword.setError("Password must be at least 6 characters");
                valid = false;
            } else {
                tilPassword.setError(null);
            }

            if (!valid) return;

            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null) {
                // Update email
                user.updateEmail(newEmail)
                        .addOnSuccessListener(unused -> {
                            // Update Firestore
                            firestore.collection("users").document(userId)
                                    .update("username", newUsername, "email", newEmail)
                                    .addOnSuccessListener(aVoid -> {
                                        // Save to SharedPreferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(KEY_USERNAME, newUsername);
                                        editor.putString(KEY_EMAIL, newEmail);
                                        editor.apply();

                                        tvUsername.setText(newUsername);
                                        tvEmail.setText(newEmail);

                                        Toast.makeText(this, "User info updated", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Firestore update failed", e);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update email", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Email update failed", e);
                        });

                // Update password if not empty
                if (!newPassword.isEmpty()) {
                    user.updatePassword(newPassword)
                            .addOnSuccessListener(unused -> Log.d(TAG, "Password updated"))
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Password update failed", e);
                            });
                }
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(R.drawable.dialog_background);
        }
    }


    // Optional utility method to save user data
    public static void saveUserData(android.content.Context context, String username, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void configureStatusBar() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configuring status bar: " + e.getMessage());
        }
    }

    public void setupToolbarWithUsername() {
        try {
            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);

                // Get current user and display username
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    String email = auth.getCurrentUser().getEmail();
                    if (email != null) {
                        // Extract username from email (part before @)
                        String username = email.substring(0, email.indexOf("@"));
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Welcome, " + username);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar: " + e.getMessage());
        }
    }
}