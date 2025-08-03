package namake.recipebook.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    initialName: String,
    initialIngredients: String,
    initialInstructions: String,
    onSaveClick: (name: String, ingredients: String, instructions: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf(initialIngredients) }
    var instructions by remember { mutableStateOf(initialInstructions) }

    LaunchedEffect(initialName) {
        name = initialName
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("レシピ名")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )
            Text("材料", modifier = Modifier.padding(top = 8.dp)) // ★追加
            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                modifier = Modifier.fillMaxWidth()
            )

            Text("作り方", modifier = Modifier.padding(top = 8.dp)) // ★追加
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSaveClick(name, ingredients, instructions) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("保存")
            }
        }
    }
}