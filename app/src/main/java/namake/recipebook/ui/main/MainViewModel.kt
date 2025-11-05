package namake.recipebook.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.OptIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow // ★ 変更なし
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import namake.recipebook.data.model.Recipe
import namake.recipebook.data.repository.RecipeRepository

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val repository: RecipeRepository) : ViewModel() {

    // ★ 修正: リフレッシュをトリガーするための StateFlow を Int カウンターで定義
    private val refreshTrigger = MutableStateFlow(0)

    // refreshTrigger が変更されるたびにリポジトリからデータを再取得する
    val allRecipes: StateFlow<List<Recipe>> = refreshTrigger.flatMapLatest {
        // トリガーが発火するたびに repository.getAllRecipes() が実行される
        repository.getAllRecipes()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // データの変更後に Flow を強制的に再起動させる
    private fun refresh() {
        viewModelScope.launch {
            // ★ 修正: カウンターをインクリメントし、必ず新しい値を発行する
            refreshTrigger.value = refreshTrigger.value + 1
        }
    }

    // 新しいレシピを追加するための関数
    fun insert(recipe: Recipe) {
        viewModelScope.launch {
            repository.insertRecipe(recipe)
            refresh() // 挿入後にリフレッシュをトリガー
        }
    }

    // レシピを削除するための関数
    fun delete(recipe: Recipe) {
        viewModelScope.launch {
            // repository.deleteRecipe(recipe) は suspend 関数なので、削除完了を待つ
            repository.deleteRecipe(recipe)
            refresh() // ★ 削除後にリフレッシュをトリガー
        }
    }

    // レシピを更新するための関数
    fun update(recipe: Recipe) {
        viewModelScope.launch {
            repository.updateRecipe(recipe)
            refresh() // 更新後にリフレッシュをトリガー
        }
    }

    fun getRecipeById(id: Long): Flow<Recipe?> {
        return repository.getRecipeById(id)
    }
}