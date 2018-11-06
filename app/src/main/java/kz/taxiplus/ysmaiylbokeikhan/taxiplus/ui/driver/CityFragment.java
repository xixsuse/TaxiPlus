package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;

public class CityFragment extends Fragment {
    public static final String TAG = Constants.CITYFRAGMENTTAG;

    private ImageButton menuIcon;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city, container, false);
        initViews(view);

        setTabs();
        return view;
    }

    private void setTabs() {
        fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        TabLayout.Tab general = tabLayout.getTabAt(0);
        TabLayout.Tab myPark = tabLayout.getTabAt(1);
        general.setCustomView(fragmentPagerAdapter.getTabView(getResources().getString(R.string.general_chat), "0"));
        myPark.setCustomView(fragmentPagerAdapter.getTabView(getResources().getString(R.string.my_taxipark), "0"));
    }

    private void initViews(View view) {
        menuIcon = view.findViewById(R.id.fc_back);
        tabLayout = view.findViewById(R.id.fc_sliding_tabs);
        viewPager = view.findViewById(R.id.fc_viewpager);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });
    }

    public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[] { "Tab1", "Tab2" };
        private Context context;

        public FragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return GeneralOrdersFragment.newInstance("sharedOrders");

                case 1:
                    return GeneralOrdersFragment.newInstance("ownOrders");

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
}
