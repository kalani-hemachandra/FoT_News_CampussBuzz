package com.fotnews.campusbuzz;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View; // Import View
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView; // Import SearchView
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton; // Import MaterialButton
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DevInfoActivity extends BaseActivity {
    private static final String TAG = "Dev Info";
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private MaterialButton exitButton; // Declare the MaterialButton
    private SearchView searchBar; // Declare the SearchView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_info);

        initViews();
        setupBottomNavigation();

        // Set up the more options menu
        setupMoreOptionsMenu();

        // This will handle the toolbar setup from BaseActivity
        getSupportActionBar().hide();

        // Setup exit button functionality
        setupExitButton();
    }

    private void setupMoreOptionsMenu() {
        ImageView moreOptions = findViewById(R.id.more_options);
        moreOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(DevInfoActivity.this, v);
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

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home_icon);
        exitButton = findViewById(R.id.exit_btn); // Initialize the exit button
        searchBar = findViewById(R.id.search_bar); // Initialize the search bar

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
                            Toast.makeText(DevInfoActivity.this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(DevInfoActivity.this, HomeActivity.class);
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

    /**
     * Sets up the functionality for the exit button to navigate to the HomeActivity.
     */
    private void setupExitButton() {
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> {
                Intent intent = new Intent(DevInfoActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the back stack
                startActivity(intent);
                finish(); // Finish the current activity
            });
        } else {
            Log.e(TAG, "Exit button is null. Cannot set up click listener.");
        }
    }
}