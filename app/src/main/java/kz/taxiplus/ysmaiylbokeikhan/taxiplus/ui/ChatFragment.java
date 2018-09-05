package kz.taxiplus.ysmaiylbokeikhan.taxiplus.ui;


import android.os.Bundle;
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
import android.widget.TextView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.Message;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;

public class ChatFragment extends Fragment {
    public static final String TAG = Constants.CHATFRAGMENTTAG;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private List<Message> messageList = new ArrayList<>();

    private TextView nameText, numberText;
    private RecyclerView recyclerView;
    private EditText inputEditText;
    private ImageButton sendButton;

    private RecyclerChatAdapter chatAdapter;

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        nameText = view.findViewById(R.id.fc_name_text);
        numberText = view.findViewById(R.id.fc_number_text);
        inputEditText = view.findViewById(R.id.fc_input_edittext);
        recyclerView = view.findViewById(R.id.fс_recyclerview);
        sendButton = view.findViewById(R.id.fc_send_button);

        setListeners();
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

    private void sendMessage(String message){
        Random random = new Random();
        String me = "";
        int r = random.nextInt(10);

        if(r%2==0){
            me = "me";
        }else {
            me = "notme";
        }

        Message messageObject = new Message(message, Calendar.getInstance().getTimeInMillis(), me);
        messageList.add(messageObject);

        chatAdapter = new RecyclerChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(messageList.size()-1);
    }


    public class RecyclerChatAdapter extends RecyclerView.Adapter<RecyclerChatAdapter.ViewHolder>{
        private List<Message> messagesList;

        public RecyclerChatAdapter(List<Message> messagesList) {
            this.messagesList = messagesList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView message;
            public BubbleLayout bubbleLayout;
            public LinearLayout view;

            public ViewHolder(View itemView) {
                super(itemView);
                message = (TextView) itemView.findViewById(R.id.rci_text);
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
            holder.message.setText(messagesList.get(position).getMessage());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int padding = getResources().getDimensionPixelOffset(R.dimen._5sdp);
            lp.setMargins(padding, padding, padding, padding);
            if(messagesList.get(position).getFrom().equals("me")){
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
}
