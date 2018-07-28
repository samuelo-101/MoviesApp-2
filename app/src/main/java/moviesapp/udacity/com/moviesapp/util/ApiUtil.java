package moviesapp.udacity.com.moviesapp.util;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import moviesapp.udacity.com.moviesapp.api.model.response.ErrorResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ApiUtil {

    public static ErrorResponse getApiErrorFromResponse(Response response) {
        try {
            ResponseBody responseBody = response.errorBody();
            if(responseBody != null) {
                String responseString = responseBody.string();
                return new Gson().fromJson(responseString, ErrorResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("api_err", e.getLocalizedMessage());
        }
        return null;
    }
}
