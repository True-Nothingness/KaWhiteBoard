package com.whiteboard.kobo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.whiteboard.kobo.model.Board;
import com.whiteboard.kobo.model.Creator;
import com.whiteboard.kobo.model.CurrentBoard;
import com.whiteboard.kobo.model.Drawing;
import com.whiteboard.kobo.model.UserResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.WhiteboardViewHolder> {
    private Context mContext;

    private List<Board> mBoards;

    public HomeAdapter(Context mContext, List<Board> mBoards) {
        this.mContext = mContext;

        this.mBoards = mBoards;
    }

    public static class WhiteboardViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView timestampTextView;

        public WhiteboardViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.whiteboardNameTextView);
            timestampTextView = itemView.findViewById(R.id.whiteboardTimestampTextView);
        }
    }

    @Override
    public WhiteboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new WhiteboardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WhiteboardViewHolder holder, int position) {
        Board currentWhiteboard = mBoards.get(position);
        holder.nameTextView.setText(currentWhiteboard.getBoardName());
        holder.timestampTextView.setText(formatTimestamp(currentWhiteboard.getTimestamp()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle board join action here
                String boardId = currentWhiteboard.getId();
                String boardName = currentWhiteboard.getBoardName();
                Date timestamp = currentWhiteboard.getTimestamp();
                Creator creator = currentWhiteboard.getCreator();
                List<UserResponse> users = currentWhiteboard.getUsers();
                List<Drawing> drawings = currentWhiteboard.getDrawings();
                int version = currentWhiteboard.getVersion();
                CurrentBoard.getInstance().setId(boardId);
                CurrentBoard.getInstance().setBoardName(boardName);
                CurrentBoard.getInstance().setCreator(creator);
                CurrentBoard.getInstance().setTimestamp(timestamp);
                CurrentBoard.getInstance().setUsers(users);
                CurrentBoard.getInstance().setDrawings(drawings);
                CurrentBoard.getInstance().setVersion(version);
                mContext.startActivity(new Intent(mContext, BoardActivity.class));
            }
        });
    }
    private String formatTimestamp(Date timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(timestamp);
    }
    @Override
    public int getItemCount() {
        return mBoards.size();
    }

    public void setWhiteboards(List<Board> mBoards) {
        this.mBoards = mBoards;
        notifyDataSetChanged();
    }
}
