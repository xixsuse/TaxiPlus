package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.esafirm.imagepicker.model.Image;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;


public class InfoFragment extends Fragment {
    public static final String TAG = Constants.INFOFRAGMENT;
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String TEXT = "text";

    private String title, message, id;

    private TextView titleText, messageText, dateText;
    private ImageButton backImage;

    public static InfoFragment newInstance(String id, String title, String text) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ID, id);
        args.putString(TITLE, title);
        args.putString(TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
            title= getArguments().getString(TITLE);
            message = getArguments().getString(TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        initViews(view);

        setInfo();
        return view;
    }

    private void setInfo() {
        titleText.setText(title);
        messageText.setText(message);
    }

    private void initViews(View view) {
        titleText = view.findViewById(R.id.fi_title);
        messageText = view.findViewById(R.id.fi_text);
        dateText = view.findViewById(R.id.fi_date);
        backImage = view.findViewById(R.id.fi_back);


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}
