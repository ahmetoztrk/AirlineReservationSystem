package me.fix3dll.airlinereservation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DatabaseReference ref = FirebaseDatabase.getInstance("https://charged-garden-192121-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    SwitchCompat resSwitch;
    boolean switchStatus = false;
    static Button[] buttons;
    static ArrayList<String> bookedSeats;
    static ArrayList<String> freeSeats;
    AlertDialog gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttons = new Button[] {
                findViewById(R.id.a1), findViewById(R.id.a2), findViewById(R.id.a3), findViewById(R.id.a4), findViewById(R.id.a5), findViewById(R.id.a6), findViewById(R.id.a7)
                , findViewById(R.id.b1), findViewById(R.id.b2), findViewById(R.id.b3), findViewById(R.id.b4), findViewById(R.id.b5), findViewById(R.id.b6), findViewById(R.id.b7)
                , findViewById(R.id.c1), findViewById(R.id.c2), findViewById(R.id.c3), findViewById(R.id.c4), findViewById(R.id.c5), findViewById(R.id.c6), findViewById(R.id.c7)
                , findViewById(R.id.d1), findViewById(R.id.d2), findViewById(R.id.d3), findViewById(R.id.d4), findViewById(R.id.d5), findViewById(R.id.d6), findViewById(R.id.d7)
                , findViewById(R.id.e1), findViewById(R.id.e2), findViewById(R.id.e3), findViewById(R.id.e4), findViewById(R.id.e5), findViewById(R.id.e6), findViewById(R.id.e7)
                , findViewById(R.id.f1), findViewById(R.id.f2), findViewById(R.id.f3), findViewById(R.id.f4), findViewById(R.id.f5), findViewById(R.id.f6), findViewById(R.id.f7)
        };

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.title);
            actionBar.setTitle("");
        }

        Bundle bundle = getIntent().getExtras();
        bookedSeats = bundle.getStringArrayList("bookedArray");
        freeSeats = bundle.getStringArrayList("freeArray");

        int i = 0;
        for (char ch = 'A'; ch <= 'F'; ch++) {
            for (int j = 1; j <= 7; j++) {
                String position = ch + "" + j;
                buttons[i].setOnClickListener(view -> {
                    if (switchStatus) {
                        gif = setProgressDialog();
                        gif.show();

                        ref.child("customers").child(position).child("id")
                                .get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult().getValue() != null) {
                                        openCancellationActivity(position, task.getResult().getValue().toString());
                                        gif.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext()
                                                ,"İptal işleminde hata. Tekrar deneyin."
                                                , Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        openRegisterForm(position);
                    }
                });
                i++;
            }
        }

        for (String seat : bookedSeats) {
            switchButtonClickability(seat, false);
        }

        resSwitch = findViewById(R.id.resSwitch);
        resSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                resSwitch.setText(R.string.switch_on);
                // Booked seats set to clickable
                for (String seat : bookedSeats) {
                    switchButtonClickability(seat, true);
                }
                // Free seats set to not clickable
                for (String seat : freeSeats) {
                    switchButtonClickability(seat, false);
                }
                switchStatus = true;
            } else {
                resSwitch.setText(R.string.switch_off);
                // Booked seats set to not clickable
                for (String seat : bookedSeats) {
                    switchButtonClickability(seat, false);
                }
                // Free seats set to clickable
                for (String seat : freeSeats) {
                    switchButtonClickability(seat, true);
                }
                switchStatus = false;
            }
        });
    }

    public void openRegisterForm(String seat) {
        Intent toReg = new Intent(this, RegisterForm.class);
        toReg.putExtra("seat", seat);
        startActivity(toReg);
    }

    public void openCancellationActivity(String seat, String id) {
        Intent toCancel = new Intent(this, CancellationActivity.class);
        toCancel.putExtra("id", id);
        toCancel.putExtra("seat", seat);
        startActivity(toCancel);
    }

    public static void switchButtonClickability(String seat, boolean bool) {
        int i = 0;
        for (char ch = 'A'; ch <= 'F'; ch++) {
            for (int j = 1; j <= 7; j++) {
                String position = ch + "" + j;
                if (position.equals(seat)) {
                    buttons[i].setClickable(bool);
                    buttons[i].setBackgroundColor(bool ? Color.GRAY : Color.LTGRAY);
                    break;
                }
                i++;
            }
        }
    }

    public AlertDialog setProgressDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCanceledOnTouchOutside(false);
        ImageView gif = new ImageView(this);
        Glide.with(this).asGif().load(R.drawable.loading).placeholder(R.drawable.loading).into(gif);
        dialog.setView(gif);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    boolean clickedOnce = false;

    @Override
    public void onBackPressed() {
        if (clickedOnce) {
            this.finishAffinity();
            return;
        }

        this.clickedOnce = true;
        Toast.makeText(getApplicationContext()
                , "Çıkmak için lütfen tekrar tıklayın"
                , Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> clickedOnce = false, 2000);
    }
}