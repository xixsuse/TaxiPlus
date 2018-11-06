package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.AuthActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class LogoutDialogFragment extends DialogFragment {
    public static final String TAG = Constants.LOGOUTVIEW;

    private Button yesBtn, noBtn;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout_dialog, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        yesBtn = view.findViewById(R.id.clv_yes_button);
        noBtn = view.findViewById(R.id.clv_no_button);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AuthActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Paper.book().delete(Constants.USER);
                Paper.book().delete(Constants.LASTPLACES);
                Paper.book().delete(getResources().getString(R.string.prefs_theme_key));
                startActivity(i);
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
