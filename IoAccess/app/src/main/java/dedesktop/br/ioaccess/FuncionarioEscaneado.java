package dedesktop.br.ioaccess;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class FuncionarioEscaneado {
    static String dados;
    static String id;

    public static Timestamp getUltima_saida() {
        return ultima_saida;
    }

    public static void setUltima_saida(Timestamp ultima_saida) {
        FuncionarioEscaneado.ultima_saida = ultima_saida;
    }

    static Timestamp ultima_saida;

    public static String getGestor() {
        return gestor;
    }

    public static void setGestor(String gestor) {
        FuncionarioEscaneado.gestor = gestor;
    }

    static String gestor;



    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        FuncionarioEscaneado.id = id;
    }

    public static String getDados() {
        return dados;
    }

    public static void setDados(String val) {
        dados = val;
    }

    String nome;
    int expediente;
    String ra_cracha;

    public FuncionarioEscaneado(String dados) {
        FuncionarioEscaneado.setUltima_saida(new Timestamp(new Date(1)));
        String d[] = dados.split(",");
        this.ra_cracha = d[0].substring(d[0].indexOf('=')+1);
        this.nome = d[1].substring(d[1].indexOf('=')+1);

        this.expediente =  Integer.parseInt(d[3].substring(d[3].indexOf('=') + 1).replace("}",""));
    }
}
