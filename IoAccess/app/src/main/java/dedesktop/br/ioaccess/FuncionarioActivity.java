package dedesktop.br.ioaccess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.util.HashMap;
import java.util.Map;

public class FuncionarioActivity extends AppCompatActivity {
    private TextView txtDadosFunc;
    private Button btnVerde, btnAmarelo, btnVermelho;
    String humor;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcionario);


        FuncionarioEscaneado fs = new FuncionarioEscaneado(FuncionarioEscaneado.getDados());


        txtDadosFunc = (TextView)findViewById(R.id.txtDadosFunc);
        btnVerde = (Button)findViewById(R.id.btnVerde);
        btnAmarelo = (Button)findViewById(R.id.btnAmarelo);
        btnVermelho = (Button)findViewById(R.id.btnVermelho);


        txtDadosFunc.setText("Nome: " + fs.nome + "\nRA: " + fs.ra_cracha + "\nGestor: " + FuncionarioEscaneado.getGestor() +
                "\nExpediente: " + fs.expediente + " horas");

        btnVerde.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                humor = "verde";
            }
        });
        btnVermelho.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                humor = "vermelho";
            }
        });
        btnAmarelo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                humor = "amarelo";
            }
        });

    }

    public void inserirResultado(View v){

        db.collection("Tabela_Expediente")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean isEntrada = true;
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if(Timestamp.now().toDate().getDate()==
                                        ((Timestamp)document.get("DateTime_Entrada")).toDate().getDate()){

                                    if(((String)document.get("ID_Funcionario")).equals(FuncionarioEscaneado.getId()) &&
                                    document.get("DateTime_Saida") == null){
                                        CollectionReference expediente = db.collection("Tabela_Expediente");

                                        Map<String, Object> exp_atual = new HashMap<>();
                                        exp_atual.put("DateTime_Saida", Calendar.getInstance().getTime());
                                        exp_atual.put("HumorSaida", humor);
                                        exp_atual.put("HumorEntrada", document.get("HumorEntrada"));
                                        exp_atual.put("DateTime_Entrada", document.get("DateTime_Entrada"));
                                        exp_atual.put("ID_Funcionario", FuncionarioEscaneado.getId());
                                        expediente.document(document.getId()).update(exp_atual);
                                        Toast toastt = Toast.makeText(getApplicationContext(),
                                                "Expiente de hoje fechado",
                                                Toast.LENGTH_LONG);
                                        toastt.show();
                                        isEntrada = false;
                                    }
                                }
                               // Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            if(isEntrada){
                                CollectionReference expediente = db.collection("Tabela_Expediente");

                                Map<String, Object> exp_atual = new HashMap<>();
                                exp_atual.put("DateTime_Entrada", Calendar.getInstance().getTime());
                                exp_atual.put("HumorEntrada", humor);
                                exp_atual.put("ID_Funcionario", FuncionarioEscaneado.getId());
                                expediente.document().set(exp_atual);
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        Toast toastt = Toast.makeText(getApplicationContext(),
                "SALVO",
                Toast.LENGTH_LONG);
        toastt.show();
    }
}