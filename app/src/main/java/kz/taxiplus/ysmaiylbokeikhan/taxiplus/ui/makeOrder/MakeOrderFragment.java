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
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain.UserMainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.OnSwipeTouchListener;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class MakeOrderFragment extends Fragment implements OnDateSetListener {
    public static final String TAG = Constants.MAKEORDERFRAGMENTTAG;

    private boolean commentViewState = false;
    private Calendar mCalendar;
    private int mode;
    private long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    private long selectedDate = 0;
    private Place toAddress, fromAddress;

    private TimePickerDialog mDialogMonthDayHourMinute;
    private TextView toText, fromText, commentText, dateText;
    private ConstraintLayout commentView, commentSwipeView;
    private EditText commentEditText;
    private Button makeOrderButton;

    private FragmentTransaction fragmentTransaction;
    private BehaviorSubject<String> mObservable = BehaviorSubject.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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

        return view;
    }

    private void initViews(View view){
        toText = view.findViewById(R.id.fmo_to_edittext);
        fromText = view.findViewById(R.id.fmo_from_edittext);
        makeOrderButton = view.findViewById(R.id.fmo_make_order_button);

        commentText = view.findViewById(R.id.fmo_comment_text);
        dateText = view.findViewById(R.id.fmo_date_text);
        commentEditText = view.findViewById(R.id.fmo_comment_edittext);

        commentView = view.findViewById(R.id.fmo_comment_view);
        commentSwipeView = view.findViewById(R.id.fmo_comment_swipe_view);

        if(toAddress != null && fromAddress != null){
            fromText.setText(fromAddress.getAddress());
            toText.setText(toAddress.getAddress());
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

                order = new NewOrder(toAddress, fromAddress, mode, commentEditText.getText().toString(), selectedDate);

                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.NEWORDER, order);

                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentTransaction = getFragmentManager().beginTransaction();

                UserMainFragment mainFragment = new UserMainFragment();
                mainFragment.setArguments(bundle);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.main_activity_frame, mainFragment, UserMainFragment.TAG);
                fragmentTransaction.addToBackStack(UserMainFragment.TAG);
                fragmentTransaction.commit();
            }
        });

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

        commentSwipeView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeTop() {
                commentViewState = !commentViewState;
                slide_up(getContext(), commentView);
                commentView.setVisibility(View.GONE);
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
}
