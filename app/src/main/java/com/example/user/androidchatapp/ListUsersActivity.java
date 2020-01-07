package com.example.user.androidchatapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.androidchatapp.Adapter.ListUsersAdapter;
import com.example.user.androidchatapp.Common.Common;
import com.example.user.androidchatapp.Holder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ListUsersActivity extends AppCompatActivity {

    ListView listUsers;
    Button btnCreateChat;


    String mode = "";
    QBChatDialog qbChatDialog;
    List<QBUser> userAdd= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        mode = getIntent().getStringExtra(Common.UPDATE_MODE);
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.UPDATE_DIALOG_EXTRA);




        listUsers = findViewById(R.id.listUsers);
        listUsers.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        btnCreateChat = findViewById(R.id.btn_create_chat);
        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mode == null) {


                    if (listUsers.getCheckedItemPositions().size() == 1) {
                        createPrivateChat(listUsers.getCheckedItemPositions());
                    } else {
                        if (listUsers.getCheckedItemPositions().size() > 1) {
                            createGroupChat(listUsers.getCheckedItemPositions());
                        } else {
                            Toast.makeText(ListUsersActivity.this, "Please select a freind to chat", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else if (mode.equals(Common.UPDATE_ADD_MODE) && qbChatDialog != null){

                    if (userAdd.size()>0){

                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                        int countChoice = listUsers.getCount();
                        SparseBooleanArray checkItemPosition = listUsers.getCheckedItemPositions();

                        for (int i =0;i<countChoice;i++){

                            if (checkItemPosition.get(i)){
                                QBUser user = (QBUser) listUsers.getItemAtPosition(i);
                                requestBuilder.addUsers(user);
                            }

                        }

                        QBRestChatService.updateChatDialog(qbChatDialog,requestBuilder)
                                .performAsync(new QBEntityCallback<QBChatDialog>() {
                                    @Override
                                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                        Toast.makeText(ListUsersActivity.this, "User added successfuly", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {

                                    }
                                });
                    }
                }
                else if (mode.equals(Common.UPDATE_REMOVE_MODE) && qbChatDialog != null){

                    if (userAdd.size()>0){
                        QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();

                        int countChoice = listUsers.getCount();
                        SparseBooleanArray checkItemPosition = listUsers.getCheckedItemPositions();

                        for (int i =0;i<countChoice;i++){

                            if (checkItemPosition.get(i)){
                                QBUser user = (QBUser) listUsers.getItemAtPosition(i);
                                requestBuilder.removeUsers(user);
                            }

                        }

                        QBRestChatService.updateChatDialog(qbChatDialog,requestBuilder)
                                .performAsync(new QBEntityCallback<QBChatDialog>() {
                                    @Override
                                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                        Toast.makeText(ListUsersActivity.this, "User removed successfuly", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {

                                    }
                                });


                    }


                }






            }
        });

        if (mode == null && qbChatDialog == null){

            retreiveAllUsers();
        }
        else {
            if (mode.equals(Common.UPDATE_ADD_MODE)){
                loadListAvailableUsers();
            }
            else if(mode.equals(Common.UPDATE_REMOVE_MODE))
                loadListUsersInGroup();
        }
    }

    private void loadListUsersInGroup() {

        btnCreateChat.setText("Remove User");

        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        List<Integer> occupantsIds = qbChatDialog.getOccupants();
                        List<QBUser> usersAlreadyInGroup = QBUsersHolder.getInstance().getUsersByIds(occupantsIds);

                        ArrayList<QBUser> users = new ArrayList<>();
                        users.addAll(usersAlreadyInGroup);

                        ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(),users);
                        listUsers.setAdapter(adapter);

                        adapter.notifyDataSetChanged();

                        userAdd=users;
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(ListUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadListAvailableUsers() {
        btnCreateChat.setText("Add User");

        QBRestChatService.getChatDialogById(qbChatDialog.getDialogId())
                .performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        ArrayList<QBUser> listusers = QBUsersHolder.getInstance().getAllUsers();

                        List<Integer> occupantsIds = qbChatDialog.getOccupants();
                        List<QBUser> usersAlreadyInChatGroup = QBUsersHolder.getInstance().getUsersByIds(occupantsIds);

                        //Remove users who already in chat group
                        for (QBUser qbUser:usersAlreadyInChatGroup)
                            listusers.remove(qbUser);

                        if (listusers.size()>0){
                            ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(),listusers);
                            listUsers.setAdapter(adapter);

                            adapter.notifyDataSetChanged();

                            userAdd=listusers;
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(ListUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mProgress = new ProgressDialog(ListUsersActivity.this);
        mProgress.setMessage("Please wait ..");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        int countChoice = listUsers.getCount();

        for (int i =0;i<countChoice;i++){
            if (checkedItemPositions.get(i)){
                final QBUser mUser = (QBUser) listUsers.getItemAtPosition(i);
               QBChatDialog dialog = DialogUtils.buildPrivateDialog(mUser.getId());

               QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                   @Override
                   public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                       mProgress.dismiss();
                       Toast.makeText(ListUsersActivity.this, "Create private chat dialog successfuly", Toast.LENGTH_SHORT).show();

                       //Send system message to reciepient id user
                       QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                       QBChatMessage qbChatMessage = new QBChatMessage();
                       qbChatMessage.setRecipientId(mUser.getId());
                       qbChatMessage.setBody(qbChatDialog.getDialogId());

                       try {
                           qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                       } catch (SmackException.NotConnectedException e) {
                           e.printStackTrace();
                       }

                       finish();
                   }

                   @Override
                   public void onError(QBResponseException e) {
                       Log.d("ERROR",e.getMessage());
                   }
               });
            }
        }

    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog mProgress = new ProgressDialog(ListUsersActivity.this);
        mProgress.setMessage("Please wait ..");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        int countChoice = listUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
        for (int i =0;i<countChoice;i++){
            if (checkedItemPositions.get(i)){
                QBUser mUser = (QBUser) listUsers.getItemAtPosition(i);
                occupantIdsList.add(mUser.getId());
            }
        }

        QBChatDialog chatDialog = new QBChatDialog();
        chatDialog.setName(Common.createChatDialogName(occupantIdsList));
        chatDialog.setType(QBDialogType.GROUP);
        chatDialog.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(chatDialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mProgress.dismiss();
                Toast.makeText(ListUsersActivity.this, "Create chat dialog successfuly", Toast.LENGTH_SHORT).show();

                //Send system message to reciepient id user
                QBSystemMessagesManager qbSystemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
                QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setBody(qbChatDialog.getDialogId());
               for (int i=0;i<qbChatDialog.getOccupants().size();i++){
                   qbChatMessage.setRecipientId(qbChatDialog.getOccupants().get(i));
                   try {
                       qbSystemMessagesManager.sendSystemMessage(qbChatMessage);
                   } catch (SmackException.NotConnectedException e) {
                       e.printStackTrace();
                   }
               }




                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("ERROR",e.getMessage());
            }
        });
    }

    private void retreiveAllUsers() {

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                QBUsersHolder.getInstance().putUsers(qbUsers);


                ArrayList<QBUser> QBUsersWithoutCurrent = new ArrayList<QBUser>();
                for (QBUser user : qbUsers){
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                        QBUsersWithoutCurrent.add(user);
                }

                ListUsersAdapter listUsersAdapter = new ListUsersAdapter(getBaseContext(),QBUsersWithoutCurrent);
                listUsers.setAdapter(listUsersAdapter);
                listUsersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("ERROR",e.getMessage());
            }
        });

    }
}
