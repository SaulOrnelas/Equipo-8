package itlapps.team8.childrenchat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import itlapps.team8.childrenchat.R;
import itlapps.team8.childrenchat.model.Mensaje;

public class RVMensajesAdapter extends RecyclerView.Adapter<RVMensajesAdapter.RVMensajesAdapterViewHolder> {
    private Context context;
    private List<Mensaje> mensajes;
    private FirebaseUser usuario;

    public RVMensajesAdapter(Context context, List<Mensaje> mensajes) {
        this.context = context;
        this.mensajes = mensajes;

        usuario = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public RVMensajesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_message, viewGroup, false);
        return new RVMensajesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVMensajesAdapterViewHolder rvMensajesAdapterViewHolder, int i) {
        Mensaje mensaje = mensajes.get(i);

        rvMensajesAdapterViewHolder.textViewMensaje.setText(mensaje.mensaje);

        if (mensaje.keyContacto.equals(usuario.getUid())) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)rvMensajesAdapterViewHolder.cardViewMensaje.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            rvMensajesAdapterViewHolder.cardViewMensaje.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public class RVMensajesAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMensaje;
        private CardView cardViewMensaje;

        public RVMensajesAdapterViewHolder(View view) {
            super(view);

            cardViewMensaje = view.findViewById(R.id.cardview_mensaje);
            textViewMensaje = view.findViewById(R.id.tv_mensaje);
        }

    }
}
