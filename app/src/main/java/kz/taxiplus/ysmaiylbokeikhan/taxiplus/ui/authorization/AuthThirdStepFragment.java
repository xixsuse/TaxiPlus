package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.authorization;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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

public class AuthThirdStepFragment extends Fragment {
    public static final String TAG = Constants.AUTHTHIRDEPFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String phone;
    private User user;

    private EditText nameEditText;
    private Button nextButton;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;

    public static AuthThirdStepFragment newInstance(String param1, String param2) {
        AuthThirdStepFragment fragment = new AuthThirdStepFragment();
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
        View view = inflater.inflate(R.layout.fragment_auth_third_step, container, false);
        initViews(view);
        Paper.init(getContext());
        return view;
    }

    private void initViews(View view) {
        phone = getArguments().getString(Constants.PHONE);

        subscription = new CompositeSubscription();
        nameEditText = view.findViewById(R.id.fats_name_edittext);
        nextButton = view.findViewById(R.id.fats_next_button);
        progressBar = view.findViewById(R.id.fats_progressbar);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditText.getText().length() > 0){
                    authThirdStep(phone, nameEditText.getText().toString());
                }
            }
        });
    }

    private void authThirdStep(String phone, String name){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .authThirdStep(phone, name)
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

            changeFireBaseToken(response.getToken(), Paper.book().read(Constants.FIREBASE_TOKEN, ""));

            Paper.book().write(Constants.USER, user);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
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
