package namake.recipebook.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import namake.recipebook.data.local.AppDatabase
import namake.recipebook.data.model.Recipe
import namake.recipebook.data.repository.RecipeRepository
import namake.recipebook.ui.main.MainViewModel
import namake.recipebook.ui.main.MainViewModelFactory
import namake.recipebook.ui.main.RecipeListScreen
import namake.recipebook.ui.theme.RecipeBookTheme // あなたのプロジェクトのテーマ

class MainActivity : ComponentActivity() {

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "recipe_database"
        ).build()
    }
    private val repository by lazy { RecipeRepository(database.recipeDao()) }
    private val factory by lazy { MainViewModelFactory(repository) }

    private val viewModel: MainViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeBookTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "recipe_list") {
                    // 一覧画面
                    composable("recipe_list") {
                        val recipes by viewModel.allRecipes.collectAsState()
                        RecipeListScreen(
                            recipes = recipes,
                            onAddClick = {
                                navController.navigate("edit_recipe")
                            },
                            onDeleteClick = {
                                recipe -> viewModel.delete(recipe)
                            }
                        )
                    }

                    // 登録画面
                    composable("edit_recipe") {
                        EditRecipeScreen(
                            onSaveClick = { name ->
                                // 保存ボタンが押されたらレシピをDBに追加
                                viewModel.insert(Recipe(name = name, imagePath = null))
                                // 一覧画面に戻る
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}