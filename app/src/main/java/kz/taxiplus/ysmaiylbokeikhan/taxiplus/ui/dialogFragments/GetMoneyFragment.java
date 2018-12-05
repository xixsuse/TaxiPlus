package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewsItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class GetMoneyFragment extends DialogFragment {
    public final static String TAG = Constants.GETMONEYFRAGMENT;

    private EditText amountEditText, cardEditText;
    private Button sendButton;

    private CompositeSubscription subscription;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_money, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        amountEditText = view.findViewById(R.id.fgm_amount_edittext);
        cardEditText = view.findViewById(R.id.fgm_card_edittext);
        sendButton = view.findViewById(R.id.fgm_send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!amountEditText.getText().toString().isEmpty() && cardEditText.getText().toString().length() == 16){
                    sendMoney(amountEditText.getText().toString(), cardEditText.getText().toString());
                }else if (amountEditText.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), getResources().getString(R.string.amount_error), Toast.LENGTH_SHORT).show();
                }else if (cardEditText.getText().toString().length() != 16){
                    Toast.makeText(getContext(), getResources().getString(R.string.card_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMoney(String amount, String card_number){
        subscription.add(NetworkUtil.getRetrofit()
                .sendMoney(Utility.getToken(getContext()), amount, card_number)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        if(response.getState().equals("success")){
            Toast.makeText(getContext(), getResources().getString(R.string.successfully_sended), Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    private void handleError(Throwable throwable) {
        Toast.makeText(getContext(), getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
    }

}
