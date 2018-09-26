package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.MainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.OnSwipeTouchListener;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class MakeOrderFragment extends Fragment implements OnDateSetListener {
    public static final String TAG = Constants.MAKEORDERFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private boolean priceViewState = false;
    private boolean commentViewState = false;
    private boolean weightViewState = false;
    private boolean modelViewState = false;
    private Calendar mCalendar, orderCalendar;
    private int mode;
    private long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private long selectedDate = 0;
    private Place toAddress, fromAddress;

    private TimePickerDialog mDialogMonthDayHourMinute;
    private TextView toText, fromText, commentText, dateText, weightText, modelText;
    private ConstraintLayout commentView, weightView, modelView, commentSwipeView,
            weightSwipeView, modelSwipeView,mainWeightView, mainModelView;
    private EditText commentEditText, volumeEditText, weightEditText, modelEditText;
    private Button makeOrderButton;

    private FragmentTransaction fragmentTransaction;
    private BehaviorSubject<String> mObservable = BehaviorSubject.create();

    public static MakeOrderFragment newInstance(String param1, String param2) {
        MakeOrderFragment fragment = new MakeOrderFragment();
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
            toAddress = getArguments().getParcelable(Constants.TOADDRESS);
            fromAddress = getArguments().getParcelable(Constants.FROMADDRESS);
            mode = getArguments().getInt(Constants.MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_make_order, container, false);
        initViews(view);
//        setObserver();
        return view;
    }

    private void initViews(View view){
        toText = view.findViewById(R.id.fmo_to_edittext);
        fromText = view.findViewById(R.id.fmo_from_edittext);
        makeOrderButton = view.findViewById(R.id.fmo_make_order_button);

//        priceText = view.findViewById(R.id.fmo_price_text);
        commentText = view.findViewById(R.id.fmo_comment_text);
        dateText = view.findViewById(R.id.fmo_date_text);
        weightText = view.findViewById(R.id.fmo_weight_text);
        modelText = view.findViewById(R.id.fmo_model_text);

//        priceEditText = view.findViewById(R.id.fmo_price_edittext);
        commentEditText = view.findViewById(R.id.fmo_comment_edittext);
        weightEditText = view.findViewById(R.id.fmo_weight_edittext);
        volumeEditText = view.findViewById(R.id.fmo_volume_edittext);
        modelEditText = view.findViewById(R.id.fmo_model_edittext);

//        priceView = view.findViewById(R.id.fmo_price_view);
        commentView = view.findViewById(R.id.fmo_comment_view);
        mainWeightView = view.findViewById(R.id.fmo_main_weight_view);
        mainModelView = view.findViewById(R.id.fmo_main_model_view);
        weightView = view.findViewById(R.id.fmo_weight_view);
        modelView = view.findViewById(R.id.fmo_model_view);

//        priceSwipeView = view.findViewById(R.id.fmo_price_swipe_view);
        commentSwipeView = view.findViewById(R.id.fmo_comment_swipe_view);
        weightSwipeView = view.findViewById(R.id.fmo_weight_swipe_view);
        modelSwipeView = view.findViewById(R.id.fmo_model_swipe_view);

        if(toAddress != null && fromAddress != null){
            fromText.setText(fromAddress.getAddress());
            toText.setText(toAddress.getAddress());

            if(mode == 5){
                mainModelView.setVisibility(View.VISIBLE);
            }else if(mode == 4){
                mainWeightView.setVisibility(View.VISIBLE);
            }
        }

        setListeners();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        makeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewOrder order;

                if(selectedDate == 0){
                    selectedDate = new Date().getTime();
                }

                if(mode == 5){
                    order = new NewOrder(toAddress, fromAddress, mode, commentEditText.getText().toString(),
                            selectedDate, modelEditText.getText().toString());
                }else if(mode == 4){
                    order = new NewOrder(toAddress, fromAddress, mode, commentEditText.getText().toString(),
                            selectedDate, volumeEditText.getText().toString(), weightEditText.getText().toString());

                }else {
                    order = new NewOrder(toAddress, fromAddress, mode, commentEditText.getText().toString(), selectedDate);
                }

                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.NEWORDER, order);

                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentTransaction = getFragmentManager().beginTransaction();

                MainFragment mainFragment = new MainFragment();
                mainFragment.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.main_activity_frame, mainFragment, MainFragment.TAG);
                fragmentTransaction.addToBackStack(MainFragment.TAG);
                fragmentTransaction.commit();
            }
        });

//        priceText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                priceViewState = !priceViewState;
//                if(!priceViewState){
//                    slide_up(getContext(),priceView);
//                    priceView.setVisibility(View.GONE);
//                }else {
//                    priceView.setVisibility(View.VISIBLE);
//                    slide_down(getContext(), priceView);
//                }
//            }
//        });

        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentViewState = !commentViewState;
                if(!commentViewState){
                    slide_up(getContext(),commentView);
                    commentView.setVisibility(View.GONE);
                }else {
                    commentView.setVisibility(View.VISIBLE);
                    slide_down(getContext(), commentView);
                }
            }
        });

        weightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightViewState = !weightViewState;
                if(!weightViewState){
                    slide_up(getContext(),weightView);
                    weightView.setVisibility(View.GONE);
                }else {
                    weightView.setVisibility(View.VISIBLE);
                    slide_down(getContext(), weightView);
                }
            }
        });

        modelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modelViewState = !modelViewState;
                if(!modelViewState){
                    slide_up(getContext(),modelView);
                    modelView.setVisibility(View.GONE);
                }else {
                    modelView.setVisibility(View.VISIBLE);
                    slide_down(getContext(), modelView);
                }
            }
        });

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                mDialogMonthDayHourMinute = new TimePickerDialog.Builder()
                        .setCallBack(MakeOrderFragment.this)
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

                mDialogMonthDayHourMinute.show(getFragmentManager(), "month_day_hour_minute");
            }
        });
//
//        priceSwipeView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
//            public void onSwipeTop() {
//                priceViewState = !priceViewState;
//                slide_up(getContext(), priceView);
//                priceView.setVisibility(View.GONE);
//            }
//        });

        commentSwipeView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeTop() {
                commentViewState = !commentViewState;
                slide_up(getContext(), commentView);
                commentView.setVisibility(View.GONE);
            }
        });

        weightSwipeView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeTop() {
                weightViewState = !weightViewState;
                slide_up(getContext(), weightView);
                weightView.setVisibility(View.GONE);
            }
        });

        modelSwipeView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeTop() {
                modelViewState = !modelViewState;
                slide_up(getContext(), modelView);
                modelView.setVisibility(View.GONE);
            }
        });

//        priceEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                checkAllFields();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        volumeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        modelEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllFields();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        this.selectedDate = millseconds;
        String text = getDateToString(millseconds);
        mObservable.onNext(text);
        dateText.setText(text);
    }

    public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(d);
    }

    private void checkAllFields(){
        if(mode == 5){
            if(!modelEditText.getText().toString().isEmpty() && selectedDate != 0){
                makeOrderButton.setVisibility(View.VISIBLE);
            }else {
                makeOrderButton.setVisibility(View.GONE);
            }
        }else if(mode ==4){
            if(!weightEditText.getText().toString().isEmpty() && !volumeEditText.getText().toString().isEmpty()
                    && selectedDate != 0){
                makeOrderButton.setVisibility(View.VISIBLE);
            }else {
                makeOrderButton.setVisibility(View.GONE);
            }
        }else {
            if(selectedDate != 0){
                makeOrderButton.setVisibility(View.VISIBLE);
            }else {
                makeOrderButton.setVisibility(View.GONE);
            }
        }
    }

    private void setObserver(){
        mObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String string) {
                checkAllFields();
            }
        });
    }
}
