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
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.adapters.RVSolicitudesAdapter;
import itlapps.team8.childrenchat.adapters.RVUsersAdapter;
import itlapps.team8.childrenchat.firebase.Database;
import itlapps.team8.childrenchat.helpers.SwipeControllerAceptarSolicitud;
import itlapps.team8.childrenchat.helpers.SwipeControllerActionsAceptarSolicitud;
import itlapps.team8.childrenchat.helpers.SwipeControllerActionsEnviarSolicitud;
import itlapps.team8.childrenchat.helpers.SwipeControllerEnviarSolicitud;
import itlapps.team8.childrenchat.model.Solicitud;

public class SolicitudesActivity extends AppCompatActivity {
    private FirebaseUser usuario;
    private RecyclerView recyclerViewSolicitudes;
    private RVSolicitudesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes);
        Slidr.attach(this);

        usuario = FirebaseAuth.getInstance().getCurrentUser();

        recyclerViewSolicitudes = findViewById(R.id.rv_solicitudes);
        recyclerViewSolicitudes.setLayoutManager(new LinearLayoutManager(this));

        Database.USERS.child(usuario.getUid()).child("solicitudes_recibidas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Solicitud> solicitudes = new ArrayList<>();

                    for (DataSnapshot dataSnapshotSolicitud : dataSnapshot.getChildren()) {
                        Solicitud solicitud = dataSnapshotSolicitud.getValue(Solicitud.class);
                        solicitudes.add(solicitud);
                    }

                    adapter = new RVSolicitudesAdapter(SolicitudesActivity.this, solicitudes);
                    recyclerViewSolicitudes.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final SwipeControllerAceptarSolicitud swipeController = new SwipeControllerAceptarSolicitud(this, new SwipeControllerActionsAceptarSolicitud() {
            @Override
            public void onRightClicked(int position) {
                String keyOfUserClick = ((RVSolicitudesAdapter) recyclerViewSolicitudes.getAdapter()).getKeyOfUser(position);
                String keyOfSolicitudClick = ((RVSolicitudesAdapter) recyclerViewSolicitudes.getAdapter()).getKeyOfSolicitud(position);

                AlertDialog.Builder confirmacion = new AlertDialog.Builder(SolicitudesActivity.this, R.style.AlertDialog);
                confirmacion.setTitle("Confirmacion");
                confirmacion.setMessage("¿Deseas aceptar la solicitud a este contacto?");

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
                            //Agregamos los contactos en cada nodo
                            Database.USERS.child(usuario.getUid()).child("contactos").child(keyOfUserClick).setValue(keyOfUserClick);
                            Database.USERS.child(keyOfUserClick).child("contactos").child(usuario.getUid()).setValue(usuario.getUid());

                            //Borramos solicitud
                            Database.USERS.child(usuario.getUid()).child("solicitudes_recibidas").child(keyOfSolicitudClick).setValue(null);
                            ((RVSolicitudesAdapter) recyclerViewSolicitudes.getAdapter()).eliminarSolicitud(position);

                            //Queda pendiente eliminar solicitudes enviadas
                            //Database.USERS.child(keyOfUserClick).child("solicitudes_enviadas")
                        } catch (Exception e) {}
                    }
                });

                confirmacion.show();
            }
        });

        ItemTouchHelper itemTouchHelperPhrases = new ItemTouchHelper(swipeController);
        itemTouchHelperPhrases.attachToRecyclerView(recyclerViewSolicitudes);

        recyclerViewSolicitudes.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
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

    /**
     * Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at sem vel diam varius aliquam. Nunc nec viverra lorem. Donec non egestas diam, quis consectetur justo. Fusce nisl sem, ultricies vel dolor vel, ornare tristique sem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Praesent rutrum arcu nisi, et hendrerit est semper at. Cras nec est interdum, laoreet leo ac, gravida odio.
     *
     * Praesent aliquam tellus vitae ipsum dignissim, vitae interdum velit pulvinar. Nullam dapibus ut erat quis convallis. Etiam ullamcorper est in sapien placerat efficitur. Praesent in euismod tortor, mollis gravida dui. Suspendisse at rhoncus ipsum. Interdum et malesuada fames ac ante ipsum primis in faucibus. Donec mattis mollis risus vitae dapibus. Proin lobortis est non risus cursus, ac sodales lectus aliquet. Vivamus in elit quis odio posuere tristique in a urna. Maecenas vulputate, sem eu iaculis fringilla, ante velit efficitur sapien, sed aliquam orci arcu at magna.
     *
     * Nulla imperdiet augue nec massa vehicula rutrum. Aliquam tincidunt eu dui id interdum. Vivamus auctor efficitur nulla, at fringilla lorem accumsan vel. Sed maximus sem id molestie tincidunt. Etiam viverra faucibus turpis in efficitur. Etiam lacinia fermentum elementum. Curabitur egestas velit nunc, et volutpat magna placerat sit amet. Nunc aliquam congue sem ut imperdiet. Nunc gravida imperdiet felis et fringilla. Aenean et suscipit magna. Ut eget pretium nisl. Quisque interdum nisl nec enim tempus laoreet. Praesent pulvinar vitae augue sed convallis. Praesent suscipit lacus interdum, vehicula eros vel, imperdiet velit. Quisque condimentum lectus magna, vitae fringilla massa commodo et.
     *
     * Nulla id lectus lectus. Maecenas vel elit felis. Sed dapibus pellentesque magna nec scelerisque. Donec egestas bibendum nisi vel luctus. Suspendisse dui mi, dictum non euismod eget, auctor ut felis. Suspendisse at vulputate ligula, vel rutrum mauris. Integer molestie et augue dapibus commodo. Vestibulum lobortis ac lectus in luctus. Pellentesque in dignissim nibh, vel rhoncus dolor. Nunc et felis elementum, ultrices lacus vitae, dictum urna. Donec luctus ligula condimentum lacinia porttitor. Duis tincidunt aliquam facilisis. Vivamus ipsum ex, porttitor mattis lorem sit amet, egestas egestas neque. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Pellentesque non risus gravida, ultrices erat eget, interdum ligula. Nulla posuere diam id massa convallis lobortis.
     *
     * Curabitur bibendum purus at aliquet pharetra. Vestibulum eleifend neque eu velit vehicula rhoncus. Etiam et sem ut nisl facilisis gravida eu at erat. Donec dignissim, libero a molestie pulvinar, velit ex gravida leo, id egestas nunc lectus sed velit. Praesent faucibus nisl quis fermentum accumsan. Phasellus porta sit amet sem id consequat. Mauris fringilla maximus lorem, vitae aliquam nisi tincidunt eget. Duis volutpat enim ipsum, in pellentesque lorem tristique vitae. In volutpat turpis augue, in tristique risus sodales non. Aliquam vitae dapibus felis, eget eleifend tellus. Vivamus tempus justo quis iaculis posuere.
     */
}
