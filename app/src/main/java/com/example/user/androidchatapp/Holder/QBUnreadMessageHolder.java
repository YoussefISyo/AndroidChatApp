package com.example.user.androidchatapp.Holder;

import android.os.Bundle;

/**
 * Created by User on 26/12/2019.
 */

public class QBUnreadMessageHolder {
    private static QBUnreadMessageHolder instance;
    private static Bundle bundle;

    public static synchronized QBUnreadMessageHolder getInstance(){
        QBUnreadMessageHolder qbUnreadMessageHolder;
        synchronized (QBUnreadMessageHolder.class){
            if (instance == null)
                instance = new QBUnreadMessageHolder();
            qbUnreadMessageHolder = instance;
        }
        return qbUnreadMessageHolder;
    }

    public QBUnreadMessageHolder(){
        bundle = new Bundle();
    }

    public void setBundle(Bundle bundle){
        this.bundle = bundle;
    }

    public Bundle getBundle(){
        return this.bundle;
    }

    public int getUnreadMessageByDialogId(String i){
        return this.bundle.getInt(i);
    }
}
