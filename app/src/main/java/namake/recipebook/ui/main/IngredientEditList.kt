package namake.recipebook.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import namake.recipebook.data.model.Ingredient

// ... (後略)
/**
 * 個々の材料の編集フィールドを提供する Composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientItemEditor(
    ingredient: Ingredient,
    onIngredientChange: (Ingredient) -> Unit,
    onRemove: () -> Unit
) {
    // 各フィールドの状態
    var name by remember { mutableStateOf(ingredient.name) }
    var price by remember { mutableStateOf(ingredient.price.toString()) }
    var priceComparison by remember { mutableStateOf(ingredient.priceComparison.toString()) }
    var description by remember { mutableStateOf(ingredient.description ?: "") }
    var allergens by remember { mutableStateOf(ingredient.allergens?.joinToString(", ") ?: "") }

    // imagePath はファイルピッカーが必要なため、ここでは表示のみ

    // 値が変更されたら親に通知
    DisposableEffect(name, price, priceComparison, description, allergens) {
        val newIngredient = ingredient.copy(
            name = name,
            price = price.toIntOrNull() ?: 0,
            priceComparison = priceComparison.toIntOrNull() ?: 0,
            description = description.takeIf { it.isNotBlank() },
            allergens = allergens.split(",").map { it.trim() }.filter { it.isNotBlank() }.takeIf { it.isNotEmpty() }
        )
        onIngredientChange(newIngredient)
        onDispose { }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable(onClick = { /* 詳細ビューの展開など */ })
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name.ifEmpty { "新しい材料" }, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "材料を削除")
            }
        }

        Spacer(Modifier.height(4.dp))

        // --- 編集フィールド ---

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("材料名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it.filter { char -> char.isDigit() } },
                label = { Text("価格") },
                // keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number), // ★ 削除
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = priceComparison,
                // ★ 修正: onValueChange で入力値をフィルタリングし、数字のみを許可
                onValueChange = { priceComparison = it.filter { char -> char.isDigit() } },
                label = { Text("価格比較スコア") },
                // keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number), // ★ 削除
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("説明") },
            modifier = Modifier.fillMaxWidth().height(80.dp).padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = allergens,
            onValueChange = { allergens = it },
            label = { Text("アレルギー (カンマ区切り)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

         HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
    }
}


/**
 * 複数の材料リスト全体を管理する Composable
 */
@Composable
fun IngredientEditList(
    ingredients: List<Ingredient>,
    onIngredientsChange: (List<Ingredient>) -> Unit
) {
    // 内部でリストの状態を保持
    var editableIngredients by remember { mutableStateOf(ingredients.toList()) }

    // 外部のリストが変更されたら、内部の状態を更新
    LaunchedEffect(ingredients) {
        editableIngredients = ingredients.toList()
    }

    // 変更を親に伝えるためのコールバック
    LaunchedEffect(editableIngredients) {
        onIngredientsChange(editableIngredients)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("材料リスト", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        editableIngredients.forEachIndexed { index, ingredient ->
            IngredientItemEditor(
                ingredient = ingredient,
                onIngredientChange = { updatedIngredient ->
                    // 変更された材料でリストを更新
                    editableIngredients = editableIngredients.toMutableList().apply {
                        this[index] = updatedIngredient
                    }
                },
                onRemove = {
                    // 削除
                    editableIngredients = editableIngredients.toMutableList().apply {
                        removeAt(index)
                    }
                }
            )
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                // 新しい材料を追加
                val newIngredient = Ingredient(
                    name = "",
                    price = 0,
                    priceComparison = 0,
                    imagePath = null,
                    description = null,
                    allergens = null
                )
                editableIngredients = editableIngredients + newIngredient
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = "材料を追加")
            Spacer(Modifier.width(8.dp))
            Text("材料を追加")
        }
    }
}