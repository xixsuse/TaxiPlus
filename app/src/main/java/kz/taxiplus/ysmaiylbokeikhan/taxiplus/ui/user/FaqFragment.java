package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.MainActivity;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.FaqItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class FaqFragment extends Fragment {
    public static final String TAG = Constants.SETTINGSFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageButton menuIcon;
    private RecyclerView recyclerView;
    private Button addRecomButton;

    private RecyclerFaqAdapter adapter;

    private FragmentTransaction fragmentTransaction;
    public static FaqFragment newInstance(String param1, String param2) {
        FaqFragment fragment = new FaqFragment();
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
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        initViews(view);

        List<FaqItem> faqItems = new ArrayList<>();
        FaqItem faqItem = new FaqItem("Lorem ipsum, or lipsum as it is sometimes known?", "Lorem ipsum, or lipsum as it is sometimes known, is dummy text used in laying out print, graphic or web designs. ... “Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.”");
        FaqItem faqItem1 = new FaqItem("Lorem ipsum, or lipsum as it is sometimes known?", "Lorem ipsum, or lipsum as it is sometimes known, is dummy text used in laying out print, graphic or web designs. ... “Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.”");

        faqItems.add(faqItem);
        faqItems.add(faqItem1);

        setRecyclerView(faqItems);
        return view;
    }

    private void initViews(View view){
        menuIcon = view.findViewById(R.id.ff_back);
        recyclerView = view.findViewById(R.id.ff_recyclerview);
        addRecomButton= view.findViewById(R.id.ff_recom_button);

        setListeners();
    }

    private void setListeners() {
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        addRecomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = getFragmentManager().beginTransaction();

                AddRecomendationFragment addRecomendationFragment = new AddRecomendationFragment();

                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.main_activity_frame, addRecomendationFragment, AddRecomendationFragment.TAG);
                fragmentTransaction.addToBackStack(AddRecomendationFragment.TAG);
                fragmentTransaction.commit();
            }
        });
    }

    private void setRecyclerView(List<FaqItem> faqItems){

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerFaqAdapter(faqItems, getContext());
        recyclerView.setAdapter(adapter);
    }

    public class RecyclerFaqAdapter extends RecyclerView.Adapter<RecyclerFaqAdapter.ViewHolder> {
        public Context mContext;
        public List<FaqItem> faqItems;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView question, answer;
            public ConstraintLayout view;


            public ViewHolder(View v) {
                super(v);
                question = (TextView)v.findViewById(R.id.rfi_question_text);
                answer = (TextView)v.findViewById(R.id.rfi_answer_text);
                view = (ConstraintLayout) v.findViewById(R.id.rfi_view);
            }
        }

        public RecyclerFaqAdapter(List<FaqItem> faqItems, Context mContext) {
            this.faqItems = faqItems;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_faq_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.question.setText(faqItems.get(position).getQuestion());
            holder.answer.setText(faqItems.get(position).getAnswer());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    faqItems.get(position).setPressed(!faqItems.get(position).isPressed());

                    if(faqItems.get(position).isPressed()){
                        holder.answer.setVisibility(View.VISIBLE);
                    }else {
                        holder.answer.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return faqItems.size();
        }
    }
}
