package me.fix3dll.airlinereservation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CancellationActivity extends AppCompatActivity {
    DatabaseReference ref = FirebaseDatabase.getInstance("https://charged-garden-192121-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    EditText id;
    TextView cancelSeat;
    String position;
    Button cancelContinue, homePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation);

        cancelSeat = findViewById(R.id.cancelSeat);
        id = findViewById(R.id.identifierCancel);
        cancelContinue = findViewById(R.id.cancelContinue);
        homePage = findViewById(R.id.backButtonCancel);
        homePage.setOnClickListener(v -> finish());

        Bundle bundle = getIntent().getExtras();
        position = bundle.get("seat").toString();
        cancelSeat.setText(position);

        cancelContinue.setOnClickListener(v -> {
            if (TextUtils.isEmpty(id.getText())) {
                id.setError("* T.C. No. boş bırakılamaz!");
            } else if (bundle.get("id").toString().equals(id.getText().toString())) {
                ref.child("customers").child(position).removeValue()
                        .addOnSuccessListener(unused -> {
                            MainActivity.bookedSeats.remove(position);
                            MainActivity.freeSeats.add(position);
                            MainActivity.switchButtonClickability(position, false);
                            Toast.makeText(getApplicationContext(), "İptal işlemi başarılı!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            } else {
                id.setError("* Hatalı T.C. numarası girdiniz!");
            }
        });
    }
}