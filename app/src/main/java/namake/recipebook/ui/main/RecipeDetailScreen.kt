package namake.recipebook.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import namake.recipebook.data.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    // IDに基づいてレシピを取得します。
    val recipeState by viewModel.getRecipeById(recipeId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipeState?.name ?: "レシピ詳細", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(recipeId) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "編集")
                    }
                    IconButton(onClick = {
                        recipeState?.let { viewModel.delete(it) }
                        onBack()
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "削除")
                    }
                }
            )
        }
    ) { paddingValues ->
        recipeState?.let { recipe ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // メイン画像
                recipe.imagePath?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // ★ カロリー情報の表示
                    recipe.calories?.let {
                        Text(
                            text = "カロリー: $it kcal",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // ★ 材料リストの表示 (List<Ingredient> に対応)
                    Text(
                        text = "材料",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (recipe.ingredients.isNullOrEmpty()) {
                        Text("材料は登録されていません。")
                    } else {
                        recipe.ingredients.forEach { ingredient ->
                            // 簡易表示: 材料名のみ
                            Text(
                                text = "・${ingredient.name}",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // ★ 作り方（instructions）の表示
                    Text(
                        text = "作り方",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = recipe.instructions ?: "作り方は登録されていません。",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // ★ 調理過程の写真（preparationSteps）の表示
                    Text(
                        text = "調理過程の写真",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (recipe.preparationSteps.isNullOrEmpty()) {
                        Text("調理過程の写真は登録されていません。")
                    } else {
                        recipe.preparationSteps.forEachIndexed { index, path ->
                            Text(
                                text = "ステップ ${index + 1}",
                                fontWeight = FontWeight.SemiBold
                            )
                            Image(
                                painter = rememberAsyncImagePainter(path),
                                contentDescription = "調理過程ステップ ${index + 1}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .padding(vertical = 8.dp),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}