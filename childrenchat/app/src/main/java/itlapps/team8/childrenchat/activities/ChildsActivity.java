package itlapps.team8.childrenchat.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childs);
        Slidr.attach(this);
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        floatingActionButtonAddChild = findViewById(R.id.fb_addchild);
        textViewEmptyChilds = findViewById(R.id.tv_emptychilds);
        recyclerViewChilds = findViewById(R.id.rv_childs);

        final SwipeController swipeController = new SwipeController(this, new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                try {
                    /*RVPhrasesAdapter adapter = (RVPhrasesAdapter) recyclerViewPhrases.getAdapter();
                    Database.deletePhrase(user.getUid(), adapter.getItem(position));*/
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

                    RVChildsAdapter adapter = new RVChildsAdapter(ChildsActivity.this, keys);
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
