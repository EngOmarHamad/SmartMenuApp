package com.example.smartmenuapp.adapters;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartmenuapp.R;
import com.example.smartmenuapp.fragments.RecipeDetailsFragment;
import com.example.smartmenuapp.models.Favorite;
import com.example.smartmenuapp.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final Context context;
    private  final List<Recipe> recipes;
    private List<String> favoriteIds = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
            FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes != null ? recipes : new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Recipe> newRecipes, List<String> favoriteIds) {
        this.recipes.clear();
        this.recipes.addAll(newRecipes);
        this.favoriteIds = favoriteIds != null ? favoriteIds : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView, priceTextView;
        private final ImageView favoriteIcon;
        private AlertDialog loadingDialog;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
        }
        private void showLoadingDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
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
        @SuppressLint("NotifyDataSetChanged")
        public void bind(Recipe recipe) {
            Glide.with(context)
                    .load(recipe.getImage())
                    .placeholder(R.drawable.logo_small)
                    .error(R.drawable.logo_small)
                    .into(imageView);

            nameTextView.setText(recipe.getName());
            priceTextView.setText(recipe.getCuisine());

            // تحقق إن كانت الوجبة من المفضلة
            boolean isFavorite = favoriteIds.contains(String.valueOf(recipe.getId()));
            Log.e(" boolean isFavorite",String.valueOf(isFavorite));
            favoriteIcon.setImageResource(isFavorite ? R.drawable.favorite_24_filled : R.drawable.favorite_24);

            itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                RecipeDetailsFragment fragment = new RecipeDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("recipe", recipe);
                bundle.putBoolean("isFavorite", isFavorite); // ✨ أضف هذه السطر لتمرير الحالة
                fragment.setArguments(bundle);
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            favoriteIcon.setOnClickListener(v -> {
                if (userId == null) {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                showLoadingDialog();

                DocumentReference favRef = db.collection("users").document(userId)
                        .collection("favorites").document(String.valueOf(recipe.getId()));

                boolean isCurrentlyFavorite = favoriteIds.contains(String.valueOf(recipe.getId()));

                if (!isCurrentlyFavorite) {
                    Favorite favorite = new Favorite();
                    favorite.setId(recipe.getId());
                    favorite.setName(recipe.getName());
                    favorite.setImage(recipe.getImage());
                    favorite.setMealType(recipe.getMealType());

                    favRef.set(favorite)
                            .addOnSuccessListener(unused -> {
                                favoriteIds.add(String.valueOf(recipe.getId()));
                                favoriteIcon.setImageResource(R.drawable.favorite_24_filled);
                                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                hideLoadingDialog();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Error adding to favorites", Toast.LENGTH_SHORT).show();
                                hideLoadingDialog();
                            });
                } else {
                    favRef.delete()
                            .addOnSuccessListener(unused -> {
                                favoriteIds.remove(String.valueOf(recipe.getId()));
                                favoriteIcon.setImageResource(R.drawable.favorite_24);
                                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                hideLoadingDialog();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Error removing from favorites", Toast.LENGTH_SHORT).show();
                                hideLoadingDialog();
                            });
                }
            });

        }
    }
}
