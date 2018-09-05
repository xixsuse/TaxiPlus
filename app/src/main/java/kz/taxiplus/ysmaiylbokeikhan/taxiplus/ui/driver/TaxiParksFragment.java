package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Model;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.TaxiPark;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

public class TaxiParksFragment extends Fragment {
    public static final String TAG = Constants.TAXIPARKSFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageButton backView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;

    public static TaxiParksFragment newInstance(String param1, String param2) {
        TaxiParksFragment fragment = new TaxiParksFragment();
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
        View view = inflater.inflate(R.layout.fragment_taxi_parks, container, false);
        initViews(view);
        getTaxiParks();
        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        backView = view.findViewById(R.id.ft_back);
        recyclerView = view.findViewById(R.id.ft_recyclerview);
        progressBar = view.findViewById(R.id.ft_progressbar);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setRecyclerView(List<TaxiPark> taxi_parks) {
        recyclerView.setAdapter(new RecyclerTaxiParksAdapter(taxi_parks, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getTaxiParks(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getTaxiParks()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(TaxiPark.GetTaxiParks response) {
        progressBar.setVisibility(View.GONE);
        setRecyclerView(response.getTaxi_parks());
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerTaxiParksAdapter extends RecyclerView.Adapter<RecyclerTaxiParksAdapter.ViewHolder> {
        public Context mContext;
        public List<TaxiPark> taxiParks;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public LinearLayout view;

            public ViewHolder(View v) {
                super(v);
                name = (TextView)v.findViewById(R.id.rti_text);
                view = (LinearLayout) v.findViewById(R.id.rti_view);
            }
        }

        public RecyclerTaxiParksAdapter(List<TaxiPark> taxiParks, Context mContext) {
            this.taxiParks = taxiParks;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_taxipark_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(taxiParks.get(position).getName());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TaxiParksFragment.class);
                    intent.putExtra(Constants.SELECTEDTAXIPARK, taxiParks.get(position));

                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                }
            });
        }

        @Override
        public int getItemCount() {
            return taxiParks.size();
        }
    }
}
