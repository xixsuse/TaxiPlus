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
import android.widget.Toast;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AuthSecondStepFragment extends Fragment {
    public static final String TAG = Constants.AUTHSECONDEPFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String phone;

    private EditText codeEditText;
    private Button nextButton;
    private ProgressBar progressBar;

    private FragmentTransaction fragmentTransaction;
    private CompositeSubscription subscription;

    public static AuthSecondStepFragment newInstance(String param1, String param2) {
        AuthSecondStepFragment fragment = new AuthSecondStepFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_second_step, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        phone = getArguments().getString(Constants.PHONE);
        codeEditText = view.findViewById(R.id.fass_code_edittext);
        nextButton = view.findViewById(R.id.fass_next_button);
        progressBar = view.findViewById(R.id.fass_progressbar);

        setListeners();
    }

    private void setListeners() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeEditText.getText().length() == 4){
                    authSecondStep(phone, codeEditText.getText().toString());
                }else {
                    Toast.makeText(getContext(), getResources().getText(R.string.enter_correct_code), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void authSecondStep(String phone, String code){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .authSecondStep(phone, code)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        progressBar.setVisibility(View.GONE);

        if(response.getState().equals("success")){
            if(response.getType().equals("0")){
                fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle b = new Bundle();
                b.putString(Constants.PHONE, phone);

                AuthThirdStepFragment authThirdStepFragment = new AuthThirdStepFragment();
                authThirdStepFragment.setArguments(b);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.auth_container, authThirdStepFragment, AuthThirdStepFragment.TAG);
                fragmentTransaction.addToBackStack(AuthThirdStepFragment.TAG);
                fragmentTransaction.commit();
            }else {
                User user = response.getUser();
                user.setSessionOpened(false);
                user.setSelectedCity(response.getCity());

                changeFireBaseToken(user.getToken(), Paper.book().read(Constants.FIREBASE_TOKEN, ""));
                Paper.book().write(Constants.USER, user);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        }
    }
    private void handleError(Throwable throwable) {

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

}
