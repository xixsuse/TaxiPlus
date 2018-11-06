package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CancelOrderDialogFragment extends DialogFragment {
    public static String TAG = Constants.CANCELORDERDIALOGVIEW;
    private static String ORDERID ="OrderId";

    private String orderId;

    private ImageView logo;
    private TextView title;
    private Button yesButton, noButton;

    private CompositeSubscription subscription;

    public interface CancelOrderDialogFragmentListener {
        void onFinishCancelDialog(boolean isSuccess);
    }


    public static CancelOrderDialogFragment newInstance(String orderId){
        CancelOrderDialogFragment dialogFragment = new CancelOrderDialogFragment();
        Bundle args = new Bundle();
        args.putString(ORDERID, orderId);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            orderId = getArguments().getString(ORDERID);
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_answer_view, container, false);

        initViews(view);

        title.setText(getResources().getString(R.string.are_you_sure_to_cancel_order));
        Glide.with(getContext()).load(R.drawable.icon_error).into(logo);

        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        logo = view.findViewById(R.id.cdv_image);
        title = view.findViewById(R.id.cdv_title_text);
        yesButton = view.findViewById(R.id.cdv_yes_button);
        noButton = view.findViewById(R.id.cdv_no_button);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(orderId);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void sendBackResult() {
        CancelOrderDialogFragmentListener listener = (CancelOrderDialogFragmentListener) getTargetFragment();
        assert listener != null;
        listener.onFinishCancelDialog(true);
        dismiss();
    }


    private void cancelOrder(String order_id){
        subscription.add(NetworkUtil.getRetrofit()
                .cancelOrder(Utility.getToken(getContext()), order_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCancelOrder, this::handleErrorCancelOrder));
    }

    private void handleResponseCancelOrder(Response response) {
        if(response.getState().equals("success")){
            Toast.makeText(getContext(), getResources().getText(R.string.order_is_canceled), Toast.LENGTH_SHORT).show();
            sendBackResult();
        }
    }

    private void handleErrorCancelOrder(Throwable throwable) {
        Toast.makeText(getContext(), "not", Toast.LENGTH_SHORT).show();
        Log.d("errror", throwable.toString());
    }

}
