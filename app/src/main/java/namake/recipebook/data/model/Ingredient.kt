package namake.recipebook.data.model

// 必要な材料のより詳細な情報を保持するデータクラス
data class Ingredient(
    // 材料の名前 (必要材料)
    val name: String,
    // 価格 (例: 100g あたりの値段など、単位は後で決める)
    val price: Int,
    // 付近の店の材料の値段の比較スコア (例: 100を平均とした比較)
    val priceComparison: Int,
    // 材料の写真のパス (外部ストレージのURIなど)
    val imagePath: String?,
    // 材料の説明文
    val description: String?,
    // 関連するアレルギー名のリスト
    val allergens: List<String>?
)