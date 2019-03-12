package itlapps.juanjoseesva.recyclerview_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import itlapps.juanjoseesva.recyclerview_example.adapters.AdapterPersons;
import itlapps.juanjoseesva.recyclerview_example.model.Person;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPersons;
    private List<Person> persons;
    private AdapterPersons adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        persons = new ArrayList<>();
        persons.add(new Person("Juda", "Alector", 21));
        persons.add(new Person("Carlos", "Luna", 28));
        persons.add(new Person("Juan", "Estrada", 20));

        adapter = new AdapterPersons(this, persons);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerViewPersons = findViewById(R.id.rv_persons);
        recyclerViewPersons.setLayoutManager(layoutManager);
        recyclerViewPersons.setAdapter(adapter);
    }
}
