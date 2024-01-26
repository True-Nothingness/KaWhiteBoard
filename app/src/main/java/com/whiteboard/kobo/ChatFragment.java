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
import android.widget.TextView;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Board;
import com.whiteboard.kobo.model.Creator;
import com.whiteboard.kobo.model.CurrentBoard;
import com.whiteboard.kobo.model.Message;
import com.whiteboard.kobo.model.SocketManager;
import com.whiteboard.kobo.model.UserData;
import com.whiteboard.kobo.model.UserResponse;
import com.whiteboard.kobo.model.drawingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
    private Socket socket;
    List<Message> messages;
    TextView gchat_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_gchat);
        adapter = new ChatAdapter(getContext(), new ArrayList<>());
        messages = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatbox = view.findViewById(R.id.edit_gchat_message);
        chatbtn = view.findViewById(R.id.button_gchat_send);
        backbtn = view.findViewById(R.id.backBtn);
        gchat_name = view.findViewById(R.id.gchat_name);
        socket = SocketManager.getSocket();
        gchat_name.setText(CurrentBoard.getInstance().getBoardName());
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        socket.emit("joinChat", CurrentBoard.getInstance().getId());
        socket.on("existingMessages", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    if (args.length > 0 && args[0] instanceof JSONArray) {
                        JSONArray messageArray = (JSONArray) args[0];

                        for (int i = 0; i < messageArray.length(); i++) {
                            JSONObject messageData = messageArray.getJSONObject(i);
                            String content = messageData.getString("content");
                            String senderId = messageData.getString("senderId");
                            String senderName = messageData.getString("senderName");
                            String temp = messageData.getString("timestamp");
                            Date timestamp = format.parse(temp);

                            Message message = new Message();
                            message.setMessage(content);
                            message.setSenderId(senderId);
                            message.setSenderName(senderName);
                            message.setTimestamp(timestamp);
                            message.setMe(message.isMe());
                            messages.add(message);
                        }
                        setMessages(messages);
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("newMessage", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    if (args.length > 0 && args[0] instanceof JSONObject) {
                        JSONObject messageData = (JSONObject) args[0];
                        String content = messageData.getString("content");
                        String senderId = messageData.getString("senderId");
                        String senderName = messageData.getString("senderName");
                        String temp = messageData.getString("timestamp");
                        Date timestamp = format.parse(temp);

                        Message message = new Message();
                        message.setMessage(content);
                        message.setSenderId(senderId);
                        message.setSenderName(senderName);
                        message.setTimestamp(timestamp);
                        message.setMe(message.isMe());
                        addMessage(message);
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredMessage = chatbox.getText().toString();
                Message message = new Message(enteredMessage, UserData.getInstance().getId(), UserData.getInstance().getUsername(), new Date(), true);
                chatbox.setText("");
                try {
                    JSONObject messageData = new JSONObject();
                    messageData.put("content", enteredMessage);
                    messageData.put("senderId", UserData.getInstance().getId());
                    messageData.put("senderName", UserData.getInstance().getUsername());

                    socket.emit("messageSent", messageData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void addMessage(final Message message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addMessage(message);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            });
        }
    }


    public void setMessages(final List<Message> messages) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (messages != null && !messages.isEmpty()) {
                        adapter.setMessages(messages);
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle empty data case (e.g., show a message)
                        Toast.makeText(getActivity(), "Write your first message!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
