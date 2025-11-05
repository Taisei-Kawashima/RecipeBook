package namake.recipebook.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import namake.recipebook.data.model.Recipe
import namake.recipebook.data.repository.RecipeRepository
import namake.recipebook.di.AppModule
import namake.recipebook.ui.theme.RecipeBookTheme

class MainActivity : ComponentActivity() {
    private val repository: RecipeRepository = AppModule.recipeRepository
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
                        LaunchedEffect(recipes) {
                            if (recipes.isEmpty()) {
                                // ユーザーが追加した "curry.png" ファイルのリソース名を使用
                                val demoRecipe = Recipe(
                                    name = "デモ・チキンカレー",
                                    imagePath = "curry", // ★ リソース名 "curry" を使用
                                    instructions = "玉ねぎを炒め、鶏肉とカレー粉、水を加えて煮込む。",
                                    preparationSteps = listOf("curry"), // ★ 調理過程も同じ画像で代用
                                    calories = 750,
                                    ingredients = listOf(
                                        namake.recipebook.data.model.Ingredient(
                                            name = "鶏もも肉", price = 400, priceComparison = 90,
                                            imagePath = null, description = null, allergens = listOf("鶏肉")
                                        )
                                    )
                                )
                                // 挿入を試みます (Supabaseが空である場合にのみ実行されます)
                                viewModel.insert(demoRecipe)
                            }
                        }
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
                            recipeId = recipeId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onEdit = { id -> navController.navigate("edit_recipe/$id") }
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
                            initialInstructions = recipeState?.instructions ?: "",
                            initialImagePath = recipeState?.imagePath,
                            initialPreparationSteps = recipeState?.preparationSteps,
                            initialCalories = recipeState?.calories,
                            initialIngredients = recipeState?.ingredients,

                            onSaveClick = { name, ingredients, instructions, imagePath, preparationSteps, calories ->
                                if(recipeId == 0L) {
                                    viewModel.insert(Recipe(
                                        name = name,
                                        ingredients = ingredients,
                                        instructions = instructions,
                                        imagePath = imagePath,
                                        preparationSteps = preparationSteps,
                                        calories = calories
                                    ))
                                } else {
                                    viewModel.update(Recipe(
                                        id = recipeId,
                                        name = name,
                                        ingredients = ingredients,
                                        instructions = instructions,
                                        imagePath = imagePath,
                                        preparationSteps = preparationSteps,
                                        calories = calories
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