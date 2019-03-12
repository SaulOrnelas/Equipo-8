package itlapps.juanjoseesva.recyclerview_example.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import itlapps.juanjoseesva.recyclerview_example.R;
import itlapps.juanjoseesva.recyclerview_example.model.Person;

public class AdapterPersons extends RecyclerView.Adapter<AdapterPersons.AdapterViewHolderPersons> {
    private Context context;
    private List<Person> persons;

    public AdapterPersons(Context context, List<Person> persons) {
        this.context = context;
        this.persons = persons;
    }

    @NonNull
    @Override
    public AdapterViewHolderPersons onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv_person, viewGroup, false);
        return new AdapterViewHolderPersons(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolderPersons adapterViewHolderPersons, int i) {
        adapterViewHolderPersons.textViewName.setText(persons.get(i).getName());
        adapterViewHolderPersons.textViewLastName.setText(persons.get(i).getLastName());
        adapterViewHolderPersons.textViewAge.setText(String.valueOf(persons.get(i).getAge()));
        adapterViewHolderPersons.person = persons.get(i);
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public class AdapterViewHolderPersons extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewLastName;
        private TextView textViewAge;
        private Person person;

        public AdapterViewHolderPersons(View view) {
            super(view);

            textViewName = view.findViewById(R.id.tv_name);
            textViewLastName = view.findViewById(R.id.tv_lastname);
            textViewAge = view.findViewById(R.id.tv_age);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle(person.getName());
                    alertDialog.setMessage("Apellido: " + person.getLastName() + "\nEdad: " + person.getAge());
                    alertDialog.show();
                }
            });
        }
    }
}
