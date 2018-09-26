package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.MainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class GeneralOrdersFragment extends Fragment {
    public static final String TAG = Constants.GENERALORDERSFRAGMENTTAG;
    private static final String FRAGMENTADDRESS = "lastFragmentAddress";

    private String lastFragment;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeLayout;

    private CompositeSubscription subscription;
    private RecyclerOrdersAdapter ordersAdapter;

    private List<Order> myOrders = new ArrayList<>();
    private List<Order> sharedOrders = new ArrayList();

    public static GeneralOrdersFragment newInstance(String lastAddress) {
        GeneralOrdersFragment fragment = new GeneralOrdersFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENTADDRESS, lastAddress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lastFragment = getArguments().getString(FRAGMENTADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_orders, container, false);
        initViews(view);

        switch (lastFragment){
            case "ownOrders":
                getOwnOrders();
                break;

            case "sharedOrders":
                getSharedOrders();
                break;
        }

        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        recyclerView = view.findViewById(R.id.fgo_recyclerview);
        swipeLayout = view.findViewById(R.id.fgo_swipe_view);
        progressBar = view.findViewById(R.id.fgo_progressbar);

        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.carrot));

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(lastFragment.equals("sharedOrders")){
                    getSharedOrders();
                }else if(lastFragment.equals("ownOrders")){
                    getOwnOrders();
                }
            }
        });
    }

    private void setRecyclerView(List<Order> orderList){
        swipeLayout.setRefreshing(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersAdapter = new RecyclerOrdersAdapter(orderList, getContext());
        recyclerView.setAdapter(ordersAdapter);
    }

    //requests
    private void getOwnOrders() {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getOwnOrders(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseOwn, this::handleErrorOwn));
    }

    private void handleResponseOwn(Order.GetOrders response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")) {
            this.myOrders = response.getOrders();
            Collections.reverse(myOrders);
            setRecyclerView(response.getOrders());
        }
    }

    private void handleErrorOwn(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void getSharedOrders() {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getSharedOrders(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseShared, this::handleErrorShared));
    }

    private void handleResponseShared(Order.GetOrders response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")) {
            this.sharedOrders = response.getOrders();
            Collections.reverse(sharedOrders);
            setRecyclerView(response.getOrders());
        }
    }

    private void handleErrorShared(Throwable throwable) {
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
        }
    }

    private void handleErrorReject(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void addComplaint(String text, String order_id) {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .addComplaint(Utility.getToken(getContext()), text, order_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseComplaint, this::handleErrorComplaint));
    }

    private void handleResponseComplaint(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            Toast.makeText(getContext(), getResources().getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleErrorComplaint(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    public class RecyclerOrdersAdapter extends RecyclerView.Adapter<RecyclerOrdersAdapter.ViewHolder> {
        public Context mContext;
        public List<Order> orderList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView userInfo, fromText, toText, priceText;
            public LinearLayout view, hideView, complaintView, onMapView;

            public ViewHolder(View v) {
                super(v);
                userInfo = (TextView)v.findViewById(R.id.rcoi_user_info_text);
                fromText = (TextView)v.findViewById(R.id.rcoi_address_from_text);
                toText = (TextView)v.findViewById(R.id.rcoi_address_to_text);
                priceText = (TextView)v.findViewById(R.id.rcoi_price_text);
                view = (LinearLayout) v.findViewById(R.id.rcoi_info_view);
                hideView = (LinearLayout) v.findViewById(R.id.rcoi_hide_view);
                complaintView = (LinearLayout) v.findViewById(R.id.rcoi_complaint_view);
                onMapView = (LinearLayout) v.findViewById(R.id.rcoi_onmap_view);
            }
        }

        public RecyclerOrdersAdapter(List<Order> orderList, Context mContext) {
            this.orderList = orderList;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_ciry_order_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            LatLng from = new LatLng(Double.valueOf(orderList.get(position).getFrom_latitude()),
                                     Double.valueOf(orderList.get(position).getFrom_longitude()));

            LatLng to = new LatLng(Double.valueOf(orderList.get(position).getTo_latitude()),
                                   Double.valueOf(orderList.get(position).getTo_longitude()));

            holder.userInfo.setText(orderList.get(position).getName() + " ( " + orderList.get(position).getPhone() + " )");
            holder.fromText.setText(getAddressFromLatLngStr(from));
            holder.toText.setText(getAddressFromLatLngStr(to));
            holder.priceText.setText(orderList.get(position).getPrice()+" тг");

            holder.hideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rejectOrder(orderList.get(position));
                }
            });

            holder.complaintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openComplaintView(orderList.get(position));
                }
            });

            holder.onMapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderInfoDialogFragment newOrderDialogFragment = OrderInfoDialogFragment.newInstance(orderList.get(position).getId());
                    newOrderDialogFragment.show(getChildFragmentManager(), OrderInfoDialogFragment.TAG);
                }
            });
        }
        @Override
        public int getItemCount() {
            return orderList.size();
        }
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

    private void openComplaintView(Order order){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_complaint_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button sendButton = (Button) dialog.findViewById(R.id.ccv_send_button);
        EditText causeEditText = (EditText) dialog.findViewById(R.id.ccv_cause_view);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!causeEditText.getText().toString().isEmpty()){
                  addComplaint(causeEditText.getText().toString(), order.getId());
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void rejectOrder(Order order){
        if(lastFragment.equals("sharedOrders")){
            this.sharedOrders.remove(order);
            ordersAdapter.notifyDataSetChanged();
        }else if(lastFragment.equals("ownOrders")){
            this.myOrders.remove(order);
            ordersAdapter.notifyDataSetChanged();
        }
        rejectOrder(order.getId());
    }
}
