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
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWechatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.User;
import cn.ucai.superwechat.db.net.IModelUser;
import cn.ucai.superwechat.db.net.ModelUser;
import cn.ucai.superwechat.db.net.OnCompleteListener;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.DisplayUtils;
import cn.ucai.superwechat.utils.OkHttpUtils;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.video.util.MFGT;

/**
 * register screen
 */
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.username)
    EditText userNameEditText;
    @BindView(R.id.nick)
    EditText usernick;
    @BindView(R.id.password)
    EditText passwordEditText;
    @BindView(R.id.confirm_password)
    EditText confirmPwdEditText;
    IModelUser model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_register);
        ButterKnife.bind(this);
        model = new ModelUser();
        DisplayUtils.initBackWithTitle(this,"注册");
    }

    @OnClick(R.id.register)
    public void registerUser(View view) {
        final String username = userNameEditText.getText().toString().trim();
        final String nick = usernick.getText().toString().trim();
        final String pwd = passwordEditText.getText().toString().trim();
        String confirm_pwd = confirmPwdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return;
        } else if (username.matches("[a-zA-Z]\\w{5,15}]")) {//正則表達式，5到16位，開頭為字母
            userNameEditText.setError(getResources().getString(R.string.illegal_user_name));
            userNameEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(nick)) {
            Toast.makeText(this, getResources().getString(R.string.User_nick_cannot_be_empty), Toast.LENGTH_SHORT).show();
            usernick.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return;
        } else if (!pwd.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return;
        }
        registerbyArea(username, nick, pwd);

    }

    private void registerbyArea(final String username, String nick, final String pwd) {
        final ProgressDialog pd = new ProgressDialog(this);
        model.RegisterEnter(this, username, nick, pwd, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
//                    将结果String转化为Result
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result.isRetMsg()) {
//                        说明注册成功
                        CommonUtils.showLongToast(R.string.Registered_successfully);
//                        跳到环信注册模块
                        registerEMServer(username, pwd);
                    } else {
//                        注册失败
                        CommonUtils.showLongToast(R.string.Registration_failed);
                    }
                } else {
                    CommonUtils.showLongToast(R.string.Registration_failed);
                }
                pd.dismiss();
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    /**
     * 环信注册用户
     * 当其注册失败时，本地注册应该取消注册
     *
     * @param username 用户名
     * @param pwd      密码
     */
    private void registerEMServer(final String username, final String pwd) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage(getResources().getString(R.string.Is_the_registered));
            pd.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        // call method in SDK
                        EMClient.getInstance().createAccount(username, pwd);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!RegisterActivity.this.isFinishing())
                                    pd.dismiss();
                                // save current user
//                                设置用户名
                                SuperWechatHelper.getInstance().setCurrentUserName(username);
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                                MFGT.gotoLoginActivity(RegisterActivity.this);
                                finish();
                            }
                        });
                    } catch (final HyphenateException e) {
//                        环信注册失败，则要删除本地上传的注册信息
                        unRegister(username);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!RegisterActivity.this.isFinishing())
                                    pd.dismiss();
                                int errorCode = e.getErrorCode();
                                if (errorCode == EMError.NETWORK_ERROR) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }).start();

        }
    }

    /**
     * 取消注册
     * @param username
     */
    private void unRegister(String username) {
        model.UnRegisterEnter(this, username, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null) {
                        CommonUtils.showLongToast(R.string.Registration_failed);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void back(View view) {
        finish();
    }

}
