package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.AccessPrice;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.CitiesResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DirectionResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.UserProfileFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.SelectCityFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

public class IntercityMakeOrderFragment extends Fragment implements OnDateSetListener {
    public static final String TAG = Constants.INTERCITYMAKEORDERFRAGMENT;

    private User user;
    private long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private long selectedDate = 0;

    private ProgressBar progressBar;
    private ImageButton backView;
    private TextView fromTextView, toTextView, dateText;
    private CompositeSubscription subscription;
    private EditText seatNumber, comment, price;
    private Button makeOrderButton;

    private Calendar mCalendar;
    private TimePickerDialog datePickerDialog;


    private CitiesResponse.City fromCity, toCity;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intercity_make_order, container, false);
        Paper.init(getContext());

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        fromTextView = view.findViewById(R.id.fimo_from_edittext);
        toTextView = view.findViewById(R.id.fimo_to_edittext);
        progressBar = view.findViewById(R.id.fi_progressbar);
        backView = view.findViewById(R.id.fs_back);
        seatNumber = view.findViewById(R.id.fiko_seats_text);
        price = view.findViewById(R.id.fiko_price_text);
        comment = view.findViewById(R.id.fiko_comment_text);
        dateText = view.findViewById(R.id.fiko_date_text);
        makeOrderButton = view.findViewById(R.id.fiko_make_order_button);

        user = Paper.book().read(Constants.USER);

        setCity();
        setListeners();
    }

    private void setListeners() {
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        fromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();

                SelectCityFragment selectCityFragment = new SelectCityFragment();
                selectCityFragment.setTargetFragment(IntercityMakeOrderFragment.this, Constants.FROMCITY);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.add(R.id.main_activity_frame, selectCityFragment, SelectCityFragment.TAG);
                fragmentTransaction.addToBackStack(SelectCityFragment.TAG);
                fragmentTransaction.commit();
            }
        });
        toTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();

                SelectCityFragment selectCityFragment = new SelectCityFragment();
                selectCityFragment.setTargetFragment(IntercityMakeOrderFragment.this, Constants.TOCITY);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.add(R.id.main_activity_frame, selectCityFragment, SelectCityFragment.TAG);
                fragmentTransaction.addToBackStack(SelectCityFragment.TAG);
                fragmentTransaction.commit();
            }
        });
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                datePickerDialog = new TimePickerDialog.Builder()
                        .setCallBack(IntercityMakeOrderFragment.this)
                        .setCancelStringId(getString(R.string.cancel))
                        .setSureStringId(getString(R.string.select))
                        .setTitleStringId(getString(R.string.date))
                        .setYearText("")
                        .setMonthText(getString(R.string.month))
                        .setDayText(getString(R.string.day))
                        .setHourText("")
                        .setMinuteText("")
                        .setCyclic(false)
                        .setCurrentMillseconds(mCalendar.getTimeInMillis())
                        .setMinMillseconds(System.currentTimeMillis())
                        .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                        .setCurrentMillseconds(System.currentTimeMillis())
                        .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                        .setType(Type.MONTH_DAY_HOUR_MIN)
                        .setWheelItemTextNormalColor(getResources().getColor(R.color.colorPrimary))
                        .setWheelItemTextSelectorColor(getResources().getColor(R.color.black))
                        .setWheelItemTextSize(15)
                        .build();

                datePickerDialog.show(getFragmentManager(), "month_day_hour_minute");
            }
        });
        makeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fromCity != null && toCity!= null && !seatNumber.getText().toString().isEmpty() &&
                        !price.getText().toString().isEmpty() && selectedDate != 0){
                    addIntercityOrder(seatNumber.getText().toString(), price.getText().toString(), selectedDate, comment.getText().toString());
                }else {
                    Toast.makeText(getContext(), getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setCity() {
        try {
            fromCity = user.getSelectedCity();
            fromTextView.setText(fromCity.getCname());
        }catch (Exception e){}
    }

    private void addIntercityOrder(String seats, String price, long date, String comment){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .addIntercityOrder(Utility.getToken(getContext()), "1", seats, fromCity.getId(), toCity.getId(), price, date, comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAdd, this::handleErrorAdd));
    }

    private void handleResponseAdd(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            Toast.makeText(getContext(), getResources().getText(R.string.wait_drivers), Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }else if(response.getState().equals("do not have access")){
            haveNotAccessView(response.getPrice());
        }
    }

    private void handleErrorAdd(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void buyAccess(String type, String accessType){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .buyAccess(Utility.getToken(getContext()),type, accessType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseBuy, this::handleErrorBuy));
    }

    private void handleResponseBuy(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            if(fromCity != null && toCity!= null && !seatNumber.getText().toString().isEmpty() &&
                    !price.getText().toString().isEmpty() && selectedDate != 0){
                addIntercityOrder(seatNumber.getText().toString(), price.getText().toString(), selectedDate, comment.getText().toString());
            }else {
                Toast.makeText(getContext(), getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(), getResources().getText(R.string.not_enought_balance), Toast.LENGTH_LONG).show();
        }
    }

    private void handleErrorBuy(Throwable throwable) {
    }

    private void haveNotAccessView(AccessPrice accessPrice){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button payButton = (Button) dialog.findViewById(R.id.cdv_close_button);
        TextView message = (TextView) dialog.findViewById(R.id.cdv_title_text);
        ImageView image = (ImageView) dialog.findViewById(R.id.cdv_image);


        payButton.setText(getResources().getText(R.string.pay));
        message.setText(getResources().getText(R.string.no_access_message) + accessPrice.getHour_price() + " тг.");
        Glide.with(getContext()).load(getResources().getDrawable(R.drawable.icon_error)).into(image);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyAccess("1", "1");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.FROMCITY) {
                fromCity = data.getParcelableExtra(Constants.SELECTEDCITY);
                fromTextView.setText(fromCity.getCname());
            }else if(requestCode == Constants.TOCITY){
                toCity = data.getParcelableExtra(Constants.SELECTEDCITY);
                toTextView.setText(toCity.getCname());
            }
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        this.selectedDate = millseconds;
        String text = getDateToString(millseconds);
        dateText.setText(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm, dd MMM");
        return sf.format(d);
    }
}
