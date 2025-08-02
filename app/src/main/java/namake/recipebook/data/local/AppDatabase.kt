package namake.recipebook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import namake.recipebook.data.model.Recipe

// AppDatabaseはRoomデータベースの定義を行うクラス
// entitiesはこのデータベースが管理するエンティティをリスト形式で指定
// versionはデータベースのバージョン
@Database(entities = [Recipe::class], version = 1, exportSchema = false)
// abstractクラスであるため、抽象クラス
abstract class AppDatabase : RoomDatabase() {

    // このデータベースがどのDAOを持っているかを定義します
    abstract fun recipeDao(): RecipeDao

}