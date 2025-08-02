package namake.recipebook.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import namake.recipebook.data.model.Recipe
import namake.recipebook.data.repository.RecipeRepository

class MainViewModel(private val repository: RecipeRepository) : ViewModel() {

    // Repositoryから全てのレシピを取得し、UIが監視できるLiveDataに変換
    // LiveDataの代わりにStateFlowを使う
    val allRecipes: StateFlow<List<Recipe>> = repository.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 新しいレシピを追加するための関数
    fun insert(recipe: Recipe) {
        // ViewModel専用のコルーチン スコープで非同期処理を実行
        viewModelScope.launch {
            repository.insertRecipe(recipe)
        }
    }
}