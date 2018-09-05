package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class IntercityFragment extends Fragment {
    public static final String TAG = Constants.INTERCITYFRAGMENTTAG;
    private static final String LAST_FRAGMENT = "last_fragment";

    private String lastFragment;

    private ImageButton menuIcon;
    private TabLayout tabLayout;
    private TextView title;
    private ViewPager viewPager;

    private FragmentPagerAdapter fragmentPagerAdapter;

    public static IntercityFragment newInstance(String lastFragment) {
        IntercityFragment fragment = new IntercityFragment();
        Bundle args = new Bundle();
        args.putString(LAST_FRAGMENT, lastFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lastFragment = getArguments().getString(LAST_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intercity, container, false);
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
        general.setCustomView(fragmentPagerAdapter.getTabView(getResources().getString(R.string.orders), "0"));
        myPark.setCustomView(fragmentPagerAdapter.getTabView(getResources().getString(R.string.my_orders), "3"));
    }

    private void initViews(View view){
        menuIcon = view.findViewById(R.id.fic_back);
        tabLayout = view.findViewById(R.id.fic_sliding_tabs);
        title = view.findViewById(R.id.fic_title);
        viewPager = view.findViewById(R.id.fic_viewpager);

        if(lastFragment.equals("cargo")){
            title.setText(getResources().getText(R.string.mode_cargo));
        }else {
            title.setText(getResources().getText(R.string.mode_intercity));
        }

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
            return MyOrdersFragment.newInstance(position, lastFragment);
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
