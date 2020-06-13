package com.example.proyecto_tm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickRegistro(View view) {
        Intent Registro = new Intent(this,SegundaActivity.class);
        startActivity(Registro);
    }

    public void onClickLogin(View view) {
        startActivity(new Intent("com.example.TerceraActivity"));
    }
}
