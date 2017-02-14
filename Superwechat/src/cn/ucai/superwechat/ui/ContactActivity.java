package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.hyphenate.easeui.domain.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWechatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.db.net.IModelUser;
import cn.ucai.superwechat.db.net.ModelUser;
import cn.ucai.superwechat.db.net.OnCompleteListener;
import cn.ucai.superwechat.utils.DisplayUtils;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.video.util.MFGT;

/**
 * Created by Administrator on 2017/2/14 0014.
 */

public class ContactActivity extends BaseActivity {
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.user_nick)
    TextView userNick;
    @BindView(R.id.user_name)
    TextView userName;
    IModelUser model;
    @BindView(R.id.address_book)
    Button addressBook;
    private ProgressDialog dialog;
    User user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity_information);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        model = new ModelUser();
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(I.User.USER_NAME);
        showinfo();
        DisplayUtils.initBackWithTitle(this, "添加人详细资料");
    }

    private void showinfo() {
        userName.setText("微信号：" + user.getMUserName());
        userNick.setText(user.getMUserNick());
        if (user != null && user.getAvatar() != null) {
//            头像设置
            User.setAppUserAvatarByPath(this, user.getAvatar(), userAvatar);
        }
        if (isFriend()) {
            // 是你的好友，点击则发送消息，采用监听事件
            addressBook.setText("发送消息");
            addressBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    进入聊天界面
                }
            });
        } else {
//            不是你的好友，点击则添加验证（先跳转页面）
            addressBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MFGT.gotoAddFriendActivity(ContactActivity.this,user);
                }
            });
        }
    }

    private boolean isFriend() {
        User u = SuperWechatHelper.getInstance().getAppContactList().get(user.getMUserName());
        if (u == null) {
            return false;
        } else {
//            将好友添加到联系人当中
            SuperWechatHelper.getInstance().saveAppContact(user);
            return true;
        }
    }

    public void addcontact() {
        String username = PreferenceManager.getInstance().getCurrentUsername();
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra(I.User.USER_NAME);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.addcontact_adding));
        dialog.show();
        model.addcontact(this, username, user.getMUserName(), new OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                if (result != null) {
                    User user = new Gson().fromJson(result.getRetData().toString(), User.class);
                    if (user != null) {
                        try {
                            int avatarResId = Integer.parseInt(user.getAvatar());
                            Glide.with(ContactActivity.this).load(avatarResId).into(userAvatar);
                            dialog.setMessage(getString(R.string.addcontact_send_msg));
                        } catch (Exception e) {
                            //use default avatar
                            Glide.with(ContactActivity.this).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_hd_avatar).into(userAvatar);
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
