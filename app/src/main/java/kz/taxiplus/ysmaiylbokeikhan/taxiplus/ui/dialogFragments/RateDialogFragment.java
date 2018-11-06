package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain.UserMainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RateDialogFragment extends DialogFragment{
    public static String TAG = Constants.RATEDIALOGVIEW;
    private static String ORDERID = "OrderId";

    private float rating = 0;
    private String orderId;

    private RatingBar ratingBar;
    private Button sendButton;
    private EditText textEditText;
    private CompositeSubscription subscription;

    public static RateDialogFragment newInstance(String orderId){
        RateDialogFragment rateDialogFragment = new RateDialogFragment();
        Bundle args = new Bundle();
        args.putString(ORDERID, orderId);
        rateDialogFragment.setArguments(args);

        return rateDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            orderId = getArguments().getString(ORDERID);
        }
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        super.onResume();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_rate_view, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        textEditText = view.findViewById(R.id.crv_text_view);
        sendButton = view.findViewById(R.id.crv_rate_button);
        ratingBar = view.findViewById(R.id.crv_ratingbar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float r, boolean fromUser) {
                rating = r;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rating != 0){
                    rateDriver(orderId, String.valueOf(rating), textEditText.getText().toString());
                }else {
                    Toast.makeText(getActivity(), getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void rateDriver(String orderId, String value, String text){
        subscription.add(NetworkUtil.getRetrofit()
                .rateDriver(Utility.getToken(getContext()), orderId, text, value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseRate, this::handleErrorRate));
    }

    private void handleResponseRate(Response response) {
        if(response.getState().equals("success")){
            UserMainFragment mainFragment = (UserMainFragment) getFragmentManager().findFragmentByTag(UserMainFragment.TAG);
            if(mainFragment != null) {
                mainFragment.clearMap();
                dismiss();
            }
            Toast.makeText(getContext(), getResources().getString(R.string.successfully_rated), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleErrorRate(Throwable throwable) { }
}
