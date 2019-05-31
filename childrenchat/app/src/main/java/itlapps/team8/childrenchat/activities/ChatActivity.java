package itlapps.team8.childrenchat.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.adapters.RVMensajesAdapter;
import itlapps.team8.childrenchat.firebase.Database;
import itlapps.team8.childrenchat.model.ChatDelNodoUsuario;
import itlapps.team8.childrenchat.model.Mensaje;

public class ChatActivity extends AppCompatActivity {
    private FirebaseUser usuario;
    private String keyOfOtherContact;

    private RecyclerView recyclerViewMessages;
    private TextInputEditText editTextMessage;
    private FloatingActionButton fabSendMessage;
    private RVMensajesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Slidr.attach(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usuario = FirebaseAuth.getInstance().getCurrentUser();
        keyOfOtherContact = getIntent().getStringExtra("key_of_other_contact");

        recyclerViewMessages = findViewById(R.id.rv_messages);
        editTextMessage = findViewById(R.id.et_message);
        fabSendMessage = findViewById(R.id.fab_sendmessage);

        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        ((LinearLayoutManager) recyclerViewMessages.getLayoutManager()).setStackFromEnd(true);

        Database.USERS.child(usuario.getUid()).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    ChatDelNodoUsuario chat = chatSnapshot.getValue(ChatDelNodoUsuario.class);

                    try {
                        if (chat.keyContacto.equals(keyOfOtherContact)) {
                            Database.CHATS.child(chat.key).child("mensajes").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<Mensaje> mensajes = new ArrayList<>();

                                    for (DataSnapshot dataSnapshotMensaje : dataSnapshot.getChildren()) {
                                        Mensaje mensaje = dataSnapshotMensaje.getValue(Mensaje.class);
                                        mensajes.add(mensaje);
                                    }

                                    adapter = new RVMensajesAdapter(ChatActivity.this, mensajes);
                                    recyclerViewMessages.setAdapter(adapter);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                        }

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fabSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editTextMessage.getText().toString())) {
                    Database.enviarMensaje(editTextMessage.getText().toString(), usuario.getUid(), keyOfOtherContact);
                    editTextMessage.setText("");
                    ((LinearLayoutManager) recyclerViewMessages.getLayoutManager()).setStackFromEnd(true);
                }
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
