package com.exercise.recipes.network.services

import com.exercise.recipes.network.model.Recipe
import retrofit2.http.*

interface RecipeApiService {

    @GET("/android-test/recipes.json")
    suspend fun getRecipes(): List<Recipe>
}