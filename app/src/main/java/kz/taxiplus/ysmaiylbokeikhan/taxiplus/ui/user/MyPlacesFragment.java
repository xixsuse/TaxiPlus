package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

public class MyPlacesFragment extends Fragment {
    public static final String TAG = Constants.MYPLACESFRAGMENTTAG;
    private static final String ISMENU = "isMenu";
    private static final String MODE = "mode";

    private boolean isMenu;
    private int modeNumber = 0;

    private ImageButton menuIcon, addView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private FragmentTransaction fragmentTransaction;
    private RecyclerPlaceAdapter adapter;
    private CompositeSubscription subscription;

    public static MyPlacesFragment newInstance(boolean isMenu, int mode) {
        MyPlacesFragment fragment = new MyPlacesFragment();
        Bundle args = new Bundle();
        args.putBoolean(ISMENU, isMenu);
        args.putInt(MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isMenu = getArguments().getBoolean(ISMENU);
            modeNumber = getArguments().getInt(MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_places, container, false);
        initViews(view);

        getPlaces(Utility.getToken(getContext()));
        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        recyclerView = view.findViewById(R.id.fmp_recyclerview);
        menuIcon = view.findViewById(R.id.fmp_back);
        addView = view.findViewById(R.id.fmp_add);
        progressBar = view.findViewById(R.id.fmp_progressbar);

        if(isMenu) {
            menuIcon.setBackground(getResources().getDrawable(R.drawable.icon_menu_white));
        }else {
            menuIcon.setBackground(getResources().getDrawable(R.drawable.icon_back));
        }

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMenu){
                    ((MainActivity)getActivity()).drawerAction();
                }else {
                    getActivity().onBackPressed();
                }
            }
        });

        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                AddPlaceFragment addPlaceFragment = new AddPlaceFragment();

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.main_activity_frame, addPlaceFragment, AddPlaceFragment.TAG);
                fragmentTransaction.addToBackStack(AddPlaceFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    private void setRecyclerView(List<Place> places) {
        adapter = new RecyclerPlaceAdapter(places, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getPlaces(String token){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getFavPlaces(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Place.GetPlaces response) {
        progressBar.setVisibility(View.GONE);

        if (response.getState().equals("success")){
            setRecyclerView(response.getAddresses());
        }
    }

    private void handleError(Throwable throwable) {

    }

    public class RecyclerPlaceAdapter extends RecyclerView.Adapter<RecyclerPlaceAdapter.ViewHolder> {
        public Context mContext;
        public List<Place> placeList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title, nick;
            public ConstraintLayout view;

            public ViewHolder(View v) {
                super(v);
                title = (TextView)v.findViewById(R.id.rspi_text);
                nick = (TextView)v.findViewById(R.id.rspi_nick);
                view = (ConstraintLayout) v.findViewById(R.id.rspi_view);
            }
        }

        public RecyclerPlaceAdapter(List<Place> placeList, Context mContext) {
            this.placeList = placeList;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_saved_places_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.title.setText(placeList.get(position).getAddress());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(modeNumber != 0) {
                        Intent intent = new Intent(getContext(), MyPlacesFragment.class);
                        intent.putExtra(Constants.ADDRESS, placeList.get(position));

                        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                        getFragmentManager().popBackStack();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return placeList.size();
        }
    }
}
