package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui.user;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.HistoryItem;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Message;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Response;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository.NetworkUtil;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Utility;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChatFragment extends Fragment {
    public static final String TAG = Constants.CHATFRAGMENTTAG;
    private static final String RECIPIENTPHONE = "recipientPhone";
    private static final String RECIPIENTNAME = "recipientName";

    private String chatUrl, recipientPhone, recipientName;

    private User user;
    private List<Message> messageList = new ArrayList<>();

    private TextView nameText, numberText;
    private RecyclerView recyclerView;
    private EditText inputEditText;
    private ImageButton sendButton;
    private ProgressBar progressBar;

    private RecyclerChatAdapter chatAdapter;
    private DatabaseReference databaseReference;
    private CompositeSubscription subscription;

    public static ChatFragment newInstance(String recipientPhone, String recipientName) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(RECIPIENTPHONE, recipientPhone);
        args.putString(RECIPIENTNAME, recipientName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipientPhone = getArguments().getString(RECIPIENTPHONE);
            recipientName = getArguments().getString(RECIPIENTNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        user = Paper.book().read(Constants.USER);

        if(user.getRole_id().equals("2")){
            chatUrl = user.getPhone() + recipientPhone;
        }else {
            chatUrl = recipientPhone + user.getPhone();
        }

        initViews(view);
        return view;
    }

    private void initViews(View view){
        databaseReference = FirebaseDatabase.getInstance().getReference("Message");

        subscription = new CompositeSubscription();
        nameText = view.findViewById(R.id.fc_name_text);
        numberText = view.findViewById(R.id.fc_number_text);
        inputEditText = view.findViewById(R.id.fc_input_edittext);
        recyclerView = view.findViewById(R.id.fс_recyclerview);
        sendButton = view.findViewById(R.id.fc_send_button);
        progressBar = view.findViewById(R.id.fc_progressbar);

        numberText.setText(recipientPhone);
        nameText.setText(recipientName);

        setListeners();
        checkIfChatExist();
        getMessages();
    }

    private void setListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputEditText.getText().toString().isEmpty()){
                    sendMessage(inputEditText.getText().toString());
                    inputEditText.setText("");
                }
            }
        });
    }

    private void checkIfChatExist(){
        Query query = databaseReference.orderByChild("id").equalTo(chatUrl);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    HashMap<String, String> newChat = new HashMap<>();
                    newChat.put("sender", user.getPhone());
                    newChat.put("id", chatUrl);
                    databaseReference.child(chatUrl).setValue(newChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMessages() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child(chatUrl).child("chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()) {
                    messageList.add(data.getValue(Message.class));
                }
                setAdapter(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter(List<Message> messageList){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatAdapter = new RecyclerChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(messageList.size()-1);
        progressBar.setVisibility(View.GONE);
    }

    public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerChatAdapter.ViewHolder>{
        private List<Message> messagesList;

        public RecyclerChatAdapter(List<Message> messagesList) {
            this.messagesList = messagesList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView message, time;
            public BubbleLayout bubbleLayout;
            public LinearLayout view;

            public ViewHolder(View itemView) {
                super(itemView);
                message = (TextView) itemView.findViewById(R.id.rci_text);
                time = (TextView) itemView.findViewById(R.id.rci_date_text);
                bubbleLayout = (BubbleLayout) itemView.findViewById(R.id.rci_bubble_view);
                view = (LinearLayout) itemView.findViewById(R.id.chat_main_view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //fdf
            holder.message.setText(messagesList.get(position).getMessage());
            holder.time.setText(getDate(messageList.get(position).getTime()));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int padding = getResources().getDimensionPixelOffset(R.dimen._5sdp);
            lp.setMargins(padding, padding, padding, padding);
            if(messagesList.get(position).getFrom().equals(user.getPhone())){
                holder.bubbleLayout.setArrowDirection(ArrowDirection.RIGHT);
                holder.bubbleLayout.setBubbleColor(getResources().getColor(R.color.colorPrimary));
                holder.message.setTextColor(getResources().getColor(R.color.white));
                holder.view.setGravity(Gravity.END);
                holder.bubbleLayout.setLayoutParams(lp);
            }else{
                holder.bubbleLayout.setArrowDirection(ArrowDirection.LEFT);
                holder.bubbleLayout.setBubbleColor(getResources().getColor(R.color.light_gray));
                holder.message.setTextColor(getResources().getColor(R.color.black));
                holder.view.setGravity(Gravity.START);
                holder.bubbleLayout.setLayoutParams(lp);
            }
        }

        @Override
        public int getItemCount() {
            return messagesList.size();
        }
    }

    private void sendMessage(String msg){
        Message message = new Message(msg, user.getPhone());
        databaseReference.child(chatUrl).child("chat").push().setValue(message);
        notifyUser(recipientPhone, msg);
    }

    public static String getDate(String milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(milliSeconds));
        return formatter.format(calendar.getTime());
    }

    private void notifyUser(String phone, String text){
        progressBar.setVisibility(View.VISIBLE);
        subscription.add(NetworkUtil.getRetrofit()
                .sendMessage(Utility.getToken(getContext()), phone, text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        progressBar.setVisibility(View.GONE);
    }

    private void handleError(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
    }
}
