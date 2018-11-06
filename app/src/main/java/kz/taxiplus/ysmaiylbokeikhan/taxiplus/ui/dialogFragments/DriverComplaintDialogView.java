package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class DriverComplaintDialogView extends DialogFragment{
    public static String TAG = Constants.DRIVERCOMPLAINTDIALOGVIEW;

    private EditText causeEditText;
    private Button sendButton;

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        super.onResume();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_complaint_view, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        causeEditText = view.findViewById(R.id.ccv_cause_view);
        sendButton = view.findViewById(R.id.ccv_send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!causeEditText.getText().toString().isEmpty()){
                    dismiss();
                }else {
                    Toast.makeText(getActivity(), getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
