package dedesktop.br.ioaccess;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FuncionarioEscaneado {
    static String dados;

    public static String getDados() {
        return dados;
    }

    public static void setDados(String val) {
        dados = val;
    }

    String nome;
    String gestor;
    int expediente;
    String ra_cracha;

    public FuncionarioEscaneado(String dados) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String d[] = dados.split(",");
        this.ra_cracha = d[0].substring(d[0].indexOf('='));
        this.nome = d[1].substring(d[1].indexOf('='));
        final String[] nome_ges = {""};
        DocumentReference docRef = db.collection("Tabela_Gestor")
                .document(d[2].substring(d[2].indexOf('=')));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       nome_ges[0] = document.getData().toString().substring(
                               document.getData().toString().indexOf('='),
                               document.getData().toString().indexOf(',')
                               );
                    }
                }
            }
        });
        this.gestor = nome_ges[0];
        this.expediente =  Integer.parseInt(d[3].substring(d[3].indexOf('=')));
    }
}
