package ch.zkb.recipeverse.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Recipe {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "image_link")
    public String image;

    @ColumnInfo(name = "health_score")
    public double healthScore;

    @ColumnInfo(name = "summary")
    public String summary;

    public Recipe(int id, String title, String image, double healthScore, String summary){
        this.id = id;
        this.title = title;
        this.image = image;
        this.healthScore = healthScore;
        this.summary = summary;
    }

}
