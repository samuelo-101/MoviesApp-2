package moviesapp.udacity.com.moviesapp.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import moviesapp.udacity.com.moviesapp.R;
import moviesapp.udacity.com.moviesapp.api.model.response.ErrorResponse;


public class DialogUtil {

    private static AlertDialog alertDialog;

    public static void showAlertDialogMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showGenericErrorMessage(Context context) {
        showAlertDialogMessage(context, context.getString(R.string.api_generic_error_title), context.getString(R.string.api_generic_error_message));
    }

    public static void showApiErrorFromErrorResponse(Context context, ErrorResponse errorResponse) {
        if (errorResponse == null) {
            showGenericErrorMessage(context);
        } else {
            String message = new StringBuilder().append(errorResponse.getStatus_message())
                    .append(" (Code: ")
                    .append(errorResponse.getStatus_code())
                    .append(")").toString();
            showAlertDialogMessage(context, context.getString(R.string.api_generic_error_title), message);
        }
    }

    public static void showConnectionFailedErrorMessage(Context context) {
        showAlertDialogMessage(context, context.getString(R.string.api_connection_error_title), context.getString(R.string.api_connection_error_message));
    }

    public static void showUnauthorizedErrorMessage(Context context) {
        showAlertDialogMessage(context, context.getString(R.string.api_generic_error_title), context.getString(R.string.api_unauthorized_error_message));
    }
}
