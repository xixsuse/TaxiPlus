package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.HistoryItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;


public class HistoryFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    public static final String TAG = Constants.HISTORYFRAGMENTTAG;

    private int mYear, mMonth, mDay;

    private RecyclerView recyclerView;
    private ImageButton menuIcon, calendarIcon;

    private RecyclerHistoryAdapter historyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(view);

        List<HistoryItem> historyItems = new ArrayList<>();
        HistoryItem historyItem = new HistoryItem("Новаи 11","Жандосова 32", "1534737997016", "600");
        HistoryItem historyItem1 = new HistoryItem("Жандосова 32","Новаи 11", "1534737997016", "900");
        historyItems.add(historyItem);
        historyItems.add(historyItem1);

        setRecyclerView(historyItems);

        return view;
    }

    private void initViews(View view){
        recyclerView = view.findViewById(R.id.fh_recyclerview);
        menuIcon = view.findViewById(R.id.fh_back);
        calendarIcon = view.findViewById(R.id.fh_date);

        setListeners();
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
                                Log.d("date",dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void setRecyclerView(List<HistoryItem> historyItemList){
        historyAdapter = new RecyclerHistoryAdapter(historyItemList, getContext());
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    public class RecyclerHistoryAdapter extends RecyclerView.Adapter<RecyclerHistoryAdapter.ViewHolder> {
        public Context mContext;
        public List<HistoryItem> historyList;

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

        public RecyclerHistoryAdapter(List<HistoryItem> historyList, Context mContext) {
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
            holder.address.setText(historyList.get(position).getAddressFrom() + " - "+ historyList.get(position).getAddressTo());
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
}
