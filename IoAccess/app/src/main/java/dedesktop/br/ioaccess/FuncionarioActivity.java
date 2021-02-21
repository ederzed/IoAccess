package dedesktop.br.ioaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class FuncionarioActivity extends AppCompatActivity {
    private TextView txtDadosFunc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcionario);

        FuncionarioEscaneado fs = new FuncionarioEscaneado(FuncionarioEscaneado.getDados());
        txtDadosFunc = (TextView)findViewById(R.id.txtDadosFunc);

        txtDadosFunc.setText("Nome: " + fs.nome + "\nRA: " + fs.ra_cracha + "\nGestor: " + fs.gestor +
                "\nExpediente: " + fs.expediente + " horas");

    }
}