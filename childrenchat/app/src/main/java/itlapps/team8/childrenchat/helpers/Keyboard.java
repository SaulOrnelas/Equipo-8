package itlapps.team8.childrenchat.helpers;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {

    public static void closeKeyboard(AppCompatActivity context) {
        View view = context.getCurrentFocus();

        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
