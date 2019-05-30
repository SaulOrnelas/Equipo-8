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

public class ContactsActivity extends AppCompatActivity {
    private FirebaseUser usuario;
    private RecyclerView recyclerViewContacts;
    private RVUsersAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Slidr.attach(this);

        usuario = FirebaseAuth.getInstance().getCurrentUser();

        //recyclerViewContacts = findViewById(R.id.rv_contacts);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));

        cargarInformacion();
    }

    private void cargarInformacion() {
        Database.USERS.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    keys.add(userSnapshot.getKey());
                }

                adapter = new RVUsersAdapter(ContactsActivity.this, keys);
                recyclerViewContacts.setAdapter(adapter);
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
