package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewOrder;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.PayType;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Price;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain.UserMainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ModesDialogFragment extends Fragment {
    public static final String TAG = Constants.MODESFRAGMENTTAG;
    private static final String ORDER = "order";
    private static final String ARG_PARAM2 = "param2";

    private NewOrder order;
    private int selectedPayType = 0;
    private int selectedModeType = 0;

    private TextView toAddressText, fromAddressText;
    private RecyclerView modeRecyclerView, payTypeRecyclerView;
    private Button callButton;
    private ProgressBar progressBar;

    private RecyclerTaxiModeAdapter taxiModeAdapter;
    private RecyclerPayTypeAdapter payTypeAdapter;
    private CompositeSubscription subscription;

    public static ModesDialogFragment newInstance(NewOrder order) {
        ModesDialogFragment fragment = new ModesDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            order = getArguments().getParcelable(ORDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modes_dialog, container, false);

        initViews(view);
        getPrices();
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        toAddressText= view.findViewById(R.id.mf_to_text);
        fromAddressText= view.findViewById(R.id.mf_from_text);
        modeRecyclerView = view.findViewById(R.id.mf_mode_recyclerview);
        payTypeRecyclerView = view.findViewById(R.id.mf_pay_type_recyclerview);
        callButton = view.findViewById(R.id.mf_call_button);
        progressBar = view.findViewById(R.id.mf_progressbar);

        toAddressText.setText(order.getToAddess().getAddress());
        fromAddressText.setText(order.getFromAddess().getAddress());

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedModeType != 0 && selectedPayType != 0){
                    makeOrder();
                }else {
                    Toast.makeText(getContext(), getResources().getString(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void makeOrder(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .makeOrder(Utility.getToken(getContext()), order.getFromAddess().getLatitude(), order.getFromAddess().getLongitude(),
                        order.getToAddess().getLatitude(), order.getToAddess().getLongitude(),
                        selectedModeType, order.getComment(), order.getDate(), selectedPayType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseMakeOrder, this::handleErrorMakeOrder));
    }

    private void handleResponseMakeOrder(Response response) {
      if (response.getState().equals("success")){
          writePlaceToLastplaces(order.getFromAddess(), order.getToAddess());

          UserMainFragment mainFragment = (UserMainFragment) getFragmentManager().findFragmentByTag(UserMainFragment.TAG);
          if(mainFragment!= null){
              mainFragment.setWaitingState(response.getMessage());
          }

          getActivity().onBackPressed();
      }
    }

    private void handleErrorMakeOrder(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void setModes(List<Price> priceList){
        List<PayType> payTypes = new ArrayList<>();
        PayType byHand = new PayType(getResources().getString(R.string.by_hand), R.drawable.icon_by_hand, 1);
        PayType byCard = new PayType(getResources().getString(R.string.by_card), R.drawable.icon_by_card, 2);
        PayType byBonuses = new PayType(getResources().getString(R.string.by_bonuses), R.drawable.icon_by_bonuses, 3);

        payTypes.add(byCard);
        payTypes.add(byHand);
        payTypes.add(byBonuses);

        taxiModeAdapter = new RecyclerTaxiModeAdapter(priceList, getContext());
        modeRecyclerView.setAdapter(taxiModeAdapter);
        modeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), priceList.size()));

        payTypeAdapter = new RecyclerPayTypeAdapter(payTypes, getContext());
        payTypeRecyclerView.setAdapter(payTypeAdapter);
        payTypeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), payTypes.size()));
    }

    private void getPrices(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getPrices(Utility.getToken(getContext()), order.getFromAddess().getLatitude(), order.getFromAddess().getLongitude(),
                        order.getToAddess().getLatitude(), order.getToAddess().getLongitude(), order.getMode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Price.GetPrices response) {
        progressBar.setVisibility(View.GONE);
        setModes(response.getPrice_list());
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerTaxiModeAdapter extends RecyclerView.Adapter<RecyclerTaxiModeAdapter.ViewHolder> {
        public Context mContext;
        public List<Price> priceList;
        public int lastPressedPosition = -1;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title, price;
            public ImageView logo;
            public LinearLayout view;
            public View left;


            public ViewHolder(View v) {
                super(v);
                title = (TextView)v.findViewById(R.id.rtmi_text);
                price = (TextView)v.findViewById(R.id.rtmi_price);
                logo = (ImageView) v.findViewById(R.id.rtmi_icon);
                view = (LinearLayout) v.findViewById(R.id.rtmi_view);
                left = (View) v.findViewById(R.id.rtmi_left_line);
            }
        }

        public RecyclerTaxiModeAdapter(List<Price> priceList, Context mContext) {
            this.priceList = priceList;
            this.mContext = mContext;
        }

        @Override
        public RecyclerTaxiModeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_taxi_mode_item, parent, false);

            RecyclerTaxiModeAdapter.ViewHolder vh = new RecyclerTaxiModeAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerTaxiModeAdapter.ViewHolder holder, final int position) {
            if(position == 0){
                holder.left.setVisibility(View.GONE);
            }

            if(position == lastPressedPosition){
                holder.price.setTextColor(getResources().getColor(R.color.colorPrimary));
                holder.title.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else {
                holder.price.setTextColor(getResources().getColor(R.color.gray));
                holder.title.setTextColor(getResources().getColor(R.color.gray));
            }

            if(position == lastPressedPosition){
                Glide.with(getContext()).load(priceList.get(position).getImg1()).into(holder.logo);
            }else {
                Glide.with(getContext()).load(priceList.get(position).getImg()).into(holder.logo);
            }

            holder.title.setText(priceList.get(position).getService_name());
            holder.price.setText(priceList.get(position).getPrice()+ " тг");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastPressedPosition = position;
                    selectedModeType = Integer.valueOf(priceList.get(position).getService_id());
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return priceList.size();
        }
    }

    public class RecyclerPayTypeAdapter extends RecyclerView.Adapter<RecyclerPayTypeAdapter.ViewHolder> {
        public Context mContext;
        public List<PayType> payTypes;
        public int lastPressedPosition = -1;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ImageView logo;
            public LinearLayout view;
            public View left;


            public ViewHolder(View v) {
                super(v);
                title = (TextView)v.findViewById(R.id.rpti_text);
                logo = (ImageView) v.findViewById(R.id.rpti_icon);
                view = (LinearLayout) v.findViewById(R.id.rpti_view);
                left = (View) v.findViewById(R.id.rpti_left_line);
            }
        }

        public RecyclerPayTypeAdapter(List<PayType> payTypes, Context mContext) {
            this.payTypes = payTypes;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_pay_type_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if(position == 0){
                holder.left.setVisibility(View.GONE);
            }

            if(position == lastPressedPosition){
                holder.title.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else {
                holder.title.setTextColor(getResources().getColor(R.color.gray));
            }

            if(position == lastPressedPosition){
                if(payTypes.get(position).getName().equals(getResources().getString(R.string.by_hand))){
                    holder.logo.setImageResource(R.drawable.icon_by_hand_p);
                }else if(payTypes.get(position).getName().equals(getResources().getString(R.string.by_card))){
                    holder.logo.setImageResource(R.drawable.icon_by_card_p);
                }else if(payTypes.get(position).getName().equals(getResources().getString(R.string.by_bonuses))){
                    holder.logo.setImageResource(R.drawable.icon_by_bonuses_p);
                }
            }else {
                holder.logo.setImageResource(payTypes.get(position).getIcon());
            }

            holder.title.setText(payTypes.get(position).getName());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastPressedPosition = position;
                    selectedPayType = payTypes.get(position).getId();
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return payTypes.size();
        }
    }

    private void writePlaceToLastplaces(Place from, Place to){
        List<Place> lastplaces = Paper.book().read(Constants.LASTPLACES, new ArrayList<>());
        lastplaces.add(from);
        lastplaces.add(to);

        if(lastplaces.size() > 2){
            int i = lastplaces.size()-3;
            while (i >=0 ){
                lastplaces.remove(i);
                i--;
            }
        }
        Paper.book().write(Constants.LASTPLACES, lastplaces);
    }
}
