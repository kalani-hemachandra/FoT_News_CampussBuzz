package com.fotnews.campusbuzz;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends BaseActivity {

    private static final String TAG = "NewsDetailActivity";
    private ImageView newsImageView;
    private TextView newsTitleTextView;
    private TextView newsDateTextView;
    private TextView newsDescriptionTextView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private String sourceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Initialize views first
        initializeViews();

        // Then load data
        loadNewsData();

        // Setup navigation
        setupBottomNavigation();
        setupMoreOptionsMenu();

        // This will handle the toolbar setup from BaseActivity
        getSupportActionBar().hide();

        // Handle back button
        MaterialButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(v -> handleBackNavigation());
    }

    private void initializeViews() {
        newsImageView = findViewById(R.id.news_detail_image);
        newsTitleTextView = findViewById(R.id.news_detail_title);
        newsDateTextView = findViewById(R.id.news_detail_date);
        newsDescriptionTextView = findViewById(R.id.news_detail_description);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Get source activity from intent
        sourceActivity = getIntent().getStringExtra("source_activity");
        if (sourceActivity == null) {
            sourceActivity = "home"; // default
        }
    }

    private void loadNewsData() {
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("news_title");
            String description = intent.getStringExtra("news_description");
            String date = intent.getStringExtra("news_date");
            String imageUrl = intent.getStringExtra("news_image_url");

            // Set data to views with null checks
            if (title != null) {
                newsTitleTextView.setText(title);
            } else {
                newsTitleTextView.setText("No Title Available");
                Log.w(TAG, "No title provided in intent");
            }

            if (description != null) {
                newsDescriptionTextView.setText(description);
            } else {
                newsDescriptionTextView.setText("No Description Available");
                Log.w(TAG, "No description provided in intent");
            }

            if (date != null) {
                newsDateTextView.setText(date);
            } else {
                newsDateTextView.setText("Date Not Specified");
                Log.w(TAG, "No date provided in intent");
            }

            // Load image with Picasso
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(newsImageView);
            } else {
                newsImageView.setImageResource(R.drawable.ic_launcher_foreground);
                Log.w(TAG, "No image URL provided in intent");
            }
        } else {
            Log.e(TAG, "No intent data received");
            Toast.makeText(this, "Error loading news details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleBackNavigation() {
        Class<?> targetActivity;
        switch (sourceActivity) {
            case "events":
                targetActivity = EventsNewsActivity.class;
                break;
            case "sports":
                targetActivity = SportsNewsActivity.class;
                break;
            case "academic":
                targetActivity = AcademicNewsActivity.class;
                break;
            case "home":
            default:
                targetActivity = HomeActivity.class;
                break;
        }

        Intent intent = new Intent(this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView == null) {
            Log.e(TAG, "bottomNavigationView is null");
            return;
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.tab_sports) {
                startActivity(new Intent(NewsDetailActivity.this, SportsNewsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.tab_academics) {
                startActivity(new Intent(NewsDetailActivity.this, AcademicNewsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.tab_faculty) {
                startActivity(new Intent(NewsDetailActivity.this, EventsNewsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupMoreOptionsMenu() {
        ImageView moreOptions = findViewById(R.id.more_options);
        if (moreOptions == null) {
            Log.e(TAG, "more_options ImageView not found");
            return;
        }

        moreOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(NewsDetailActivity.this, v);
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
                    // Check if admin user
                    if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                            "admin235@gmail.com".equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        startActivity(new Intent(this, AddNewsActivity.class));
                    } else {
                        Toast.makeText(this, "Admin access required", Toast.LENGTH_SHORT).show();
                    }
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
            popupMenu.show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackNavigation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleBackNavigation();
    }
}