package kz.taxiplus.ysmaiylbokeikhan.taxiplus;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.RemoteMessage;

import io.fabric.sdk.android.Fabric;
import java.util.HashMap;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.BaseActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class SplashActivity extends BaseActivity {

    private RemoteMessage remoteMessage;
    private String str = "not";
    private int animCounter = 1;

    private View first, second, third, fourth, fifth, sixth, seventh;
    private Animation anim;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Paper.init(SplashActivity.this);
        setContentView(R.layout.activity_splash);

        initViews();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(Paper.book().read(Constants.USER) == null){
                    intent = new Intent(SplashActivity.this, AuthActivity.class);
                    startActivity(intent);
                }else{
                    remoteMessage = getIntent().getParcelableExtra(Constants.PENDINGINTENTEXTRA);
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra(Constants.PENDINGINTENTEXTRA, remoteMessage);
                    startActivity(intent);
                }
                finish();
            }
        },3000);
    }

    private void initViews(){
        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.forth);
        fifth = findViewById(R.id.fifth);
        sixth = findViewById(R.id.sixth);
        seventh = findViewById(R.id.seventh);

        anim = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAnims();
                first.startAnimation(anim);
            }
        }, 200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAnims();
                second.startAnimation(anim);
            }
        }, 600);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAnims();
                third.startAnimation(anim);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAnims();
                fourth.startAnimation(anim);
            }
        }, 1400);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAnims();
                fifth.startAnimation(anim);
            }
        }, 1800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAnims();
                sixth.startAnimation(anim);
            }
        }, 2200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAnims();
                seventh.startAnimation(anim);
            }
        }, 2600);
    }

    private void clearAnims(){
        first.clearAnimation();
        second.clearAnimation();
        third.clearAnimation();
        fourth.clearAnimation();
        fifth.clearAnimation();
        sixth.clearAnimation();
        seventh.clearAnimation();
    }
}
