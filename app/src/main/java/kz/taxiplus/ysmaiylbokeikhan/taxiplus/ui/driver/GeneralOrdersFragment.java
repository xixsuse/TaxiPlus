package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.AcceptOrderInterface;
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
    private AcceptOrderInterface acceptOrderInterface;

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

    public void setAcceptOrderInterface(AcceptOrderInterface acceptOrderInterface){
        this.acceptOrderInterface = acceptOrderInterface;
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


    private void acceptOrder(String orderId) {
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .acceptOrderDriver(Utility.getToken(getContext()), orderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAccept, this::handleErrorAccept));
    }

    private void handleResponseAccept(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            acceptOrderInterface.onOrderAccept(true);
            Toast.makeText(getContext(), getResources().getString(R.string.wait_response), Toast.LENGTH_LONG).show();
        }
    }

    private void handleErrorAccept(Throwable throwable){
        progressBar.setVisibility(View.GONE);
    }


    public class RecyclerOrdersAdapter extends RecyclerView.Adapter<RecyclerOrdersAdapter.ViewHolder> {
        public Context mContext;
        public List<Order> orderList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView userInfo, fromText, toText, priceText, distanceText;
            public LinearLayout view, acceptView, onMapView;

            public ViewHolder(View v) {
                super(v);
                userInfo = (TextView)v.findViewById(R.id.rcoi_user_info_text);
                fromText = (TextView)v.findViewById(R.id.rcoi_address_from_text);
                toText = (TextView)v.findViewById(R.id.rcoi_address_to_text);
                priceText = (TextView)v.findViewById(R.id.rcoi_price_text);
                distanceText = (TextView)v.findViewById(R.id.rcoi_distance_text);
                view = (LinearLayout) v.findViewById(R.id.rcoi_info_view);
                acceptView = (LinearLayout) v.findViewById(R.id.rcoi_accept_view);
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

            holder.userInfo.setText(orderList.get(position).getName());
            holder.fromText.setText(getAddressFromLatLngStr(from));
            holder.toText.setText(getAddressFromLatLngStr(to));
            holder.priceText.setText(orderList.get(position).getPrice()+" тг");
            holder.distanceText.setText(distanceBetween(from) + " м");


            if((new Date().getTime()/1000 - orderList.get(position).getCreated()) > 15*60){
                blinkingView(holder.view);
            }

            holder.acceptView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptOrder(orderList.get(position).getId());
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

            if(addressList.size() > 0){
                addresReturn = addressList.get(0);
                title = addresReturn.getAddressLine(0).substring(0, addresReturn.getAddressLine(0).indexOf(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return title;
    }

    private String distanceBetween(LatLng latLng){
        String result = "0";
        float[] results = new float[1];
        LatLng currentLatLng = Paper.book().read(Constants.MYLOCATION);

        if(currentLatLng != null) {
            Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude,
                    latLng.latitude, latLng.longitude, results);
            result = String.valueOf(Math.round(results[0]));
        }

        return result;
    }

    private void blinkingView(LinearLayout view){
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }
}
