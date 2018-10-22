package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.AccessPrice;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DirectionResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.IntercityOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class IntercityOrdersFragment extends Fragment {
    public static final String TAG = Constants.INTERCITYORDERSFRAGMENT;
    private static final String DIRECTION = "direction";

    private DirectionResponse.Direction direction;

    private ImageButton backButton;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;

    public static IntercityOrdersFragment newInstance(DirectionResponse.Direction direction) {
        IntercityOrdersFragment fragment = new IntercityOrdersFragment();
        Bundle args = new Bundle();
        args.putParcelable(DIRECTION, direction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            direction = getArguments().getParcelable(DIRECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intercity_orders, container, false);
        initViews(view);

        getOrder(direction);
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();

        backButton = view.findViewById(R.id.fio_back);
        recyclerView = view.findViewById(R.id.fio_recyclerview);
        progressBar = view.findViewById(R.id.fio_progressbar);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setOrders(List<IntercityOrder> orders){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerOrdersAdapter(orders, getContext()));
    }

    private void haveNotAccessView(AccessPrice accessPrice){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button payButton = (Button) dialog.findViewById(R.id.cdv_close_button);
        TextView message = (TextView) dialog.findViewById(R.id.cdv_title_text);
        ImageView image = (ImageView) dialog.findViewById(R.id.cdv_image);


        payButton.setText(getResources().getText(R.string.pay));
        message.setText(getResources().getText(R.string.no_access_message) + accessPrice.getHour_price() + " тг.");
        Glide.with(getContext()).load(getResources().getDrawable(R.drawable.icon_error)).into(image);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyAccess("1", "0");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //requests
    private void getOrder(DirectionResponse.Direction direction){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getIntercityOrders(Utility.getToken(getContext()), direction.getStart_id(), direction.getEnd_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseOrders, this::handleErrorOrders));
    }

    private void handleResponseOrders(IntercityOrder.InterCityOrdersResponse response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            setOrders(response.getOrders());
        }else if(response.getState().equals("do not have access")){
            haveNotAccessView(response.getAccessPrice());
        }
    }

    private void handleErrorOrders(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void buyAccess(String type, String accessType){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .buyAccess(Utility.getToken(getContext()),type, accessType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseBuy, this::handleErrorBuy));
    }

    private void handleResponseBuy(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            getOrder(direction);
        }else {
            Toast.makeText(getContext(), getResources().getText(R.string.not_enought_balance), Toast.LENGTH_LONG).show();
        }
    }

    private void handleErrorBuy(Throwable throwable) {
    }


    public class RecyclerOrdersAdapter extends RecyclerView.Adapter<RecyclerOrdersAdapter.ViewHolder> {
        public Context mContext;
        public List<IntercityOrder> orderList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView infoText, fromText, toText, modelText, seatsText, dateText, priceText;
            public LinearLayout view;

            public ViewHolder(View v) {
                super(v);
                infoText = (TextView)v.findViewById(R.id.rioi_user_info_text);
                priceText = (TextView)v.findViewById(R.id.rioi_price_text);
                fromText = (TextView)v.findViewById(R.id.rioi_address_from_text);
                toText = (TextView) v.findViewById(R.id.rioi_address_to_text);
                modelText = (TextView) v.findViewById(R.id.rioi_car_text);
                seatsText = (TextView) v.findViewById(R.id.rioi_seat_text);
                dateText = (TextView) v.findViewById(R.id.rioi_date_text);
                view = (LinearLayout) v.findViewById(R.id.rdi_info_view);
            }
        }

        public RecyclerOrdersAdapter(List<IntercityOrder> orderList, Context mContext) {
            this.orderList = orderList;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_intercityorder_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.infoText.setText(orderList.get(position).getName() + " (" + orderList.get(position).getPhone() + ")");
            holder.fromText.setText(orderList.get(position).getStart());
            holder.toText.setText(orderList.get(position).getEnd());
            holder.seatsText.setText(getResources().getString(R.string.seatnumbers) + orderList.get(position).getSeats_number());
            holder.dateText.setText(setDataString(orderList.get(position).getDate()));
            holder.priceText.setText(orderList.get(position).getPrice() + " тг.");

            if(orderList.get(position).getModel() == null){
                holder.modelText.setVisibility(View.GONE);
            }else {
                holder.modelText.setText(orderList.get(position).getModel() + " " + orderList.get(position).getSubmodel());
            }
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

    public static String setDataString(String miliseconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(miliseconds));
        return formatter.format(calendar.getTime());
    }
}
