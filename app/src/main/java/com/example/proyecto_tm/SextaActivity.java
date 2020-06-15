package com.example.proyecto_tm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class SextaActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private EditText etName;
    private EditText etMensaje;
    private ImageButton btnSend;

    private List<MensajeVO> lstMensaje;
    private AdapterRVMensajes mAdapterRVMensajes;

    private void setComponents(){
        rvMensajes=findViewById(R.id.rvMensajes);
        etName=findViewById(R.id.etName);
        etMensaje=findViewById(R.id.etMensaje);
        btnSend=findViewById(R.id.btnSend);

        lstMensaje= new ArrayList<>();
        mAdapterRVMensajes=new AdapterRVMensajes(lstMensaje);
        rvMensajes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvMensajes.setAdapter(mAdapterRVMensajes);
        rvMensajes.setHasFixedSize(true);


        FirebaseFirestore.getInstance().collection( collectionPath: "Chat")
            .addSnapshotListener(new EventListener<QuerySnapshot>(){

                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,@Nullable FirebaseFirestoreException e) {
                    for (DocumentChange mDocumentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (mDocumentChange.getType() == DocumentChange.Type.ADDED) {
                            lstMensaje.add(mDocumentChange.getDocument().toObject(MensajeVO.class));
                            mAdapterRVMensajes.notifyDataSetChanged();
                            rvMensajes.smoothScrollToPosition(lstMensaje.size());

                        }
                    }
                }
            });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.length() ==0 || etMensaje.length() ==0 )
                    return;

                MensajeVO mMensajeVo=new MensajeVO();
                mMensajeVo.setMessage(etMensaje.getText().toString());
                mMensajeVo.setName(etName.getText().toString());
                FirebaseFirestore.getInstance().collection().add(mMensajeVo);
                etMensaje.setText("");
                etName.setText("");
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sexta);
    }
}
