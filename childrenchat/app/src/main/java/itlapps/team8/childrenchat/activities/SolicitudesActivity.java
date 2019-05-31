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
}
