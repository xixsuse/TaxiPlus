package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.TaxiPark;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.AcceptOrderInterface;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CityFragment extends Fragment implements AcceptOrderInterface{
    public static final String TAG = Constants.CITYFRAGMENTTAG;

    private ImageButton menuIcon;
    private Button newOrdersButton;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private ViewPager viewPager;

    private FragmentPagerAdapter fragmentPagerAdapter;
    private CompositeSubscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city, container, false);
        initViews(view);

        getAccess();

        return view;
    }

    private void setTabs(boolean isShow) {
        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(), getContext(), isShow, this);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab general = tabLayout.getTabAt(0);
        general.setCustomView(fragmentPagerAdapter.getTabView(getResources().getString(R.string.general_chat), "0"));
        if(isShow){
            TabLayout.Tab myPark = tabLayout.getTabAt(1);
            myPark.setCustomView(fragmentPagerAdapter.getTabView(getResources().getString(R.string.my_taxipark), "0"));
        }
    }

    private void initViews(View view) {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myBroadcastReceiver,
                new IntentFilter("thisIsForCityFragment"));

        subscription = new CompositeSubscription();
        menuIcon = view.findViewById(R.id.fc_back);
        tabLayout = view.findViewById(R.id.fc_sliding_tabs);
        viewPager = view.findViewById(R.id.fc_viewpager);
        newOrdersButton = view.findViewById(R.id.fc_new_orders);
        progressBar = view.findViewById(R.id.fc_progressbar);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        newOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOrdersButton.setVisibility(View.GONE);
                getAccess();
            }
        });
    }

    private void getAccess(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .howManyChats(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            setTabs(response.isShow_chat());
        }
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onOrderAccept(boolean isAccept) {
        if(isAccept){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            DriverMainFragment driverMainFragment = new DriverMainFragment();
            fragmentTransaction.replace(R.id.main_activity_frame, driverMainFragment, DriverMainFragment.TAG);
            fragmentTransaction.addToBackStack(DriverMainFragment.TAG);
            fragmentTransaction.commit();
        }
    }

    public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
        private String tabTitles[] = new String[] { "Tab1", "Tab2" };
        private Context context;
        private boolean isShowAll;
        private AcceptOrderInterface acceptOrderInterface;

        public FragmentPagerAdapter(FragmentManager fm, Context context, boolean isShowAll, AcceptOrderInterface acceptOrderInterface) {
            super(fm);
            this.context = context;
            this.isShowAll = isShowAll;
            this.acceptOrderInterface = acceptOrderInterface;
        }

        @Override
        public int getCount() {
            return isShowAll ? 2 : 1;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    GeneralOrdersFragment sharedOrders = GeneralOrdersFragment.newInstance("sharedOrders");
                    sharedOrders.setAcceptOrderInterface(acceptOrderInterface);
                    return sharedOrders;

                case 1:
                    GeneralOrdersFragment ownOrders = GeneralOrdersFragment.newInstance("ownOrders");
                    ownOrders.setAcceptOrderInterface(acceptOrderInterface);
                    return ownOrders;

                default:
                    return GeneralOrdersFragment.newInstance(CityFragment.TAG);
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(String title, String count) {
            View v = LayoutInflater.from(context).inflate(R.layout.tablayout_custom_bagde_view, null);

            TextView countText = (TextView) v.findViewById(R.id.tcbv_text);
            TextView titleText = (TextView) v.findViewById(R.id.tcbv_title);

            titleText.setText(title);

            if(count.equals("0")){
                countText.setVisibility(View.GONE);
            }else {
                countText.setText(count);
            }

            return v;
        }

    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            String orderId = intent.getStringExtra(Constants.ORDERID);

           if (newOrdersButton.getVisibility() == View.GONE){
                newOrdersButton.setVisibility(View.VISIBLE);
           }
        }
    };
}
