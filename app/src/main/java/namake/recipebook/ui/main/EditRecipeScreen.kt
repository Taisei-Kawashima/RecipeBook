package namake.recipebook.ui.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import namake.recipebook.data.model.Ingredient // ★ 追加

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    initialName: String,
    initialInstructions: String,
    initialImagePath: String?,
    initialPreparationSteps: List<String>?,
    initialCalories: Int?,
    initialIngredients: List<Ingredient>?,

    onSaveClick: (
        name: String,
        ingredients: List<Ingredient>,
        instructions: String,
        imagePath: String?,
        preparationSteps: List<String>?,
        calories: Int?
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }

    var calories by remember { mutableStateOf<String>("") }
    var preparationSteps by remember { mutableStateOf<String>("") }

    var ingredients by remember { mutableStateOf(initialIngredients ?: emptyList()) }
    var tempIngredientName by remember { mutableStateOf("") }



    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { imagePath = it.toString() } }
    )

    // 調理過程の写真選択ランチャー
    val prepImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { preparationSteps = it.toString() } }
    )

    LaunchedEffect(initialName, initialInstructions, initialImagePath, initialPreparationSteps, initialCalories, initialIngredients) {
        name = initialName
        instructions = initialInstructions
        imagePath = initialImagePath

        calories = initialCalories?.toString() ?: ""
        preparationSteps = initialPreparationSteps?.firstOrNull() ?: ""

        ingredients = initialIngredients ?: emptyList()

    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("レシピ名")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )

            // 新しい材料入力（簡易版: 名前のテキストフィールドのみ）
            Spacer(Modifier.height(16.dp))
            IngredientEditList(
                ingredients = ingredients,
                onIngredientsChange = {
                    ingredients = it // 更新されたリストを状態に反映
                }
            )
            Spacer(Modifier.height(8.dp))

            Text("作り方", modifier = Modifier.padding(top = 8.dp))
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                modifier = Modifier.fillMaxWidth()
            )

            // カロリー入力フィールドの追加
            Text("カロリー (kcal)", modifier = Modifier.padding(top = 8.dp))
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("例: 450") }
            )

            Button(onClick = {
                imagePickerLauncher.launch("image/*")
            }) {
                Text("メイン画像を選択")
            }

            imagePath?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "選択されたメイン画像",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // 調理過程の写真入力フィールドの追加
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                prepImagePickerLauncher.launch("image/*") // 調理過程の写真を選択
            }) {
                Text("調理過程の写真を選択")
            }

            preparationSteps.let {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "選択されたメイン画像",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // 保存ロジック: 新しい Recipe モデルに合わせてデータを構築

                    val newIngredientsList = if (tempIngredientName.isNotEmpty()) {
                        // 最小限のIngredientオブジェクトを作成（他のフィールドはデフォルト値）
                        listOf(Ingredient(
                            name = tempIngredientName,
                            price = 0,
                            priceComparison = 0,
                            imagePath = null,
                            description = null,
                            allergens = null
                        ))
                    } else {
                        emptyList()
                    }

                    val parsedCalories = calories.toIntOrNull()

                    val parsedPreparationSteps: List<String>? = if (preparationSteps.isNotEmpty()) {
                        listOf(preparationSteps)
                    } else {
                        null
                    }

                    onSaveClick(
                        name,
                        ingredients.filter { it.name.isNotBlank() },
                        instructions,
                        imagePath,
                        parsedPreparationSteps,
                        parsedCalories
                    )
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("保存")
            }
        }
    }
}