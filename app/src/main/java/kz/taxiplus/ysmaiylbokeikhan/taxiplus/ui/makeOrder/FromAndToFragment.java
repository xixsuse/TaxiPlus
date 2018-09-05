package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.MainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

import static android.app.Activity.RESULT_OK;

public class FromAndToFragment extends Fragment {
    public static final String TAG = Constants.FROMFRAGMENTTAG;
    private int mode = 0;

    private ConstraintLayout onMapView, savedPlaceView;

    private RecyclerView recyclerView;
    private EditText addressEditText;
    private RecyclerPlaceAdapter adapter;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_from, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        recyclerView = view.findViewById(R.id.ff_recyclerview);
        savedPlaceView = view.findViewById(R.id.ff_save_place);
        onMapView = view.findViewById(R.id.ff_on_map);
        addressEditText = view.findViewById(R.id.ff_mode_edittext);

        mode = getArguments().getInt(Constants.MODE);
        if(mode == 1){
            addressEditText.setHint(getResources().getText(R.string.from));
        }else {
            addressEditText.setHint(getResources().getText(R.string.to));
        }

        setPlaces();
        setListeners();
    }

    private void setListeners() {
        savedPlaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                MyPlacesFragment myPlacesFragment = MyPlacesFragment.newInstance(false, mode);
                myPlacesFragment.setTargetFragment(FromAndToFragment.this, Constants.FROMANDTOFRAGMENTCODESAVED);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.add(R.id.main_activity_frame, myPlacesFragment, MyPlacesFragment.TAG);
                fragmentTransaction.addToBackStack(MyPlacesFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        onMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FromAndToFragment.class);
                intent.putExtra(Constants.FROMMAP, "yes");
                intent.putExtra(Constants.MODE, mode);

                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                getFragmentManager().popBackStack();
            }
        });

        addressEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onMapSearch(addressEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    public void onMapSearch(String location) {
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(getContext(), getResources().getConfiguration().locale);
            try {
                addressList = geocoder.getFromLocationName(location, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(addressList != null && addressList.size() > 0){
                Address address = addressList.get(0);
                String title = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));

                Place place = new Place(title, address.getLatitude(), address.getLongitude());

                Intent intent = new Intent(getContext(), FromAndToFragment.class);
                intent.putExtra("address", place);

                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                getFragmentManager().popBackStack();
            }else {
                Toast.makeText(getContext(), getResources().getString(R.string.not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    // TODO: set last selected two places
    private void setPlaces() {
        List<Place> places = new ArrayList<>();

        Place place = new Place("Абая - Манаса");
        Place place1 = new Place("Жандосова - Манаса");

        places.add(place);
        places.add(place1);

        adapter = new RecyclerPlaceAdapter(places, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.FROMANDTOFRAGMENTCODESAVED){
                Place place = data.getParcelableExtra(Constants.ADDRESS);

                Intent intent = new Intent(getContext(), FromAndToFragment.class);
                intent.putExtra("address", place);

                getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                getFragmentManager().popBackStack();
            }
        }
    }

    public class RecyclerPlaceAdapter extends RecyclerView.Adapter<RecyclerPlaceAdapter.ViewHolder> {
        public Context mContext;
        public List<Place> placeList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ConstraintLayout view;


            public ViewHolder(View v) {
                super(v);
                title = (TextView)v.findViewById(R.id.rspi_text);
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

                }
            });
        }

        @Override
        public int getItemCount() {
            return placeList.size();
        }
    }
}
