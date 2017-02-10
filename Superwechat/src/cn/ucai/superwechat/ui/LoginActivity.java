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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWechatApplication;
import cn.ucai.superwechat.SuperWechatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.db.SuperWechatDBManager;
import cn.ucai.superwechat.db.net.IModelUser;
import cn.ucai.superwechat.db.net.ModelUser;
import cn.ucai.superwechat.db.net.OnCompleteListener;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.DisplayUtils;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * Login screen
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int REQUEST_CODE_SETNICK = 1;
    @BindView(R.id.username)
    EditText usernameEditText;
    @BindView(R.id.password)
    EditText passwordEditText;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.forgetPassword)
    TextView forgetPassword;
    @BindView(R.id.register)
    Button register;

    private boolean progressShow;
    private boolean autoLogin = false;
    IModelUser model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enter the main activity if already logged in
//		如果已经登录，则直接进入MainActivity中
        if (SuperWechatHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return;
        }

        setContentView(R.layout.em_activity_login);
        ButterKnife.bind(this);
        model = new ModelUser();
        DisplayUtils.initBackWithTitle(this,"登录");
        // if user changed, clear the password
//        addTextChangedListener(new TextWatcher)
//        用于检测，及时提醒用户输入合不合法，或者，是否已经被注册等等功能
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//				当用户名变化时，密码设置为null
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (SuperWechatHelper.getInstance().getCurrentUsernName() != null) {
            usernameEditText.setText(SuperWechatHelper.getInstance().getCurrentUsernName());
        }
    }

    /**
     * login
     * 环信和本地，登录过程相同，只有用户名和密码，所以可以采用环信登录，这也是优化
     * @param view
     */
    @OnClick(R.id.login)
    public void loginAppServer(View view) {
        final ProgressDialog pd = new ProgressDialog(this);
        final String currentUsername = usernameEditText.getText().toString().trim();
        final String currentPassword = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(currentUsername)) {
            usernameEditText.requestFocus();
            Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            passwordEditText.requestFocus();
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        pd.show();
        model.LoginEnter(this, currentUsername, currentPassword, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s !=null){
                    Log.i("main","s="+s);
//                    {"retCode":0,"retMsg":true,
// "retData":{"muserName":"fasf","muserNick":"sfadf","mavatarId":1245,"mavatarPath":"user_avatar","mavatarSuffix":null,"mavatarType":0,"mavatarLastUpdateTime":"1486559510419"}
                 Result result = ResultUtils.getResultFromJson(s, User.class);
                    Log.i("main","result="+result);
                    if (result !=null){
                        if (result.isRetMsg()) {
//                    CommonUtils.showLongToast(R.string.login);
                            pd.dismiss();
                            loginEMServer(currentUsername, currentPassword);
                        } else {
                            pd.dismiss();
                            CommonUtils.showLongToast(R.string.Login_failed);
                        }
                    }
                }else{
                    pd.dismiss();
                    CommonUtils.showLongToast(R.string.Login_failed);
                }
            }

            @Override
            public void onError(String error) {
                CommonUtils.showLongToast(R.string.Login_failed);
            }
        });
    }

    private void loginEMServer(String currentUsername, String currentPassword) {
        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        progressShow = true;
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "EMClient.getInstance().onCancel");
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
//        登录成功之后，关闭数据库
        SuperWechatDBManager.getInstance().closeDB();

        // reset current user name before login
        SuperWechatHelper.getInstance().setCurrentUserName(currentUsername);

        final long start = System.currentTimeMillis();
        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(currentUsername, MD5.getMessageDigest(currentPassword), new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");


                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(
                        SuperWechatApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }

                // get user's info (this should be get from App's server or 3rd party service)
//                用于set昵称和头像,便于后面调用
//                	setCurrentUserNick(value.getNick());
//                setCurrentUserAvatar(value.getAvatar());
                SuperWechatHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo(LoginActivity.this);
//              登录成功之后，跳转到MainActivity界面
                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /**
     * register
     *
     * @param view
     */
    @OnClick(R.id.register)
    public void register(View view) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
}
