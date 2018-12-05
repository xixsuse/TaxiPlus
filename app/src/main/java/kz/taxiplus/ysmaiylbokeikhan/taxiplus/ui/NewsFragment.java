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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.DirectionResponse;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.FreightItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.NewsItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user.IntercityOrdersFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class NewsFragment extends Fragment {
    public static final String TAG = Constants.NEWSFRAGMENT;

    private ImageButton menuIcon;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        initViews(view);
        getNews();
        return view;
    }

    private void initViews(View view) {
        subscription = new CompositeSubscription();
        menuIcon = view.findViewById(R.id.fn_menu);
        recyclerView = view.findViewById(R.id.fn_recyclerview);
        progressBar = view.findViewById(R.id.fn_progressbar);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerAction();
            }
        });
    }

    private void setRecyclerView(List<NewsItem> newsItemList){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerNewsAdapter(newsItemList, getContext()));
    }

    private void getNews(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getNews(Utility.getToken(getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(NewsItem.NewsResponse response) {
        progressBar.setVisibility(View.GONE);
        setRecyclerView(response.getMessages());
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerNewsAdapter extends RecyclerView.Adapter<RecyclerNewsAdapter.ViewHolder> {
        public Context mContext;
        public List<NewsItem> newsItemList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView titleText, dateText;
            public ConstraintLayout view;

            public ViewHolder(View v) {
                super(v);
                titleText = v.findViewById(R.id.rni_title);
                dateText = v.findViewById(R.id.rni_date);
                view = v.findViewById(R.id.rni_view);
            }
        }

        public RecyclerNewsAdapter(List<NewsItem> newsItemList, Context mContext) {
            this.newsItemList = newsItemList;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_news_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.titleText.setText(newsItemList.get(position).getTitle());
            holder.dateText.setText(Utility.setDataString(newsItemList.get(position).getCreated()));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsItem newsItem = newsItemList.get(position);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    InfoFragment infoFragment = InfoFragment.newInstance(newsItem.getId(), newsItem.getTitle(), newsItem.getText());

                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    fragmentTransaction.add(R.id.main_activity_frame, infoFragment, InfoFragment.TAG);
                    fragmentTransaction.addToBackStack(InfoFragment.TAG);
                    fragmentTransaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return newsItemList.size();
        }
    }
}
