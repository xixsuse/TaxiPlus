package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Car;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.CitiesResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Order;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.OrderInfoDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.SelectCityFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserProfileFragment extends Fragment {
    public static final String TAG = Constants.USERPROFILEFRAGMENTTAG;

    private User user;
    private CitiesResponse.City selectedCity;
    private boolean isEditable = false;

    private ProgressBar progressBar;
    private ImageButton backView, editIcon;
    private Button saveButton;
    private TextView nameText, phoneText, coinsText, cityText;
    private RecyclerView carsRecyclerView;

    private CompositeSubscription subscription;

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
        saveButton = view.findViewById(R.id.fs_save_button);
        carsRecyclerView = view.findViewById(R.id.fs_cars_recyclerview);

        setEditable(false);
        setListeners();
    }

    private void setListeners() {
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        cityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditable) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                    SelectCityFragment selectCityFragment = new SelectCityFragment();
                    selectCityFragment.setTargetFragment(UserProfileFragment.this, Constants.SELECTCITYFROMREGISTER);

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.add(R.id.main_activity_frame, selectCityFragment, SelectCityFragment.TAG);
                    fragmentTransaction.addToBackStack(SelectCityFragment.TAG);
                    fragmentTransaction.commit();
                }
            }
        });

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditable = !isEditable;
                setEditable(isEditable);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditable = false;
                setEditable(isEditable);
            }
        });
    }

    private void setRecyclerView(List<Car> cars) {
        carsRecyclerView.setVisibility(View.VISIBLE);
        carsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        carsRecyclerView.setAdapter(new RecyclerCarsAdapter(cars, getContext()));
    }

    private void setUser(User.GetFullInfo user){
        nameText.setText(user.getUser().getName());
        phoneText.setText(user.getUser().getPhone());
        coinsText.setText(user.getUser().getBalance());
        cityText.setText(user.getCity().getCname());

        if(user.getUser().getRole_id().equals("2")){
            setRecyclerView(user.getCars());
        }else {
            carsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setEditable(boolean isEditable){
        if (isEditable){
            saveButton.setVisibility(View.VISIBLE);
        }else {
            saveButton.setVisibility(View.GONE);
        }
    }

    //requests
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
        selectedCity = response.getCity();
        user.setSelectedCity(selectedCity);
        user.setCars(response.getCars());

        setUser(response);
        Paper.book().write(Constants.USER, user);
    }

    private void handleErrorInfo(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    public class RecyclerCarsAdapter extends RecyclerView.Adapter<RecyclerCarsAdapter.ViewHolder> {
        public Context mContext;
        public List<Car> carList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title, carName;
            public ConstraintLayout view;
            public ImageView image;

            public ViewHolder(View v) {
                super(v);
                title = (TextView)v.findViewById(R.id.rct_title);
                carName = (TextView)v.findViewById(R.id.rct_carname);
                image = (ImageView) v.findViewById(R.id.rct_image);
                view = (ConstraintLayout) v.findViewById(R.id.rcti_view);
            }
        }

        public RecyclerCarsAdapter(List<Car> cars, Context mContext) {
            this.carList = cars;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_user_car_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.carName.setText(carList.get(position).getModel() + " " + carList.get(position).getSubmodel());
            int imageSrc = R.drawable.icon_taxi;
            int title = R.string.modeTaxi;
            switch (carList.get(position).getType()){
                case "1":
                    title = R.string.modeTaxi;
                    imageSrc = R.drawable.icon_taxi;
                    break;
                case "2":
                    title = R.string.modeCargoTaxi;
                    imageSrc = R.drawable.icon_cargo;
                    break;

                case "3":
                    title = R.string.modeEvo;
                    imageSrc = R.drawable.icon_evo;
                    break;
                case "4":
                    title = R.string.modeInvaTaxi;
                    imageSrc = R.drawable.icon_inva;
                    break;
            }

            holder.title.setText(title);
            Glide.with(mContext).load(imageSrc).into(holder.image);
        }
        @Override
        public int getItemCount() {
            return carList.size();
        }
    }
}
