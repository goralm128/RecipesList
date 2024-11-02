package com.exercise.recipes

import com.exercise.recipes.network.model.Recipe
import com.exercise.recipes.network.services.RecipeApiService
import java.io.IOException

class RecipeRepository(private val apiService: RecipeApiService) {

    suspend fun fetchRecipes(): List<Recipe> {
        return try {
            apiService.getRecipes()
        } catch (e: Exception) {
            throw IOException("Failed to load recipes: ${e.message}", e)
        }
    }
}
