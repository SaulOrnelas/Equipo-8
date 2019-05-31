package itlapps.team8.childrenchat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import itlapps.team8.childrenchat.model.Usuario;

public class RVSetContactAdapter extends RecyclerView.Adapter<RVSetContactAdapter.RVChildsAdapterViewHolder> {
    private FirebaseUser usuarioGlobal;
    private Context context;
    private List<String> keys;

    public RVSetContactAdapter(Context context, List<String> keys) {
        this.context = context;
        this.keys = keys;

        usuarioGlobal = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public RVChildsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_user, viewGroup, false);
        return new RVChildsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVChildsAdapterViewHolder rvChildsAdapterViewHolder, int i) {
        Database.obtenerUsuario(keys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
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

                rvChildsAdapterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder confirmacion = new AlertDialog.Builder(context, R.style.AlertDialog);
                        confirmacion.setTitle("Confirmacion");
                        confirmacion.setMessage("¿Deseas asignar a este usuario tu hijo?, en caso de aceptar el usuario mantener conversaciones abiertas con tu hijo");
                        confirmacion.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        confirmacion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Implementar asignacion de contacto
                            }
                        });
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
        return keys.size();
    }

    public String getKeyOfUser(int position) {
        return keys.get(position);
    }

    public class RVChildsAdapterViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageViewUserPhoto;
        private TextView textViewUserName;
        private TextView textViewUserCurp;
        private TextView textViewUserEmail;

        public RVChildsAdapterViewHolder(View view) {
            super(view);

            imageViewUserPhoto = view.findViewById(R.id.imageview_userphoto);
            textViewUserName = view.findViewById(R.id.tv_username);
            textViewUserCurp = view.findViewById(R.id.tv_usercurp);
            textViewUserEmail = view.findViewById(R.id.tv_useremail);
        }

    }

}
