package itlapps.team8.childrenchat.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.activities.ChatActivity;
import itlapps.team8.childrenchat.firebase.Database;
import itlapps.team8.childrenchat.firebase.Storage;
import itlapps.team8.childrenchat.model.Solicitud;
import itlapps.team8.childrenchat.model.Usuario;

public class RVSolicitudesAdapter extends RecyclerView.Adapter<RVSolicitudesAdapter.RVSolicitudesdapterViewHolder> {
    private FirebaseUser usuarioGlobal;
    private Context context;
    private List<Solicitud> solicitudes;

    public RVSolicitudesAdapter(Context context, List<Solicitud> solicitudes) {
        this.context = context;
        this.solicitudes = solicitudes;

        usuarioGlobal = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public RVSolicitudesdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_user, viewGroup, false);
        return new RVSolicitudesdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVSolicitudesdapterViewHolder rvChildsAdapterViewHolder, int i) {
        Database.obtenerUsuario(solicitudes.get(i).keyUsuarioEnviado).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                rvChildsAdapterViewHolder.textViewUserName.setText(usuario.propiedades.nombre);
                rvChildsAdapterViewHolder.textViewUserCurp.setText(usuario.propiedades.curp);
                rvChildsAdapterViewHolder.textViewUserEmail.setText(usuario.propiedades.email);


                Storage.obtenerReferenciaFotoPerfilUsuario(usuario.propiedades.key + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        try {
                            Picasso.get().load(task.getResult()).fit().into(rvChildsAdapterViewHolder.imageViewUserPhoto);
                        } catch (Exception exception) {
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return solicitudes.size();
    }

    public String getKeyOfUser(int position) {
        return solicitudes.get(position).keyUsuarioEnviado;
    }

    public String getKeyOfSolicitud(int position) {
        return solicitudes.get(position).key;
    }

    public void eliminarSolicitud(int position) {
        solicitudes.remove(position);
        notifyDataSetChanged();
    }


    public class RVSolicitudesdapterViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageViewUserPhoto;
        private TextView textViewUserName;
        private TextView textViewUserCurp;
        private TextView textViewUserEmail;

        public RVSolicitudesdapterViewHolder(View view) {
            super(view);

            imageViewUserPhoto = view.findViewById(R.id.imageview_userphoto);
            textViewUserName = view.findViewById(R.id.tv_username);
            textViewUserCurp = view.findViewById(R.id.tv_usercurp);
            textViewUserEmail = view.findViewById(R.id.tv_useremail);
        }

    }

}
