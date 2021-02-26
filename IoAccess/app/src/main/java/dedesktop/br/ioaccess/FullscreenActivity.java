package dedesktop.br.ioaccess;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.app.PendingIntent.getActivity;


public class FullscreenActivity extends AppCompatActivity {


    private View mContentView;
    private TextView txtToogle;
    private Guideline lineO, lineB, lineR;
    private float perctO = 0.54f, perctB = 0.65f, perctR = 0.76f;
    private boolean sobeO = false, sobeB = true, sobeR = true;
    private Handler handler = new Handler();
    final Animation in = new AlphaAnimation(0.0f, 1.0f);
    final Animation out = new AlphaAnimation(1.0f, 0.0f);


    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mContentView = findViewById(R.id.fullscreen_content);
        txtToogle = (TextView) findViewById(R.id.textView);
        lineO = (Guideline)findViewById(R.id.guideline5);
        lineB = (Guideline)findViewById(R.id.guideline6);
        lineR = (Guideline)findViewById(R.id.guideline7);

        in.setDuration(1200);
        out.setDuration(1200);
        in.setAnimationListener(new AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                txtToogle.startAnimation(out);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }


        });
        out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                txtToogle.startAnimation(in);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        txtToogle.startAnimation(in);


        Runnable r = new Runnable() {
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run () {
                        perctO = attPerct(perctO, sobeO);
                        sobeO = attSobe(perctO, sobeO);

                        perctB = attPerct(perctB, sobeB);
                        sobeB = attSobe(perctB, sobeB);

                        perctR = attPerct(perctR, sobeR);
                        sobeR = attSobe(perctR, sobeR);

                        lineO.setGuidelinePercent(perctO);
                        lineB.setGuidelinePercent(perctB);
                        lineR.setGuidelinePercent(perctR);

                    }
                });

                handler.postDelayed(this, 16);
            }
        };

        Thread thread = new Thread(r);
        thread.start();
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


    }


    private  boolean attSobe(float per, boolean sobe){
        if(per >= 0.76f || per <= 0.54f)
            return !sobe;
        else
            return sobe;
    }

    private float attPerct(float per, boolean sobe){
        if(sobe)
            return (per - 0.00075f);
        else
            return (per + 0.00075f);
    }


    private void toggle() {
        db.collection("Tabela_Gestor")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            startActivity(new Intent(FullscreenActivity.this, QrActivity.class));
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "DEU RUIM",
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });

    }
}
