package moviesapp.udacity.com.moviesapp.api.service;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import moviesapp.udacity.com.moviesapp.R;
import moviesapp.udacity.com.moviesapp.api.service.interceptor.MoviesApiServiceInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesApiServiceHelper {

    private static MoviesApiService moviesApiService;

    public static synchronized MoviesApiService getInstance(Context context) {
        if(moviesApiService == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    /*.connectionSpecs(Arrays.asList(
                            ConnectionSpec.CLEARTEXT))*/
                    .addInterceptor(MoviesApiServiceInterceptor.getInterceptor(context))
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(context.getString(R.string.api_base_uri))
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            moviesApiService = retrofit.create(MoviesApiService.class);
        }

        return moviesApiService;
    }
}
