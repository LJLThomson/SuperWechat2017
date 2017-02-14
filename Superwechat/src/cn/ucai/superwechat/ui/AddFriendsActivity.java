package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.widget.EaseAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWechatHelper;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.video.util.MFGT;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class AddFriendsActivity extends BaseActivity {
    @BindView(R.id.add_list_friends)
    TextView addListFriends;
    @BindView(R.id.search)
    Button search;
    @BindView(R.id.title)
    RelativeLayout title;
    @BindView(R.id.mEditext)
    EditText mEditext;
    User user;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        ButterKnife.bind(this);
        addListFriends.setText("验证消息");
        initDate();
    }

    private void initDate() {
        user = (User) getIntent().getSerializableExtra(cn.ucai.superwechat.I.User.USER_NAME);
        if (user != null) {
//            从内存中取出消息
            mEditext.setText("我是" + PreferenceManager.getInstance().getCurrentUserNick());
        } else {
            MFGT.finish(this);
        }
    }

    @OnClick({R.id.image, R.id.search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                finish();
                break;
            case R.id.search:
//                发送消息
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String msg = mEditext.getText().toString();
        addContact(msg);
    }

    /**
     * add contact
     *
     * @param
     */
    public void addContact(final String msg) {
        final String username = user.getMUserName();
        if (EMClient.getInstance().getCurrentUser().equals(username)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }
//      查看，集合或者本地数据库中是否保存了该用户，保存了， 则表示该用户已添加
        if (SuperWechatHelper.getInstance().getContactList().containsKey(username)) {
            //let the user know the contact already in your contact list
//            这段是判断环信上，互相加好友木有，加了，我们这边无法删除，将其屏蔽
//            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(username)) {
//                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
//                return;
//            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {
                try {
                    //demo use a hardcode reason here, you need let user to input if you like
//                    String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(username, msg);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                            MFGT.finish(AddFriendsActivity.this);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                            MFGT.finish(AddFriendsActivity.this);
                        }
                    });
                }
            }
        }).start();
    }
}
