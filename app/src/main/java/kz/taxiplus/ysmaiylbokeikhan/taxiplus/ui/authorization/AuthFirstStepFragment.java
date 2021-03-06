package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.authorization;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.AgreementFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AuthFirstStepFragment extends Fragment {
    public static final String TAG = Constants.AUTHFIRSTSTEPFRAGMENTTAG;

    private EditText phoneEditText;
    private TextView agreementText;
    private Button nextButton;
    private ProgressBar progressBar;
    private FragmentTransaction fragmentTransaction;
    private CompositeSubscription subscription;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_first_step, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        phoneEditText = view.findViewById(R.id.fafs_phone_edittext);
        nextButton = view.findViewById(R.id.fafs_next_button);
        agreementText = view.findViewById(R.id.fafs_user_agreement);
        progressBar = view.findViewById(R.id.fafs_progressbar);

        phoneEditText.clearFocus();
        setListeners();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneEditText.getText().length() == 17){
                    authFirstStep(setValidPhone(phoneEditText.getText().toString()));
                }else {
                    Toast.makeText(getContext(), getResources().getText(R.string.enter_correct_phone), Toast.LENGTH_SHORT).show();
                }
            }
        });

        phoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b && phoneEditText.getText().length() == 0){
                    phoneEditText.getText().insert(phoneEditText.getSelectionStart(), "+7");
                    phoneEditText.setSelection(phoneEditText.getText().length());
                }else {
                }
            }
        });

        phoneEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                phoneEditText.setSelection(phoneEditText.getText().length());
                return false;
            }
        });

        phoneEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                phoneEditText.onTouchEvent(event);
                phoneEditText.setSelection(phoneEditText.getText().length());
                return true;
            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                phoneEditText.setSelection(phoneEditText.getText().length());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.length()==3) {
                    if (!charSequence.toString().contains("(")) {
                        phoneEditText.setText(new StringBuilder(charSequence.toString()).insert(charSequence.toString().length() - 1, "(").toString());
                        phoneEditText.setSelection(phoneEditText.getText().length());
                    }
                }else if(charSequence.length()==7){
                    if (!charSequence.toString().contains(")")) {
                        phoneEditText.setText(new StringBuilder(charSequence.toString()).insert(charSequence.toString().length() - 1, ")").toString());
                        if(!phoneEditText.getText().toString().substring(charSequence.toString().length()-1).equals("-")) {
                            phoneEditText.setSelection(phoneEditText.getText().length());
                        }
                    }
                }else if(charSequence.toString().length() == 8 || charSequence.toString().length() == 12 || charSequence.toString().length() == 15){
                    if(!phoneEditText.getText().toString().substring(charSequence.toString().length()-1).equals("-")) {
                        phoneEditText.setText(new StringBuilder(charSequence.toString()).insert(charSequence.toString().length() - 1, "-").toString());
                        phoneEditText.setSelection(phoneEditText.getText().length());
                    }
                }
                phoneEditText.setSelection(phoneEditText.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        agreementText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                AgreementFragment agreementFragment = new AgreementFragment();

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.enter_from_top, R.anim.exit_to_bottom, R.anim.exit_to_top);
                fragmentTransaction.replace(R.id.auth_container, agreementFragment, AgreementFragment.TAG);
                fragmentTransaction.addToBackStack(AgreementFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    private void authFirstStep(String phone){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .authFirstStep(phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        progressBar.setVisibility(View.GONE);

        if(response.getCode() == 0){
            fragmentTransaction = getFragmentManager().beginTransaction();
            Bundle b = new Bundle();
            b.putString(Constants.PHONE, setValidPhone(phoneEditText.getText().toString()));

            AuthSecondStepFragment authSecondStepFragment = new AuthSecondStepFragment();
            authSecondStepFragment.setArguments(b);

            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.replace(R.id.auth_container, authSecondStepFragment, AuthSecondStepFragment.TAG);
            fragmentTransaction.addToBackStack(AuthSecondStepFragment.TAG);
            fragmentTransaction.commit();
        }
    }
    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private String setValidPhone(String phone){
        phone = phone.replace("+", "");
        phone = phone.replace("(", "");
        phone = phone.replace(")", "");
        phone = phone.replace("-", "");

        return phone;
    }
}
