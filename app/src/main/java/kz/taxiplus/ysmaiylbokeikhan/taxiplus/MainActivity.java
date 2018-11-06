package kz.taxiplus.ysmaiylbokeikhan.taxiplus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.messaging.RemoteMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Car;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.RecyclerMenuItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.RetrofitInterface;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.CargoFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments.LogoutDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.InvaTaxiFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.AddCarFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments.InfoDialogView;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.dialogFragments.RateDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.SettingsFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.DriverMainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.OpenSessionFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.AddSoberOrderFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.HistoryFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.MyCoinsFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.CityFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.DriverProfileFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.IntercityFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.MyBalanceFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.OrderInfoDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.UserMain.UserMainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.BaseActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int RESULT_LOAD_IMAGE = 11;

    private User user;
    private int theme;
    private String role;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private ImageView userLogo;
    private TextView userName, userPhone;
    private RecyclerView menuRecyclerView;
    private Button logoutButton;
    private ProgressBar progressBar;

    private RecyclerMenuAdapter menuAdapter;
    private FragmentTransaction fragmentTransaction;
    private CompositeSubscription subscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(MainActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initViews();
        openMainFragment(user);
        checkRemoteMessage();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(myBroadcastReceiver,
                new IntentFilter("thisIsForMyFragment"));
    }

    private void initViews() {
        subscription = new CompositeSubscription();
        userLogo = navigationView.findViewById(R.id.manhm_imageView);
        userName = navigationView.findViewById(R.id.manhm_name);
        userPhone = navigationView.findViewById(R.id.manhm_phone);
        menuRecyclerView = navigationView.findViewById(R.id.ma_recyclerView);
        logoutButton = navigationView.findViewById(R.id.ma_logout_button);
        progressBar = findViewById(R.id.main_progressbar);
        theme = Paper.book().read(getString(R.string.prefs_theme_key), 1);

        setUserData();
        setListeners();
    }

    private void setListeners(){
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutDialogFragment logoutDialogFragment = new LogoutDialogFragment();
                logoutDialogFragment.show(getSupportFragmentManager(), LogoutDialogFragment.TAG);
            }
        });

        userLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(MainActivity.this)
                        .single()
                        .start(RESULT_LOAD_IMAGE);
            }
        });
    }

    private void setUserMenu() {
        List<RecyclerMenuItem>recyclerMenuItemList = new ArrayList<>();

        RecyclerMenuItem taxi = new RecyclerMenuItem(getResources().getString(R.string.modeTaxi),R.drawable.icon_taxi, 1);
        RecyclerMenuItem ladyTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeLadyTaxi),R.drawable.icon_taxi, 2);
        RecyclerMenuItem invaTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeInvaTaxi),R.drawable.icon_inva, 3);
        RecyclerMenuItem interCityTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeCitiesTaxi),R.drawable.icon_cities_taxi, 4);
        RecyclerMenuItem cargoTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeCargoTaxi),R.drawable.icon_cargo, 5);
        RecyclerMenuItem evoTaxi = new RecyclerMenuItem(getResources().getString(R.string.modeEvo),R.drawable.icon_evo, 8);
        RecyclerMenuItem sober = new RecyclerMenuItem(getResources().getString(R.string.order_driver),R.drawable.icon_driver, 11);

        RecyclerMenuItem menuItem = new RecyclerMenuItem(getResources().getString(R.string.trip_history), R.drawable.icon_history, 10);
        RecyclerMenuItem menuItem2 = new RecyclerMenuItem(getResources().getString(R.string.add_card), R.drawable.icon_add_card, 6);
        RecyclerMenuItem menuItem4 = new RecyclerMenuItem(getResources().getString(R.string.settings), R.drawable.icon_settings, 20);
        RecyclerMenuItem menuItem7 = new RecyclerMenuItem(getResources().getString(R.string.my_bonuses), R.drawable.icon_driver, 7);
        RecyclerMenuItem menuItem8 = new RecyclerMenuItem(getResources().getString(R.string.driver_mode), R.drawable.icon_switch, 30);
        RecyclerMenuItem menuItem9 = new RecyclerMenuItem(getResources().getString(R.string.share), R.drawable.icon_share, 40);

        recyclerMenuItemList.add(taxi);
        recyclerMenuItemList.add(ladyTaxi);
        recyclerMenuItemList.add(interCityTaxi);
        recyclerMenuItemList.add(cargoTaxi);
        recyclerMenuItemList.add(evoTaxi);
        recyclerMenuItemList.add(invaTaxi);
        recyclerMenuItemList.add(sober);
        recyclerMenuItemList.add(menuItem);
        recyclerMenuItemList.add(menuItem2);
        recyclerMenuItemList.add(menuItem4);
        recyclerMenuItemList.add(menuItem7);
        recyclerMenuItemList.add(menuItem8);
        recyclerMenuItemList.add(menuItem9);

        menuAdapter = new RecyclerMenuAdapter(recyclerMenuItemList, MainActivity.this);
        menuRecyclerView.setAdapter(menuAdapter);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    private void setDriverMenu(){
        List<RecyclerMenuItem>recyclerMenuItemList = new ArrayList<>();
        RecyclerMenuItem menuItem11 = new RecyclerMenuItem(getResources().getString(R.string.navigation), R.drawable.icon_main, 100);
        RecyclerMenuItem menuItem0 = new RecyclerMenuItem(getResources().getString(R.string.open_session), R.drawable.icon_clock, 200);
        RecyclerMenuItem menuItem = new RecyclerMenuItem(getResources().getString(R.string.mode_city_), R.drawable.icon_taxi, 300);
        RecyclerMenuItem menuItem1 = new RecyclerMenuItem(getResources().getString(R.string.mode_intercity), R.drawable.icon_cities_taxi, 400);
        RecyclerMenuItem menuItem2 = new RecyclerMenuItem(getResources().getString(R.string.mode_cargo), R.drawable.icon_cargo, 500);
        RecyclerMenuItem menuItemEvo = new RecyclerMenuItem(getResources().getString(R.string.modeEvo), R.drawable.icon_evo, 600);
        RecyclerMenuItem menuItemInva = new RecyclerMenuItem(getResources().getString(R.string.modeInvaTaxi), R.drawable.icon_inva, 700);
        RecyclerMenuItem menuItem3 = new RecyclerMenuItem(getResources().getString(R.string.trip_history), R.drawable.icon_history, 10);
        RecyclerMenuItem menuItem5 = new RecyclerMenuItem(getResources().getString(R.string.mode_coins), R.drawable.icon_by_bonuses_p, 800);
        RecyclerMenuItem menuItem9 = new RecyclerMenuItem(getResources().getString(R.string.user_mode), R.drawable.icon_switch, 50);
        RecyclerMenuItem menuItem6 = new RecyclerMenuItem(getResources().getString(R.string.settings), R.drawable.icon_settings, 20);
        RecyclerMenuItem menuItem7 = new RecyclerMenuItem(getModeString(), R.drawable.icon_driver, 900);
        RecyclerMenuItem menuItem8 = new RecyclerMenuItem(getResources().getString(R.string.share), R.drawable.icon_share, 40);

        recyclerMenuItemList.add(menuItem0);
        recyclerMenuItemList.add(menuItem11);
        recyclerMenuItemList.add(menuItem);
        recyclerMenuItemList.add(menuItem1);
        recyclerMenuItemList.add(menuItem2);
        recyclerMenuItemList.add(menuItemEvo);
        recyclerMenuItemList.add(menuItemInva);
        recyclerMenuItemList.add(menuItem3);
        recyclerMenuItemList.add(menuItem5);
        recyclerMenuItemList.add(menuItem9);
        recyclerMenuItemList.add(menuItem6);
        recyclerMenuItemList.add(menuItem7);
        recyclerMenuItemList.add(menuItem8);

        menuAdapter = new RecyclerMenuAdapter(recyclerMenuItemList, MainActivity.this);
        menuRecyclerView.setAdapter(menuAdapter);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    public void getUser(){
        user = Paper.book().read(Constants.USER);
    }

    //requests
    private void shareApp(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getReferalLink(Utility.getToken(MainActivity.this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseShare, this::handleErrorShare));
    }

    private void handleResponseShare(Response response) {
        progressBar.setVisibility(View.GONE);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, response.getLink());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share_via)));
    }

    private void handleErrorShare(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    public void switchRole(String role){
        this.role = role;
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .changeRole(Utility.getToken(MainActivity.this), role)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseRole, this::handleErrorRole));
    }

    private void handleResponseRole(Response response) {
        progressBar.setVisibility(View.GONE);
        if(response.getState().equals("success")){
            if(role.equals("2")) {
                if(response.getCars() != null && response.getCars().size()>0){
                    user.setRole_id("2");
                    user.setCars(response.getCars());
                    Paper.book().write(Constants.USER, user);
                    recreateActivity();
                }else {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    DriverProfileFragment driverProfileFragment = new DriverProfileFragment();
                    fragmentTransaction.replace(R.id.main_activity_frame, driverProfileFragment, DriverProfileFragment.TAG);
                    fragmentTransaction.addToBackStack(DriverProfileFragment.TAG);
                    fragmentTransaction.commit();
                }
            }else {
                user.setRole_id("1");
                Paper.book().write(Constants.USER, user);
                recreateActivity();
            }
        }
    }

    private void handleErrorRole(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }


    //helper functions
    private void setUserData() {
        getUser();
        userName.setText(user.getName());
        userPhone.setText(user.getPhone());
        setLogo(user);

        if(user.getRole_id().equals("2")){
            setDriverMenu();
        }else {
            setUserMenu();
        }
    }

    public void recreateActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void setNightMode(int mode) {
        if(mode == 2){
            Utility.setTheme(getApplicationContext(), 1);
            recreateActivity();
        }else {
            Utility.setTheme(getApplicationContext(), 2);
            recreateActivity();
        }
    }

    private String getModeString(){
        String mode;
        if(theme == 2){
            mode = getResources().getString(R.string.mode_day);
        }else {
            mode = getResources().getString(R.string.mode_night);
        }
        return mode;
    }

    private void setLogo(User user){
        String logo;
        if(user.getAvatar_path() != null){
            logo = user.getAvatar_path();
        }else {
            logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS1qA-_Sk2ctUnAl9RXfBHQ5WYOMh04hnZ9SnkbaNhhgaIxRpn20Q";
        }

        Glide.with(MainActivity.this)
                .load(logo)
                .apply(RequestOptions.circleCropTransform())
                .into(userLogo);
    }

    private boolean checkCar(String type){
        boolean have = false;
        for(int i = 0; i < user.getCars().size();i++){
            if(user.getCars().get(i).getType().equals(type)){
                have = true;
                break;
            }
        }
        return have;
    }

    //navigation
    private void openMainFragment(User user) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(user.getRole_id().equals("2")){
            Set<String> types = new HashSet<>();
            for (Car car:user.getCars()) {
                types.add(car.getType());
            }
            if(types.contains("1")){
                CityFragment cityFragment = new CityFragment();
                fragmentTransaction.add(R.id.main_activity_frame, cityFragment, CityFragment.TAG);
                fragmentTransaction.addToBackStack(CityFragment.TAG);
            }else if(types.contains("2")){
                CargoFragment cargoFragment = CargoFragment.newInstance("2");
                fragmentTransaction.add(R.id.main_activity_frame, cargoFragment, CargoFragment.TAG);
                fragmentTransaction.addToBackStack(CargoFragment.TAG);
            }else if(types.contains("3")){
                CargoFragment cargoFragment = CargoFragment.newInstance("3");
                fragmentTransaction.add(R.id.main_activity_frame, cargoFragment, CargoFragment.TAG);
                fragmentTransaction.addToBackStack(CargoFragment.TAG);
            }else if(types.contains("4")){
                InvaTaxiFragment invaTaxiFragment = new InvaTaxiFragment();
                fragmentTransaction.add(R.id.main_activity_frame, invaTaxiFragment, InvaTaxiFragment.TAG);
                fragmentTransaction.addToBackStack(InvaTaxiFragment.TAG);
            }
        }else {
            UserMainFragment userMainFragment = UserMainFragment.newInstance(1);
            fragmentTransaction.add(R.id.main_activity_frame, userMainFragment, UserMainFragment.TAG);
            fragmentTransaction.addToBackStack(UserMainFragment.TAG);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if(count != 1){
                super.onBackPressed();
            }else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    public void drawerAction() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            drawer.openDrawer(Gravity.START);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK &&requestCode == RESULT_LOAD_IMAGE) {
            if(Utility.checkPermissionReadImage(this)){
                Image image = ImagePicker.getFirstImageOrNull(data);
                File file = new File(image.getPath());

                RetrofitInterface retrofitInterface = NetworkUtil.getRetrofit();
                RequestBody token = RequestBody.create(MediaType.parse("text/plain"), Utility.getToken(this));
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("myfile", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

                Call<Response> call = retrofitInterface.uploadAva(filePart, token);
                call.enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, @NonNull retrofit2.Response<Response> response){
                        if(response.body().getState() != null &&response.body().getState().equals("success")  && response.body().getPath() != null) {
                            user.setAvatar_path(response.body().getPath());
                            Glide.with(MainActivity.this)
                                    .load(response.body().getPath())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(userLogo);
                            Paper.book().write(Constants.USER, user);
                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                    }
                });
            }
        }
    }

    public class RecyclerMenuAdapter extends RecyclerView.Adapter<RecyclerMenuAdapter.ViewHolder> {
        public Context mContext;
        public List<RecyclerMenuItem> menuList;

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

        public RecyclerMenuAdapter(List<RecyclerMenuItem> menuList, Context mContext) {
            this.menuList = menuList;
            this.mContext = mContext;
        }

        @Override
        public RecyclerMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_menu_item, parent, false);

            RecyclerMenuAdapter.ViewHolder vh = new RecyclerMenuAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerMenuAdapter.ViewHolder holder, final int position) {
            holder.title.setText(menuList.get(position).getTitle());
            holder.logo.setImageDrawable(getResources().getDrawable(menuList.get(position).getLogo()));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    switch (menuList.get(position).getIndex()){
                        case 1:
                            UserMainFragment userMainFragment = UserMainFragment.newInstance(1);
                            fragmentTransaction.replace(R.id.main_activity_frame, userMainFragment, UserMainFragment.TAG);
                            fragmentTransaction.addToBackStack(UserMainFragment.TAG);
                            break;

                        case 2:
                            UserMainFragment ladyFragment = UserMainFragment.newInstance(2);
                            fragmentTransaction.replace(R.id.main_activity_frame, ladyFragment, UserMainFragment.TAG);
                            fragmentTransaction.addToBackStack(UserMainFragment.TAG);
                            break;

                        case 3:
                            InvaTaxiFragment invaTaxiFragment = new InvaTaxiFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, invaTaxiFragment, InvaTaxiFragment.TAG);
                            fragmentTransaction.addToBackStack(InvaTaxiFragment.TAG);
                            break;

                        case 4:
                            IntercityFragment intercityFragment = new IntercityFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, intercityFragment, IntercityFragment.TAG);
                            fragmentTransaction.addToBackStack(IntercityFragment.TAG);
                            break;

                        case 5:
                            CargoFragment cargoFragmentUser = CargoFragment.newInstance("2");
                            fragmentTransaction.replace(R.id.main_activity_frame, cargoFragmentUser, CargoFragment.TAG);
                            fragmentTransaction.addToBackStack(CargoFragment.TAG);
                            break;

                        case 6:

                            break;

                        case 7:
                            MyCoinsFragment myCoinsFragment = new MyCoinsFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, myCoinsFragment, MyCoinsFragment.TAG);
                            fragmentTransaction.addToBackStack(MyCoinsFragment.TAG);
                            break;

                        case 8:
                            CargoFragment evoTaxiFragment = CargoFragment.newInstance("3");
                            fragmentTransaction.replace(R.id.main_activity_frame, evoTaxiFragment, CargoFragment.TAG);
                            fragmentTransaction.addToBackStack(CargoFragment.TAG);
                            break;

                        case 10:
                            HistoryFragment historyFragment = new HistoryFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, historyFragment, HistoryFragment.TAG);
                            fragmentTransaction.addToBackStack(HistoryFragment.TAG);
                            break;

                        case 11:
                            AddSoberOrderFragment addSoberOrderFragment = new AddSoberOrderFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, addSoberOrderFragment, AddSoberOrderFragment.TAG);
                            fragmentTransaction.addToBackStack(AddSoberOrderFragment.TAG);
                            break;

                        case 20:
                            SettingsFragment settingsFragment = new SettingsFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, settingsFragment, SettingsFragment.TAG);
                            fragmentTransaction.addToBackStack(SettingsFragment.TAG);
                            break;

                        case 30:
                            switchRole("2");
                            break;

                        case 40:
                            shareApp();
                            break;

                        case 50:
                            switchRole("1");
                            break;

                        case 100:
                            DriverMainFragment driverMainFragment = new DriverMainFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, driverMainFragment, DriverMainFragment.TAG);
                            fragmentTransaction.addToBackStack(DriverMainFragment.TAG);
                            break;

                        case 200:
                            OpenSessionFragment openSessionFragment = new OpenSessionFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, openSessionFragment, OpenSessionFragment.TAG);
                            fragmentTransaction.addToBackStack(OpenSessionFragment.TAG);
                            break;

                        case 300:
                            if(checkCar("1")) {
                                CityFragment cityFragment = new CityFragment();
                                fragmentTransaction.replace(R.id.main_activity_frame, cityFragment, CityFragment.TAG);
                                fragmentTransaction.addToBackStack(CityFragment.TAG);
                            }else {
                                AddCarFragment addCarFragment = new AddCarFragment();
                                fragmentTransaction.replace(R.id.main_activity_frame, addCarFragment, AddCarFragment.TAG);
                                fragmentTransaction.addToBackStack(AddCarFragment.TAG);
                            }
                            break;
                        case 400:
                            if(checkCar("1")) {
                                IntercityFragment driverIntercityFragment = new IntercityFragment();
                                fragmentTransaction.replace(R.id.main_activity_frame, driverIntercityFragment, IntercityFragment.TAG);
                                fragmentTransaction.addToBackStack(IntercityFragment.TAG);
                            }else {
                                AddCarFragment addCarFragment = new AddCarFragment();
                                fragmentTransaction.replace(R.id.main_activity_frame, addCarFragment, AddCarFragment.TAG);
                                fragmentTransaction.addToBackStack(AddCarFragment.TAG);
                            }
                            break;

                        case 500:
                            if(checkCar("2")) {
                                CargoFragment cargoFragment = CargoFragment.newInstance("2");
                                fragmentTransaction.replace(R.id.main_activity_frame, cargoFragment, CargoFragment.TAG);
                                fragmentTransaction.addToBackStack(CargoFragment.TAG);
                            }else {
                                AddCarFragment addCarFragment = new AddCarFragment();
                                fragmentTransaction.replace(R.id.main_activity_frame, addCarFragment, AddCarFragment.TAG);
                                fragmentTransaction.addToBackStack(AddCarFragment.TAG);
                            }
                            break;

                        case 600:
                            if(checkCar("3")) {
                                CargoFragment cargoFragment = CargoFragment.newInstance("3");
                                fragmentTransaction.replace(R.id.main_activity_frame, cargoFragment, CargoFragment.TAG);
                                fragmentTransaction.addToBackStack(CargoFragment.TAG);
                            }else {
                                AddCarFragment addCarFragment = new AddCarFragment();
                                fragmentTransaction.replace(R.id.main_activity_frame, addCarFragment, AddCarFragment.TAG);
                                fragmentTransaction.addToBackStack(AddCarFragment.TAG);
                            }
                            break;

                        case 700:
                            if(checkCar("4")){
                                InvaTaxiFragment invaTaxiDriverFragment = new InvaTaxiFragment();
                                fragmentTransaction.replace(R.id.main_activity_frame, invaTaxiDriverFragment, InvaTaxiFragment.TAG);
                                fragmentTransaction.addToBackStack(InvaTaxiFragment.TAG);
                            }else {
                                AddCarFragment addCarFragment = new AddCarFragment();
                                fragmentTransaction.replace(R.id.main_activity_frame, addCarFragment, AddCarFragment.TAG);
                                fragmentTransaction.addToBackStack(AddCarFragment.TAG);
                            }
                            break;

                        case 800:
                            MyBalanceFragment myBalanceFragment = new MyBalanceFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, myBalanceFragment, MyBalanceFragment.TAG);
                            fragmentTransaction.addToBackStack(MyBalanceFragment.TAG);
                            break;

                        case 900:
                            int mode = Paper.book().read(getString(R.string.prefs_theme_key), 1);
                            setNightMode(mode);
                            break;
                    }
                    fragmentTransaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }
    }


    //handle notification
    private void checkRemoteMessage() {
        RemoteMessage remoteMessage = getIntent().getParcelableExtra(Constants.PENDINGINTENTEXTRA);
        if(remoteMessage != null){
            String type = remoteMessage.getData().get("type");
            String orderId = remoteMessage.getData().get("order_id");

            if (type.equals("101")) {
                OrderInfoDialogFragment newOrderDialogFragment = OrderInfoDialogFragment.newInstance(orderId);
                newOrderDialogFragment.show(getSupportFragmentManager(), OrderInfoDialogFragment.TAG);
            } else if (type.equals("201")) {
                UserMainFragment mainFragment = (UserMainFragment) getSupportFragmentManager().findFragmentByTag(UserMainFragment.TAG);
                if(mainFragment != null) {
                    mainFragment.setWithOrderInfo(orderId);
                }
            } else if (type.equals("301")) {
                DriverMainFragment mainFragment = (DriverMainFragment) getSupportFragmentManager().findFragmentByTag(DriverMainFragment.TAG);
                if(mainFragment != null) {
                    mainFragment.clientIsAccepted(orderId);
                }
            }else if(type.equals("401")){
                Toast.makeText(MainActivity.this, getResources().getString(R.string.driver_is_came), Toast.LENGTH_LONG).show();
            }else if(type.equals("501")){
                RateDialogFragment rateDialogFragment = RateDialogFragment.newInstance(orderId);
                rateDialogFragment.show(getSupportFragmentManager(), RateDialogFragment.TAG);
            }else if(type.equals("0")){
                InfoDialogView infoDialogView = InfoDialogView.newInstance(getResources().getString(R.string.failed_payment), R.drawable.icon_error);
                infoDialogView.show(getSupportFragmentManager(), InfoDialogView.TAG);
            }
        }
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            String type = intent.getStringExtra(Constants.TYPE);
            String orderId = intent.getStringExtra(Constants.ORDERID);
            if(type.equals("101")) {
                try {
                    OrderInfoDialogFragment newOrderDialogFragment = OrderInfoDialogFragment.newInstance(orderId);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    newOrderDialogFragment.show(fragmentManager, OrderInfoDialogFragment.TAG);
                }catch (Throwable r){}
            }else if(type.equals("201")){
                UserMainFragment mainFragment = (UserMainFragment) getSupportFragmentManager().findFragmentByTag(UserMainFragment.TAG);
                if(mainFragment != null) {
                    mainFragment.setWithOrderInfo(orderId);
                }
            }else if(type.equals("301")){
                DriverMainFragment mainFragment = (DriverMainFragment) getSupportFragmentManager().findFragmentByTag(DriverMainFragment.TAG);
                if(mainFragment != null) {
                    mainFragment.clientIsAccepted(orderId);
                }
            }else if(type.equals("401")){
                InfoDialogView infoDialogView = InfoDialogView.newInstance(getResources().getString(R.string.driver_is_came), R.drawable.icon_big_clock);
                infoDialogView.show(getSupportFragmentManager(), InfoDialogView.TAG);
            }else if(type.equals("501")){
                RateDialogFragment rateDialogFragment = RateDialogFragment.newInstance(orderId);
                rateDialogFragment.show(getSupportFragmentManager(), RateDialogFragment.TAG);
            }else if(type.equals("0")){
                InfoDialogView infoDialogView = InfoDialogView.newInstance(getResources().getString(R.string.failed_payment), R.drawable.icon_error);
                infoDialogView.show(getSupportFragmentManager(), InfoDialogView.TAG);
            }
        }
    };
}
