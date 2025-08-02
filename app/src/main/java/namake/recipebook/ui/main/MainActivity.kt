package namake.recipebook.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.room.Room
import namake.recipebook.data.local.AppDatabase
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
                // ViewModelからレシピリストの状態(State)を取得
                val recipes by viewModel.allRecipes.collectAsState()

                // UIの本体を呼び出す
                RecipeListScreen(recipes = recipes)
            }
        }
    }
}