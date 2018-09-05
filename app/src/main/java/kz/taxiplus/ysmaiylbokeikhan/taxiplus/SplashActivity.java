package kz.taxiplus.ysmaiylbokeikhan.taxiplus;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.BaseActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(SplashActivity.this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if(Paper.book().read(Constants.USER) == null){
                    intent = new Intent(SplashActivity.this, AuthActivity.class);
                    startActivity(intent);
                }else{
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },1000);
    }
}
