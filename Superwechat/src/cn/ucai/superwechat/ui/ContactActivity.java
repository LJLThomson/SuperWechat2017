package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.hyphenate.easeui.domain.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.db.net.IModelUser;
import cn.ucai.superwechat.db.net.ModelUser;
import cn.ucai.superwechat.db.net.OnCompleteListener;
import cn.ucai.superwechat.utils.DisplayUtils;
import cn.ucai.superwechat.utils.PreferenceManager;

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
    private ProgressDialog dialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity_information);
        ButterKnife.bind(this);
        initView();
        model = new ModelUser();
        DisplayUtils.initBackWithTitle(this, "添加人详细资料");
    }

    private void initView() {
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra(I.User.USER_NAME);
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

    @OnClick(R.id.address_book)
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
