package namake.recipebook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    // レシピのID、データベースで自動で番号が割り振られる
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val imagePath: String?,
    val ingredients: String?,
    val instructions: String?,
)