package ch.zkb.recipeverse;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ch.zkb.recipeverse.model.Recipe;
import ch.zkb.recipeverse.model.RecipeDao;

@Database(entities = {Recipe.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
}
