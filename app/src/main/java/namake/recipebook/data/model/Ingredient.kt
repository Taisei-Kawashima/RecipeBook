package namake.recipebook.data.model

import kotlinx.serialization.Serializable // ★ 追加が必要

@Serializable // ★ このアノテーションが必須です
data class Ingredient(
    // 材料の名前 (必要材料)
    val name: String,
    // 価格 (int)
    val price: Int,
    // 付近の店の材料の値段の比較スコア (int)
    val priceComparison: Int,
    // 材料の写真のパス
    val imagePath: String?,
    // 材料の説明文
    val description: String?,
    // 関連するアレルギー名のリスト
    val allergens: List<String>?
)