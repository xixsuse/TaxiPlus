package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.SessionPrices;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.TaxiPark;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class OpenSessionFragment extends Fragment {
    public static final String TAG = Constants.OPENSESSIONFRAGMENTTAG;

    private TextView balanceText, sixHoursText, unlimText, remainderText;
    private Button openButton;
    private RadioGroup radioGroup;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;
    private int time = 0;
    private String sixHourPrice, unlimPrice, balance, selectedPrice;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_session, container, false);
        Paper.init(getContext());

        initViews(view);
        getPrices();

        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        progressBar = view.findViewById(R.id.fos_progressbar);
        balanceText = view.findViewById(R.id.fos_coins_text);
        sixHoursText = view.findViewById(R.id.fos_six_hour_text);
        unlimText = view.findViewById(R.id.fos_unlim_text);
        remainderText = view.findViewById(R.id.fos_remainder_text);
        radioGroup = view.findViewById(R.id.fos_prices_radio_buttons);
        openButton = view.findViewById(R.id.fos_open_button);
        user = Paper.book().read(Constants.USER);

        balance = user.getBalance();
        balanceText.setText(balance);
        remainderText.setText(balance);

        if(user.isSessionOpened()){
            openButton.setText(getResources().getString(R.string.close_session_event));
        }else {
            openButton.setText(getResources().getString(R.string.open_session_event));
        }
        setListeners();
    }

    private void setListeners() {
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.isSessionOpened()){

                }else {
                    if(time != 0){
                        if(time == 12){
                            openSessionUnlim();
                        }else {
                            openSession();
                        }
                    }else {
                        Toast.makeText(getContext(), getResources().getString(R.string.select_time), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    switch (checkedId) {
                        case R.id.fos_six_hour_rabiobutton:
                            time = 6;
                            remainderText.setText(String.valueOf(Integer.valueOf(balance) - Integer.valueOf(sixHourPrice)));
                            selectedPrice = sixHourPrice;
                            break;

                        case R.id.fos_unlim_rabiobutton:
                            time = 12;
                            remainderText.setText(String.valueOf(Integer.valueOf(balance) - Integer.valueOf(unlimPrice)));
                            selectedPrice = unlimPrice;
                            break;
                    }
                }catch (Throwable e){}
            }
        });
    }

    //requests
    private void getPrices(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getSessionPrices(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePrice, this::handleErrorPrice));
    }

    private void handleResponsePrice(SessionPrices response) {
        if(response.getState().equals("success")){
            sixHourPrice = response.getSix_hours_price();
            unlimPrice = response.getUnlim_price();

            sixHoursText.setText(getResources().getString(R.string.six_hour) + "\n" + sixHourPrice + " тг");
            unlimText.setText(getResources().getString(R.string.unlim) + "\n" + unlimPrice + " тг");
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleErrorPrice(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void openSessionUnlim(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .startSession(Utility.getToken(getContext()), "12")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUnlim, this::handleErrorUnlim));
    }

    private void handleResponseUnlim(Response response) {
        if(response.getState().equals("success")){
            saveUserSession(true);
            Toast.makeText(getContext(), getResources().getString(R.string.successfully_opened), Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleErrorUnlim(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void openSession(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .startSession(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        if(response.getState().equals("success")){
            saveUserSession(true);
            Toast.makeText(getContext(), getResources().getString(R.string.successfully_opened), Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void saveUserSession(boolean sessionState){
        balance = String.valueOf(Integer.valueOf(balance) - Integer.valueOf(selectedPrice));
        user.setSessionOpened(sessionState);
        user.setBalance(balance);
        Paper.book().write(Constants.USER, user);
    }
}
