package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.Collections;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.AccessPrice;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.FreightItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.AddCargoFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class InvaTaxiFragment extends Fragment {
    public static final String TAG = Constants.INVATAXIFRAGMENT;

    private ImageButton menuIcon,addButton;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inva_taxi, container, false);
        initViews(view);
        getOrder();

        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        menuIcon = view.findViewById(R.id.fit_menu);
        addButton = view.findViewById(R.id.fit_add);
        recyclerView = view.findViewById(R.id.fit_recyclerview);
        progressBar = view.findViewById(R.id.fit_progressbar);

        setListeners();
    }

    private void setListeners() {
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                AddCargoFragment addCargoFragment = AddCargoFragment.newInstance("4");
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.main_activity_frame, addCargoFragment, AddCargoFragment.TAG);
                fragmentTransaction.addToBackStack(AddCargoFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    private void setOrders(List<FreightItem> orders){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerOrdersAdapter(orders, getContext()));
    }

    private void getOrder(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getInvaOrders(Utility.getToken(getContext()),"4")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseOrders, this::handleErrorOrders));
    }

    private void handleResponseOrders(FreightItem.CargoResponse response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            Collections.reverse(response.getChats());
            setOrders(response.getChats());
        }else if (response.getState().equals("do not have access")){
            haveNotAccessView(response.getPrice());
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
            getOrder();
        }else {
            Toast.makeText(getContext(), getResources().getText(R.string.not_enought_balance), Toast.LENGTH_LONG).show();
        }
    }

    private void handleErrorBuy(Throwable throwable) {
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
        message.setText(getResources().getText(R.string.no_access_message) + " "+ accessPrice.getHour_price() + " тг.");
        Glide.with(getContext()).load(getResources().getDrawable(R.drawable.icon_error)).into(image);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyAccess("4", "0");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public class RecyclerOrdersAdapter extends RecyclerView.Adapter<RecyclerOrdersAdapter.ViewHolder> {
        public Context mContext;
        public List<FreightItem> orderList;

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

        public RecyclerOrdersAdapter(List<FreightItem> orderList, Context mContext) {
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
            holder.fromText.setText(orderList.get(position).getFrom_string());
            holder.toText.setText(orderList.get(position).getTo_string());
            holder.dateText.setText(setDataString(orderList.get(position).getDate()));
            holder.priceText.setText(orderList.get(position).getPrice() + " тг.");
            holder.seatsText.setVisibility(View.GONE);

            if(orderList.get(position).getModel() != null) {
                holder.modelText.setText(orderList.get(position).getSubmodel() + " " + orderList.get(position).getModel() +
                        "\n" + orderList.get(position).getComment());
            }else {
                holder.modelText.setText(orderList.get(position).getComment());
            }
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

    public static String setDataString(String miliseconds){
        String date = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm, dd MMM");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(miliseconds));
            date = formatter.format(calendar.getTime());
        }catch (Exception e){}
        return date;
    }
}
