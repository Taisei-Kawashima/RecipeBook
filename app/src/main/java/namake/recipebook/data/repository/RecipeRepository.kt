package namake.recipebook.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException // ★ 追加
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers // ★ 追加
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch // ★ 追加
import kotlinx.coroutines.flow.flow // ★ 変更 (flowOf から flow に変更)
import kotlinx.coroutines.withContext // ★ 追加
import namake.recipebook.data.model.Recipe

// UI側（ViewModel）とデータベース（DAO）の仲介役
class RecipeRepository(private val client: SupabaseClient) {

    private val recipesTable = client.postgrest["recipes"]

    /**
     * Supabaseから全てのレシピを取得し、FlowとしてUIに提供します。
     * Flowを使用することで、リアルタイム同期に近い形でデータの変化を通知できます。
     */
    fun getAllRecipes(): Flow<List<Recipe>> = flow {
        // 全てのカラム(*)を選択し、IDで昇順に並べ替えて取得
        val response = recipesTable.select(columns = Columns.ALL) {
            order("id", Order.ASCENDING)
        }

        // 取得したJSONデータをList<Recipe>に変換
        // Supabase-ktは@Serializableクラスを自動的にデシリアライズします
        val recipes = response.decodeList<Recipe>()

        // データを流します
        emit(recipes)
    }.catch { e ->
        // エラーハンドリング（例: ネットワークエラー、認証エラーなど）
        println("Error fetching recipes: ${e.message}")
        emit(emptyList()) // エラー発生時は空のリストを流す
    }

    /**
     * 指定されたIDのレシピを一つ取得し、Flowとして提供します。
     */
    fun getRecipeById(id: Long): Flow<Recipe?> = flow {
        val response = recipesTable.select(columns = Columns.ALL) {
            // where 句でIDが一致するものをフィルタリング
            filter { eq("id", id) }
        }

        // 結果が一つであることを期待してdecodeSingleOrNullを使用
        val recipe = response.decodeSingleOrNull<Recipe>()
        emit(recipe)
    }.catch { e ->
        println("Error fetching recipe by ID $id: ${e.message}")
        emit(null)
    }

    /**
     * 新しいレシピをSupabaseに挿入します。
     */
    suspend fun insertRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        try {
            // PostgRESTのinsert関数でRecipeオブジェクトをそのまま送信
            recipesTable.insert(value = recipe)
        } catch (e: RestException) {
            // 例外が発生した場合の処理（例: ログ出力や、ユーザーへの通知）
            println("Error inserting recipe: ${e.message}")
        }
    }

    /**
     * 既存のレシピをSupabaseで更新します。
     * 更新時はIDを where 句に使用します。
     */
    suspend fun updateRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        try {
            // PostgRESTのupdate関数でRecipeオブジェクトを送信し、
            // idが一致する行のみを更新します
            recipesTable.update(value = recipe) {
                filter { eq("id", recipe.id) }
            }
        } catch (e: RestException) {
            println("Error updating recipe: ${e.message}")
        }
    }

    /**
     * 指定されたレシピをSupabaseから削除します。
     */
    suspend fun deleteRecipe(recipe: Recipe) = withContext(Dispatchers.IO) {
        try {
            // delete関数でidが一致する行を削除します
            recipesTable.delete {
                filter { eq("id", recipe.id) }
            }
        } catch (e: RestException) {
            println("Error deleting recipe: ${e.message}")
        }
    }
}