package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

import static android.app.Activity.RESULT_OK;

public class CheckoutOrderDialogFragment extends DialogFragment {
    public static final String TAG = Constants.CHECKOUTFRAGMENTFRAGMENTTAG;
    private static final String ORDERINFO = "order_info";
    private static final int REQUEST_CALL_PERMISSION = 123;

    private OrderToDriver.GetOrderInfo orderInfo;
    private BottomSheetBehavior sheetBehavior;

    private ConstraintLayout layoutBottomSheet;
    private TextView confirmFromText, confirmToText, confNameText;
    private TextView confPhoneText, confModelText, confNumberText;
    private TextView confDateText, confModeText;
    private Button confCallButton;

    public static CheckoutOrderDialogFragment newInstance(OrderToDriver.GetOrderInfo orderInfo) {
        CheckoutOrderDialogFragment fragment = new CheckoutOrderDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ORDERINFO, orderInfo);
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
            orderInfo = getArguments().getParcelable(ORDERINFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout_order_dialog, container, false);

        initViewsBottomSheet(view);
        setInfo(orderInfo);
        return view;
    }

    private void initViewsBottomSheet(View view) {
        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        confirmFromText = view.findViewById(R.id.mf_confirm_from_text);
        confirmToText = view.findViewById(R.id.mf_confirm_to_text);

        confNameText = view.findViewById(R.id.mf_confirm_name_text);
        confPhoneText = view.findViewById(R.id.mf_confirm_phone_text);
        confNumberText = view.findViewById(R.id.mf_confirm_number_text);
        confModelText = view.findViewById(R.id.mf_confirm_model_text);

        confModeText = view.findViewById(R.id.mf_confirm_mode_text);
        confDateText = view.findViewById(R.id.mf_confirm_date_text);

        confCallButton = view.findViewById(R.id.mf_confirm_call_button);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        confCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCallPermission()){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+" + orderInfo.getDriver().getPhone()));
                    startActivity(intent);
                }else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                }
            }
        });
    }

    private void setInfo(OrderToDriver.GetOrderInfo orderInfo) {
        confirmFromText.setText(getAddressFromLatLngStr(new LatLng(orderInfo.getOrder().getFrom_latitude(), orderInfo.getOrder().getFrom_longitude())));
        confirmToText.setText(getAddressFromLatLngStr(new LatLng(orderInfo.getOrder().getTo_latitude(), orderInfo.getOrder().getTo_longitude())));

        confNameText.setText(orderInfo.getDriver().getName());
        confPhoneText.setText(orderInfo.getDriver().getPhone());
        confNumberText.setText(orderInfo.getDriver().getCar_number());
        confModelText.setText(orderInfo.getCar());
        confModeText.setText(setOrder(orderInfo.getOrder().getOrder_type()));
        confDateText.setText(setDataString(orderInfo.getOrder().getDate()));
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+" + orderInfo.getDriver().getPhone()));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                }
                return;
            }
            default:
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                break;
        }
    }

    private String setOrder(String order_type) {
        String typeString = "";
        switch (order_type){
            case "1":
                typeString = getResources().getString(R.string.econom_mode);
                break;

            case "2":
                typeString = getResources().getString(R.string.comfort_mode);
                break;

            case "3":
                typeString = getResources().getString(R.string.business_mode);
                break;

            case "4":
                typeString = getResources().getString(R.string.modeLadyTaxi);
                break;

            case "5":
                typeString = getResources().getString(R.string.modeInvaTaxi);
                break;

            case "6":
                typeString = getResources().getString(R.string.modeCitiesTaxi);
                break;

            case "7":
                typeString = getResources().getString(R.string.modeCargoTaxi);
                break;

            case "8":
                typeString = getResources().getString(R.string.modeEvo);
                break;
        }
        return typeString;
    }

    private String getAddressFromLatLngStr(LatLng latLng){
        List<Address> addressList;
        Address addresReturn = null;
        String title;

        Geocoder geocoder = new Geocoder(getContext(), getResources().getConfiguration().locale);

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            addresReturn = addressList.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert addresReturn != null;
        title = addresReturn.getAddressLine(0).substring(0, addresReturn.getAddressLine(0).indexOf(","));

        return title;
    }

    private String setDataString(String miliseconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(miliseconds));
        return formatter.format(calendar.getTime());
    }

    private boolean checkCallPermission() {
        String permission = "android.permission.CALL_PHONE";
        int res = getContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}
