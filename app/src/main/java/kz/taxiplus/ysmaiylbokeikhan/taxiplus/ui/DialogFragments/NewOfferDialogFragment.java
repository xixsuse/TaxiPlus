package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.DialogFragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class NewOfferDialogFragment extends DialogFragment {
    public static final String TAG = Constants.NEWOFFERDIALOGFRAGMENTTAG;
    private static final String DRIVERID = "driver_id";
    private static final String ORDERID = "order_id";

    private String driverId, orderId;
    private ProgressBar progressBar;

    private TextView offerNameText, offerCarModelText, offerCarNumberText, offerDataText;
    private Button offerAccept, offerDecline;
    private CompositeSubscription subscription;

    public static NewOfferDialogFragment newInstance(String driverId, String orderId) {
        NewOfferDialogFragment fragment = new NewOfferDialogFragment();
        Bundle args = new Bundle();
        args.putString(DRIVERID, driverId);
        args.putString(ORDERID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_Panel);

        if (getArguments() != null) {
            driverId = getArguments().getString(DRIVERID);
            orderId = getArguments().getString(ORDERID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_offer_dialog, container, false);

        initViews(view);
        getFulInfo(driverId);
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        offerNameText= view.findViewById(R.id.nof_name_text);
        offerCarModelText= view.findViewById(R.id.nof_model_text);
        offerCarNumberText= view.findViewById(R.id.nof_number_text);
//        offerDataText= view.findViewById(R.id.nof_date_text);
        offerAccept= view.findViewById(R.id.nof_accept_button);
        offerDecline= view.findViewById(R.id.nof_decline_button);
        progressBar= view.findViewById(R.id.nof_progressbar);

        setListeners();
    }

    private void setListeners() {
        offerAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDriver(driverId, orderId);
//                getDialog().dismiss();
            }
        });

        offerDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void getFulInfo(String driverId) {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getDriverInfo(driverId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLoc, this::handleErrorLoc));
    }

    private void handleResponseLoc(OrderToDriver.GetOrderInfo response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            offerNameText.setText(response.getDriver().getName());
            offerCarModelText.setText(response.getCar());
            offerCarNumberText.setText(response.getDriver().getCar_number());
        }
    }

    private void handleErrorLoc(Throwable throwable){
        progressBar.setVisibility(View.GONE);
    }

    private void acceptDriver(String driverId, String orderId) {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .acceptDriver(orderId, driverId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAccept, this::handleErrorAccept));
    }

    private void handleResponseAccept(OrderToDriver.GetOrderInfo response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
//            MainFragment mainFragment = (MainFragment) getFragmentManager().findFragmentByTag(MainFragment.TAG);
//            getDialog().dismiss();
//            mainFragment.setCheckoutView(response);
        }
    }

    private void handleErrorAccept(Throwable throwable){
        progressBar.setVisibility(View.GONE);
    }
}
