package com.exercise.recipes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exercise.recipes.network.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class RecipeListViewModel(private val repository: RecipeRepository) : ViewModel() {

    val recipeList = MutableStateFlow<List<Recipe>>(emptyList())
    val errorMessage = MutableStateFlow<String?>(null)

    companion object {
        const val TAG: String = "RecipeListViewModel"
    }

    init {
        Log.i(TAG, "$TAG created!")
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            try {
                val recipes = repository.fetchRecipes()
                recipeList.value = recipes
            } catch (e: IOException) {
                errorMessage.value = e.message
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "$TAG destroyed!")
        super.onCleared()
    }
}