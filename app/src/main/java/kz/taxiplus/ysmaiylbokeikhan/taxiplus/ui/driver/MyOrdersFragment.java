package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;


public class MyOrdersFragment extends Fragment {
    public static final String TAG = Constants.MYORDERSFRAGMENTTAG;
    private static final String POSITION = "position";
    private static final String ADDRESS = "address";

    private int position;
    private String address;

    private RecyclerView recyclerView;
    private Button newOrderButton;

    private RecyclerOrdersAdapter ordersAdapter;

    public static MyOrdersFragment newInstance(int position, String address) {
        MyOrdersFragment fragment = new MyOrdersFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putString(ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            address = getArguments().getString(ADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        initViews(view);

        List<NewOrder> newOrderList = new ArrayList<>();
        LatLng from = new LatLng(43.268866, 76.957347);
        LatLng to = new LatLng(43.191931, 76.834330);
        NewOrder order = new NewOrder("8 (777) 123 45 67", "Таир", "Айнабулак 1","пос. Алатау 23", "500", "1534737997016", from, to);
        NewOrder order1 = new NewOrder("8 (777) 153 76 12", "Жандос", "Орбита 1, 34","Адем", "1000", "1534737997516", from, to);

        if(position == 0) {
            newOrderList.add(order);
            newOrderList.add(order1);
            newOrderList.add(order);
            newOrderList.add(order);
            newOrderList.add(order1);
            newOrderList.add(order);
            newOrderList.add(order);
            newOrderList.add(order1);
            newOrderList.add(order);
        }else {
            newOrderList.add(order);
            newOrderList.add(order1);
            newOrderButton.setVisibility(View.GONE);
        }

        setRecyclerView(newOrderList);

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.fmo_recyclerview);
        newOrderButton = view.findViewById(R.id.fmo_new_order_button);

        newOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.equals("cargo")){
                    newOrderCargo();
                }else {
                    newOrderIntercity();
                }
            }
        });
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
            public TextView userInfo, fromText, toText, priceText, date;
            public LinearLayout view;

            public ViewHolder(View v) {
                super(v);
                userInfo = (TextView)v.findViewById(R.id.rii_user_info_text);
                fromText = (TextView)v.findViewById(R.id.rii_address_from_text);
                toText = (TextView)v.findViewById(R.id.rii_address_to_text);
                priceText = (TextView)v.findViewById(R.id.rii_price_text);
                date = (TextView)v.findViewById(R.id.rii_date_text);
                view = (LinearLayout) v.findViewById(R.id.rii_info_view);
            }
        }

        public RecyclerOrdersAdapter(List<NewOrder> orderList, Context mContext) {
            this.orderList = orderList;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_intercity_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.userInfo.setText(orderList.get(position).getUser_name() + "( " + orderList.get(position).getPhone() + " )");
            holder.fromText.setText(orderList.get(position).getFromAddress());
            holder.toText.setText(orderList.get(position).getToAddress());
            holder.priceText.setText(orderList.get(position).getPrice()+" тг");
            holder.date.setText(getDateToString(orderList.get(position).getDate()));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderInfoDialogFragment orderInfoDialogFragment = OrderInfoDialogFragment.newInstance(orderList.get(position));
                    orderInfoDialogFragment.show(getChildFragmentManager(), OrderInfoDialogFragment.TAG);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

    public String getDateToString(String time) {
        Date d = new Date(Long.valueOf(time));
        SimpleDateFormat sf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        return sf.format(d);
    }

    private void newOrderIntercity(){
        Dialog newOrderDialog = new Dialog(getContext());
        newOrderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newOrderDialog.setContentView(R.layout.custom_neworder_dialog);
        newOrderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText fromText = newOrderDialog.findViewById(R.id.cnod_from_text);
        EditText toText = newOrderDialog.findViewById(R.id.cnod_to_text);
        EditText date = newOrderDialog.findViewById(R.id.cnod_date_text);
        EditText price = newOrderDialog.findViewById(R.id.cnod_price_text);
        Button sendButton = newOrderDialog.findViewById(R.id.cnod_send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fromText.getText().toString().isEmpty() && !toText.getText().toString().isEmpty() &&
                        !date.getText().toString().isEmpty() && !price.getText().toString().isEmpty()){
                    newOrderDialog.dismiss();
                }
            }
        });
        newOrderDialog.show();
    }

    private void newOrderCargo(){
        Dialog newOrderDialog = new Dialog(getContext());
        newOrderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newOrderDialog.setContentView(R.layout.custom_newordercargo_dialog);
        newOrderDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText nameText = newOrderDialog.findViewById(R.id.cncod_name_text);
        EditText desctext = newOrderDialog.findViewById(R.id.cncod_desc_text);
        EditText price = newOrderDialog.findViewById(R.id.cncod_price_text);
        Button sendButton = newOrderDialog.findViewById(R.id.cncod_send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameText.getText().toString().isEmpty() && !desctext.getText().toString().isEmpty() &&
                        !price.getText().toString().isEmpty()){
                    newOrderDialog.dismiss();
                }
            }
        });
        newOrderDialog.show();
    }
}
