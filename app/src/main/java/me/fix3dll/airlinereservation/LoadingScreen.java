package me.fix3dll.airlinereservation;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class LoadingScreen extends AppCompatActivity {
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://charged-garden-192121-default-rtdb.europe-west1.firebasedatabase.app/");
    ArrayList<String> bookedSeats = new ArrayList<>();
    ArrayList<String> freeSeats = new ArrayList<>(Arrays.asList(
            "A1", "A2", "A3", "A4", "A5", "A6", "A7"
            , "B1", "B2", "B3", "B4", "B5", "B6", "B7"
            , "C1", "C2", "C3", "C4", "C5", "C6", "C7"
            , "D1", "D2", "D3", "D4", "D5", "D6", "D7"
            , "E1", "E2", "E3", "E4", "E5", "E6", "E7"
            , "F1", "F2", "F3", "F4", "F5", "F6", "F7"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        getSupportActionBar().hide();
        
        db.getReference().child("customers").get().addOnSuccessListener(task -> {
            for (DataSnapshot child : task.getChildren()) {
                bookedSeats.add(child.getKey());
                freeSeats.remove(child.getKey());
            }
            Intent toMain = new Intent(this, MainActivity.class);
            toMain.putExtra("bookedArray", bookedSeats);
            toMain.putExtra("freeArray", freeSeats);
            startActivity(toMain);
        });
    }
}