package dedesktop.br.ioaccess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.logging.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class QrActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        // Set the scanner view as the content view
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(QrActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(
                    QrActivity.this,
                    new String[] {Manifest.permission.CAMERA},
                    1);
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Prints scan results
        //Logger.verbose("result", rawResult.getText());
        // Prints the scan format (qrcode, pdf417 etc.)
        //Logger.verbose("result", rawResult.getBarcodeFormat().toString());
        //If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
        //Intent intent = new Intent();
        //intent.putExtra(AppConstants.KEY_QR_CODE, rawResult.getText());
        //setResult(RESULT_OK, intent);

        DocumentReference docRef = db.collection("Tabela_Funcionario").document(rawResult.getText());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Dados: " + document.getData(),
                                Toast.LENGTH_LONG);
                        toast.show();
                        FuncionarioEscaneado.setDados("Dados: " + document.getData());
                        startActivity(new Intent(QrActivity.this, FuncionarioActivity.class));
                       // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "registro n encontrado",
                                Toast.LENGTH_LONG);
                        toast.show();
                      //  Log.d(TAG, "No such document");
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

        finish();
    }
}