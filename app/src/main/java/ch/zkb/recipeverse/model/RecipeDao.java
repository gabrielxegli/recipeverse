package ch.zkb.recipeverse.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe")
    List<Recipe> getAll();

    @Query("SELECT * FROM recipe WHERE id=(:id)")
    Recipe getByID(int id);

    @Insert
    void insertAll(Recipe... recipes);

    @Delete
    void delete(Recipe recipe);
}
