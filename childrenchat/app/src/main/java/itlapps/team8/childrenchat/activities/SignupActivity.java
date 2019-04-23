package itlapps.team8.childrenchat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.firebase.Database;
import itlapps.team8.childrenchat.firebase.Storage;
import itlapps.team8.childrenchat.helpers.Keyboard;
import itlapps.team8.childrenchat.helpers.Message;

public class SignupActivity extends AppCompatActivity {
    private FirebaseUser usuario;
    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_GALLERY = 1;

    //Datos SOAP para llamada a servicio de CURP
    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
    String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    String SOAP_ADDRESS = "http://187.216.144.153:8080/WSCurp/ConsultaCurp.asmx";
    String OPERATION_NAME = "ConsultaPorCurp";

    //Componentes graficos
    private CircleImageView imageViewPhoto;
    private TextInputEditText editTextCurp;
    private TextInputEditText editTextNombre;
    private TextInputEditText editTextFechaCumple;
    private TextInputEditText editTextEdad;
    private RadioButton radioButtonHombre;
    private RadioButton radioButtonMujer;
    private MaterialButton materialButtonRegistrarse;
    private FrameLayout frameLayoutMensaje;
    private MaterialButton materialButtonCerrarMensaje;

    //Variable que inicializa en true para determinar que al inicio todos los usuarios quieren foto
    private boolean quiereImagen = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        usuario = FirebaseAuth.getInstance().getCurrentUser();

        //Permite hacer llamadas SOAP en el hilo principal
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageViewPhoto = findViewById(R.id.imageview_photo);
        editTextCurp = findViewById(R.id.edittext_curp);
        editTextNombre = findViewById(R.id.edittext_nombre);
        editTextFechaCumple = findViewById(R.id.edittext_fechacumple);
        editTextEdad = findViewById(R.id.edittext_edad);
        radioButtonHombre = findViewById(R.id.radiobutton_hombre);
        radioButtonMujer = findViewById(R.id.radiobutton_mujer);
        materialButtonRegistrarse = findViewById(R.id.materialbutton_registrarse);
        frameLayoutMensaje = findViewById(R.id.framelayout_mensaje);
        materialButtonCerrarMensaje = findViewById(R.id.materialbutton_cerrarmensaje);

        //Filtro para convertir todas las letras en mayusculas
        editTextCurp.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(18)});

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

        //Cierra el mensaje con la animacion
        materialButtonCerrarMensaje.setOnClickListener(v -> frameLayoutMensaje.setVisibility(View.GONE));

        //Abre el menu para seleccionar una foto de perfil
        imageViewPhoto.setOnClickListener(v -> seleccionarFoto());

        materialButtonRegistrarse.setOnClickListener(v -> registrarse());
    }

    /**
     * Abre menu para seleccionar una foto de camara o galeria
     */
    private void seleccionarFoto() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View viewSelect = inflater.inflate(R.layout.menu_selectphoto, null);

        LinearLayout layoutCamera = viewSelect.findViewById(R.id.ll_camera);
        LinearLayout layoutGallery = viewSelect.findViewById(R.id.ll_gallery);

        layoutCamera.setOnClickListener(view -> {
            dialog.dismiss();
            openCamera();
        });

        layoutGallery.setOnClickListener(view -> {
            dialog.dismiss();
            openGallery();
        });

        dialog.setContentView(viewSelect);
        dialog.show();
    }

    /**
     * Genera un intent para abrir la camara
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * Genera un intent para abrir la galeria
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    /**
     * A partir del codigo de peticion se ejecuta la accion de camara o galeria
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    imageViewPhoto.setImageBitmap(image);
                    //uploadImagesToFirebase();
                    break;
                }
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri image = data.getData();
                    imageViewPhoto.setImageURI(image);
                    //uploadImagesToFirebase();
                    break;
                }
        }
    }


    /**
     * Limpia todos los campos excepto el del curp para no enviar nada al servidor en caso de error
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

    /**
     * Genera una llamada al servicio de CURP y llena los datos obtenidos en los edittext
     */
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
     *
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

    /**
     * Genera el registro del usuario en el servidor
     */
    private void registrarse() {
        if (camposValidos()) {
            String genero = "";

            if (radioButtonHombre.isChecked()) {
                genero = "h";
            } else if (radioButtonMujer.isChecked()) {
                genero = "m";
            }

            //Registra usuario en Firebase
            Database.registrarPadre(usuario.getUid(),
                    editTextNombre.getText().toString(),
                    editTextFechaCumple.getText().toString(),
                    genero, editTextCurp.getText().toString(),
                    usuario.getEmail());

            if (seleccionoImagen()) {
                guardarImagenPerfilUsuario();
            }

            Intent intent = new Intent(this, MainActivityFather.class);
            startActivity(intent);
        }
    }

    /**
     * Toma los bytes de la imagen seleccionada y almacena la imagen en la base de datos
     */
    private void guardarImagenPerfilUsuario() {
        imageViewPhoto.setDrawingCacheEnabled(true);
        imageViewPhoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageViewPhoto.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

        byte[] dataBytes = baos.toByteArray();

        String nombreArchivo = FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg";

        Storage.guardarImagenPerfilUsuario(nombreArchivo, dataBytes);
    }

    /**
     * Valida los campos de curp y retorna un booleano para hacerle saber al usuario los errores
     * o advertencias
     *
     * @return
     */
    private boolean camposValidos() {
        //Valida el tamaño correcto del CURP
        if (editTextCurp.getText().toString().length() == 18) {
            //Valida que los datos se hallan obtenido exitosamente
            if (!TextUtils.isEmpty(editTextNombre.getText().toString()) && !TextUtils.isEmpty(editTextFechaCumple.getText().toString()) && !TextUtils.isEmpty(editTextEdad.getText().toString())) {
                if (Integer.parseInt(editTextEdad.getText().toString()) >= 18) {
                    //Pregunta si el usuario quiere una imagen de perfil
                    if (quiereImagen) {
                        //Valida si el usuario ha seleccionado una imagen
                        if (seleccionoImagen()) {
                            return true;
                        } else {
                            //En caso de que no la halla seleccionado muestra una advertencia donde le
                            //pregunta si quiere continuar asi o la quiere agregar
                            AlertDialog.Builder preguntarPorImagen = new AlertDialog.Builder(this, R.style.AlertDialog);
                            preguntarPorImagen.setTitle(R.string.signupactivity_preguntarporimagen_titulo);
                            preguntarPorImagen.setMessage(R.string.signupactivity_preguntarporimagen_mensaje);

                            preguntarPorImagen.setPositiveButton(R.string.signupactivity_preguntarporimagen_buttonpositive, (dialog, which) -> {
                                //Sigue queriendo imagen y abre el menu para que la seleccione
                                quiereImagen = true;
                                dialog.dismiss();
                                seleccionarFoto();
                            });

                            preguntarPorImagen.setNegativeButton(R.string.signupactivity_preguntarporimagen_buttonnegative, (dialog, which) -> {
                                //No quiere imagen y continua con el registro
                                quiereImagen = false;
                                dialog.dismiss();
                                registrarse();
                            });

                            preguntarPorImagen.show();

                            return false;
                        }
                    } else {
                        return true;
                    }
                } else {
                    Message.makeSimpleMessage(this, R.string.signupactivity_error_menoredad_titulo, R.string.signupactivity_error_menoredad_mensaje, R.string.global_accept);
                    return false;
                }
            } else {
                editTextCurp.setError(getString(R.string.signupactivity_error_invalidcurp));
                editTextCurp.requestFocus();
                return false;
            }
        } else {
            editTextCurp.setError(getString(R.string.signupactivity_error_lengthcurp));
            editTextCurp.requestFocus();
            return false;
        }
    }

    /**
     * Valida si el ImageView aun tiene el avatar circular, si no lo tiene significa que el
     * usuario seleccino una imagen
     *
     * @return
     */
    private boolean seleccionoImagen() {
        return !imageViewPhoto.getDrawable().getConstantState().
                equals(getResources().getDrawable(R.drawable.ic_avatar).getConstantState());
    }
}
