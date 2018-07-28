package moviesapp.udacity.com.moviesapp.api.service.interceptor;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MoviesApiServiceInterceptor {

    public static Interceptor getInterceptor(final Context context) {
        return new Interceptor() {
            @Override
            public Response intercept(@NonNull final Interceptor.Chain chain) throws IOException {
                Request request = chain.request();

                Response response = chain.proceed(request);

                if(response.code() == 401 || response.code() == 404) {
                    // TODO: Consider implementing global error handling here
                    /*Intent intent = new Intent(activity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);*/
                }

                return response;
            }
        };
    }
}
