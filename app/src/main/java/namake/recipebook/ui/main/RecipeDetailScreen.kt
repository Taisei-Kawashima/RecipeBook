package namake.recipebook.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import namake.recipebook.data.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: Recipe?,
    onEditClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(recipe?.name ?: "レシピ詳細") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "レシピを編集")
            }
        }
    ) { paddingValues ->
        if (recipe == null) {
            // レシピが見つからない場合の表示
            Text("レシピが見つかりません。", modifier = Modifier.padding(paddingValues))
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("材料", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(recipe.ingredients ?: "登録されていません", modifier = Modifier.padding(top = 8.dp))

                Text("作り方", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                Text(recipe.instructions ?: "登録されていません", modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}