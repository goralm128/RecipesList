package com.exercise.recipes.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.exercise.recipes.RecipeListViewModel
import com.exercise.recipes.network.model.Recipe

@Composable
fun RecipeListScreen(viewModel: RecipeListViewModel, onRecipeClick: (Recipe) -> Unit) {
    val recipes by viewModel.recipeList.collectAsState(initial = emptyList())
    val error by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Display error message if any
        error?.let {
            Text(
                text = "Error: $it",
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        LazyColumn {
            items(recipes) { recipe ->
                RecipeListItem(recipe = recipe, onClick = { onRecipeClick(recipe) })
            }
        }
    }
}