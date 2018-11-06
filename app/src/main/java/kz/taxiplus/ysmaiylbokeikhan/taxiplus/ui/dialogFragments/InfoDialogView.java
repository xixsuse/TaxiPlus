package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class InfoDialogView extends DialogFragment{
    public static String TAG = Constants.INFODIALOGVIEW;
    private static String MSG = "message";
    private static String IMG = "image";

    private String message;
    private int image;

    private ImageView imageView;
    private TextView textView;
    private Button closeButton;

    public static InfoDialogView newInstance(String message, int image){
        InfoDialogView infoDialogView = new InfoDialogView();
        Bundle args = new Bundle();
        args.putString(MSG, message);
        args.putInt(IMG, image);
        infoDialogView.setArguments(args);

        return infoDialogView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            message = getArguments().getString(MSG);
            image = getArguments().getInt(IMG);
        }
    }

    @Override
    public void onResume() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        super.onResume();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
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
        View view = inflater.inflate(R.layout.custom_dialog_view, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        imageView = view.findViewById(R.id.cdv_image);
        textView = view.findViewById(R.id.cdv_title_text);
        closeButton = view.findViewById(R.id.cdv_close_button);

        Glide.with(getContext()).load(image).into(imageView);
        textView.setText(message);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
