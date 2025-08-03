package namake.recipebook.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import namake.recipebook.data.local.AppDatabase
import namake.recipebook.data.model.Recipe
import namake.recipebook.data.repository.RecipeRepository
import namake.recipebook.ui.theme.RecipeBookTheme

class MainActivity : ComponentActivity() {

    private val database by lazy {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // バージョン1から2へのマイグレーション処理をここに記述
                database.execSQL("ALTER TABLE recipes ADD COLUMN ingredients TEXT")
                database.execSQL("ALTER TABLE recipes ADD COLUMN instructions TEXT")
            }
        }


        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "recipe_database"
        )   .addMigrations(MIGRATION_1_2)
            .build()
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
                                // 新規作成時はID=0として画面遷移
                                navController.navigate("edit_recipe/0")
                            },
                            onRecipeClick = { recipe ->
                                // レシピをタップしたら、そのIDを渡して画面遷移
                                navController.navigate("recipe_detail/${recipe.id}")
                            },
                            onDeleteClick = { recipe -> viewModel.delete(recipe) }
                        )
                    }
                    // 詳細画面
                    composable(
                        route = "recipe_detail/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0
                        val recipeState by produceState<Recipe?>(initialValue = null, recipeId) {
                            if (recipeId != 0L) {
                                viewModel.getRecipeById(recipeId).collect { value = it }
                            }
                        }
                        RecipeDetailScreen(
                            recipe = recipeState,
                            onEditClick = {
                                // 編集ボタンが押されたら編集画面へ
                                navController.navigate("edit_recipe/$recipeId")
                            }
                        )
                    }

                    // 登録・編集画面
                    composable(
                        route = "edit_recipe/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0

                        val recipeState by produceState<Recipe?>(initialValue = null, recipeId) {
                            if (recipeId != 0L) {
                                viewModel.getRecipeById(recipeId).collect { value = it }
                            }
                        }

                        EditRecipeScreen(
                            initialName = recipeState?.name ?: "",
                            initialIngredients = recipeState?.ingredients ?: "",
                            initialInstructions = recipeState?.instructions ?: "",
                            initialImagePath = recipeState?.imagePath, // ★ 不足していた引数を追加
                            onSaveClick = { name, ingredients, instructions, imagePath -> // ★ imagePath を追加
                                if (recipeId == 0L) {
                                    viewModel.insert(Recipe(
                                        name = name,
                                        ingredients = ingredients,
                                        instructions = instructions,
                                        imagePath = imagePath // ★ imagePath を使用
                                    ))
                                } else {
                                    viewModel.update(Recipe(
                                        id = recipeId,
                                        name = name,
                                        ingredients = ingredients,
                                        instructions = instructions,
                                        imagePath = imagePath // ★ imagePath を使用
                                    ))
                                }
                                navController.navigate("recipe_list") {
                                    popUpTo("recipe_list") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}