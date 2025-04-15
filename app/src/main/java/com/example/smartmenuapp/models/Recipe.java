package com.example.smartmenuapp.models;

import java.util.List;
import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable {
    private int id;
    private String name;
    private List<String> ingredients;
    private List<String> instructions;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private int servings;
    private String difficulty;
    private String cuisine;
    private int caloriesPerServing;
    private List<String> tags;
    private String image;
    private double rating;
    private int reviewCount;
    private List<String> mealType;
    private int userId; // New field for API

    // No-arg constructor (Required for Firebase)
    public Recipe() {
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getInstructions() { return instructions; }
    public int getPrepTimeMinutes() { return prepTimeMinutes; }
    public int getCookTimeMinutes() { return cookTimeMinutes; }
    public int getServings() { return servings; }
    public String getDifficulty() { return difficulty; }
    public String getCuisine() { return cuisine; }
    public int getCaloriesPerServing() { return caloriesPerServing; }
    public List<String> getTags() { return tags; }
    public String getImage() { return image; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public List<String> getMealType() { return mealType; }
    public int getUserId() { return userId; }

    // Optional method to help filter recipes by meal type
    public boolean isOfMealType(String type) {
        return mealType != null && mealType.contains(type);
    }
}
