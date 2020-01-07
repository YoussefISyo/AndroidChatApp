package com.example.user.androidchatapp.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.user.androidchatapp.R;
import com.example.user.androidchatapp.Holder.QBUsersHolder;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 22/12/2019.
 */

public class ChatMessageAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<QBChatMessage> qbChatMessage;

    public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessage) {
        this.context = context;
        this.qbChatMessage = qbChatMessage;
    }

    @Override
    public int getCount() {
        return qbChatMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatMessage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (qbChatMessage.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId())){
                view = inflater.inflate(R.layout.list_send_message, null);
                BubbleTextView bubbleTextView = view.findViewById(R.id.content_message);
                bubbleTextView.setText(qbChatMessage.get(position).getBody());
            }
            else{
                view = inflater.inflate(R.layout.list_recv_message, null);
                BubbleTextView bubbleTextView = view.findViewById(R.id.content_message);
                bubbleTextView.setText(qbChatMessage.get(position).getBody());
                final CircleImageView profileImageChat = view.findViewById(R.id.profile_image_chat);

                if (QBUsersHolder.getInstance().getUserById(qbChatMessage.get(position).getSenderId()).getFileId() != null) {

                        int profilePictureId = QBUsersHolder.getInstance().getUserById(qbChatMessage.get(position).getSenderId()).getFileId();

                        QBContent.getFile(profilePictureId)
                                .performAsync(new QBEntityCallback<QBFile>() {
                                    @Override
                                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                                        String fileUrl = qbFile.getPublicUrl();
                                        Glide.with(context)
                                                .load(fileUrl)
                                                .into(profileImageChat);
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {

                                    }
                                });

                    }


            }

        }
        return view;
    }
}
