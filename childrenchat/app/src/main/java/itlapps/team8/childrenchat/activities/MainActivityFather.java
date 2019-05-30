package itlapps.team8.childrenchat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.firebase.Database;
import itlapps.team8.childrenchat.firebase.Storage;
import itlapps.team8.childrenchat.model.Usuario;

public class MainActivityFather extends AppCompatActivity {
    private FirebaseUser usuario;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toogle;
    private NavigationView navigationView;
    private View headerLayout;
    private CircleImageView imageViewFotoPerfil;
    private TextView textViewNombre;
    private TextView textViewCurp;
    private TextView textViewEmail;

    private SharedPreferences sharedPreferences;


    private static final String TIPO_PADRE = "padre";
    private static final String TIPO_HIJO = "hijo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_father);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Guardamos en las preferencias de la app que hay una sesion activa
        sharedPreferences.edit().putBoolean("active_session", true).apply();

        usuario = FirebaseAuth.getInstance().getCurrentUser();
        drawerLayout = findViewById(R.id.activity_main_father);
        toogle = new ActionBarDrawerToggle(this, drawerLayout,R.string.global_accept, R.string.global_cancel);

        drawerLayout.addDrawerListener(toogle);

        toogle.syncState();

        configureToolbar();



        navigationView = findViewById(R.id.activity_main_father_navigationview);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            switch(id)
            {
                case R.id.menu_navigationfather_contactos:
                    drawerLayout.closeDrawers();
                    Intent intentContacts = new Intent(this, ContactsActivity.class);
                    startActivity(intentContacts);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case R.id.menu_navigationfather_mishijos:
                    drawerLayout.closeDrawers();
                    Intent intent = new Intent(this, ChildsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
                case R.id.menu_navigationfather_solicitudes:
                    break;
                case R.id.menu_navigationfather_ajustes:
                    break;
                case R.id.menu_navigationfather_cerrarsesion:
                    //Eliminamos la sesion del dispositivo
                    sharedPreferences.edit().putBoolean("active_session", false).apply();
                    cerrarSesion();
                    break;
            }

            return true;

        });

        headerLayout = navigationView.getHeaderView(0);

        imageViewFotoPerfil = headerLayout.findViewById(R.id.nav_header_father_imageprofile);
        textViewNombre = headerLayout.findViewById(R.id.nav_header_father_tvname);
        textViewCurp = headerLayout.findViewById(R.id.nav_header_father_tvcurp);
        textViewEmail = headerLayout.findViewById(R.id.nav_header_father_tvemail);


        navigationView.getMenu().getItem(2).setVisible(false);
        navigationView.getMenu().getItem(3).setVisible(false);

        if (sharedPreferences.getString("user_type", "padre").equals(TIPO_HIJO)) {
            navigationView.getMenu().getItem(1).setVisible(false);
        }

        cargarInformacionHeader();

    }


    /**
     * Carga la informacion del usuario en el header, imagen, nombre, curp y email
     */
    private void cargarInformacionHeader() {
        DatabaseReference referenciaUsuario = Database.obtenerUsuario(usuario.getUid());

        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                textViewNombre.setText(usuario.propiedades.nombre);
                textViewCurp.setText(usuario.propiedades.curp);
                textViewEmail.setText(usuario.propiedades.email);

                Storage.obtenerReferenciaFotoPerfilUsuario(usuario.propiedades.key + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        try {
                            Picasso.get().load(task.getResult()).fit().into(imageViewFotoPerfil);
                        } catch (Exception exception) {}
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_father);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if(toogle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_searchfather, menu);
        //MenuItem searchItem = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Configure the search info and add any event listeners

        return super.onCreateOptionsMenu(menu);
    }

    private void cerrarSesion() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            //sharedPreferences.edit().putBoolean("opensession", false).apply();
            //MyApplication.cancelAlarm();
            Intent intent = new Intent(MainActivityFather.this, MainActivity.class);
            startActivity(intent);
            MainActivityFather.this.finish();
        });
    }
}
