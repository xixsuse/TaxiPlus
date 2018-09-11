package kz.taxiplus.ysmaiylbokeikhan.taxiplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.RecyclerMenuItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.ActiveOrdersFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.FaqFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.HistoryFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.MainFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.MyCoinsFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.SettingsFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.CityFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.DriverProfileFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.IntercityFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.MyBalanceFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver.OrderInfoDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.MyPlacesFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.NewOfferDialogFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.BaseActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private User user;
    private int theme;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private ImageView userLogo;
    private TextView userName, userPhone;
    private RecyclerView menuRecyclerView;
    private Button logoutButton;

    private RecyclerMenuAdapter menuAdapter;
    private FragmentTransaction fragmentTransaction;

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
        openMainFragment();
        checkRemoteMessage();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(myBroadcastReceiver,
                new IntentFilter("thisIsForMyFragment"));
    }

    private void initViews() {
        userLogo = navigationView.findViewById(R.id.manhm_imageView);
        userName = navigationView.findViewById(R.id.manhm_name);
        userPhone = navigationView.findViewById(R.id.manhm_phone);
        menuRecyclerView = navigationView.findViewById(R.id.ma_recyclerView);
        logoutButton = navigationView.findViewById(R.id.ma_logout_button);
        theme = Paper.book().read(getString(R.string.prefs_theme_key), 1);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setRole_id("1");
                Paper.book().write(Constants.USER, user);
                recreateActivity();
            }
        });

        setUserData();
    }

    private void setUserData() {
        user = Paper.book().read(Constants.USER);
        userName.setText(user.getName());
        userPhone.setText(user.getPhone());
        Glide.with(MainActivity.this)
                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS1qA-_Sk2ctUnAl9RXfBHQ5WYOMh04hnZ9SnkbaNhhgaIxRpn20Q")
                .apply(RequestOptions.circleCropTransform())
                .into(userLogo);

        if(user.getRole_id().equals("2")){
            setDriverMenu();
        }else {
            setUserMenu();
        }
    }

    private void openMainFragment() {
        MainFragment mainFragment = new MainFragment();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_activity_frame, mainFragment, MainFragment.TAG);
        fragmentTransaction.addToBackStack(MainFragment.TAG);
        fragmentTransaction.commit();
    }

    private void setUserMenu() {
        List<RecyclerMenuItem>recyclerMenuItemList = new ArrayList<>();
        RecyclerMenuItem menuItem = new RecyclerMenuItem(getResources().getString(R.string.trip_history), R.drawable.icon_history, 0);
        RecyclerMenuItem menuItem1 = new RecyclerMenuItem(getResources().getString(R.string.current_trips), R.drawable.icon_current_order, 1);
        RecyclerMenuItem menuItem2 = new RecyclerMenuItem(getResources().getString(R.string.add_card), R.drawable.icon_add_card, 2);
        RecyclerMenuItem menuItem3 = new RecyclerMenuItem(getResources().getString(R.string.my_places), R.drawable.icon_fav, 3);
        RecyclerMenuItem menuItem4 = new RecyclerMenuItem(getResources().getString(R.string.settings), R.drawable.icon_settings, 4);
        RecyclerMenuItem menuItem5 = new RecyclerMenuItem(getResources().getString(R.string.faq), R.drawable.icon_faq, 5);
        RecyclerMenuItem menuItem6 = new RecyclerMenuItem(getResources().getString(R.string.order_driver), R.drawable.icon_driver, 6);
        RecyclerMenuItem menuItem7 = new RecyclerMenuItem(getResources().getString(R.string.my_bonuses), R.drawable.icon_driver, 7);
        RecyclerMenuItem menuItem8 = new RecyclerMenuItem(getResources().getString(R.string.driver_mode), R.drawable.icon_switch, 8);

        recyclerMenuItemList.add(menuItem);
        recyclerMenuItemList.add(menuItem1);
        recyclerMenuItemList.add(menuItem2);
        recyclerMenuItemList.add(menuItem3);
        recyclerMenuItemList.add(menuItem4);
        recyclerMenuItemList.add(menuItem5);
        recyclerMenuItemList.add(menuItem6);
        recyclerMenuItemList.add(menuItem7);
        recyclerMenuItemList.add(menuItem8);

        menuAdapter = new RecyclerMenuAdapter(recyclerMenuItemList, MainActivity.this);
        menuRecyclerView.setAdapter(menuAdapter);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    private void setDriverMenu(){
        List<RecyclerMenuItem>recyclerMenuItemList = new ArrayList<>();
        RecyclerMenuItem menuItem = new RecyclerMenuItem(getResources().getString(R.string.mode_city_), R.drawable.icon_history, 10);
        RecyclerMenuItem menuItem1 = new RecyclerMenuItem(getResources().getString(R.string.mode_intercity), R.drawable.icon_current_order, 11);
        RecyclerMenuItem menuItem2 = new RecyclerMenuItem(getResources().getString(R.string.mode_cargo), R.drawable.icon_add_card, 12);
        RecyclerMenuItem menuItem3 = new RecyclerMenuItem(getResources().getString(R.string.trip_history), R.drawable.icon_history, 13);
        RecyclerMenuItem menuItem4 = new RecyclerMenuItem(getResources().getString(R.string.current_trips), R.drawable.icon_current_order, 14);
        RecyclerMenuItem menuItem5 = new RecyclerMenuItem(getResources().getString(R.string.mode_coins), R.drawable.icon_faq, 15);
        RecyclerMenuItem menuItem6 = new RecyclerMenuItem(getResources().getString(R.string.settings), R.drawable.icon_settings, 16);
        RecyclerMenuItem menuItem7 = new RecyclerMenuItem(getModeString(), R.drawable.icon_driver, 17);

        recyclerMenuItemList.add(menuItem);
        recyclerMenuItemList.add(menuItem1);
        recyclerMenuItemList.add(menuItem2);
        recyclerMenuItemList.add(menuItem3);
        recyclerMenuItemList.add(menuItem4);
        recyclerMenuItemList.add(menuItem5);
        recyclerMenuItemList.add(menuItem6);
        recyclerMenuItemList.add(menuItem7);

        menuAdapter = new RecyclerMenuAdapter(recyclerMenuItemList, MainActivity.this);
        menuRecyclerView.setAdapter(menuAdapter);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }


    private void checkRemoteMessage() {
        RemoteMessage remoteMessage = getIntent().getParcelableExtra(Constants.PENDINGINTENTEXTRA);
        if(remoteMessage != null){
            String type = remoteMessage.getData().get("type");
            String orderId = remoteMessage.getData().get("order_id");

            if (type.equals("101")) {
                OrderInfoDialogFragment newOrderDialogFragment = OrderInfoDialogFragment.newInstance(orderId);
                newOrderDialogFragment.show(getSupportFragmentManager(), OrderInfoDialogFragment.TAG);
            } else if (type.equals("201")) {
                String driverId = remoteMessage.getData().get("driver_id");
                NewOfferDialogFragment newOfferDialogFragment = NewOfferDialogFragment.newInstance(driverId, orderId);
                newOfferDialogFragment.show(getSupportFragmentManager(), NewOfferDialogFragment.TAG);
            } else if (type.equals("301")) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.user_accepted), Toast.LENGTH_LONG).show();
                MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
                mainFragment.clientIsAccepted(orderId);
            }else if(type.equals("401")){
                Toast.makeText(MainActivity.this, getResources().getString(R.string.driver_is_came), Toast.LENGTH_LONG).show();
            }
        }
    }

    private final BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            String type = intent.getStringExtra(Constants.TYPE);
            String orderId = intent.getStringExtra(Constants.ORDERID);

            if(type.equals("101")){
                OrderInfoDialogFragment newOrderDialogFragment = OrderInfoDialogFragment.newInstance(orderId);
                newOrderDialogFragment.show(getSupportFragmentManager(), OrderInfoDialogFragment.TAG);
            }else if(type.equals("201")){
                String driverId = intent.getStringExtra(Constants.DRIVERID);
                NewOfferDialogFragment newOfferDialogFragment = NewOfferDialogFragment.newInstance(driverId, orderId);
                newOfferDialogFragment.show(getSupportFragmentManager(), NewOfferDialogFragment.TAG);
            }else if(type.equals("301")){
                Toast.makeText(MainActivity.this, getResources().getString(R.string.user_accepted), Toast.LENGTH_LONG).show();
                MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
                mainFragment.clientIsAccepted(orderId);
            }else if(type.equals("401")){
                Toast.makeText(MainActivity.this, getResources().getString(R.string.driver_is_came), Toast.LENGTH_LONG).show();
            }
        }
    };

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
                        case 0:
                            HistoryFragment historyFragment = new HistoryFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, historyFragment, HistoryFragment.TAG);
                            fragmentTransaction.addToBackStack(HistoryFragment.TAG);
                            break;

                        case 1:
                            ActiveOrdersFragment activeOrdersFragment = new ActiveOrdersFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, activeOrdersFragment, ActiveOrdersFragment.TAG);
                            fragmentTransaction.addToBackStack(ActiveOrdersFragment.TAG);
                            break;

                        case 2:

                            break;

                        case 3:
                            MyPlacesFragment myPlacesFragment = MyPlacesFragment.newInstance(true, 0);
                            fragmentTransaction.replace(R.id.main_activity_frame, myPlacesFragment, MyPlacesFragment.TAG);
                            fragmentTransaction.addToBackStack(MyPlacesFragment.TAG);
                            break;

                        case 4:
                            SettingsFragment settingsFragment = new SettingsFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, settingsFragment, SettingsFragment.TAG);
                            fragmentTransaction.addToBackStack(SettingsFragment.TAG);
                            break;

                        case 5:
                            FaqFragment faqFragment = new FaqFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, faqFragment, FaqFragment.TAG);
                            fragmentTransaction.addToBackStack(FaqFragment.TAG);
                            break;

                        case 6:

                            break;

                        case 7:
                            MyCoinsFragment myCoinsFragment = new MyCoinsFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, myCoinsFragment, MyCoinsFragment.TAG);
                            fragmentTransaction.addToBackStack(MyCoinsFragment.TAG);
                            break;

                        case 8:
                            DriverProfileFragment driverProfileFragment = new DriverProfileFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, driverProfileFragment, DriverProfileFragment.TAG);
                            fragmentTransaction.addToBackStack(DriverProfileFragment.TAG);
                            break;

                        case 10:
                            CityFragment cityFragment = new CityFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, cityFragment, CityFragment.TAG);
                            fragmentTransaction.addToBackStack(CityFragment.TAG);
                            break;
                        case 11:
                            IntercityFragment intercityFragment = IntercityFragment.newInstance("intercity");
                            fragmentTransaction.replace(R.id.main_activity_frame, intercityFragment, IntercityFragment.TAG);
                            fragmentTransaction.addToBackStack(IntercityFragment.TAG);
                            break;

                        case 12:
                            IntercityFragment cargoFragment = IntercityFragment.newInstance("cargo");
                            fragmentTransaction.replace(R.id.main_activity_frame, cargoFragment, IntercityFragment.TAG);
                            fragmentTransaction.addToBackStack(IntercityFragment.TAG);
                            break;

                        case 13:
                            HistoryFragment driverHistoryFragment = new HistoryFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, driverHistoryFragment, HistoryFragment.TAG);
                            fragmentTransaction.addToBackStack(HistoryFragment.TAG);
                            break;

                        case 14:
                            ActiveOrdersFragment driverActiveOrdersFragment = new ActiveOrdersFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, driverActiveOrdersFragment, ActiveOrdersFragment.TAG);
                            fragmentTransaction.addToBackStack(ActiveOrdersFragment.TAG);
                            break;

                        case 15:
                            MyBalanceFragment myBalanceFragment = new MyBalanceFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, myBalanceFragment, MyBalanceFragment.TAG);
                            fragmentTransaction.addToBackStack(MyBalanceFragment.TAG);
                            break;

                        case 16:
                            SettingsFragment driverSettingsFragment = new SettingsFragment();
                            fragmentTransaction.replace(R.id.main_activity_frame, driverSettingsFragment, SettingsFragment.TAG);
                            fragmentTransaction.addToBackStack(SettingsFragment.TAG);
                            break;

                        case 17:
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

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if(count != 1){
                super.onBackPressed();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
