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

    public static String getNome_ges() {
        return nome_ges;
    }

    public static void setNome_ges(String nome_ges) {
        FuncionarioEscaneado.nome_ges = nome_ges;
    }

    static String nome_ges;
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
        this.ra_cracha = d[0].substring(d[0].indexOf('=')+1);
        this.nome = d[2].substring(d[2].indexOf('=')+1);
        final String[] nome_ges = {""};
        DocumentReference docRef = db.collection("Tabela_Gestor")
                .document(d[1].substring(d[1].indexOf('=')+ 1));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       FuncionarioEscaneado.setNome_ges(document.getData().toString().substring(
                               document.getData().toString().lastIndexOf('=') + 1,
                               document.getData().toString().indexOf('}')
                               ));
                    }
                } else {
                    FuncionarioEscaneado.setNome_ges("DEU RUIM");
                }
            }
        });
        this.gestor = FuncionarioEscaneado.getNome_ges();
        this.expediente =  Integer.parseInt(d[3].substring(d[3].indexOf('=') + 1).replace("}",""));
    }
}
