package itlapps.team8.childrenchat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.helpers.Message;
import itlapps.team8.childrenchat.helpers.Wifi;

public class MainActivity extends AppCompatActivity {
    //Codigo para saber que activity tiene que iniciar
    private static final int RC_SIGN_IN = 123;

    //Componentes graficos
    private MaterialButton materialButtonIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NoActionBarTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Binding vistas
        materialButtonIngresar = findViewById(R.id.material_button_ingresar);

        //Inicia la vista para loguearse
        materialButtonIngresar.setOnClickListener(v -> {
            if (Wifi.isConnected(this)) {
                loginScreen();
            } else {
                Message.makeSimpleMessage(this, R.string.global_error_wifi_title, R.string.global_error_wifi_message, R.string.global_accept);
            }
        });
    }

    /**
     * Añade los providers para iniciar sesion e inicializa la activity para mostrarlos
     */
    private void loginScreen() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logoconletras)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Resultado del inicio de sesion
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.e("user", user.toString());
                // ...
            } else {
                //Error al iniciar sesion, se muestra mensaje
                /*AlertDialog.Builder errorLogin = new AlertDialog.Builder(this);
                errorLogin.setTitle(R.string.mainactivity_errorlogin_title);
                errorLogin.setMessage(R.string.mainactivity_errorlogin_message);
                errorLogin.setPositiveButton(R.string.global_accept, (dialog, which) -> dialog.dismiss());
                errorLogin.show();*/
            }
        }
    }

}
