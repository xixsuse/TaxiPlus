package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class GeneralOrdersFragment extends Fragment {
    public static final String TAG = Constants.GENERALORDERSFRAGMENTTAG;
    private static final String FRAGMENTADDRESS = "lastFragmentAddress";

    private String lastFragment;

    private RecyclerView recyclerView;

    private RecyclerOrdersAdapter ordersAdapter;

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

        List<NewOrder> newOrderList = new ArrayList<>();
        LatLng from = new LatLng(43.268866, 76.957347);
        LatLng to = new LatLng(43.191931, 76.834330);
        NewOrder order = new NewOrder("8 (777) 123 45 67", "Таир", "Айнабулак 1","пос. Алатау 23", "500", "1534737997016", from, to);
        NewOrder order1 = new NewOrder("8 (777) 153 76 12", "Жандос", "Орбита 1, 34","Адем", "1000", "1534737997516", from, to);

        newOrderList.add(order);
        newOrderList.add(order1);

        setRecyclerView(newOrderList);

        return view;
    }

    private void initViews(View view){
        recyclerView = view.findViewById(R.id.fgo_recyclerview);
    }

    private void setRecyclerView(List<NewOrder> orderList){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersAdapter = new RecyclerOrdersAdapter(orderList, getContext());
        recyclerView.setAdapter(ordersAdapter);
    }

    public class RecyclerOrdersAdapter extends RecyclerView.Adapter<RecyclerOrdersAdapter.ViewHolder> {
        public Context mContext;
        public List<NewOrder> orderList;

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

        public RecyclerOrdersAdapter(List<NewOrder> orderList, Context mContext) {
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
            holder.userInfo.setText(orderList.get(position).getUser_name() + "( " + orderList.get(position).getPhone() + " )");
            holder.fromText.setText(orderList.get(position).getFromAddress());
            holder.toText.setText(orderList.get(position).getToAddress());
            holder.priceText.setText(orderList.get(position).getPrice()+" тг");

            holder.hideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.complaintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.onMapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }
}
