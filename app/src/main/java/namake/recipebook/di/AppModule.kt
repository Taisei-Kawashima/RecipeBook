package namake.recipebook.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import namake.recipebook.data.repository.RecipeRepository

object AppModule{
    private const val SUPABASE_URL = "https://mgnvdinktcxwnxqqfifq.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1nbnZkaW5rdGN4d254cXFmaWZxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk3MDgzNzIsImV4cCI6MjA3NTI4NDM3Mn0.aVjVBcD-W3k8AtxaUhie5sbEcuHIjA-LcfM5eNKXW48"

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