package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.driver;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Model;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.makeOrder.FromAndToFragment;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;

public class ModelsFragment extends Fragment {
    public static final String TAG = Constants.MODELSFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String selectedModelId;
    private List<Model> modelList;
    private boolean mode = true;

    private ImageButton backView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText searchView;

    private CompositeSubscription subscription;

    public static ModelsFragment newInstance(String param1, String param2) {
        ModelsFragment fragment = new ModelsFragment();
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
            mode = getArguments().getBoolean(Constants.MODELMODE);
            selectedModelId = getArguments().getString(Constants.MODELID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_models, container, false);
        initViews(view);

        if(mode){
            getModels();
        }else {
            getSubmodels(selectedModelId);
        }
        return view;
    }

    private void initViews(View view){
        subscription = new CompositeSubscription();
        backView = view.findViewById(R.id.fm_back);
        recyclerView = view.findViewById(R.id.fm_recyclerview);
        progressBar = view.findViewById(R.id.fm_progressbar);
        searchView = view.findViewById(R.id.fm_search_edittext);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                if(newText != null || newText != "") {
                    List<Model> modelListForSearch = new ArrayList<>();
                    if(modelList != null) {
                        for (Model model : modelList) {
                            if (model.getModel().toLowerCase().contains(newText.toString().toLowerCase())) {
                                modelListForSearch.add(model);
                            }
                        }
                        setRecyclerView(modelListForSearch);
                    }
                }else {
                    setRecyclerView(modelList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setRecyclerView(List<Model> models){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecyclerModelsAdapter(models, getContext()));
    }

    private void getModels(){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getModels()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Model.GetModels response) {
        this.modelList = response.getModels();
        progressBar.setVisibility(View.GONE);
        setRecyclerView(response.getModels());
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    private void getSubmodels(String id){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .getSubmodels(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSub, this::handleErrorSub));
    }

    private void handleResponseSub(Model.GetModels response) {
        this.modelList = response.getModels();
        progressBar.setVisibility(View.GONE);
        setRecyclerView(response.getModels());
    }

    private void handleErrorSub(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerModelsAdapter extends RecyclerView.Adapter<RecyclerModelsAdapter.ViewHolder> {
        public Context mContext;
        public List<Model> models;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public LinearLayout view;

            public ViewHolder(View v) {
                super(v);
                name = (TextView)v.findViewById(R.id.rmi_text);
                view = (LinearLayout) v.findViewById(R.id.rmi_view);
            }
        }

        public RecyclerModelsAdapter(List<Model> models, Context mContext) {
            this.models = models;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_model_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(models.get(position).getModel());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ModelsFragment.class);
                    intent.putExtra(Constants.SELECTEDMODEL, models.get(position));

                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                }
            });
        }

        @Override
        public int getItemCount() {
            return models.size();
        }
    }
}
