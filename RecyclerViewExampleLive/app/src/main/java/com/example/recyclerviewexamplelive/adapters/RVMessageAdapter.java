package com.example.recyclerviewexamplelive.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.recyclerviewexamplelive.R;
import com.example.recyclerviewexamplelive.model.Message;

import java.util.List;

public class RVMessageAdapter extends RecyclerView.Adapter<RVMessageAdapter.RVMessageAdapterViewHolder> {
    private Context context;
    private List<Message> messages;

    //Constructor de nuestro adapter
    public RVMessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    //Creamos el item principal que se reciclara en cada uno de los elementos
    @NonNull
    @Override
    public RVMessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //Inflamos nuestra vista de item
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_rv_message,
                viewGroup,
                false);

        //Retornamos una instancia del viewholder pasandole como parametro la vista anteriormente creada
        return new RVMessageAdapterViewHolder(view);
    }

    //Se ejecutara por cada elemento de la lista
    @Override
    public void onBindViewHolder(@NonNull RVMessageAdapterViewHolder rvMessageAdapterViewHolder, int i) {
        //Creamos una instancia de tipo "Message" dependiendo del lugar de la lista en el que este
        final Message message = messages.get(i);

        //Asignamos a las vistas del viewholder los textos del mensaje en esa posicion
        rvMessageAdapterViewHolder.textViewUsername.setText(message.getUserName());
        rvMessageAdapterViewHolder.textViewTitle.setText(message.getTitle());
        rvMessageAdapterViewHolder.textViewDescription.setText(message.getDescription());
        rvMessageAdapterViewHolder.textViewHour.setText(message.getHour());

        //Asignamos a ese item en particular un listener para cuando el usuario haga click en el
        rvMessageAdapterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(message.getUserName());
                alertDialog.setMessage(message.getDescription());

                alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
    }

    //Retornamos el tamaño de la lista
    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class RVMessageAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewHour;

        public RVMessageAdapterViewHolder(View view) {
            super(view);

            //Instanciamos nuestros objetos que seran buscados segun si id en la vista pasada por el constructor
            textViewUsername = view.findViewById(R.id.tv_username);
            textViewTitle = view.findViewById(R.id.tv_title);
            textViewDescription = view.findViewById(R.id.tv_description);
            textViewHour = view.findViewById(R.id.tv_hour);
        }
    }
}
