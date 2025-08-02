package namake.recipebook.data.repository

import kotlinx.coroutines.flow.Flow
import namake.recipebook.data.local.RecipeDao
import namake.recipebook.data.model.Recipe

// UI側（ViewModel）とデータベース（DAO）の仲介役
class RecipeRepository(private val recipeDao: RecipeDao) {

    // DAOにレシピの全件取得を依頼する
    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

    // DAOにレシピの追加を依頼する
    suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }

}