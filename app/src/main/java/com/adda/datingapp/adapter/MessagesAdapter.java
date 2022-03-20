package com.adda.datingapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adda.datingapp.model.MessageModel;
import com.adda.datingapp.R;
import com.adda.datingapp.activity.MessagePhotoActivity;
import com.adda.datingapp.databinding.ItemReceiveBinding;
import com.adda.datingapp.databinding.ItemSentBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> list;
    Context context;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    public MessagesAdapter(ArrayList<MessageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel messageModel=list.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(messageModel.getSenderId())){
            return ITEM_SENT;
        }else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel=list.get(position);
        if (holder.getClass()==SenderViewHolder.class){
            SenderViewHolder senderViewHolder=(SenderViewHolder)holder;
            if (messageModel.getMessage().equals("photo")){
                senderViewHolder.binding.image.setVisibility(View.VISIBLE);
                senderViewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(messageModel.getImageUrl())
                        .placeholder(R.drawable.plasholder)
                        .into(senderViewHolder.binding.image);

                senderViewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, MessagePhotoActivity.class);
                        intent.putExtra("msgPhoto", messageModel.getImageUrl());
                        context.startActivity(intent);

                    }
                });
            }else {
                senderViewHolder.binding.image.setVisibility(View.GONE);
                senderViewHolder.binding.message.setVisibility(View.VISIBLE);
            }
            senderViewHolder.binding.message.setText(messageModel.getMessage());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                        .withLocale(Locale.getDefault() )
                        .withZone( ZoneId.systemDefault() );
                long timeStamp = messageModel.getTimestamp();
                Instant instant = Instant.ofEpochMilli(timeStamp);
                String messageTimeSent = formatter.format(instant);

                senderViewHolder.binding.mainRowSent.setOnClickListener(view -> {
                    senderViewHolder.binding.messageDateSent.setVisibility(View.VISIBLE);
                    senderViewHolder.binding.messageDateSent.setText(messageTimeSent);
                });
            }

        }else {
            ReceiverViewHolder receiverViewHolder=(ReceiverViewHolder) holder;
            if (messageModel.getMessage().equals("photo")){
                receiverViewHolder.binding.image.setVisibility(View.VISIBLE);
                receiverViewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(messageModel.getImageUrl())
                        .placeholder(R.drawable.plasholder)
                        .into(receiverViewHolder.binding.image);
                receiverViewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, MessagePhotoActivity.class);
                        intent.putExtra("msgPhoto", messageModel.getImageUrl());
                        context.startActivity(intent);

                    }
                });
            }else {
                receiverViewHolder.binding.image.setVisibility(View.GONE);
                receiverViewHolder.binding.message.setVisibility(View.VISIBLE);
            }
            receiverViewHolder.binding.message.setText(messageModel.getMessage());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                        .withLocale(Locale.getDefault() )
                        .withZone( ZoneId.systemDefault() );
                long timeStamp = messageModel.getTimestamp();
                Instant instant = Instant.ofEpochMilli(timeStamp);
                String messageTimeSent = formatter.format(instant);

                receiverViewHolder.binding.mainRowReceiver.setOnClickListener(view -> {
                    receiverViewHolder.binding.messageDateReceiver.setVisibility(View.VISIBLE);
                    receiverViewHolder.binding.messageDateReceiver.setText(messageTimeSent);
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        ItemSentBinding binding;
        public SenderViewHolder(@NonNull  View itemView) {
            super(itemView);
            binding=ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        ItemReceiveBinding binding;
        public ReceiverViewHolder(@NonNull  View itemView) {
            super(itemView);
            binding=ItemReceiveBinding.bind(itemView);
        }
    }
}
