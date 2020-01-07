package com.example.user.androidchatapp.Holder;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 22/12/2019.
 */

public class QBChatMessageHolder {

    private static QBChatMessageHolder instance;
    private HashMap<String,ArrayList<QBChatMessage>> qbChatMessageArray;

    public static synchronized QBChatMessageHolder getInstance(){
        QBChatMessageHolder qbChatMessageHolder;
        synchronized (QBChatMessageHolder.class){
            if (instance == null)
                instance = new QBChatMessageHolder();
            qbChatMessageHolder = instance;
        }
        return qbChatMessageHolder;
    }

    private QBChatMessageHolder(){
        this.qbChatMessageArray = new HashMap<>();
    }

    public void putMessages(String dialogId, ArrayList<QBChatMessage> qbChatMessage){
        this.qbChatMessageArray.put(dialogId,qbChatMessage);
    }

    public void putMessage(String dialogId,QBChatMessage qbChatMessage){
        List<QBChatMessage> listResult = this.qbChatMessageArray.get(dialogId);
        listResult.add(qbChatMessage);
        ArrayList<QBChatMessage> listAdded = new ArrayList<>(listResult.size());
        listAdded.addAll(listResult);
        putMessages(dialogId,listAdded);

    }



    public ArrayList<QBChatMessage> getMessageByIdDialog(String dialogId){
        return this.qbChatMessageArray.get(dialogId);
    }
}
