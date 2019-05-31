package itlapps.team8.childrenchat.activities;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.adapters.RVChildsAdapter;
import itlapps.team8.childrenchat.adapters.RVUsersAdapter;
import itlapps.team8.childrenchat.firebase.Database;
import itlapps.team8.childrenchat.helpers.SwipeController;
import itlapps.team8.childrenchat.helpers.SwipeControllerActions;
import itlapps.team8.childrenchat.helpers.SwipeControllerActionsEnviarSolicitud;
import itlapps.team8.childrenchat.helpers.SwipeControllerEnviarSolicitud;
import itlapps.team8.childrenchat.model.Solicitud;
import itlapps.team8.childrenchat.model.Usuario;

public class ContactsActivity extends AppCompatActivity {
    private FirebaseUser usuario;
    private RecyclerView recyclerViewYourContacts;
    private RecyclerView recyclerViewOtherContacts;
    private RVUsersAdapter adapterYourContacts;
    private RVUsersAdapter adapterOtherContacts;

    private static final String TIPO_PADRE = "padre";
    private static final String TIPO_HIJO = "hijo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Slidr.attach(this);

        usuario = FirebaseAuth.getInstance().getCurrentUser();

        recyclerViewYourContacts = findViewById(R.id.rv_your_contacts);
        recyclerViewYourContacts.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewOtherContacts = findViewById(R.id.rv_other_contacts);
        recyclerViewOtherContacts.setLayoutManager(new LinearLayoutManager(this));

        final SwipeControllerEnviarSolicitud swipeController = new SwipeControllerEnviarSolicitud(this, new SwipeControllerActionsEnviarSolicitud() {
            @Override
            public void onRightClicked(int position) {
                String keyOfUserClick = ((RVUsersAdapter) recyclerViewOtherContacts.getAdapter()).getKeyOfUser(position);

                AlertDialog.Builder confirmacion = new AlertDialog.Builder(ContactsActivity.this, R.style.AlertDialog);
                confirmacion.setTitle("Confirmacion");
                confirmacion.setMessage("¿Deseas enviar solicitud a este contacto?");

                confirmacion.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                confirmacion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            //Validamos que no exista la solicitud
                            Database.USERS.child(usuario.getUid()).child("solicitudes_enviadas").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot dataSnapshotSolicitud : dataSnapshot.getChildren()) {
                                            Solicitud solicitud = dataSnapshotSolicitud.getValue(Solicitud.class);

                                            if (solicitud.keyUsuarioRecibido.equals(keyOfUserClick)) {
                                                AlertDialog.Builder mensaje = new AlertDialog.Builder(ContactsActivity.this, R.style.AlertDialog);
                                                mensaje.setTitle("En espera");
                                                mensaje.setMessage("Ya has enviado una solicitud a este usuario, espera a que responda");
                                                mensaje.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                mensaje.show();
                                            } else {
                                                //Crear solicitud
                                                Database.enviarSolicitud(usuario.getUid(), keyOfUserClick);
                                            }
                                        }
                                    } else {
                                        Database.enviarSolicitud(usuario.getUid(), keyOfUserClick);
                                        AlertDialog.Builder exito = new AlertDialog.Builder(ContactsActivity.this, R.style.AlertDialog);
                                        exito.setTitle("Enviado");
                                        exito.setMessage("La solicitud ha sido enviada");
                                        exito.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        exito.show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } catch (Exception e) {}
                    }
                });

                confirmacion.show();
            }
        });

        ItemTouchHelper itemTouchHelperPhrases = new ItemTouchHelper(swipeController);
        itemTouchHelperPhrases.attachToRecyclerView(recyclerViewOtherContacts);

        recyclerViewOtherContacts.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        cargarInformacion();
    }

    private void cargarInformacion() {
        Database.obtenerContactos(usuario.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> keys = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    keys.add(userSnapshot.getKey());
                }

                adapterYourContacts = new RVUsersAdapter(ContactsActivity.this, keys);
                recyclerViewYourContacts.setAdapter(adapterYourContacts);

                Database.USERS.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> keys1 = new ArrayList<>();

                        for (DataSnapshot userSnapshot2 : dataSnapshot.getChildren()) {

                            if (!keys.contains(userSnapshot2.getKey())) {
                                Database.obtenerUsuario(userSnapshot2.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Usuario usuarioFromDatabase = dataSnapshot.getValue(Usuario.class);

                                        if (usuarioFromDatabase.propiedades.tipo.equals(TIPO_PADRE) && !usuarioFromDatabase.propiedades.email.equals(usuario.getEmail())) {
                                            keys1.add(userSnapshot2.getKey());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        adapterOtherContacts = new RVUsersAdapter(ContactsActivity.this, keys1);
                        recyclerViewOtherContacts.setAdapter(adapterOtherContacts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
