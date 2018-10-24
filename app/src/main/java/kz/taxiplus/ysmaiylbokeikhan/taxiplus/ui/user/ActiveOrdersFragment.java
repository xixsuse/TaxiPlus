package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DriverBalance;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ActiveOrdersFragment extends Fragment {
    public static final String TAG = Constants.ACTIVEORDERSFRAGMENTTAG;
    private static final String ROLE = "param2";

    private String role;

    private ImageButton menuIcon;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerActiveOrdersAdapter adapter;

    private CompositeSubscription subscription;

    public static ActiveOrdersFragment newInstance(String role) {
        ActiveOrdersFragment fragment = new ActiveOrdersFragment();
        Bundle args = new Bundle();
        args.putString(ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            role = getArguments().getString(ROLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_orders, container, false);
        initViews(view);
        getActiveOrders();

        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        menuIcon = view.findViewById(R.id.fco_back);
        recyclerView = view.findViewById(R.id.fco_recyclerview);
        progressBar = view.findViewById(R.id.fco_progressbar);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });
    }

    private void setRecyclerView(List<OrderToDriver> orders) {
        adapter = new RecyclerActiveOrdersAdapter(orders, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void getActiveOrders(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getActiveOrders(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(OrderToDriver.GetOrders response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")) {
            setRecyclerView(response.getOrders());
        }
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void getOrderInfo(String order_id){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getOrderInfo(order_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseOrderInfo, this::handleErrorOrderInfo));
    }

    private void handleResponseOrderInfo(OrderToDriver.GetOrderInfo response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")) {
            if(response.getDriver()!=null) {
                openDetails(response.getDriver(), response.getOrder().getId());
            }else {
                Toast.makeText(getContext(), getResources().getText(R.string.null_driver), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleErrorOrderInfo(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void rejectOrder(String order_id) {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .rejectOrder(Utility.getToken(getContext()), order_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseReject, this::handleErrorReject));
    }

    private void handleResponseReject(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")) {
            Toast.makeText(getContext(), getResources().getText(R.string.successfully_rejected), Toast.LENGTH_SHORT).show();
            getActiveOrders();
        }
    }

    private void handleErrorReject(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerActiveOrdersAdapter extends RecyclerView.Adapter<RecyclerActiveOrdersAdapter.ViewHolder> {
        public Context mContext;
        public List<OrderToDriver> orderList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView date, address, price;
            public ConstraintLayout view;


            public ViewHolder(View v) {
                super(v);
                date = (TextView)v.findViewById(R.id.rhi_date_text);
                address = (TextView)v.findViewById(R.id.rhi_address_text);
                price = (TextView)v.findViewById(R.id.rhi_price_text);
                view = (ConstraintLayout) v.findViewById(R.id.rhi_view);
            }
        }

        public RecyclerActiveOrdersAdapter(List<OrderToDriver> orderList, Context mContext) {
            this.orderList = orderList;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_history_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            LatLng from = new LatLng(Double.valueOf(orderList.get(position).getFrom_latitude()),
                    Double.valueOf(orderList.get(position).getFrom_longitude()));

            LatLng to = new LatLng(Double.valueOf(orderList.get(position).getTo_latitude()),
                    Double.valueOf(orderList.get(position).getTo_longitude()));

            holder.address.setText(getAddressFromLatLngStr(from) + " - "+ getAddressFromLatLngStr(to));
            holder.date.setText(getDateToString(orderList.get(position).getDate()));
            holder.price.setText(orderList.get(position).getPrice());
            holder.date.setTextColor(getResources().getColor(R.color.colorPrimary));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: check
                    getOrderInfo(orderList.get(position).getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

    private void openDetails(User driver, String order_id){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_dialog_order_details);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button declineButton = (Button) dialog.findViewById(R.id.cadoa_decline_button);
        TextView name = (TextView) dialog.findViewById(R.id.cadoa_name);
        TextView phone = (TextView) dialog.findViewById(R.id.cadoa_phone);
        TextView model = (TextView) dialog.findViewById(R.id.cadoa_car_model);
        TextView number = (TextView) dialog.findViewById(R.id.cadoa_car_number);

        name.setText(driver.getName());
        phone.setText(driver.getPhone());
//        model.setText(driver.getCar());
//        number.setText(driver.getCar_number());

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectOrder(order_id);
            }
        });

        dialog.show();
    }

    public String getDateToString(String time) {
        Date d = new Date(Long.valueOf(time));
        SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
        return sf.format(d);
    }

    private String getAddressFromLatLngStr(LatLng latLng){
        List<Address> addressList;
        Address addresReturn = null;
        String title = "";

        Geocoder geocoder = new Geocoder(getContext(), getResources().getConfiguration().locale);

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            addresReturn = addressList.get(0);

            title = addresReturn.getAddressLine(0).substring(0, addresReturn.getAddressLine(0).indexOf(","));
        } catch (IOException e) {
            e.printStackTrace();
        }


        return title;
    }
}
