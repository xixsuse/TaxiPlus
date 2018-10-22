package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Place;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.MyPlacesFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.PlaceArrayAdapter;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;

import static android.app.Activity.RESULT_OK;

public class FromAndToFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    public static final String TAG = Constants.FROMFRAGMENTTAG;
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private int mode = 0;

    private ConstraintLayout onMapView, savedPlaceView;

    private RecyclerView recyclerView;
    private AutoCompleteTextView addressEditText;
    private RecyclerPlaceAdapter adapter;
    private FragmentTransaction fragmentTransaction;

    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private AutocompleteFilter filter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_from, container, false);
        Paper.init(getContext());

        initViews(view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    private void initViews(View view){
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

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

        filter = new AutocompleteFilter.Builder().setCountry("KZ").build();
        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1,
                toBounds(new LatLng(43.249940, 76.895426), 15000), filter);
        addressEditText.setAdapter(mPlaceArrayAdapter);
        addressEditText.setOnItemClickListener(mAutocompleteClickListener);
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
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = places -> {
        if (places.getStatus().isSuccess()) {
            final com.google.android.gms.location.places.Place place = places.get(0);
            Place returnPlace = new Place(place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
            Intent intent = new Intent(getContext(), FromAndToFragment.class);
            intent.putExtra("address", returnPlace);

            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
            getFragmentManager().popBackStack();
            Utility.dismissKeyboard(getActivity());
        }
    };

    private void setPlaces() {
        List<Place> places = Paper.book().read(Constants.LASTPLACES, new ArrayList<>());

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                    Intent intent = new Intent(getContext(), FromAndToFragment.class);
                    intent.putExtra("address", placeList.get(position));

                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                }
            });
        }

        @Override
        public int getItemCount() {
            return placeList.size();
        }
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }
}
