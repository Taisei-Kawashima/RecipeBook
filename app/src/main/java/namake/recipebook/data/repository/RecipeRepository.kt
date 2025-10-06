package namake.recipebook.data.repository

import android.util.Log // ★ 追加
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import namake.recipebook.data.model.Recipe

// UI側（ViewModel）とデータベース（DAO）の仲介役
class RecipeRepository(private val client: SupabaseClient) {

    private val recipesTable = client.postgrest["recipes"]
    private val TAG = "RecipeRepository" // Log Tag を定義

    /**
     * Supabaseから全てのレシピを取得し、FlowとしてUIに提供します。
     */
    fun getAllRecipes(): Flow<List<Recipe>> = flow {
        val response = recipesTable.select(columns = Columns.ALL) {
            order("id", Order.ASCENDING)
        }

        val recipes = response.decodeList<Recipe>()

        // ★ Log.d でログ出力
        Log.d(TAG, "Fetched ${recipes.size} recipes.")
        recipes.forEach { recipe ->
            Log.d(TAG, "Recipe ID=${recipe.id}, Name=${recipe.name}")
        }

        emit(recipes)
    }.catch { e ->
        // エラーログは Log.e で出力
        Log.e(TAG, "Error fetching all recipes: ${e.message}", e)
        emit(emptyList())
    }

    /**
     * 新しいレシピをSupabaseに挿入するための関数
     */
    suspend fun insertRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        try {
            recipesTable.insert(value = recipe)
            Log.d(TAG, "Inserted new recipe: ${recipe.name}")
        } catch (e: RestException) {
            Log.e(TAG, "Error inserting recipe: ${e.message}", e)
        }
    }

    /**
     * レシピを更新するための関数
     */
    suspend fun updateRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        try {
            recipesTable.update(value = recipe) {
                filter { eq("id", recipe.id) }
            }
            Log.d(TAG, "Updated recipe ID: ${recipe.id}")
        } catch (e: RestException) {
            Log.e(TAG, "Error updating recipe ID ${recipe.id}: ${e.message}", e)
        }
    }

    /**
     * レシピを削除するための関数
     */
    suspend fun deleteRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        try {
            recipesTable.delete {
                filter { eq("id", recipe.id) }
            }
            Log.d(TAG, "Deleted recipe ID: ${recipe.id}")
        } catch (e: RestException) {
            Log.e(TAG, "Error deleting recipe ID ${recipe.id}: ${e.message}", e)
        }
    }

    /**
     * 指定されたIDのレシピを一つ取得し、Flowとして提供します。
     */
    fun getRecipeById(id: Long): Flow<Recipe?> = flow {
        val response = recipesTable.select(columns = Columns.ALL) {
            filter { eq("id", id) }
        }

        val recipe = response.decodeSingleOrNull<Recipe>()
        Log.d(TAG, "Fetched recipe by ID $id. Found: ${recipe != null}")
        emit(recipe)
    }.catch { e ->
        Log.e(TAG, "Error fetching recipe by ID $id: ${e.message}", e)
        emit(null)
    }
}