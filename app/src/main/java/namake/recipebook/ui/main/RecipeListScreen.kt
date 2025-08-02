package namake.recipebook.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import namake.recipebook.data.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(recipes: List<Recipe>) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: 新規作成画面への遷移 */ }) {
                Icon(Icons.Default.Add, contentDescription = "レシピを追加")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(recipes) { recipe ->
                RecipeListItem(recipe = recipe)
            }
        }
    }
}

@Composable
fun RecipeListItem(recipe: Recipe) {
    Box(modifier = Modifier.padding(16.dp)) {
        Text(
            text = recipe.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}