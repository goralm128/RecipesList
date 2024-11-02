package com.exercise.recipes.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.exercise.recipes.network.model.Recipe


@Composable
fun RecipeDetailScreen(
    recipe: Recipe
) {
    DisplayRecipeDetails(recipe)
}

@Composable
fun DisplayRecipeDetails(recipe: Recipe) {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        content = {
            item {
                Image(
                    painter = rememberAsyncImagePainter(model = recipe.image),
                    contentDescription = "Recipe Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fats: ${recipe.fats}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Calories: ${recipe.calories}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Carbos: ${recipe.carbos}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}
