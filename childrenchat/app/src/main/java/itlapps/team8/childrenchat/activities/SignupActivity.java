package itlapps.team8.childrenchat.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.helpers.Keyboard;
import itlapps.team8.childrenchat.helpers.Message;

public class SignupActivity extends AppCompatActivity {
    //Datos SOAP para llamada a servicio de CURP
    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
    String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    String SOAP_ADDRESS = "http://187.216.144.153:8080/WSCurp/ConsultaCurp.asmx";
    String OPERATION_NAME = "ConsultaPorCurp";

    //Componentes graficos
    private TextInputEditText editTextCurp;
    private TextInputEditText editTextNombre;
    private TextInputEditText editTextFechaCumple;
    private TextInputEditText editTextEdad;
    private RadioButton radioButtonHombre;
    private RadioButton radioButtonMujer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        editTextCurp = findViewById(R.id.edittext_curp);
        editTextNombre = findViewById(R.id.edittext_nombre);
        editTextFechaCumple = findViewById(R.id.edittext_fechacumple);
        editTextEdad = findViewById(R.id.edittext_edad);
        radioButtonHombre = findViewById(R.id.radiobutton_hombre);
        radioButtonMujer = findViewById(R.id.radiobutton_mujer);

        //Permite hacer llamadas SOAP en el hilo principal
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Filtro para convertir todas las letras en mayusculas
        editTextCurp.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        //Listener para saber el tamaño de la cadena actualmente en el curp
        editTextCurp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 18) {
                    llamarServicioCURP();
                } else {
                    limpiarCampos();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * Limpia todos los campos excepto el del curp para no enviar nada al servidor
     */
    private void limpiarCampos() {
        Log.e("pasa", "pasa");
        editTextNombre.setText("");
        editTextFechaCumple.setText("");
        editTextEdad.setText("");
        //TODO verificar por que se limpian cuando no deberian
        //radioButtonHombre.setChecked(false);
        //radioButtonMujer.setChecked(false);
    }

    private void llamarServicioCURP() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);

        SoapObject body = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        body.addProperty("curp", editTextCurp.getText().toString());

        envelope.setOutputSoapObject(body);

        HttpTransportSE httpTransportSE = new HttpTransportSE(SOAP_ADDRESS);
        httpTransportSE.debug = true;
        String soapAction = WSDL_TARGET_NAMESPACE + OPERATION_NAME;
        try {
            httpTransportSE.call(soapAction, envelope);

            SoapObject response = (SoapObject) envelope.getResponse();

            String userName = response.getPrimitivePropertyAsString("nombres") + " " + response.getPrimitivePropertyAsString("PrimerApellido") + " " + response.getPrimitivePropertyAsString("SegundoApellido");
            String fechaNac = response.getPrimitivePropertyAsString("fechNac");
            String curp = response.getPrimitivePropertyAsString("CurpRenapo");
            String sexo = response.getPrimitivePropertyAsString("sexo");
            Log.e("username", userName);
            Log.e("birthday", fechaNac);
            Log.e("curp", curp);
            Log.e("sexo", sexo);

            editTextNombre.setText(userName);
            editTextFechaCumple.setText(fechaNac);
            editTextEdad.setText(edad(fechaNac));

            if (sexo.equals("H")) {
                radioButtonHombre.setChecked(true);
                radioButtonMujer.setChecked(false);
            } else if (sexo.equals("M")) {
                radioButtonMujer.setChecked(true);
                radioButtonHombre.setChecked(false);
            }

            Message.makeSimpleMessage(this, R.string.signupactivity_successcurp_title, R.string.signupactivity_successcurp_message, R.string.global_accept);
            Keyboard.closeKeyboard(this);

        } catch (Exception e) {
            limpiarCampos();
            Message.makeSimpleMessage(this, R.string.signupactivity_errorcurp_title, R.string.signupactivity_errorcurp_message, R.string.global_accept);
            Keyboard.closeKeyboard(this);
        }
    }

    /**
     * Calcula la edad del usuario a partir de su fecha de nacimiento
     * @param fechaNacimiento
     * @return
     */
    private String edad(String fechaNacimiento) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse(fechaNacimiento);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        Calendar fechaNacimientoUsuario = Calendar.getInstance();
        fechaNacimientoUsuario.setTime(date);

        Calendar hoy = Calendar.getInstance();

        long diferencia = hoy.getTimeInMillis() - fechaNacimientoUsuario.getTimeInMillis();

        long anios = (diferencia / (24 * 60 * 60 * 1000)) / 365;

        return String.valueOf(anios);
    }
}
