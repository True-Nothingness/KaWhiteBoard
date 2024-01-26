package com.whiteboard.kobo;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whiteboard.kobo.model.Board;
import com.whiteboard.kobo.model.Message;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private Context mContext;
    private List<Message> messages;

    // Constructor to initialize the adapter with a list of messages
    public ChatAdapter(Context mContext, List<Message> messages) {
        this.mContext = mContext;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_ME) {
            View view = inflater.inflate(R.layout.item_chat_me, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_other, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(message);
        } else if (holder instanceof OtherMessageViewHolder) {
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.isMe() ? VIEW_TYPE_ME : VIEW_TYPE_OTHER;
    }

    // ViewHolder for messages from the user
    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView chatContainer;
        public TextView timestampDate;
        public TextView timestampHour;

        MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            chatContainer = itemView.findViewById(R.id.text_gchat_message_me);
            timestampDate = itemView.findViewById(R.id.text_gchat_date_me);
            timestampHour = itemView.findViewById(R.id.text_gchat_timestamp_me);
        }

        void bind(Message message) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            chatContainer.setText(message.getMessage());
            timestampDate.setText(dateFormat.format(message.getTimestamp()));  // Pass the date to formatDate method
            timestampHour.setText(hourFormat.format(message.getTimestamp()));  // Pass the date to formatHour method
        }
    }

    // ViewHolder for messages from other users
    static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView chatContainer;
        public TextView timestampDate;
        public TextView timestampHour;
        public TextView senderName;

        OtherMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            chatContainer = itemView.findViewById(R.id.text_gchat_message_other);
            timestampDate = itemView.findViewById(R.id.text_gchat_date_other);
            timestampHour = itemView.findViewById(R.id.text_gchat_timestamp_other);
            senderName = itemView.findViewById(R.id.text_gchat_user_other);
        }

        void bind(Message message) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            chatContainer.setText(message.getMessage());
            timestampDate.setText(dateFormat.format(message.getTimestamp()));  // Pass the date to formatDate method
            timestampHour.setText(hourFormat.format(message.getTimestamp()));  // Pass the date to formatHour method
            senderName.setText(message.getSenderName());
        }
    }
    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(getItemCount()-1); // Notify the adapter that the data set has changed
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
}
