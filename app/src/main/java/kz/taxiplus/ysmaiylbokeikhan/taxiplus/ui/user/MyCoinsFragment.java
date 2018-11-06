package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.CoinItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class MyCoinsFragment extends Fragment {
    public static final String TAG = Constants.MYCOINSFRAGMENTTAG;

    private ImageButton menuIcon;
    private RecyclerView recyclerView;
    private Button coinsButton;

    private RecyclerMyCoinsAdapter coinsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_coins, container, false);
        initViews(view);

        List<CoinItem> coinItems = new ArrayList<>();
        CoinItem item = new CoinItem("1534737997016", "40", "Эконом", "400","Новаи Жандосова","Абай 44");
        CoinItem item1 = new CoinItem("1534738392682", "60", "Эконом", "600","Жандосова 88","Новаи 12");

        coinItems.add(item);
        coinItems.add(item1);

        setRecyclerView(coinItems);

        return view;
    }

    private void setRecyclerView(List<CoinItem> coinItems) {
        coinsAdapter = new RecyclerMyCoinsAdapter(coinItems, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(coinsAdapter);
    }

    private void initViews(View view){
        menuIcon = view.findViewById(R.id.fmc_back);
        recyclerView = view.findViewById(R.id.fmc_recyclerview);
        coinsButton = view.findViewById(R.id.fmc_coins_button);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });
    }

    public class RecyclerMyCoinsAdapter extends RecyclerView.Adapter<RecyclerMyCoinsAdapter.ViewHolder> {
        public Context mContext;
        public List<CoinItem> coinItemList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView date, addressFrom, addressTo, mode, coins;
            public ConstraintLayout view;


            public ViewHolder(View v) {
                super(v);
                date = (TextView)v.findViewById(R.id.rci_date);
                addressFrom = (TextView)v.findViewById(R.id.rci_address_from);
                addressTo = (TextView)v.findViewById(R.id.rci_address_from);
                mode = (TextView)v.findViewById(R.id.rci_type);
                coins = (TextView)v.findViewById(R.id.rci_coins_number);
                view = (ConstraintLayout) v.findViewById(R.id.rci_view);
            }
        }

        public RecyclerMyCoinsAdapter(List<CoinItem> coinItems, Context mContext) {
            this.coinItemList = coinItems;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_my_coin_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.addressFrom.setText(coinItemList.get(position).getAddressFrom());
            holder.addressTo.setText(coinItemList.get(position).getAddressTo());
            holder.date.setText(getDateToString(coinItemList.get(position).getDate()));
            holder.mode.setText(coinItemList.get(position).getMode()+ ", " + coinItemList.get(position).getPrice() + " тг.");
            holder.coins.setText(coinItemList.get(position).getCoinItems() + " монет");
        }

        @Override
        public int getItemCount() {
            return coinItemList.size();
        }
    }

    public String getDateToString(String time) {
        Date d = new Date(Long.valueOf(time));
        SimpleDateFormat sf = new SimpleDateFormat("dd MMMM HH:mm");
        return sf.format(d);
    }
}
