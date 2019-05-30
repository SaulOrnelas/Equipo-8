package itlapps.team8.childrenchat.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import itlapps.team8.childrenchat.adapters.RVUsersAdapter;
import itlapps.team8.childrenchat.firebase.Database;
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
