package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.authorization;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.CitiesResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.SelectCityFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

public class AuthThirdStepFragment extends Fragment {
    public static final String TAG = Constants.AUTHTHIRDEPFRAGMENTTAG;

    private String phone;
    private User user;
    private CitiesResponse.City selectedCity;

    private EditText nameEditText;
    private TextView cityTextView;
    private Button nextButton;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_third_step, container, false);
        initViews(view);
        Paper.init(getContext());
        return view;
    }

    private void initViews(View view) {
        phone = getArguments().getString(Constants.PHONE);

        subscription = new CompositeSubscription();
        nameEditText = view.findViewById(R.id.fats_name_edittext);
        cityTextView = view.findViewById(R.id.fats_city_text);
        nextButton = view.findViewById(R.id.fats_next_button);
        progressBar = view.findViewById(R.id.fats_progressbar);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditText.getText().length() > 0 && selectedCity != null){
                    authThirdStep(phone, nameEditText.getText().toString(), selectedCity.getId());
                }else {
                    Toast.makeText(getContext(),getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();

                SelectCityFragment selectCityFragment = new SelectCityFragment();
                selectCityFragment.setTargetFragment(AuthThirdStepFragment.this, Constants.SELECTCITYFROMREGISTER);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.add(R.id.auth_container, selectCityFragment, SelectCityFragment.TAG);
                fragmentTransaction.addToBackStack(SelectCityFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    private void authThirdStep(String phone, String name, String city_id){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .authThirdStep(phone, name, city_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        progressBar.setVisibility(View.GONE);

        if(response.getState().equals("success")){
            user = new User();
            user.setName(nameEditText.getText().toString());
            user.setPhone(phone);
            user.setRole_id("1");
            user.setToken(response.getToken());
            user.setSessionOpened(false);
            user.setSelectedCity(selectedCity);

            changeFireBaseToken(response.getToken(), Paper.book().read(Constants.FIREBASE_TOKEN, ""));

            Paper.book().write(Constants.USER, user);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }
    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void changeFireBaseToken(String token, String push_id) {
        subscription.add(NetworkUtil.getRetrofit()
                .sendFirebasePush(token, push_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseChange, this::handleErrorChange));
    }

    private void handleResponseChange(Response response) {
    }

    private void handleErrorChange(Throwable throwable) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.SELECTCITYFROMREGISTER) {
                selectedCity = data.getParcelableExtra(Constants.SELECTEDCITY);
                cityTextView.setText(selectedCity.getCname());
            }
        }
    }
}
