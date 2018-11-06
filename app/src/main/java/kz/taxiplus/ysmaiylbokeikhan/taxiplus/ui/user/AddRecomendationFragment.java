package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddRecomendationFragment extends Fragment {
    public static final String TAG = Constants.ADDRECOMFRAGMENTTAG;

    private float ratedRating = 0;

    private ImageButton backView;
    private EditText editText;
    private RatingBar ratingBar;
    private Button sendButton;
    private ProgressBar progressBar;

    private CompositeSubscription subscription;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recomendation_fragment, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        backView = view.findViewById(R.id.far_back);
        editText = view.findViewById(R.id.far_edittext);
        ratingBar = view.findViewById(R.id.far_ratingbar);
        sendButton = view.findViewById(R.id.far_send_button);
        progressBar = view.findViewById(R.id.far_progressbar);

        setListeners();
    }

    private void setListeners() {
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().isEmpty() && ratedRating != 0){
                    addRecomendation(editText.getText().toString(), String.valueOf(Math.round(ratedRating)));
                }else {
                    Toast.makeText(getContext(), getResources().getText(R.string.fill_fields), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratedRating = rating;
            }
        });
    }

    private void addRecomendation(String name, String rating){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .addRecomendation(Utility.getToken(getContext()),name, rating)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        progressBar.setVisibility(View.GONE);
        if (response.getState().equals("success")){
            Toast.makeText(getContext(), getResources().getString(R.string.successfully_added), Toast.LENGTH_LONG).show();
            getActivity().onBackPressed();
        }
    }
    private void handleError(Throwable throwable) {

    }
}
