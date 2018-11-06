package kz.taxiplus.ysmaiylbokeikhan.taxiplus;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.authorization.AuthFirstStepFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.BaseActivity;

public class AuthActivity extends BaseActivity {

    private FragmentTransaction fragmentTransaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        openFirstStep();
    }

    private void openFirstStep() {
        AuthFirstStepFragment authFirstStepFragment = new AuthFirstStepFragment();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.auth_container, authFirstStepFragment, AuthFirstStepFragment.TAG);
        fragmentTransaction.addToBackStack(AuthFirstStepFragment.TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 1){
            super.onBackPressed();
        }else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
