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
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class MyBalanceFragment extends Fragment {
    public static final String TAG = Constants.MYBALANCERAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageButton menuIcon;
    private CalendarView calendarView;
    private TextView tripsAmountText, coinsText;

    public static MyBalanceFragment newInstance(String param1, String param2) {
        MyBalanceFragment fragment = new MyBalanceFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_balance, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        menuIcon = view.findViewById(R.id.fmb_menu);
        calendarView = view.findViewById(R.id.fmb_calendarview);
        tripsAmountText = view.findViewById(R.id.fmb_trips_amount_text);
        coinsText = view.findViewById(R.id.fmb_coins_amount_text);

        calendarView.setDate(Calendar.getInstance().getTimeInMillis(),false,true);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar = new GregorianCalendar( year, month, dayOfMonth);

                Log.d("selectedDay", String.valueOf(calendar.getTimeInMillis()));
            }
        });
    }

}