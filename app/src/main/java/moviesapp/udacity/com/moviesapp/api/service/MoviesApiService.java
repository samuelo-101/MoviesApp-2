package moviesapp.udacity.com.moviesapp.api.service;

import io.reactivex.Single;
import moviesapp.udacity.com.moviesapp.api.model.response.FetchMoviesResponse;
import moviesapp.udacity.com.moviesapp.api.model.response.FetchVideosResponse;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesApiService {

    @GET("popular")
    Single<Response<FetchMoviesResponse>> fetchPopularMovies(@Query("api_key") String apiKey);

    @GET("top_rated")
    Single<Response<FetchMoviesResponse>> fetchTopRatedMovies(@Query("api_key") String apiKey);

    @GET("{movie_id}/videos")
    Single<Response<FetchVideosResponse>> fetchMoviesVideos(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
}
