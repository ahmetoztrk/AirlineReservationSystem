package me.fix3dll.airlinereservation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterForm extends AppCompatActivity {
    TextView seat;
    HashMap<String, Object> user;
    EditText firstName, lastName, id;
    Button registerContinue, homePage;
    DatabaseReference ref = FirebaseDatabase.getInstance("https://charged-garden-192121-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    AlertDialog gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.title);
            actionBar.setTitle("");
        }

        seat = findViewById(R.id.registerSeat);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        id = findViewById(R.id.identifierRegister);
        registerContinue = findViewById(R.id.registerContinue);
        homePage = findViewById(R.id.backButtonRegister);
        homePage.setOnClickListener(v -> finish());

        Bundle bundle = getIntent().getExtras();
        String position = bundle.getCharSequence("seat").toString();

        seat.setText(position);

        registerContinue.setOnClickListener(view -> {
            if (firstName.getText().length() == 0) {
                firstName.setError("* Ad boş bırakılamaz!");
            }

            if (lastName.getText().length() == 0) {
                lastName.setError("* Soyad boş bırakılamaz!");
            }

            if (id.getText().length() == 0) {
                id.setError("* T.C. No. boş bırakılamaz!");
            }

            if (firstName.getText().length() != 0
                    && id.getText().length() != 0
                    && lastName.getText().length() != 0) {
                user = new HashMap<>();
                user.put("first", firstName.getText().toString());
                user.put("last", lastName.getText().toString());
                user.put("id", id.getText().toString());

                gif = setProgressDialog();
                gif.show();

                ref.child("customers").child(position).get()
                        .addOnCompleteListener(task -> {
                            if (!task.getResult().exists()) {
                                setCustomer(position, user);
                                MainActivity.freeSeats.remove(position);
                                MainActivity.bookedSeats.add(position);
                                MainActivity.switchButtonClickability(position, false);
                                // TODO: go to next activity for save ticket to calendar
                                Toast.makeText(getApplicationContext(), "İşlem başarılı.", Toast.LENGTH_SHORT).show();
                                gif.dismiss();
                            } else {
                                Log.d("REGISTER", "DocumentSnapshot " + position + " exist on database!");
                                Toast.makeText(getApplicationContext(), "İşlem başarısız. Tekrar deneyin.", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        });
            }
        });
    }

    public void setCustomer(String position, HashMap<String, Object> user) {
        ref.child("customers").child(position).setValue(user)
                .addOnSuccessListener(documentReference -> Log.d("REGISTER", "DocumentSnapshot added with ID: " + position))
                .addOnFailureListener(e -> Log.w("REGISTER", "Error adding document", e));
    }

    public AlertDialog setProgressDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCancelable(false);
        ImageView gif = new ImageView(this);
        Glide.with(this).asGif().load(R.drawable.loading).placeholder(R.drawable.loading).into(gif);
        dialog.setView(gif);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}