package moviesapp.udacity.com.moviesapp.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import moviesapp.udacity.com.moviesapp.db.entity.MovieFavouriteEntity;

@Dao
public interface MovieFavouriteEntityDao {

    @Query("SELECT * FROM movie_favourite ORDER BY title")
    LiveData<List<MovieFavouriteEntity>> getAll();

    @Query("SELECT * FROM movie_favourite WHERE id = :movieId")
    MovieFavouriteEntity getOne(int movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieFavouriteEntity movieFavouriteEntity);

    @Insert
    void insertAll(MovieFavouriteEntity... movieFavouriteEntities);

    @Update
    void update(MovieFavouriteEntity movieFavouriteEntity);

    @Query("DELETE FROM movie_favourite WHERE id = :id")
    void deleteById(int id);

}
