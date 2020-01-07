package com.example.user.androidchatapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.androidchatapp.Common.Common;
import com.example.user.androidchatapp.Holder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    EditText edtOldPassword,edtPassword,edtFullname,edtEmail,edtPhone;
    Button saveBtn,cancelBtn;

    TextView userName;
    CircleImageView user_avatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.user_profile_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ProgressDialog mProgress = new ProgressDialog(UserProfile.this);

        initViews();

        loadUserProfile();

        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectImage = new Intent();
                selectImage.setType("image/*");
                selectImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(selectImage,"Select Picture"),Common.SELECT_PICTURE);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = edtOldPassword.getText().toString();
                String newPassword = edtPassword.getText().toString();
                String FullName = edtFullname.getText().toString();
                String Email = edtEmail.getText().toString();
                String Phone = edtPhone.getText().toString();

                QBUser user = new QBUser();
                user.setId(QBChatService.getInstance().getUser().getId());

                if (!Common.isNullOrEmptyString(oldPassword))
                    user.setOldPassword(oldPassword);

                if (!Common.isNullOrEmptyString(newPassword))
                    user.setPassword(newPassword);

                if (!Common.isNullOrEmptyString(FullName))
                    user.setFullName(FullName);

                if (!Common.isNullOrEmptyString(Email))
                    user.setEmail(Email);

                if (!Common.isNullOrEmptyString(Phone))
                    user.setPhone(Phone);

                mProgress.setTitle("please wait ...");
                mProgress.show();

                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "User : "+qbUser.getLogin()+" Updated", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(UserProfile.this, "ERROR : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){

            if (requestCode == Common.SELECT_PICTURE){

                Uri selectedImageUri = data.getData();
                final ProgressDialog mDialog = new ProgressDialog(UserProfile.this);
                mDialog.setMessage("Please wait ...");
                mDialog.setCancelable(false);
                mDialog.show();

                try {
                    InputStream in = getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);

                    File file = new File(Environment.getExternalStorageDirectory()+"/myimage.png");
                    FileOutputStream fileOus = new FileOutputStream(file);
                    fileOus.write(bos.toByteArray());
                    fileOus.flush();
                    fileOus.close();

                    int imageSizeKb = (int) (file.length()/1024);
                    if (imageSizeKb >= (1024*100)){

                        Toast.makeText(this, "Error Size", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    QBContent.uploadFileTask(file,true,null)
                            .performAsync(new QBEntityCallback<QBFile>() {
                                @Override
                                public void onSuccess(QBFile qbFile, Bundle bundle) {
                                    QBUser user = new QBUser();
                                    user.setId(QBChatService.getInstance().getUser().getId());
                                    user.setFileId(Integer.parseInt(qbFile.getId().toString()));

                                    QBUsers.updateUser(user)
                                            .performAsync(new QBEntityCallback<QBUser>() {
                                                @Override
                                                public void onSuccess(QBUser qbUser, Bundle bundle) {
                                                    mDialog.dismiss();
                                                    user_avatar.setImageBitmap(bitmap);
                                                }

                                                @Override
                                                public void onError(QBResponseException e) {

                                                }
                                            });
                                }

                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void loadUserProfile() {

        QBUsers.getUser(QBChatService.getInstance().getUser().getId())
                .performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        QBUsersHolder.getInstance().putUser(qbUser);

                        userName.setText(qbUser.getFullName());
                        edtFullname.setText(qbUser.getFullName());
                        edtEmail.setText(qbUser.getEmail());
                        if (qbUser.getPhone() != null)
                            edtPhone.setText(qbUser.getPhone());

                        if (qbUser.getFileId() != null){

                            int profilePictureId = qbUser.getFileId();

                            QBContent.getFile(profilePictureId)
                                    .performAsync(new QBEntityCallback<QBFile>() {
                                        @Override
                                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                                            String fileUrl = qbFile.getPublicUrl();
                                            Glide.with(getBaseContext())
                                                    .load(fileUrl)
                                                    .into(user_avatar);
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });


        QBUser currentUser = QBChatService.getInstance().getUser();

        String fullName = currentUser.getFullName();
        String mEmail = currentUser.getEmail();
        String mPhone = currentUser.getPhone();

        edtFullname.setText(fullName);
        edtEmail.setText(mEmail);
        edtPhone.setText(mPhone);
    }

    private void initViews() {
        saveBtn = findViewById(R.id.SaveBtn);
        cancelBtn = findViewById(R.id.CancelBtn);

        edtOldPassword = findViewById(R.id.edit_old_password);
        edtPassword = findViewById(R.id.edit_new_password);
        edtFullname = findViewById(R.id.edit_fullname);
        edtEmail = findViewById(R.id.edit_email);
        edtPhone = findViewById(R.id.edit_phone);

        userName = findViewById(R.id.userNameProfile);

        user_avatar = findViewById(R.id.user_avatar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_update_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.user_menu_log_out:
                logout();
                break;

            default:
                break;
        }
        return true;
    }

    private void logout() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        Toast.makeText(UserProfile.this, "you are logout !!", Toast.LENGTH_SHORT).show();
                        Intent mIntent = new Intent(UserProfile.this,MainActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // clear all previous activity
                        startActivity(mIntent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }
}
