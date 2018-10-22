package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.CitiesResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Facility;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Model;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.TaxiPark;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.authorization.AuthThirdStepFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.ModelsFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.SelectCityFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment {
    public static final String TAG = Constants.USERPROFILEFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private User user;
    private boolean isLocked = true;
    private List<String> selectedFacilities = new ArrayList<>();
    private Facility.GetFacilities facilities;
    private TaxiPark selectedPark;

    private ProgressBar progressBar;
    private ImageButton backView, editIcon;
    private ConstraintLayout driverInfo;
    private Button saveButton;
    private TextView nameText, phoneText, coinsText, parkText, cityText;
    private EditText emailEditText, modelEditText, subModelEditText, yearEditText, numberEditText;
    private RecyclerView facilitiesRecyclerView;

    private Model selectedModel;
    private Model selectedSubModel;
    private CitiesResponse.City selectedCity;


    private CompositeSubscription subscription;
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Paper.init(getContext());
        initViews(view);

        getUser();
        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        user = Paper.book().read(Constants.USER);
        backView = view.findViewById(R.id.fs_back);
        nameText = view.findViewById(R.id.fs_name);
        phoneText = view.findViewById(R.id.fs_phone);
        coinsText = view.findViewById(R.id.fs_bonuses);
        cityText = view.findViewById(R.id.fs_city);
        progressBar = view.findViewById(R.id.ff_progressbar);
        editIcon = view.findViewById(R.id.fs_edit);
        emailEditText = view.findViewById(R.id.fs_email);
        modelEditText = view.findViewById(R.id.fs_model);
        subModelEditText = view.findViewById(R.id.fs_submodel);
        yearEditText = view.findViewById(R.id.fs_year);
        numberEditText = view.findViewById(R.id.fs_number);
        parkText = view.findViewById(R.id.fs_park);
        saveButton = view.findViewById(R.id.fs_save_button);
        driverInfo = view.findViewById(R.id.fs_driver_view);
        facilitiesRecyclerView = view.findViewById(R.id.fs_facilities_recyclerview);

        lockEditTexts(isLocked);
        setEditableCity(isLocked);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getRole_id().equals("2")) {
                    isLocked = !isLocked;
                    setRecyclerView(facilities.getFacilities(), !isLocked);
                    lockEditTexts(isLocked);
                }else {
                    isLocked = !isLocked;
                    setEditableCity(isLocked);
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!modelEditText.getText().toString().isEmpty() && !subModelEditText.getText().toString().isEmpty()
                    &&!yearEditText.getText().toString().isEmpty() && !numberEditText.getText().toString().isEmpty()){
                    HashMap<String, Object> objectHashMap = new HashMap<>();
                    objectHashMap.put("token", Utility.getToken(getContext()));
                    objectHashMap.put("gender", 1);
                    objectHashMap.put("car_number", numberEditText.getText().toString());
                    objectHashMap.put("car_model", selectedSubModel.getId());
                    objectHashMap.put("year_of_birth", "1900");
                    objectHashMap.put("car_year", yearEditText.getText().toString());
                    objectHashMap.put("facilities", selectedFacilities);

                    driverAuth(objectHashMap);

                    setRecyclerView(facilities.getFacilities(), false);
                    lockEditTexts(true);
                }else {
                    Toast.makeText(getContext(), getResources().getString(R.string.fill_fields), Toast.LENGTH_LONG).show();
                }
            }
        });

        modelEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                Bundle b = new Bundle();
                b.putBoolean(Constants.MODELMODE, true);

                ModelsFragment modelsFragment = new ModelsFragment();
                modelsFragment.setTargetFragment(UserProfileFragment.this, Constants.DRIVERPROFILEFRAGMENTCODEMODEL);
                modelsFragment.setArguments(b);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.add(R.id.main_activity_frame, modelsFragment, ModelsFragment.TAG);
                fragmentTransaction.addToBackStack(ModelsFragment.TAG);
                fragmentTransaction.commit();
            }
        });

        subModelEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedModel != null) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    Bundle b = new Bundle();
                    b.putBoolean(Constants.MODELMODE, false);
                    b.putString(Constants.MODELID, selectedModel.getId());

                    ModelsFragment modelsFragment = new ModelsFragment();
                    modelsFragment.setTargetFragment(UserProfileFragment.this, Constants.DRIVERPROFILEFRAGMENTCODESUBMODEL);
                    modelsFragment.setArguments(b);

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.add(R.id.main_activity_frame, modelsFragment, ModelsFragment.TAG);
                    fragmentTransaction.addToBackStack(ModelsFragment.TAG);
                    fragmentTransaction.commit();
                }
            }
        });

        cityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                SelectCityFragment selectCityFragment = new SelectCityFragment();
                selectCityFragment.setTargetFragment(UserProfileFragment.this, Constants.SELECTCITYFROMREGISTER);

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.add(R.id.main_activity_frame, selectCityFragment, SelectCityFragment.TAG);
                fragmentTransaction.addToBackStack(SelectCityFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    private void setRecyclerView(List<Facility> facilities, boolean isLocked) {
        facilitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        facilitiesRecyclerView.setAdapter(new RecyclerFacilitiesAdapter(facilities, getContext(), isLocked));
    }

    private void setUser(User.GetFullInfo user){
        selectedSubModel = new Model();

        nameText.setText(user.getUser().getName());
        phoneText.setText(user.getUser().getPhone());
        coinsText.setText(user.getUser().getBalance());
        parkText.setText(user.getTaxi_park());
        modelEditText.setText(user.getModel());
        subModelEditText.setText(user.getSubmodel());
        cityText.setText(user.getCity().getCname());
        selectedSubModel.setId(user.getUser().getCar());

        if(user.getUser().getRole_id().equals("2")){
            getFacilities();
            driverInfo.setVisibility(View.VISIBLE);
//            editIcon.setVisibility(View.VISIBLE);

            yearEditText.setText(user.getUser().getCar_year());
            numberEditText.setText(user.getUser().getCar_number());
        }else {
            driverInfo.setVisibility(View.GONE);
//            editIcon.setVisibility(View.GONE);
        }
    }

    private void lockEditTexts(boolean isLocked){
        if (isLocked){
            emailEditText.setFocusable(false);
            emailEditText.setFocusableInTouchMode(false);
            emailEditText.setClickable(false);

            modelEditText.setFocusable(false);
            modelEditText.setFocusableInTouchMode(false);
            modelEditText.setClickable(false);
            modelEditText.setEnabled(false);

            subModelEditText.setFocusable(false);
            subModelEditText.setFocusableInTouchMode(false);
            subModelEditText.setClickable(false);
            subModelEditText.setEnabled(false);

            yearEditText.setFocusable(false);
            yearEditText.setFocusableInTouchMode(false);
            yearEditText.setClickable(false);

            numberEditText.setFocusable(false);
            numberEditText.setFocusableInTouchMode(false);
            numberEditText.setClickable(false);

            saveButton.setVisibility(View.GONE);
        }else {
            emailEditText.setFocusable(true);
            emailEditText.setFocusableInTouchMode(true);
            emailEditText.setClickable(true);

            modelEditText.setFocusable(false);
            modelEditText.setFocusableInTouchMode(false);
            modelEditText.setClickable(true);
            modelEditText.setEnabled(true);

            subModelEditText.setFocusable(false);
            subModelEditText.setFocusableInTouchMode(false);
            subModelEditText.setClickable(true);
            subModelEditText.setEnabled(true);

            yearEditText.setFocusable(true);
            yearEditText.setFocusableInTouchMode(true);
            yearEditText.setClickable(true);

            numberEditText.setFocusable(true);
            numberEditText.setFocusableInTouchMode(true);
            numberEditText.setClickable(true);

            saveButton.setVisibility(View.VISIBLE);
        }
    }

    private void setEditableCity(boolean isLocked){
        if (isLocked){
            cityText.setFocusable(false);
            cityText.setClickable(false);
            cityText.setEnabled(false);

            saveButton.setVisibility(View.GONE);
        }else {
            cityText.setFocusable(true);
            cityText.setClickable(true);
            cityText.setEnabled(true);

            saveButton.setVisibility(View.VISIBLE);
        }
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
        this.facilities = response;
        progressBar.setVisibility(View.GONE);
        setRecyclerView(response.getFacilities(), false);
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    private void getUser(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getUser(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseInfo, this::handleErrorInfo));
    }

    private void handleResponseInfo(User.GetFullInfo response) {
        progressBar.setVisibility(View.GONE);

        user = response.getUser();
        selectedFacilities = response.getFacilities();
        selectedCity = response.getCity();
        user.setSelectedCity(selectedCity);

        setUser(response);
        Paper.book().write(Constants.USER, user);
    }

    private void handleErrorInfo(Throwable throwable) {
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
            Toast.makeText(getContext(), getResources().getString(R.string.after_moderation_will_change), Toast.LENGTH_LONG).show();
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
                    selectedSubModel = null;
                }
                selectedModel = data.getParcelableExtra(Constants.SELECTEDMODEL);
                modelEditText.setText(selectedModel.getModel());
                subModelEditText.setText("");
            }else if(requestCode == Constants.DRIVERPROFILEFRAGMENTCODESUBMODEL){
                selectedSubModel = data.getParcelableExtra(Constants.SELECTEDMODEL);
                subModelEditText.setText(selectedSubModel.getModel());
            }else if(requestCode == Constants.SELECTCITYFROMREGISTER){
                selectedCity = data.getParcelableExtra(Constants.SELECTEDCITY);
                cityText.setText(selectedCity.getCname());
            }
        }
    }

    public class RecyclerFacilitiesAdapter extends RecyclerView.Adapter<RecyclerFacilitiesAdapter.ViewHolder> {
        public Context mContext;
        public List<Facility> facilities;
        public boolean isVisible;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text;
            public CheckBox checkBox;
            public LinearLayout view;

            public ViewHolder(View v) {
                super(v);
                checkBox = (CheckBox) v.findViewById(R.id.rfi_checkbox);
                text = (TextView) v.findViewById(R.id.rfi_text);
                view = (LinearLayout) v.findViewById(R.id.rfi_view);
            }
        }

        public RecyclerFacilitiesAdapter(List<Facility> facilities, Context mContext, boolean isVisable) {
            this.facilities = facilities;
            this.mContext = mContext;
            this.isVisible = isVisable;
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
            if(isExist(facilities.get(position).getId())){
                holder.checkBox.setChecked(true);
                holder.text.setTextColor(getResources().getColor(R.color.carrot));
            }

            if (isVisible){
                holder.checkBox.setVisibility(View.VISIBLE);
            }else {
                holder.checkBox.setVisibility(View.GONE);
            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isExist(facilities.get(position).getId())){
                        selectedFacilities.remove(facilities.get(position).getId());
                        holder.text.setTextColor(getResources().getColor(R.color.black));
                    }else {
                        selectedFacilities.add(facilities.get(position).getId());
                        holder.text.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return facilities.size();
        }
    }

    private boolean isExist(String id){
        boolean isContain = false;
        for(int i = 0;i<selectedFacilities.size();i++){
            if(selectedFacilities.get(i).equals(id)){
                isContain = true;
                break;
            }
        }

        return isContain;
    }
}
