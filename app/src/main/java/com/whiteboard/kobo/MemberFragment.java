package com.whiteboard.kobo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whiteboard.kobo.model.CurrentBoard;
import com.whiteboard.kobo.model.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends Fragment {
    private RecyclerView userList;
    private MemberAdapter adapter;
    private List<UserResponse> members;
    public MemberFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        userList = view.findViewById(R.id.userRecyclerList);
        members = CurrentBoard.getInstance().getUsers();
        adapter = new MemberAdapter(getContext(), members);
        userList.setAdapter(adapter);
        userList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}