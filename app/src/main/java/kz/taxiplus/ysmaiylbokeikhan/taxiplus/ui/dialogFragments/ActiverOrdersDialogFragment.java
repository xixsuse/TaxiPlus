package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.FromAndToFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiverOrdersDialogFragment extends DialogFragment {
    public static final String TAG = Constants.ACTIVEORDERSDIALOGFRAGMENT;
    private static final String ORDERS = "orders";

    private RecyclerView recyclerView;
    private List<Order> orders;

    public static ActiverOrdersDialogFragment newInstance(List<Order> orders){
        ActiverOrdersDialogFragment dialogFragment = new ActiverOrdersDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ORDERS, (ArrayList<? extends Parcelable>) orders);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            orders = getArguments().getParcelableArrayList(ORDERS);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activer_orders_dialog, container, false);
        initViews(view);

        return  view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.orders_recyclerview);
        setRecyclerView(orders);
    }

    private void setRecyclerView(List<Order> orders){
        recyclerView.setAdapter(new RecyclerOrdersAdapter(orders));
    }

    public class RecyclerOrdersAdapter extends RecyclerView.Adapter<RecyclerOrdersAdapter.ViewHolder>{
        private List<Order> orderList;

        public RecyclerOrdersAdapter(List<Order> orders) {
            this.orderList = orders;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView direction, time;
            public ConstraintLayout view;

            public ViewHolder(View itemView) {
                super(itemView);
                direction = (TextView) itemView.findViewById(R.id.roi_direction);
                time = (TextView) itemView.findViewById(R.id.roi_date);
                view = (ConstraintLayout) itemView.findViewById(R.id.roi_view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_recyclerview_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Order order = orderList.get(position);
            holder.time.setText(Utility.setDataString(order.getDate()));
            String from = Utility.getAddressFromLatLngStr(new LatLng(Double.valueOf(order.getFrom_latitude()),
                    Double.valueOf(order.getFrom_longitude())), getContext());

            String to = Utility.getAddressFromLatLngStr(new LatLng(Double.valueOf(order.getTo_latitude()),
                    Double.valueOf(order.getTo_longitude())), getContext());

            holder.direction.setText(from + " - " + to);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ActiverOrdersDialogFragment.class);
                    intent.putExtra("order_id", order.getId());

                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

}
