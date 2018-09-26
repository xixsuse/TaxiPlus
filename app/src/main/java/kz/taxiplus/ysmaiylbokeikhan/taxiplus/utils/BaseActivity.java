package kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;

public class BaseActivity extends AppCompatActivity {
    private final static int THEME_DAY = 1;
    private final static int THEME_NIGHT = 2;

    @Override
    protected void onPause() {
        super.onPause();
        Application.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.activityResumed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        updateTheme();
    }
    public void updateTheme() {
        if (Utility.getTheme(getApplicationContext()) <= THEME_DAY) {
            setTheme(R.style.AppTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }
        } else if (Utility.getTheme(getApplicationContext()) == THEME_NIGHT) {
            setTheme(R.style.AppThemeDark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark));
            }
        }
    }

    public void recreateActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
