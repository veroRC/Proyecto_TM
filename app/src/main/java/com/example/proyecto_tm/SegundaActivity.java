package com.example.proyecto_tm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

public class SegundaActivity extends AppCompatActivity {
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected static final int TIMER_RUNTIME = 10000;
    protected boolean nbActivo;
    protected ProgressBar nProgressBar;
    DatabaseReference mRootReference;
    Button mButtonSubirDatosFirebase;
    EditText mEditTextDatoNombreyApellidoUsuario, mEditTextDatoDNIUsuario, mEditTextTelefonoUsuario, mEditTextCorreoUsuario, mEditTextContrasenaUsuario;
    ImageView mFotoPersona;
    private String Document_img1="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.segundaactividad);
        FirebaseApp.initializeApp(this);
        mFotoPersona = findViewById(R.id.etFotoPersona);
        mButtonSubirDatosFirebase = findViewById(R.id.btnSubirDatos);
        mEditTextDatoNombreyApellidoUsuario = findViewById(R.id.etNombreyApellidos);
        mEditTextDatoDNIUsuario = findViewById(R.id.etDNI);
        mEditTextTelefonoUsuario = findViewById(R.id.etTelefono);
        mEditTextCorreoUsuario = findViewById(R.id.etCorreo);
        mEditTextContrasenaUsuario = findViewById(R.id.etContraseña);

        mRootReference = FirebaseDatabase.getInstance().getReference();

        mFotoPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if we have write permission
                int permission = ActivityCompat.checkSelfPermission(SegundaActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            SegundaActivity.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                } else {
                    selectImage();
                }
            }
        });

        mButtonSubirDatosFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NombreyApellido = mEditTextDatoNombreyApellidoUsuario.getText().toString();
                String DNI = mEditTextDatoDNIUsuario.getText().toString();
                int Telefono = Integer.parseInt(mEditTextTelefonoUsuario.getText().toString());
                String Correo = mEditTextCorreoUsuario.getText().toString();
                String Contrasena = mEditTextContrasenaUsuario.getText().toString();

                Map<String, Object> datosUsuario = new HashMap<>();
                datosUsuario.put("Nombre y Apellido", NombreyApellido);
                datosUsuario.put("DNI", DNI);
                datosUsuario.put("Telefono", Telefono);
                datosUsuario.put("Correo", Correo);
                datosUsuario.put("Contraseña", Contrasena);


                mRootReference.child("Usuario").push().setValue(datosUsuario);
                }
        });

        nProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        final Thread timerThread = new Thread() {
            @Override
            public void run() {
                nbActivo = true;
                try {
                    int espera = 0;
                    while (nbActivo && (espera < TIMER_RUNTIME)) {
                        sleep(200);
                        if (nbActivo) {
                            espera += 200;
                            actualizarProgress(espera);
                        }
                    }
                } catch (InterruptedException e) {
                } finally {
                    onContinuar();
                }
            }
        };
        timerThread.start();
    }

    public void actualizarProgress(final int timePassed){
        if(null != nProgressBar){
            final int progress = nProgressBar.getMax() * timePassed
                    /TIMER_RUNTIME;
            nProgressBar.setProgress(progress);
        }
    }

    public void onContinuar(){
        Log.d("mensajeFinal", "Su barra de progreso acaba de cargar");
    }

    public void onClickAtras(View view) {
        Intent Atras = new Intent(this,MainActivity.class);
        startActivity(Atras);
    }

    private void selectImage() {
        final CharSequence[] options = { "Elegir desde galeria","Cancelar" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SegundaActivity.this);
        builder.setTitle("Agregar Foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Elegir desde galeria")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImage = data.getData();
                Log.println(Log.INFO, "uri img",selectedImage.toString());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mFotoPersona.setImageBitmap(bitmap);
                /*
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                thumbnail=getResizedBitmap(thumbnail, 400);
                //Log.w("path of image from gallery......******************.........", picturePath+"");
                mFotoPersona.setImageBitmap(thumbnail);
                BitMapToString(thumbnail);
                */
            }
        }
    }
    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        Document_img1 = Base64.encodeToString(b, Base64.DEFAULT);
        return Document_img1;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
