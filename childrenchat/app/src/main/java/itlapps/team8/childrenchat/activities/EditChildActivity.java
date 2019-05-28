package itlapps.team8.childrenchat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.extras.CCUtil;
import itlapps.team8.childrenchat.firebase.Database;
import itlapps.team8.childrenchat.firebase.Storage;
import itlapps.team8.childrenchat.model.Usuario;


public class EditChildActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_GALLERY = 1;

    private FirebaseAuth firebaseAuth;
    private Usuario child;

    private CircleImageView imageViewPhoto;
    private TextInputEditText editTextCurp;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextNombre;
    private TextInputEditText editTextFechaCumple;
    private TextInputEditText editTextEdad;

    private RadioButton radioButtonHombre;
    private RadioButton radioButtonMujer;
    private Boolean isPictureUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);

        this.firebaseAuth = FirebaseAuth.getInstance();

        Slidr.attach(this);

        Bundle extra = this.getIntent().getExtras();
        String childKey = extra.getString("key");

        isPictureUpdated = false;
        imageViewPhoto = findViewById(R.id.child_edit_imageview_photo);
        editTextCurp = findViewById(R.id.child_edit_edittext_curp);
        editTextEmail = findViewById(R.id.child_edit_edittext_email);
        editTextNombre = findViewById(R.id.child_edit_edittext_nombre);
        editTextFechaCumple = findViewById(R.id.child_edit_edittext_fechacumple);
        editTextEdad = findViewById(R.id.child_edit_edittext_edad);
        radioButtonHombre = findViewById(R.id.child_edit_radiobutton_hombre);
        radioButtonMujer = findViewById(R.id.child_edit_radiobutton_mujer);

        Database.obtenerUsuario(childKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        child = dataSnapshot.getValue(Usuario.class);

                        editTextCurp.setText(child.propiedades.curp);
                        editTextEmail.setText(child.propiedades.email);
                        editTextNombre.setText(child.propiedades.nombre);
                        editTextFechaCumple.setText(child.propiedades.fechaCumple);
                        editTextEdad.setText(CCUtil.edad(child.propiedades.fechaCumple));
                        if (child.propiedades.genero.equals("h")) {
                            radioButtonHombre.setChecked(true);
                        } else {
                            radioButtonMujer.setChecked(true);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        Storage.obtenerReferenciaFotoPerfilUsuario(childKey + ".jpg")
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                try {
                    Picasso.get().load(task.getResult()).fit()
                            .into(imageViewPhoto);
                } catch (Exception exception) {
                }
            }
        });

        imageViewPhoto.setOnClickListener(v -> seleccionarFoto());

    }

    public  void recoveryEmail(View view){
        this.firebaseAuth.sendPasswordResetEmail(this.child.propiedades.email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            Toast.makeText(EditChildActivity.this,
                                    "El correo debería llegar en pocos minutos",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(EditChildActivity.this,
                                    "Intenta enviar el correo más tarde",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    imageViewPhoto.setImageBitmap(image);
                    isPictureUpdated = true;
                    break;
                }
            case REQUEST_CODE_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri image = data.getData();
                    imageViewPhoto.setImageURI(image);
                    isPictureUpdated = true;
                    break;
                }
        }
    }

    public void updateInfo(View view){
        if(isPictureUpdated){//...También la contraseña;
            CCUtil.guardarImagenPerfilUsuario(imageViewPhoto, child.propiedades.key);
            Toast.makeText(
                    this, "Se actualizcó la foto",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(
                    this, "Primero selecciona una foto",
                    Toast.LENGTH_SHORT).show();
        }

        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
