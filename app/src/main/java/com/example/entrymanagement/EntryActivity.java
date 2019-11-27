package com.example.entrymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EntryActivity extends AppCompatActivity {

    Button b1;
    Button b2;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        txt = findViewById(R.id.textView3);
        final String enter = " You're Welcome!! ";
        final String exit = " Thanks For Your Visit!! ";
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txt.setText(enter);

                    //Thread.sleep(1000);
                    Intent intent = new Intent(EntryActivity.this, Check.class);
                    startActivity(intent);

                }


        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt.setText(exit);

                //Thread.sleep(2000);
                Intent intent = new Intent(EntryActivity.this, Checkout.class);
                startActivity(intent);

            }
        });

    }

}
