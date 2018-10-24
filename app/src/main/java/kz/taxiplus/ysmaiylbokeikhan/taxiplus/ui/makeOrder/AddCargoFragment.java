package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.AccessPrice;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddCargoFragment extends Fragment implements OnDateSetListener {
    public static final String TAG = Constants.ADDCARGOFRAGMENT;

    private long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private long selectedDate = 0;

    private EditText fromText, toText,priceText, commentText;
    private TextView dateText;
    private Button addButton;
    private ImageButton backIcon;
    private ProgressBar progressBar;

    private Calendar mCalendar;
    private TimePickerDialog datePickerDialog;
    private CompositeSubscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_cargo, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        backIcon = view.findViewById(R.id.fac_back);
        fromText = view.findViewById(R.id.fac_from_edittext);
        toText = view.findViewById(R.id.fac_to_edittext);
        priceText = view.findViewById(R.id.fac_price_text);
        commentText = view.findViewById(R.id.fac_comment_text);
        dateText = view.findViewById(R.id.fac_date_text);
        addButton = view.findViewById(R.id.fac_make_order_button);
        progressBar = view.findViewById(R.id.fac_progressbar);

        setListeners();
    }

    private void setListeners() {
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                datePickerDialog = new TimePickerDialog.Builder()
                        .setCallBack(AddCargoFragment.this)
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fromText.getText().toString().isEmpty() && !toText.getText().toString().isEmpty() &&
                        !priceText.getText().toString().isEmpty() && selectedDate != 0 && !commentText.getText().toString().isEmpty()){
                    addCargo(fromText.getText().toString(), toText.getText().toString(), priceText.getText().toString(),
                            selectedDate, commentText.getText().toString());
                }else {
                    Toast.makeText(getContext(), getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addCargo(String start, String end, String price, long date, String comment){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .addCargo(Utility.getToken(getContext()), "2", price, date, start, end,comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAdd, this::handleErrorAdd));
    }

    private void handleResponseAdd(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            Toast.makeText(getContext(), getResources().getText(R.string.order_added), Toast.LENGTH_SHORT).show();
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
            if(!fromText.getText().toString().isEmpty() && !toText.getText().toString().isEmpty() &&
                    !priceText.getText().toString().isEmpty() && selectedDate != 0 && !commentText.getText().toString().isEmpty()){
                addCargo(fromText.getText().toString(), toText.getText().toString(), priceText.getText().toString(),
                        selectedDate, commentText.getText().toString());
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
        message.setText(getResources().getText(R.string.no_access_message_publish) + " " +accessPrice.getHour_price() + " тг.");
        Glide.with(getContext()).load(getResources().getDrawable(R.drawable.icon_error)).into(image);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyAccess("2", "1");
                dialog.dismiss();
            }
        });

        dialog.show();
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
