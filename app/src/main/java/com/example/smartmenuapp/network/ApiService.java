package com.example.smartmenuapp.network;

// ApiService.java

import com.example.smartmenuapp.models.Recipe;
import com.example.smartmenuapp.models.RecipeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("recipes")
    Call<RecipeResponse> getRecipes();
    @GET("recipes/meal-type/{mealType}")
    Call<RecipeResponse> getRecipesByMealType(@Path("mealType") String mealType);
}
