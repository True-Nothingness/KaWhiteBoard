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
import com.whiteboard.kobo.model.Drawing;
import com.whiteboard.kobo.model.User;
import com.whiteboard.kobo.model.UserData;
import com.whiteboard.kobo.model.UserResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FilesFragment extends Fragment {
    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);

        recyclerView = view.findViewById(R.id.ownedBoardList);
        adapter = new HomeAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d("userId",":"+UserData.getInstance().getId());
        getBoard();
        // Implement the logic to fetch and update the list of created whiteboards here

        return view;
    }
    private void getBoard(){
        apiService.apiService.getOwnedBoard(UserData.getInstance().getId()).enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {
                if (response.isSuccessful()) {
                    List<Board> mBoards = parseServerResponse(response.body());
                    updateRecyclerView(mBoards);
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private List<Board> parseServerResponse(List<Board> rawResponse) {
        List<Board> whiteboards = new ArrayList<>();

        for (Board rawWhiteboard : rawResponse) {
            Creator creator = rawWhiteboard.getCreator();
            String id = rawWhiteboard.getId();
            String name = rawWhiteboard.getBoardName();
            Date timestamp = rawWhiteboard.getTimestamp();
            List<UserResponse> users = rawWhiteboard.getUsers();
            List<Drawing> drawings = rawWhiteboard.getDrawings();
            int version = rawWhiteboard.getVersion();
            Log.d("Board Data",":"+creator+id+users);
            Board whiteboard = new Board(creator, id, name, timestamp, users, drawings, version);
            whiteboards.add(whiteboard);
        }

        return whiteboards;
    }
    private void updateRecyclerView(List<Board> mBoards) {
        if (mBoards != null && !mBoards.isEmpty()) {
            adapter.setWhiteboards(mBoards);
            adapter.notifyDataSetChanged();
        } else {
            // Handle empty data case (e.g., show a message)
            Toast.makeText(getActivity(), "No boards available", Toast.LENGTH_SHORT).show();
        }
    }
}