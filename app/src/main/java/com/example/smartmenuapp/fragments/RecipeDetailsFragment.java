package com.example.smartmenuapp.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.smartmenuapp.R;
import com.example.smartmenuapp.databinding.FragmentRecipeDetailsBinding;
import com.example.smartmenuapp.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RecipeDetailsFragment extends Fragment {

    private FragmentRecipeDetailsBinding binding;
    private Recipe recipe;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private  boolean isFavorite;
    public RecipeDetailsFragment() {
    }

    public static RecipeDetailsFragment newInstance(Recipe recipe) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("recipe", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    private AlertDialog loadingDialog;

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loading, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            recipe = (Recipe) getArguments().getSerializable("recipe");
             isFavorite = getArguments().getBoolean("isFavorite", false);

        }

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

     // Prevent dismissing while adding

        if (recipe != null) {
            Glide.with(requireContext()).load(recipe.getImage()).into(binding.imageView);
            binding.nameTextView.setText(recipe.getName());
            binding.priceTextView.setText("$" + recipe.getCuisine());
            binding.preparationTimeTextView.setText("Preparation Time: " + recipe.getDifficulty());
            binding.ingredientsTextView.setText("Ingredients: " + String.join(", ", recipe.getIngredients()));
            binding.stepsTextView.setText("Steps: " + String.join("\n", recipe.getMealType()));
            if (isFavorite) {
                binding.favoriteButton.setText("Remove from Favorites");
                binding.favoriteButton.setOnClickListener(v -> removeFromFavorites());
            } else {
                binding.favoriteButton.setText("Add to Favorites");
                binding.favoriteButton.setOnClickListener(v -> addToFavorites());
            }
        }

    }
    private void removeFromFavorites() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId != null) {
            showLoadingDialog();

            firestore.collection("users").document(userId)
                    .collection("favorites").document(String.valueOf(recipe.getId()))
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        hideLoadingDialog();
                        Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                        // اختياري: حدث حالة الزر بعد الحذف
                        binding.favoriteButton.setText("Add to Favorites");
                        isFavorite = false;
                        binding.favoriteButton.setOnClickListener(v -> addToFavorites());
                    })
                    .addOnFailureListener(e -> {
                        hideLoadingDialog();
                        Log.e("RecipeDetailsFragment", "Error removing from favorites", e);
                        Toast.makeText(requireContext(), "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "Please login to manage favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToFavorites() {
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId != null) {
            showLoadingDialog();

            firestore.collection("users").document(userId)
                    .collection("favorites").document(String.valueOf(recipe.getId()))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            hideLoadingDialog();
                            Toast.makeText(requireContext(), "This recipe is already in your favorites", Toast.LENGTH_SHORT).show();
                            binding.favoriteButton.setText("Remove from Favorites");
                            isFavorite = true;
                            binding.favoriteButton.setOnClickListener(v -> removeFromFavorites());
                        } else {
                            Map<String, Object> favoriteData = new HashMap<>();
                            favoriteData.put("id", recipe.getId());
                            favoriteData.put("name", recipe.getName());
                            favoriteData.put("image", recipe.getImage());
                            favoriteData.put("mealType", recipe.getMealType());

                            firestore.collection("users").document(userId)
                                    .collection("favorites").document(String.valueOf(recipe.getId()))
                                    .set(favoriteData)
                                    .addOnSuccessListener(aVoid -> {
                                        hideLoadingDialog();
                                        binding.favoriteButton.setText("Remove from Favorites");
                                        isFavorite = true;
                                        binding.favoriteButton.setOnClickListener(v -> removeFromFavorites());
                                        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show();

                                    })
                                    .addOnFailureListener(e -> {
                                        hideLoadingDialog();
                                        Log.e("RecipeDetailsFragment", "Error adding to favorites", e);
                                        Toast.makeText(requireContext(), "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        hideLoadingDialog();
                        Toast.makeText(requireContext(), "Error checking favorites", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "Please login to add to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
