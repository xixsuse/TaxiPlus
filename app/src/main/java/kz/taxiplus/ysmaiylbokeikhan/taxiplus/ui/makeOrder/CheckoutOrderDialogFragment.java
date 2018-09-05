package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

import static android.app.Activity.RESULT_OK;

public class CheckoutOrderDialogFragment extends DialogFragment {
    public static final String TAG = Constants.CHECKOUTFRAGMENTFRAGMENTTAG;

    private String mParam1;
    private String mParam2;

    private TextView confirmFromText, confirmToText, confNameText, confPhoneText, confModelText, confNumberText;
    private TextView confDateText, confModeText;
    private Button confCallButton;

    public static CheckoutOrderDialogFragment newInstance() {
        CheckoutOrderDialogFragment fragment = new CheckoutOrderDialogFragment();
        Bundle args = new Bundle();

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

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout_order_dialog, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        confirmFromText = view.findViewById(R.id.mf_confirm_from_text);
        confirmToText = view.findViewById(R.id.mf_confirm_to_text);
        confNameText = view.findViewById(R.id.mf_confirm_name_text);
        confPhoneText = view.findViewById(R.id.mf_confirm_phone_text);
        confNumberText = view.findViewById(R.id.mf_confirm_number_text);
        confModelText = view.findViewById(R.id.mf_confirm_model_text);
        confModeText = view.findViewById(R.id.mf_confirm_mode_text);
        confDateText = view.findViewById(R.id.mf_confirm_date_text);
        confCallButton = view.findViewById(R.id.mf_confirm_call_button);

        confCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CheckoutOrderDialogFragment.class);

                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                getDialog().dismiss();
            }
        });
    }

}
