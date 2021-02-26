package dedesktop.br.ioaccess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FuncionarioActivity extends AppCompatActivity {
    private TextView txtDadosFunc;
    private Button btnVerde, btnAmarelo, btnVermelho;
    String humor_atual;
    String humor_entrada;
    String id_exp;
    Timestamp tempo_entrada;
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
                humor_atual = "verde";
            }
        });
        btnVermelho.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                humor_atual = "vermelho";
            }
        });
        btnAmarelo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                humor_atual = "amarelo";
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

                                if(document.get("DateTime_Saida") != null && ((Timestamp)document
                                        .get("DateTime_Saida")).toDate().after(FuncionarioEscaneado.getUltima_saida().toDate()) &&
                                        ((String) document.get("ID_Funcionario")).equals(FuncionarioEscaneado.getId())){
                                    /*Toast toastt = Toast.makeText(getApplicationContext(),
                                            "data de saida atualizada",
                                            Toast.LENGTH_LONG);
                                    toastt.show();*/
                                    FuncionarioEscaneado.setUltima_saida((Timestamp)document.get("DateTime_Saida"));
                                }



                                    if (((String) document.get("ID_Funcionario")).equals(FuncionarioEscaneado.getId())) {
                                        if(document.get("DateTime_Saida") == null){
                                            isEntrada = false;
                                            humor_entrada = (String)document.get("HumorEntrada");
                                            tempo_entrada = (Timestamp)document.get("DateTime_Entrada");
                                            id_exp = document.getId();

                                            DocumentReference _docRef = db.collection("Tabela_Funcionario")
                                                    .document(FuncionarioEscaneado.getId());

                                            _docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot _document = task.getResult();
                                                        if (_document.exists()) {

                                                            long hrs_trabalhadas =
                                                                    (((long)(Calendar.getInstance().getTime().getTime()
                                                                    - tempo_entrada.toDate().getTime()))
                                                                    /(3600000));
                                                            long hrs_expediente =  (long)_document.get("Expediente");

                                                            CollectionReference expediente = db.collection("Tabela_Expediente");

                                                            Map<String, Object> exp_atual = new HashMap<>();
                                                            exp_atual.put("DateTime_Saida", Calendar.getInstance().getTime());
                                                            exp_atual.put("HumorSaida", humor_atual);
                                                            exp_atual.put("HumorEntrada", humor_entrada);
                                                            exp_atual.put("DateTime_Entrada", tempo_entrada);
                                                            exp_atual.put("ID_Funcionario", FuncionarioEscaneado.getId());


                                                            if(hrs_trabalhadas > hrs_expediente){
                                                                Toast toastt = Toast.makeText(getApplicationContext(),
                                                                        "Realizou "
                                                                                + (hrs_trabalhadas - hrs_expediente) +
                                                                                " hora(s) de hora extra",
                                                                        Toast.LENGTH_LONG);
                                                                toastt.show();

                                                                exp_atual.put("HoraExtra_Justificativa", "Realizou "+(hrs_trabalhadas - hrs_expediente)+" hora(s) de hora extra, Aguardando justificativa");
                                                            }

                                                            expediente.document(id_exp).update(exp_atual);
                                                            Toast toastt = Toast.makeText(getApplicationContext(),
                                                                    "Expiente de hoje fechado",
                                                                    Toast.LENGTH_LONG);
                                                            toastt.show();

                                                            FuncionarioEscaneado.setUltima_saida(new Timestamp(new Date(1)));
                                                            FuncionarioEscaneado.setId("");
                                                            FuncionarioEscaneado.setGestor("");
                                                            FuncionarioEscaneado.setDados("");
                                                            finish();
                                                        } else {
                                                            //possivel msg de erro
                                                        }
                                                    } else {
                                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                                "DEU RUIM DNV",
                                                                Toast.LENGTH_LONG);
                                                        toast.show();
                                                        //Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                        }
                                    }

                            }
                            if(isEntrada){
                                db.collection("Tabela_Configs").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    long descanso_devido = (long)task.getResult()
                                                            .getDocuments().get(0).get("Descanso_Global");
                                                    long descanso_real =
                                                            (((long)(Calendar.getInstance().getTime().getTime()
                                                                    - FuncionarioEscaneado.getUltima_saida().toDate().getTime()))
                                                                    /(3600000));
                                                    if(descanso_real < descanso_devido ){
                                                        Toast toastt = Toast.makeText(getApplicationContext(),
                                                                "CATRACA BLOQUEADA \nDescanso esperado: " + descanso_devido + "\nDescanso feito: " + descanso_real,
                                                                Toast.LENGTH_LONG);
                                                        toastt.show();
                                                    } else{

                                                        CollectionReference expediente = db.collection("Tabela_Expediente");

                                                        Map<String, Object> exp_atual = new HashMap<>();
                                                        exp_atual.put("DateTime_Entrada", Calendar.getInstance().getTime());
                                                        exp_atual.put("HumorEntrada", humor_atual);
                                                        exp_atual.put("ID_Funcionario", FuncionarioEscaneado.getId());
                                                        expediente.document().set(exp_atual);
                                                        Toast toastt = Toast.makeText(getApplicationContext(),
                                                                "SALVO",
                                                                Toast.LENGTH_LONG);
                                                        toastt.show();
                                                        FuncionarioEscaneado.setUltima_saida(new Timestamp(new Date(1)));
                                                        FuncionarioEscaneado.setId("");
                                                        FuncionarioEscaneado.setGestor("");
                                                        FuncionarioEscaneado.setDados("");
                                                        finish();
                                                    }
                                                } else {
                                                    //possivel msg de erro
                                                }
                                            }
                                        });

                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}