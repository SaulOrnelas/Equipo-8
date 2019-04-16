package itlapps.team8.childrenchat.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    private static FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public static DatabaseReference USERS = DATABASE.getReference("users");


}
