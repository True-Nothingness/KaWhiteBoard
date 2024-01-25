package com.whiteboard.kobo;

import android.graphics.PointF;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Board;
import com.whiteboard.kobo.model.Creator;
import com.whiteboard.kobo.model.Message;
import com.whiteboard.kobo.model.SocketManager;
import com.whiteboard.kobo.model.UserData;
import com.whiteboard.kobo.model.UserResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText chatbox;
    private ImageButton backbtn;
    private Button chatbtn;
    private List<Message> messages;
    private Socket socket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_gchat);
        messages = new ArrayList<>();
        adapter = new ChatAdapter(getContext(), messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatbox = view.findViewById(R.id.edit_gchat_message);
        chatbtn = view.findViewById(R.id.button_gchat_send);
        backbtn = view.findViewById(R.id.backBtn);
        socket = SocketManager.getSocket();
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        socket.on("messageReceived", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject messageData = (JSONObject) args[0];
                        String messageContent = messageData.getString("message");
                        String sender = messageData.getString("sender");
                        Message message = new Message(messageContent, sender, new Date(), false);
                        addMessage(message);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredMessage = chatbox.getText().toString();
                chatbox.setText("");
                Message sentMessage = new Message(enteredMessage, UserData.getInstance().getUsername(), new Date(), true);
                addMessage(sentMessage);
                try {
                    JSONObject messageData = new JSONObject();
                    messageData.put("message", enteredMessage);
                    messageData.put("sender", UserData.getInstance().getUsername());

                    socket.emit("messageSent", messageData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    private void addMessage(Message message) {
        adapter.addMessage(message);
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }
}
