package com.exercise.recipes

import com.exercise.recipes.network.NetworkServiceManager

object RecipeComponentsController {

    lateinit var repository: RecipeRepository

    fun prepare() {
        repository = RecipeRepository(NetworkServiceManager.apiService)
    }
}