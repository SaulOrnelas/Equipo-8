package itlapps.team8.childrenchat.helpers;

import android.app.AlertDialog;
import android.content.Context;

public class Message {

    /**
     * Genera un mensaje simple con un boton para cerrarlo
     * @param context
     * @param title
     * @param message
     * @param buttonAccept
     */
    public static void makeSimpleMessage(Context context, int title, int message, int buttonAccept) {
        AlertDialog.Builder messageDialog = new AlertDialog.Builder(context);
        messageDialog.setTitle(title);
        messageDialog.setMessage(message);
        messageDialog.setPositiveButton(buttonAccept, (dialog, which) -> {
            dialog.dismiss();
        });
        messageDialog.show();
    }
}
