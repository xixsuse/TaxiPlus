package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DriverBalance;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments.GetMoneyFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments.LogoutDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MyBalanceFragment extends Fragment {
    public static final String TAG = Constants.MYBALANCERAGMENTTAG;
    private static final String ISUSER = "isUser";

    private boolean isUser;

    private ImageButton menuIcon;
    private ProgressBar progressBar;
    private LinearLayout driverView, userView;
    private TextView addedMoneyText, orderMoneyText, balanceText, refMoneyText, userBalanceText;
    private Button getMoneyButton;

    private CompositeSubscription subscription;

    public static MyBalanceFragment newInstance(boolean isUser) {
        MyBalanceFragment fragment = new MyBalanceFragment();
        Bundle args = new Bundle();
        args.putBoolean(ISUSER, isUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isUser = getArguments().getBoolean(ISUSER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_balance, container, false);
        initViews(view);

        getBalance();
        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        menuIcon = view.findViewById(R.id.fmb_menu);
        progressBar = view.findViewById(R.id.fmb_progressbar);
        driverView = view.findViewById(R.id.fmb_driver_view);
        userView = view.findViewById(R.id.fmb_user_view);
        addedMoneyText = view.findViewById(R.id.fmb_added_monets);
        orderMoneyText = view.findViewById(R.id.fmb_order_monets);
        balanceText = view.findViewById(R.id.fmb_balance_monets);
        userBalanceText = view.findViewById(R.id.fmb_us_balance_monets);
        refMoneyText = view.findViewById(R.id.fmb_ref_monets);
        getMoneyButton = view.findViewById(R.id.fmb_get_money);

        setListeners();
    }

    private void setListeners() {
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        getMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetMoneyFragment getMoneyFragment = new GetMoneyFragment();
                getMoneyFragment.show(getFragmentManager(), GetMoneyFragment.TAG);
            }
        });
    }

    private void getBalance(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getBalance(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(DriverBalance response) {
        if(isUser){
            driverView.setVisibility(View.GONE);
            userView.setVisibility(View.VISIBLE);

            refMoneyText.setText(response.getMonets());
            userBalanceText.setText(response.getBalance());
        }else {
            userView.setVisibility(View.GONE);
            driverView.setVisibility(View.VISIBLE);

            addedMoneyText.setText(response.getAdded_monets());
            orderMoneyText.setText(response.getOrders_monets());
            balanceText.setText(response.getBalance());
        }
        progressBar.setVisibility(View.GONE);
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }
}
