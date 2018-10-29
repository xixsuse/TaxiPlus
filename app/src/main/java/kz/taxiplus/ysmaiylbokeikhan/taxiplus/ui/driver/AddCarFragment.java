package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Car;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.CarType;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Facility;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Model;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

public class AddCarFragment extends Fragment{
    public static final String TAG = Constants.ADDCARFRAGMENT;

    private RecyclerView facilitiesRecyclerView;
    private EditText gosNunber, yearEdit, seatNumbers;
    private LinearLayout modelView, submodelView;
    private TextView modelText, submodelText;
    private Button addButton;
    private ProgressBar progressBar;
    private ImageButton backView;
    private Spinner typeSpinner;

    private List<Facility> selectedFacilities = new ArrayList<>();
    private Model selectedModel, selectedSubmodel;
    private CarType selectedCarType;
    private User user;
    private Car newCar;

    private FragmentTransaction fragmentTransaction;
    private CompositeSubscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_car_dialog, container, false);
        initViews(view);

        getFacilities();
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        facilitiesRecyclerView = view.findViewById(R.id.facf_facilities_recyclerview);

        gosNunber = view.findViewById(R.id.facf_gos_number_edittext);
        yearEdit = view.findViewById(R.id.facf_year_edittext);
        seatNumbers = view.findViewById(R.id.facf_seats_edittext);

        modelView = view.findViewById(R.id.facf_car_model_view);
        submodelView = view.findViewById(R.id.facf_car_submodel_view);

        modelText = view.findViewById(R.id.facf_car_model_text);
        submodelText = view.findViewById(R.id.facf_car_submodel_text);
        addButton = view.findViewById(R.id.facf_add_button);
        progressBar = view.findViewById(R.id.facf_progressbar);
        backView = view.findViewById(R.id.facf_back);
        typeSpinner = view.findViewById(R.id.facf_spinner);

        user = Paper.book().read(Constants.USER);

        setListeners();
    }

    private void setListeners() {
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        modelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle b = new Bundle();
                b.putBoolean(Constants.MODELMODE, true);

                ModelsFragment modelsFragment = new ModelsFragment();
                modelsFragment.setTargetFragment(AddCarFragment.this, Constants.DRIVERPROFILEFRAGMENTCODEMODEL);
                modelsFragment.setArguments(b);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.add(R.id.main_activity_frame, modelsFragment, ModelsFragment.TAG);
                fragmentTransaction.addToBackStack(ModelsFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        submodelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedModel != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    Bundle b = new Bundle();
                    b.putBoolean(Constants.MODELMODE, false);
                    b.putString(Constants.MODELID, selectedModel.getId());

                    ModelsFragment modelsFragment = new ModelsFragment();
                    modelsFragment.setTargetFragment(AddCarFragment.this, Constants.DRIVERPROFILEFRAGMENTCODESUBMODEL);
                    modelsFragment.setArguments(b);

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.add(R.id.main_activity_frame, modelsFragment, ModelsFragment.TAG);
                    fragmentTransaction.addToBackStack(ModelsFragment.TAG);
                    fragmentTransaction.commit();
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()){
                    HashMap<String, Object> objectHashMap = new HashMap<>();
                    objectHashMap.put("token", Utility.getToken(getContext()));
                    objectHashMap.put("car_number", gosNunber.getText().toString());
                    objectHashMap.put("car_model", selectedSubmodel.getId());
                    objectHashMap.put("car_year", yearEdit.getText().toString());
                    objectHashMap.put("seats_number", seatNumbers.getText().toString());
                    objectHashMap.put("facilities", getSelectedFacilities());
                    objectHashMap.put("type", selectedCarType.getType());

                    driverAuth(objectHashMap);
                }else {
                    Toast.makeText(getContext(), getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //helper functions
    private void setTypesRecyclerView(List<Facility> facilities) {
        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        facilitiesRecyclerView.setAdapter(new RecyclerFacilitiesAdapter(facilities, getContext()));

        CarType usual = new CarType("1", getResources().getString(R.string.modeTaxi), R.drawable.icon_taxi);
        CarType cargo = new CarType("2", getResources().getString(R.string.mode_cargo), R.drawable.icon_cargo);
        CarType intercity = new CarType("1", getResources().getString(R.string.intercity), R.drawable.icon_cities_taxi);
        CarType inva = new CarType("4", getResources().getString(R.string.modeInvaTaxi), R.drawable.icon_inva);
        CarType evac = new CarType("3",getResources().getString(R.string.modeEvo),  R.drawable.icon_evo);

        List<CarType> carTypes = new ArrayList<>();
        carTypes.add(usual);
        carTypes.add(cargo);
        carTypes.add(intercity);
        carTypes.add(evac);
        carTypes.add(inva);

        selectedCarType = usual;
        typeSpinner.setAdapter(new CustomTypesAdapter(getContext(), R.layout.recyclerview_car_type_item, carTypes));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCarType = carTypes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean checkFields(){
        boolean isCorrect = false;
        if(selectedCarType != null && selectedModel!=null && selectedSubmodel!=null && !gosNunber.getText().toString().isEmpty() &&
                !yearEdit.getText().toString().isEmpty() && !seatNumbers.getText().toString().isEmpty()){
            isCorrect = true;
        }

        return isCorrect;
    }

    private List<String> getSelectedFacilities() {
        List<String> facilities = new ArrayList<>();

        for(int i = 0;i<selectedFacilities.size();i++){
            facilities.add(selectedFacilities.get(i).getId());
        }

        return facilities;
    }

    //requests
    private void getFacilities(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getFacilities()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Facility.GetFacilities response) {
        progressBar.setVisibility(View.GONE);
        setTypesRecyclerView(response.getFacilities());
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void driverAuth(HashMap<String, Object> body){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .driverRegistration(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAuth, this::handleErrorAuth));
    }

    private void handleResponseAuth(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            Toast.makeText(getContext(), getResources().getText(R.string.successfully_added), Toast.LENGTH_LONG).show();
            newCar = response.getCar();
            newCar.setSubmodel(selectedSubmodel.getModel());
            newCar.setModel(selectedModel.getModel());
            user.getCars().add(newCar);
            Paper.book().write(Constants.USER, user);
            ((MainActivity) Objects.requireNonNull(getActivity())).getUser();
            getActivity().onBackPressed();
        }
    }

    private void handleErrorAuth(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), getResources().getString(R.string.try_later), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.DRIVERPROFILEFRAGMENTCODEMODEL) {
                if(selectedModel != null){
                    selectedSubmodel = null;
                    submodelText.setText(getResources().getText(R.string.car_type));
                }
                selectedModel = data.getParcelableExtra(Constants.SELECTEDMODEL);
                modelText.setText(selectedModel.getModel());
            }else if(requestCode == Constants.DRIVERPROFILEFRAGMENTCODESUBMODEL) {
                selectedSubmodel = data.getParcelableExtra(Constants.SELECTEDMODEL);
                submodelText.setText(selectedSubmodel.getModel());
            }
        }
    }

    public class RecyclerFacilitiesAdapter extends RecyclerView.Adapter<RecyclerFacilitiesAdapter.ViewHolder> {
        public Context mContext;
        public List<Facility> facilities;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text;
            public CheckBox checkBox;
            public LinearLayout view;

            public ViewHolder(View v) {
                super(v);
                checkBox = (CheckBox) v.findViewById(R.id.rfi_checkbox);
                text = (TextView)v.findViewById(R.id.rfi_text);
                view = (LinearLayout) v.findViewById(R.id.rfi_view);
            }
        }

        public RecyclerFacilitiesAdapter(List<Facility> facilities, Context mContext) {
            this.facilities = facilities;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_facilitiy_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.text.setText(facilities.get(position).getName());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!selectedFacilities.contains(facilities.get(position))){
                        selectedFacilities.add(facilities.get(position));
                    }else {
                        selectedFacilities.remove(facilities.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return facilities.size();
        }
    }

    public class CustomTypesAdapter extends ArrayAdapter<CarType> {
        private List<CarType> carTypes;
        private Context context;

        public CustomTypesAdapter(Context context, int textViewResourceId, List<CarType> carTypes) {
            super(context, textViewResourceId, carTypes);
            this.carTypes = carTypes;
            this.context = context;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.recyclerview_car_type_item, parent, false);

            TextView title=(TextView)row.findViewById(R.id.rct_title);
            title.setText(carTypes.get(position).getTitle());

            ImageView icon=(ImageView)row.findViewById(R.id.rct_image);
            Glide.with(context).load(carTypes.get(position).getImage()).into(icon);

            return row;
        }
    }
}
