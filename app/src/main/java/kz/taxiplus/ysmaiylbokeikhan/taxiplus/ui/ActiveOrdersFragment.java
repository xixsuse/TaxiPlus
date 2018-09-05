package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.app.Dialog;
import android.content.Context;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class ActiveOrdersFragment extends Fragment {
    public static final String TAG = Constants.ACTIVEORDERSFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageButton menuIcon;
    private RecyclerView recyclerView;
    private RecyclerActiveOrdersAdapter adapter;

    public static ActiveOrdersFragment newInstance(String param1, String param2) {
        ActiveOrdersFragment fragment = new ActiveOrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_orders, container, false);
        initViews(view);


        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setToAddressString("Новаи Жандосова");
        order.setFromAddressString("Жандосова Гагарина");
        order.setCost("500");
        order.setStatus("Вызов");

        orders.add(order);
        orders.add(order);
        orders.add(order);

        setRecyclerView(orders);
        return view;
    }

    private void initViews(View view){
        menuIcon = view.findViewById(R.id.fco_back);
        recyclerView = view.findViewById(R.id.fco_recyclerview);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });
    }

    private void setRecyclerView(List<Order> orders) {
        adapter = new RecyclerActiveOrdersAdapter(orders, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    public class RecyclerActiveOrdersAdapter extends RecyclerView.Adapter<RecyclerActiveOrdersAdapter.ViewHolder> {
        public Context mContext;
        public List<Order> orderList;

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

        public RecyclerActiveOrdersAdapter(List<Order> orderList, Context mContext) {
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
            holder.address.setText(orderList.get(position).getFromAddressString() + " - "+ orderList.get(position).getToAddressString());
            holder.date.setText(orderList.get(position).getStatus());
            holder.price.setText(orderList.get(position).getCost());
            holder.date.setTextColor(getResources().getColor(R.color.colorPrimary));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetails(orderList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

    private void openDetails(Order order){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alert_dialog_order_details);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button declineButton = (Button) dialog.findViewById(R.id.cadoa_decline_button);
        TextView name = (TextView) dialog.findViewById(R.id.cadoa_name);
        TextView phone = (TextView) dialog.findViewById(R.id.cadoa_phone);
        TextView model = (TextView) dialog.findViewById(R.id.cadoa_car_model);
        TextView number = (TextView) dialog.findViewById(R.id.cadoa_car_number);
        TextView time = (TextView) dialog.findViewById(R.id.cadoa_time);


        name.setText("Жандос");
        phone.setText("87775674544");
        model.setText("Toyota Camry");
        number.setText("A070YYM");
        time.setText("30 мин");

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }
}
