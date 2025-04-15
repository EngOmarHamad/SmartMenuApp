package com.example.smartmenuapp.activities;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.smartmenuapp.R;
import com.example.smartmenuapp.fragments.FavoritesFragment;
import com.example.smartmenuapp.fragments.RecipesContainerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.smartmenuapp.databinding.ActivityRecipeListBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RecipeListActivity extends AppCompatActivity {

    private ActivityRecipeListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            int color = ContextCompat.getColor(this, R.color.appbar_icon_color); // استخدم اللون الذي ترغب فيه
            overflowIcon.setTint(color);
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RecipesContainerFragment())
                    .commit();
        }
        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.item_home) {
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                loadRecipeListFragment();
                return true;
            } else if (id == R.id.item_favorite) {
                Toast.makeText(this, "Favorites clicked", Toast.LENGTH_SHORT).show();
                loadFavoritesFragment();
                return true;
            }
            return false;
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut(); // تسجيل الخروج من Firebase
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class); // افترض أن لديك Activity لتسجيل الدخول
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavoritesFragment() {
        // Load the FavoritesFragment when navigating to favorites
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new FavoritesFragment())
                .addToBackStack(null)
                .commit();
    }

    private void loadRecipeListFragment() {
        // Load the FavoritesFragment when navigating to favorites
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RecipesContainerFragment())
                .addToBackStack(null)
                .commit();
    }
}