package com.example.proyecto_tm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CuartaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuarta);
    }


    public void OnClickCapacitacion(View view){
        Intent Capacitacion = new Intent(this,QuintaActivity.class);
        startActivity(Capacitacion);
    }
    public void OnClickChat(View view) {
        Intent Chat = new Intent(this, SextaActivity.class);
        startActivity(Chat);
    }
}
