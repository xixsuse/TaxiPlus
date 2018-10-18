package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.RecyclerMenuItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class SelectModeFragment extends Fragment {
    public static final String TAG = Constants.SELECTMODEFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Place toAddress, fromAddress;

    private RecyclerView recyclerView;
    private ImageButton bachView;

    private RecyclerModeAdapter adapter;
    private FragmentTransaction fragmentTransaction;

    public static SelectModeFragment newInstance(String param1, String param2) {
        SelectModeFragment fragment = new SelectModeFragment();
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
            toAddress = getArguments().getParcelable(Constants.TOADDRESS);
            fromAddress = getArguments().getParcelable(Constants.FROMADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_mode, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.fsm_recyclerview);
        bachView = view.findViewById(R.id.fsm_back);

        setModes();
        setListeners();
    }

    private void setListeners() {
        bachView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setModes() {
        List<RecyclerMenuItem> modeList = new ArrayList<>();

        RecyclerMenuItem taxi = new RecyclerMenuItem(getResources().getString(R.string.modeTaxi),R.drawable.icon_taxi, 0);
        RecyclerMenuItem ladyTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeLadyTaxi),R.drawable.icon_taxi, 1);
        RecyclerMenuItem invaTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeInvaTaxi),R.drawable.icon_inva, 2);
        RecyclerMenuItem cityTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeCitiesTaxi),R.drawable.icon_cities_taxi, 3);
        RecyclerMenuItem cargoTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeCargoTaxi),R.drawable.icon_cargo, 4);
        RecyclerMenuItem evoTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeEvo),R.drawable.icon_evo, 5);

        modeList.add(taxi);
        modeList.add(ladyTaxi);
        modeList.add(invaTaxi);
        modeList.add(cityTaxi);
        modeList.add(cargoTaxi);
        modeList.add(evoTaxi);

        adapter = new RecyclerModeAdapter(modeList, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public class RecyclerModeAdapter extends RecyclerView.Adapter<RecyclerModeAdapter.ViewHolder> {
        public Context mContext;
        public List<RecyclerMenuItem> modeList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ImageView logo;
            public ConstraintLayout view;


            public ViewHolder(View v) {
                super(v);
                title = (TextView)v.findViewById(R.id.rmi_title);
                logo = (ImageView) v.findViewById(R.id.rmi_icon);
                view = (ConstraintLayout) v.findViewById(R.id.rmi_view);
            }
        }

        public RecyclerModeAdapter(List<RecyclerMenuItem> modeList, Context mContext) {
            this.modeList = modeList;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_mode_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.logo.setImageResource(modeList.get(position).getLogo());
            holder.title.setText(modeList.get(position).getTitle());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.MODE, position);
                    bundle.putParcelable(Constants.TOADDRESS, toAddress);
                    bundle.putParcelable(Constants.FROMADDRESS, fromAddress);

                    MakeOrderFragment makeOrderFragment = new MakeOrderFragment();
                    makeOrderFragment.setArguments(bundle);

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.add(R.id.main_activity_frame, makeOrderFragment, MakeOrderFragment.TAG);
                    fragmentTransaction.addToBackStack(MakeOrderFragment.TAG);
                    fragmentTransaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return modeList.size();
        }
    }
}
