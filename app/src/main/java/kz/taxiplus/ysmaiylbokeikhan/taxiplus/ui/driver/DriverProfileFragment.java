package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Facility;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Model;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.TaxiPark;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.DialogFragments.CancelOrderDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
public class DriverProfileFragment extends Fragment{
    public static final String TAG = Constants.DRIVERPROFILEFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private int male = 0;
    private User user;
    private Model selectedModel;
    private Model selectedSubmodel;
    private List<Facility> selectedFacilities = new ArrayList<>();

    private EditText nameEditText, phoneEditText, emailEditText, gosNumberEditText, yearEditText;
    private TextView carModelText, carSubmodelText;
    private LinearLayout carModelView, carSubmodelView;
    private ImageButton menuIcon;
    private RecyclerView facilitiesRecyclerView;
    private ImageView logo;
    private ProgressBar progressBar;
    private Button registerButton, maleButton, femaleButton;

    private CompositeSubscription subscription;
    private FragmentTransaction fragmentTransaction;

    public static DriverProfileFragment newInstance(String param1, String param2) {
        DriverProfileFragment fragment = new DriverProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);
        Paper.init(getContext());
        initViews(view);
        getFacilities();
        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();

        user = Paper.book().read(Constants.USER);

        nameEditText = view.findViewById(R.id.fdp_name_edittext);
        phoneEditText = view.findViewById(R.id.fdp_phone_edittext);
        emailEditText = view.findViewById(R.id.fdp_email_edittext);
        gosNumberEditText = view.findViewById(R.id.fdp_gos_number_edittext);
        yearEditText = view.findViewById(R.id.fdp_year_edittext);

        carModelText = view.findViewById(R.id.fdp_car_model_text);
        carModelView = view.findViewById(R.id.fdp_car_model_view);

        carSubmodelView = view.findViewById(R.id.fdp_car_submodel_view);
        carSubmodelText = view.findViewById(R.id.fdp_car_submodel_text);

        registerButton = view.findViewById(R.id.fdp_register_button);
        maleButton = view.findViewById(R.id.fdp_male_button);
        femaleButton = view.findViewById(R.id.fdp_female_button);

        menuIcon = view.findViewById(R.id.fdp_menu_view);
        progressBar = view.findViewById(R.id.fdp_progressbar);
        facilitiesRecyclerView = view.findViewById(R.id.fdp_facilities_recyclerview);
        logo = view.findViewById(R.id.fdp_logo);

        nameEditText.setText(user.getName());
        phoneEditText.setText(user.getPhone());

        Glide.with(getContext()).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS1qA-_Sk2ctUnAl9RXfBHQ5WYOMh04hnZ9SnkbaNhhgaIxRpn20Q")
                .apply(RequestOptions.circleCropTransform())
                .into(logo);

        setListeners();
    }

    private void setRecyclerView(List<Facility> facilities) {
        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        facilitiesRecyclerView.setAdapter(new RecyclerFacilitiesAdapter(facilities, getContext()));
    }

    private void setListeners() {
        carModelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle b = new Bundle();
                b.putBoolean(Constants.MODELMODE, true);

                ModelsFragment modelsFragment = new ModelsFragment();
                modelsFragment.setTargetFragment(DriverProfileFragment.this, Constants.DRIVERPROFILEFRAGMENTCODEMODEL);
                modelsFragment.setArguments(b);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.add(R.id.main_activity_frame, modelsFragment, ModelsFragment.TAG);
                fragmentTransaction.addToBackStack(ModelsFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        carSubmodelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedModel != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    Bundle b = new Bundle();
                    b.putBoolean(Constants.MODELMODE, false);
                    b.putString(Constants.MODELID, selectedModel.getId());

                    ModelsFragment modelsFragment = new ModelsFragment();
                    modelsFragment.setTargetFragment(DriverProfileFragment.this, Constants.DRIVERPROFILEFRAGMENTCODESUBMODEL);
                    modelsFragment.setArguments(b);

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.add(R.id.main_activity_frame, modelsFragment, ModelsFragment.TAG);
                    fragmentTransaction.addToBackStack(ModelsFragment.TAG);
                    fragmentTransaction.commit();
                }
            }
        });

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(checkFields()){
                   HashMap<String, Object> objectHashMap = new HashMap<>();
                   objectHashMap.put("token", Utility.getToken(getContext()));
                   objectHashMap.put("gender", 2);
                   objectHashMap.put("car_number", gosNumberEditText.getText().toString());
                   objectHashMap.put("car_model", selectedSubmodel.getId());
                   objectHashMap.put("year_of_birth", "1900");
                   objectHashMap.put("car_year", yearEditText.getText().toString());
                   objectHashMap.put("facilities", getSelectedFacilities());


                   driverAuth(objectHashMap);
               }else {
                   Toast.makeText(getContext(), getResources().getString(R.string.fill_fields), Toast.LENGTH_LONG).show();
               }
            }
        });

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male = 2;
                maleButton.setBackground(getResources().getDrawable(R.drawable.rounded_green_view));
                maleButton.setTextColor(getResources().getColor(R.color.white));

                femaleButton.setBackground(getResources().getDrawable(R.drawable.border_for_view));
                femaleButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male = 1;
                maleButton.setBackground(getResources().getDrawable(R.drawable.border_for_view));
                maleButton.setTextColor(getResources().getColor(R.color.colorAccent));

                femaleButton.setBackground(getResources().getDrawable(R.drawable.rounded_green_view));
                femaleButton.setTextColor(getResources().getColor(R.color.white));
            }
        });
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
            user.setRole_id("2");
            user.setCar_year(yearEditText.getText().toString());
            user.setCar(selectedModel.getModel() + " " + selectedSubmodel.getModel());
            user.setCar_number(gosNumberEditText.getText().toString());

            Paper.book().write(Constants.USER, user);
            ((MainActivity)getActivity()).recreateActivity();
        }
    }

    private void handleErrorAuth(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), getResources().getString(R.string.try_later), Toast.LENGTH_LONG).show();
    }

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
        setRecyclerView(response.getFacilities());
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.DRIVERPROFILEFRAGMENTCODEMODEL) {
                if(selectedModel != null){
                    selectedSubmodel = null;
                    carSubmodelText.setText(getResources().getText(R.string.car_type));
                }
                selectedModel = data.getParcelableExtra(Constants.SELECTEDMODEL);
                carModelText.setText(selectedModel.getModel());
            }else if(requestCode == Constants.DRIVERPROFILEFRAGMENTCODESUBMODEL) {
                selectedSubmodel = data.getParcelableExtra(Constants.SELECTEDMODEL);
                carSubmodelText.setText(selectedSubmodel.getModel());
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

    private List<String> getSelectedFacilities() {
        List<String> facilities = new ArrayList<>();

        for(int i = 0;i<selectedFacilities.size();i++){
            facilities.add(selectedFacilities.get(i).getId());
        }

        return facilities;
    }

    private boolean checkFields(){
        boolean isCorrect = false;
        if(selectedModel!=null && selectedSubmodel!=null && !gosNumberEditText.getText().toString().isEmpty() &&
                !yearEditText.getText().toString().isEmpty() ){
            isCorrect = true;
        }

        return isCorrect;
    }
}
