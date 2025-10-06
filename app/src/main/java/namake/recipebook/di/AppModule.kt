package namake.recipebook.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import namake.recipebook.data.repository.RecipeRepository

object AppModule{
    private const val SUPABASE_URL = "https://mgnvdinktcxwnxqqfifq.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_secret_Zu0-tD8UMHxumhWSxy2gyg_0roPe46F"

    private val supabase: SupabaseClient = createSupabaseClient (
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ){
        install(Postgrest)
    }

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepository(supabase)
    }
}