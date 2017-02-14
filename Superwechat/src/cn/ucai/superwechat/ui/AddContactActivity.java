/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.widget.EaseAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.db.net.IModelUser;
import cn.ucai.superwechat.db.net.ModelUser;
import cn.ucai.superwechat.db.net.OnCompleteListener;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.video.util.MFGT;

public class AddContactActivity extends BaseActivity {
    @BindView(R.id.add_list_friends)
    TextView addListFriends;
    @BindView(R.id.search)
    Button searchBtn;
    @BindView(R.id.title)
    RelativeLayout title;
    @BindView(R.id.edit_note)
    EditText editText;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.name)
    TextView nameText;
    @BindView(R.id.indicator)
    Button indicator;
    @BindView(R.id.ll_user)
    RelativeLayout searchedUserLayout;
    @BindView(R.id.no_cuser)
    TextView noCuser;

    private String toAddUsername;
    private ProgressDialog progressDialog;
    IModelUser model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_add_contact);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String strAdd = getResources().getString(R.string.add_friend);
        addListFriends.setText(strAdd);
        String strUserName = getResources().getString(R.string.user_name);
        editText.setHint(strUserName);
        model = new ModelUser();
    }

    @OnClick({R.id.search, R.id.indicator})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
//                查找好友
                searchContact();
                break;
        }
    }

    /**
     * search contact
     *
     * @param
     */
    public void searchContact() {
        final String name = editText.getText().toString();
        String saveText = searchBtn.getText().toString();
//        非空验证
        if (getString(R.string.button_search).equals(saveText)) {
            toAddUsername = name;
            if (TextUtils.isEmpty(name) ) {
                new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
                return;
            }
            // TODO you can search the user from your app server here.
            //show the userame and add button if user exist
//            下面进行查找
            if (name.equals(PreferenceManager.getInstance().getCurrentUsername())){
                new EaseAlertDialog(this, R.string.not_add_myself).show();
                return;
            }
            progressDialog = new ProgressDialog(this);
            String str1 = getResources().getString(R.string.addcontact_search);
            progressDialog.setMessage(str1);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            searchAppUser(toAddUsername);
//            searchedUserLayout.setVisibility(View.VISIBLE);
//            nameText.setText(toAddUsername);
        }
    }

    private void searchAppUser(final String cname) {
        model.getUserByName(this, cname, new OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                if (result != null && result.isRetMsg()) {
                    final User user = new Gson().fromJson(result.getRetData().toString(), User.class);
                    if (user != null) {
//                       查到之后，就可以显示出来了
                        searchedUserLayout.setVisibility(View.VISIBLE);
                        nameText.setText(user.getMUserName());
                        if (user.getAvatar() != null) {
                            try {
                                int avatarResId = Integer.parseInt(user.getAvatar());
                                Glide.with(AddContactActivity.this).load(avatarResId).into(avatar);
                            } catch (Exception e) {
                                //use default avatar
                                Glide.with(AddContactActivity.this).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_hd_avatar).into(avatar);
                            }
                        }
                        indicator.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MFGT.gotoContact_add(AddContactActivity.this, user);
                            }
                        });
                        progressDialog.dismiss();
                    }
                } else {
//                    添加失败
                    noCuser.setVisibility(View.VISIBLE);
                    CommonUtils.showLongToast(R.string.search_contanier);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(String error) {
                noCuser.setVisibility(View.VISIBLE);
                CommonUtils.showLongToast(R.string.search_contanier);
                progressDialog.dismiss();
            }
        });
    }

//    /**
//     * add contact
//     *
//     * @param view
//     */
//    public void addContact(View view) {
//        if (EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())) {
//            new EaseAlertDialog(this, R.string.not_add_myself).show();
//            return;
//        }
//
//        if (SuperWechatHelper.getInstance().getContactList().containsKey(nameText.getText().toString())) {
//            //let the user know the contact already in your contact list
//            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())) {
//                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
//                return;
//            }
//            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
//            return;
//        }
//
//        progressDialog = new ProgressDialog(this);
//        String stri = getResources().getString(R.string.Is_sending_a_request);
//        progressDialog.setMessage(stri);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//
//        new Thread(new Runnable() {
//            public void run() {
//
//                try {
//                    //demo use a hardcode reason here, you need let user to input if you like
//                    String s = getResources().getString(R.string.Add_a_friend);
//                    EMClient.getInstance().contactManager().addContact(toAddUsername, s);
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            String s1 = getResources().getString(R.string.send_successful);
//                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
//                        }
//                    });
//                } catch (final Exception e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            progressDialog.dismiss();
//                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
//                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            }
//        }).start();
//    }

    public void back(View v) {
        finish();
    }

}
