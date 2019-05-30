package itlapps.team8.childrenchat.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import itlapps.team8.childrenchat.firebase.Database;
import itlapps.team8.childrenchat.helpers.SwipeController;
import itlapps.team8.childrenchat.helpers.SwipeControllerActions;

public class ChildsActivity extends AppCompatActivity {
    private FirebaseUser usuario;
    private FloatingActionButton floatingActionButtonAddChild;
    private TextView textViewEmptyChilds;
    private RecyclerView recyclerViewChilds;
    private RVChildsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childs);
        Slidr.attach(this);
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        floatingActionButtonAddChild = findViewById(R.id.fb_addchild);
        textViewEmptyChilds = findViewById(R.id.tv_emptychilds);
        recyclerViewChilds = findViewById(R.id.rv_childs);
        adapter = new RVChildsAdapter(this, new ArrayList<>());

        final SwipeController swipeController = new SwipeController(this, new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                try {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChildsActivity.this, R.style.AlertDialog);

                    //Agregar titulo y mensaje de alert dialog
                    alertDialogBuilder.setTitle("Eliminar hijo");
                    alertDialogBuilder.setMessage("Esta seguro de que quiere eliminar el registro");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder
                            .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    //Si se seleccionó esta opción eliminar el hijo
                                    Database.eliminarHijo(usuario.getUid(), ((RVChildsAdapter)recyclerViewChilds.getAdapter()).getKeyOfChild(position));
                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
                    alertDialogBuilder.show();
                } catch (Exception e) {}
            }
        });
        ItemTouchHelper itemTouchHelperPhrases = new ItemTouchHelper(swipeController);
        itemTouchHelperPhrases.attachToRecyclerView(recyclerViewChilds);

        recyclerViewChilds.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        //Obtenemos la lista de los hijos del padre actual
        Database.obtenerHijos(usuario.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Validamos que el nodo de hijos exista
                if (dataSnapshot.exists()) {
                    textViewEmptyChilds.setVisibility(View.GONE);
                    recyclerViewChilds.setVisibility(View.VISIBLE);

                    List<String> keys = new ArrayList<>();

                    for (DataSnapshot aux : dataSnapshot.getChildren()) {
                        keys.add(aux.getKey());
                    }

                    adapter = new RVChildsAdapter(ChildsActivity.this, keys);
                    recyclerViewChilds.setLayoutManager(new LinearLayoutManager(ChildsActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerViewChilds.setAdapter(adapter);
                } else {
                    textViewEmptyChilds.setVisibility(View.VISIBLE);
                    recyclerViewChilds.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        floatingActionButtonAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddChildActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("gg", FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
}
