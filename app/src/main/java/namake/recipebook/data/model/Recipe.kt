package namake.recipebook.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Long = 0,
    val name: String,
    val imagePath: String?,
    val instructions: String?,

    val preparationSteps: List<String>?,
    val calories: Int?,

    val ingredients: List<Ingredient>?,
)