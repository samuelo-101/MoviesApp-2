package moviesapp.udacity.com.moviesapp.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import moviesapp.udacity.com.moviesapp.db.dao.MovieFavouriteEntityDao;
import moviesapp.udacity.com.moviesapp.db.entity.MovieFavouriteEntity;

@Database(entities = { MovieFavouriteEntity.class }, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "moviesapp2_udacity_movie_favourite";

    private static MovieDatabase movieDatabase;

    public abstract MovieFavouriteEntityDao movieFavouriteDao();

    public static synchronized MovieDatabase getInstance(Context context) {
        if(movieDatabase == null) {
            movieDatabase = Room.databaseBuilder(context, MovieDatabase.class, DATABASE_NAME).build();
        }
        return movieDatabase;
    }
}
