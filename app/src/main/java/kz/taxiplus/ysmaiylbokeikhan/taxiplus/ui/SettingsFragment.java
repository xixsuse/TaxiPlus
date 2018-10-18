package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.FaqFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class SettingsFragment extends Fragment {
    public static String TAG = Constants.SETTINGSFRAGMENTTAG;

    private ImageButton menuIcon;
    private LinearLayout profileSettings, faqView;

    private FragmentTransaction fragmentTransaction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        menuIcon = view.findViewById(R.id.fs_back);
        profileSettings = view.findViewById(R.id.profile_view);
        faqView = view.findViewById(R.id.faq_view);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                UserProfileFragment userProfileFragment = new UserProfileFragment();
                fragmentTransaction.add(R.id.main_activity_frame, userProfileFragment, UserProfileFragment.TAG);
                fragmentTransaction.addToBackStack(UserProfileFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        faqView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                FaqFragment faqFragment = new FaqFragment();
                fragmentTransaction.add(R.id.main_activity_frame, faqFragment, FaqFragment.TAG);
                fragmentTransaction.addToBackStack(FaqFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

}
