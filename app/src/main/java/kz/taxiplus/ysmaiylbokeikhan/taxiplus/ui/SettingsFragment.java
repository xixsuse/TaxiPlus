package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class SettingsFragment extends Fragment {
    public static final String TAG = Constants.SETTINGSFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private User user;

    private ImageButton menuIcon;
    private TextView nameText, lastNameText, phoneText, coinsText;

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Paper.init(getContext());
        initViews(view);
        return view;
    }

    private void initViews(View view){
        user = Paper.book().read(Constants.USER);
        menuIcon = view.findViewById(R.id.fs_back);
        nameText = view.findViewById(R.id.fs_name);
        lastNameText = view.findViewById(R.id.fs_lastname);
        phoneText = view.findViewById(R.id.fs_phone);
        coinsText = view.findViewById(R.id.fs_bonuses);


        nameText.setText(user.getName());
        phoneText.setText(user.getPhone());

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });
    }

}
