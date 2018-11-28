package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;


public class InfoFragment extends Fragment {
    public static final String TAG = Constants.INFOFRAGMENT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {

    }
}
