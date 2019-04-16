package itlapps.team8.childrenchat.helpers;

import android.app.AlertDialog;
import android.content.Context;

import itlapps.team8.childrenchat.R;

public class Message {

    /**
     * Genera un mensaje simple con un boton para cerrarlo, con estilo propio
     * @param context
     * @param title
     * @param message
     * @param buttonAccept
     */
    public static void makeSimpleMessage(Context context, int title, int message, int buttonAccept) {
        AlertDialog.Builder messageDialog = new AlertDialog.Builder(context, R.style.AlertDialog);
        messageDialog.setTitle(title);
        messageDialog.setMessage(message);
        messageDialog.setPositiveButton(buttonAccept, (dialog, which) -> {
            dialog.dismiss();
        });
        messageDialog.show();
    }
}
