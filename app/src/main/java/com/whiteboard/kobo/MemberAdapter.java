package com.whiteboard.kobo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.whiteboard.kobo.api.apiService;
import com.whiteboard.kobo.model.Board;
import com.whiteboard.kobo.model.Creator;
import com.whiteboard.kobo.model.CurrentBoard;
import com.whiteboard.kobo.model.UserResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private Context mContext;

    private List<UserResponse> mUsers;
    String selectedRole;

    public MemberAdapter(Context mContext, List<UserResponse> mUsers) {
        this.mContext = mContext;

        this.mUsers = mUsers;
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public TextView roleTextView;
        public ImageButton changeRoleButton, removeButton;

        public MemberViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            changeRoleButton = itemView.findViewById(R.id.changeRoleButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        UserResponse currentUser = mUsers.get(position);
        holder.usernameTextView.setText(currentUser.getName());
        holder.roleTextView.setText(currentUser.getRole());
        if ("Admin".equals(currentUser.getRole())) {
            // Hide the Remove and Change Role buttons
            holder.removeButton.setVisibility(View.GONE);
            holder.changeRoleButton.setVisibility(View.GONE);
        }
        holder.changeRoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeRoleDialog(currentUser.getId());
            }
        });
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("Are you sure you want to remove this user?", new Runnable() {
                    @Override
                    public void run() {
                        // Execute the removal logic (call your API, update UI, etc.)
                        removeMember(currentUser.getId());
                    }
                });
            }
        });
    }
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void setMembers(List<UserResponse> mUsers) {
        this.mUsers = mUsers;
        notifyDataSetChanged();
    }
    public void showChangeRoleDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Choose User Role:");

        // Define the options to display in the dialog
        final String[] roles = {"Viewer", "Editor"};

        builder.setItems(roles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index of the selected item
                selectedRole = roles[which];
                changeRole(userId, selectedRole);
            }
        });

        // Create and show the dialog
        builder.create().show();
    }
    public void showConfirmationDialog(String message, final Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Confirmation");
        builder.setMessage(message);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, execute the onConfirm action
                if (onConfirm != null) {
                    onConfirm.run();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, do nothing or handle accordingly
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        builder.create().show();
    }

    public void changeRole(String userId, String role){
        apiService.apiService.changeRole(userId, role, CurrentBoard.getInstance().getId()).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
            if(response.isSuccessful()){
                List<UserResponse> updatedUserList = response.body();
                setMembers(updatedUserList);
                Toast.makeText(mContext, "Role changed successfully!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                Toast.makeText(mContext, "Server Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void removeMember(String userId){
        apiService.apiService.removeMember(userId, CurrentBoard.getInstance().getId()).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if(response.isSuccessful()){
                    List<UserResponse> updatedUserList = response.body();
                    setMembers(updatedUserList);
                    Toast.makeText(mContext, "User removed!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                Toast.makeText(mContext, "Server Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

