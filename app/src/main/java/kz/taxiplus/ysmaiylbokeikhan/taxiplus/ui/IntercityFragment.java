package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DirectionResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.IntercityMakeOrderFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.IntercityOrdersFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class IntercityFragment extends Fragment {
    public static final String TAG = Constants.INTERCITYFRAGMENTTAG;

    private List<DirectionResponse.Direction> directions;

    private ImageButton menuIcon, addIcon;
    private EditText searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intercity, container, false);
        initViews(view);

        getDirection("1");
        return view;
    }


    private void initViews(View view){
        subscription = new CompositeSubscription();
        menuIcon = view.findViewById(R.id.fi_menu);
        searchView = view.findViewById(R.id.fi_search_edittext);
        addIcon = view.findViewById(R.id.fi_add);
        recyclerView = view.findViewById(R.id.fi_recyclerview);
        progressBar = view.findViewById(R.id.fi_progressbar);

        setListeners();
    }

    private void setListeners() {
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                if(newText != null || newText != "") {
                    List<DirectionResponse.Direction> directListForSearch = new ArrayList<>();
                    if(directions != null) {
                        for (DirectionResponse.Direction direct : directions) {
                            if (direct.getStart().toLowerCase().contains(newText.toString().toLowerCase()) ||
                                    direct.getEnd().toLowerCase().contains(newText.toString().toLowerCase())){
                                directListForSearch.add(direct);
                            }
                        }
                        setDirections(directListForSearch);
                    }
                }else {
                    setDirections(directions);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                IntercityMakeOrderFragment makeOrderFragment = new IntercityMakeOrderFragment();

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.main_activity_frame, makeOrderFragment, IntercityMakeOrderFragment.TAG);
                fragmentTransaction.addToBackStack(IntercityMakeOrderFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    private void setDirections(List<DirectionResponse.Direction> directionList){
        recyclerView.setAdapter(new RecyclerDirectionsAdapter(directionList, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getDirection(String type){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getDirections(Utility.getToken(getContext()), type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseDirections, this::handleErrorDirections));
    }

    private void handleResponseDirections(DirectionResponse response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            this.directions = response.getChats();
            setDirections(directions);
        }
    }

    private void handleErrorDirections(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerDirectionsAdapter extends RecyclerView.Adapter<RecyclerDirectionsAdapter.ViewHolder> {
        public Context mContext;
        public List<DirectionResponse.Direction> directionList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView fromText, toText;
            public LinearLayout view;

            public ViewHolder(View v) {
                super(v);
                fromText = (TextView)v.findViewById(R.id.rdi_from);
                toText = (TextView) v.findViewById(R.id.rdi_to);
                view = (LinearLayout) v.findViewById(R.id.rdi_info_view);
            }
        }

        public RecyclerDirectionsAdapter(List<DirectionResponse.Direction> directions, Context mContext) {
            this.directionList = directions;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_direction_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.fromText.setText(directionList.get(position).getStart());
            holder.toText.setText(directionList.get(position).getEnd());

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    IntercityOrdersFragment ordersFragment = IntercityOrdersFragment.newInstance(directionList.get(position));

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.add(R.id.main_activity_frame, ordersFragment, IntercityOrdersFragment.TAG);
                    fragmentTransaction.addToBackStack(IntercityOrdersFragment.TAG);
                    fragmentTransaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return directionList.size();
        }
    }
}
