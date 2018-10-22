package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.CitiesResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;


public class SelectCityFragment extends Fragment {
   public static String TAG = Constants.SELECTCITYFRAGMENT;

   private List<CitiesResponse.City> cityList;

    private ImageButton backView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText searchView;

    private CompositeSubscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_city, container, false);
        initViews(view);

        getCities();
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        backView = view.findViewById(R.id.fsc_back);
        searchView = view.findViewById(R.id.fsc_search_edittext);
        recyclerView = view.findViewById(R.id.fsc_recyclerview);
        progressBar = view.findViewById(R.id.fsc_progressbar);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                if(newText != null || newText != "") {
                    List<CitiesResponse.City> citiesListForSearch = new ArrayList<>();
                    if(cityList != null) {
                        for (CitiesResponse.City city : cityList) {
                            if (city.getName().toLowerCase().contains(newText.toString().toLowerCase())) {
                                citiesListForSearch.add(city);
                            }
                        }
                        setAdapter(citiesListForSearch);
                    }
                }else {
                    setAdapter(cityList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setAdapter(List<CitiesResponse.City> cities){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerCitiesAdapter(cities, getContext()));
    }

    private void getCities(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getCities()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(CitiesResponse response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            this.cityList = response.getCities();
            setAdapter(cityList);
        }
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerCitiesAdapter extends RecyclerView.Adapter<RecyclerCitiesAdapter.ViewHolder> {
        public Context mContext;
        public List<CitiesResponse.City> cityList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView city, region;
            public LinearLayout view;

            public ViewHolder(View v) {
                super(v);
                city = (TextView)v.findViewById(R.id.rci_city_text);
                region = (TextView)v.findViewById(R.id.rci_region_text);
                view = (LinearLayout) v.findViewById(R.id.rci_view);
            }
        }

        public RecyclerCitiesAdapter(List<CitiesResponse.City> cityList, Context mContext) {
            this.cityList = cityList;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_city_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.city.setText(cityList.get(position).getCname());
            holder.region.setText(cityList.get(position).getName());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), SelectCityFragment.class);
                    intent.putExtra(Constants.SELECTEDCITY, cityList.get(position));

                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                }
            });
        }

        @Override
        public int getItemCount() {
            return cityList.size();
        }
    }
}
