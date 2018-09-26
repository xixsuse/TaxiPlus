package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.app.DatePickerDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.HistoryItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.OrderToDriver;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class HistoryFragment extends Fragment{
    public static final String TAG = Constants.HISTORYFRAGMENTTAG;

    private int mYear, mMonth, mDay;

    private RecyclerView recyclerView;
    private ImageButton menuIcon, calendarIcon;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;
    private RecyclerHistoryAdapter historyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        recyclerView = view.findViewById(R.id.fh_recyclerview);
        menuIcon = view.findViewById(R.id.fh_back);
        calendarIcon = view.findViewById(R.id.fh_date);
        progressBar = view.findViewById(R.id.fh_progressbar);

        setListeners();
        getHistory(String.valueOf(System.currentTimeMillis()/1000));
    }

    private void setListeners() {
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                long date = c.getTimeInMillis()/1000;

                                getHistory(String.valueOf(date));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void setRecyclerView(List<OrderToDriver> historyList){
        historyAdapter = new RecyclerHistoryAdapter(historyList, getContext());
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getHistory(String date){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getHistory(Utility.getToken(getContext()), date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(HistoryItem response) {
        progressBar.setVisibility(View.GONE);
        setRecyclerView(response.getOrders());
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerHistoryAdapter extends RecyclerView.Adapter<RecyclerHistoryAdapter.ViewHolder> {
        public Context mContext;
        public List<OrderToDriver> historyList;

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

        public RecyclerHistoryAdapter(List<OrderToDriver> historyList, Context mContext) {
            this.historyList = historyList;
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

            LatLng from = new LatLng(Double.valueOf(historyList.get(position).getFrom_latitude()),
                    Double.valueOf(historyList.get(position).getFrom_longitude()));

            LatLng to = new LatLng(Double.valueOf(historyList.get(position).getTo_latitude()),
                    Double.valueOf(historyList.get(position).getTo_longitude()));

            holder.address.setText(getAddressFromLatLngStr(from) + " - "+ getAddressFromLatLngStr(to));
            holder.date.setText(getDateToString(historyList.get(position).getDate()));
            holder.price.setText(historyList.get(position).getPrice());
        }

        @Override
        public int getItemCount() {
            return historyList.size();
        }
    }

    public String getDateToString(String time) {
        Date d = new Date(Long.valueOf(time));
        SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
        return sf.format(d);
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
}
