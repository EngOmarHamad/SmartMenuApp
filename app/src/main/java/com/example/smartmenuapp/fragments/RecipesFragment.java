package com.example.smartmenuapp.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartmenuapp.R;
import com.example.smartmenuapp.adapters.RecipeAdapter;
import com.example.smartmenuapp.adapters.ShimmerAdapter;
import com.example.smartmenuapp.models.Recipe;
import com.example.smartmenuapp.models.RecipeResponse;
import com.example.smartmenuapp.network.ApiClient;
import com.example.smartmenuapp.network.ApiService;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipesFragment extends Fragment {

    private String mealType;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> recipes = new ArrayList<>();
    private Call<RecipeResponse> call;
    private final List<String> favoriteIds = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private RecyclerView shimmerRecyclerView;
    private ShimmerAdapter shimmerAdapter;
    public RecipesFragment() {
        // Required empty public constructor
    }

    public static RecipesFragment newInstance(String mealType) {
        RecipesFragment fragment = new RecipesFragment();
        Bundle args = new Bundle();
        args.putString("mealType", mealType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        recyclerView = view.findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeAdapter(requireContext(), recipes);
        recyclerView.setAdapter(adapter);
        shimmerRecyclerView = view.findViewById(R.id.shimmerRecyclerView);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shimmerAdapter = new ShimmerAdapter();
        shimmerRecyclerView.setAdapter(shimmerAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mealType = getArguments().getString("mealType");
            Log.d(TAG, "Meal type received: " + mealType); // للتأكد من وصول القيمة
        }
        if (auth.getCurrentUser() != null) {
            loadFavoriteIdsThenRecipes();
        } else {
            Toast.makeText(getContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadFavoriteIdsThenRecipes() {
        shimmerRecyclerView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        db.collection("users").document(userId).collection("favorites")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteIds.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        favoriteIds.add(doc.getId());
                    }
                    loadRecipesByMealType();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "فشل في جلب المفضلات", e);
                    loadRecipesByMealType();
                });
    }
    private void loadRecipesByMealType() {


        ApiService apiService = ApiClient.getApiClient();
        call = apiService.getRecipesByMealType(mealType);

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecipeResponse> call, @NonNull Response<RecipeResponse> response) {
                if (!isAdded() || getContext() == null) return;
                shimmerRecyclerView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null) {
                    recipes = response.body().getRecipes();
                    adapter.updateData(recipes, favoriteIds);
                } else {
                    Toast.makeText(getContext(), "فشل تحميل الوجبات", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<RecipeResponse> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                shimmerRecyclerView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                Log.e(TAG, "API Failure: ", t);
                Toast.makeText(getContext(), "حدث خطأ أثناء تحميل البيانات: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }
}
