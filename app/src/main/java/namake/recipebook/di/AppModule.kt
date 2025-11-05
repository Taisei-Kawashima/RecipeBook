package namake.recipebook.di

import namake.recipebook.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import namake.recipebook.data.repository.RecipeRepository

object AppModule{
    private const val SUPABASE_URL = "https://mgnvdinktcxwnxqqfifq.supabase.co"

    private val supabase: SupabaseClient = createSupabaseClient (
        supabaseUrl = SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ){
        install(Postgrest)
    }

    val recipeRepository: RecipeRepository by lazy {
        RecipeRepository(supabase)
    }

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }


}