package com.example.user.androidchatapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.example.user.androidchatapp.Holder.QBUnreadMessageHolder;
import com.example.user.androidchatapp.R;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by User on 20/12/2019.
 */

public class ChatDialogsAdapters extends BaseAdapter {

    private Context mContext;
    private ArrayList<QBChatDialog> qbChatDialog;

    public ChatDialogsAdapters(Context mContext, ArrayList<QBChatDialog> qbChatDialog) {
        this.mContext = mContext;
        this.qbChatDialog = qbChatDialog;
    }

    @Override
    public int getCount() {
        return qbChatDialog.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialog.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_chat_dialog,null);


            TextView txtTitle,txtMessage;
            final ImageView mImage,image_unread;

            txtMessage = view.findViewById(R.id.listChatDialog_message);
            txtTitle = view.findViewById(R.id.listChatDialog_title);

            txtMessage.setText(qbChatDialog.get(position).getLastMessage());
            txtTitle.setText(qbChatDialog.get(position).getName());

            mImage = view.findViewById(R.id.image_chatDialog);
            image_unread = view.findViewById(R.id.image_unread);

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();

            if (qbChatDialog.get(position).getPhoto() == null) {

                TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                        .withBorder(4)
                        .endConfig()
                        .round();

                TextDrawable drawable = builder.build(txtTitle.getText().toString().substring(0, 1).toUpperCase(), randomColor);

                mImage.setImageDrawable(drawable);

            }
            else{

                QBContent.getFile(Integer.parseInt(qbChatDialog.get(position).getPhoto()))
                        .performAsync(new QBEntityCallback<QBFile>() {
                            @Override
                            public void onSuccess(QBFile qbFile, Bundle bundle) {
                                String fileUrl = qbFile.getPublicUrl();
                                Glide.with(mContext)
                                        .load(fileUrl)
                                        .into(mImage);

                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Log.e("Error_Image",""+e.getMessage());
                            }
                        });

            }

            //set unread message count
            TextDrawable.IBuilder unreadBuilder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();

            int unreadCount = QBUnreadMessageHolder.getInstance().getBundle().getInt(qbChatDialog.get(position).getDialogId());
            if (unreadCount > 0){
                TextDrawable unreadDrawable = unreadBuilder.build(""+unreadCount, Color.RED);
                image_unread.setImageDrawable(unreadDrawable);
            }
        }

        return view;
    }
}
