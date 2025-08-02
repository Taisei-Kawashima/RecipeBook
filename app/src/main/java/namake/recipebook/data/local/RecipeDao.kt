package namake.recipebook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import namake.recipebook.data.model.Recipe

// RecipeDaoはレシピデータを操作するためのDAO（Data Access Object）
// Roomデータベースとやり取りするための命令書、データベース操作の窓口
@Dao
interface RecipeDao {

    // recipesテーブルから全てのデータ（*）を、name（名前）のアルファベット順（ASC）で取得する命令
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    // 上記のSQLクエリを実行するための関数
    // Flowは、データが変更されると自動的に新しいデータを通知してくれる仕組み
    fun getAllRecipes(): Flow<List<Recipe>>

    // データをデータベースに追加する命令
    @Insert
    // suspend関数なので、非同期で実行される
    // Recipe型のデータを一つ受け取り、それをデータベースに挿入するための関数
    suspend fun insertRecipe(recipe: Recipe)
}