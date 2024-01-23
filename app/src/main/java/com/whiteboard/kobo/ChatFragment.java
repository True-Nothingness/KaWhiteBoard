package com.whiteboard.kobo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Board;
import com.whiteboard.kobo.model.Creator;
import com.whiteboard.kobo.model.Message;
import com.whiteboard.kobo.model.UserData;
import com.whiteboard.kobo.model.UserResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_gchat);
        messages = new ArrayList<>();
        adapter = new ChatAdapter(getContext(), messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Set up other UI elements and chat-related functionality
        addDummyData();
        return view;
    }


    private void addMessage(Message message) {
        adapter.addMessage(message);
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }
    private void addDummyData() {
        messages.add(new Message("Hello guys!", "Administrator", new Date(), false));
        messages.add(new Message("Hello, how is everyone?", "test user", new Date(), true));
        messages.add(new Message("I'm good and ready to work", "trial user", new Date(), false));
        adapter.notifyDataSetChanged();
    }
}
