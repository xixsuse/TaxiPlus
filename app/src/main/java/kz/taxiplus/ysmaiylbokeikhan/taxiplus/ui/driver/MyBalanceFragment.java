package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DriverBalance;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.HistoryItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MyBalanceFragment extends Fragment {
    public static final String TAG = Constants.MYBALANCERAGMENTTAG;

    private ImageButton menuIcon;
    private ProgressBar progressBar;
    private MaterialCalendarView calendarView;
    private TextView tripsAmountText, coinsText;

    private CompositeSubscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_balance, container, false);
        initViews(view);

        getBalance(String.valueOf(System.currentTimeMillis()/1000));
        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        menuIcon = view.findViewById(R.id.fmb_menu);
        calendarView = view.findViewById(R.id.fmb_calendarview);
        tripsAmountText = view.findViewById(R.id.fmb_trips_amount_text);
        coinsText = view.findViewById(R.id.fmb_coins_amount_text);
        progressBar = view.findViewById(R.id.fmb_progressbar);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        calendarView.setSelectedDate(CalendarDay.today());

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                Calendar selectedCalndar = Calendar.getInstance();
                selectedCalndar.set(Calendar.YEAR, calendarDay.getYear());
                selectedCalndar.set(Calendar.MONTH, calendarDay.getMonth());
                selectedCalndar.set(Calendar.DAY_OF_MONTH, calendarDay.getDay());
                getBalance(String.valueOf(selectedCalndar.getTimeInMillis()));
            }
        });
    }

    private void getBalance(String date){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getBalanceHistory(Utility.getToken(getContext()), date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(DriverBalance response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")) {
            coinsText.setText(response.getSum());
            tripsAmountText.setText(response.getAmount());
        }
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }
}
