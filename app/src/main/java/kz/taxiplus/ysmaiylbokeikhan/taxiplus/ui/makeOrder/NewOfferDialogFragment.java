package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DriverOffer;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

import static android.app.Activity.RESULT_OK;

public class NewOfferDialogFragment extends DialogFragment {
    public static final String TAG = Constants.NEWOFFERDIALOGFRAGMENTTAG;
    private static final String OFFER = "offer";

    private DriverOffer offer;

    private TextView offerNameText, offerCarModelText, offerCarNumberText, offerDataText;
    private Button offerAccept, offerDecline;

    public static NewOfferDialogFragment newInstance(DriverOffer offer) {
        NewOfferDialogFragment fragment = new NewOfferDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(OFFER, offer);
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
            offer = getArguments().getParcelable(OFFER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_offer_dialog, container, false);

        initViews(view);
        newResponse(offer);
        return view;
    }

    private void initViews(View view) {
        offerNameText= view.findViewById(R.id.nof_name_text);
        offerCarModelText= view.findViewById(R.id.nof_model_text);
        offerCarNumberText= view.findViewById(R.id.nof_number_text);
        offerDataText= view.findViewById(R.id.nof_date_text);
        offerAccept= view.findViewById(R.id.nof_accept_button);
        offerDecline= view.findViewById(R.id.nof_decline_button);
    }

    private void newResponse(DriverOffer offer) {
        offerNameText.setText(offer.getDriverName());
        offerCarModelText.setText(offer.getCarModel());
        offerCarNumberText.setText(offer.getCarNumber());
        offerDataText.setText(setDataString(offer.getDate()));

        offerAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewOfferDialogFragment.class);

                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                getDialog().dismiss();
            }
        });

        offerDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private String setDataString(String miliseconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(miliseconds));
        return formatter.format(calendar.getTime());
    }
}
