package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.DisplayUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.PreferenceManager;

/**
 * Created by Administrator on 2017/2/10 0010.
 */

public class PersonalInformationAcitivity extends BaseActivity {
    private static String TAG = PersonalInformationAcitivity.class.getSimpleName();
    @BindView(R.id.layout_userinfo_avatar)
    RelativeLayout layoutUserinfoAvatar;
    @BindView(R.id.tv_userinfo_nick)
    TextView tvUserinfoNick;
    @BindView(R.id.layout_userinfo_nick)
    LinearLayout layoutUserinfoNick;
    @BindView(R.id.tv_userinfo_name)
    TextView tvUserinfoName;
    @BindView(R.id.tv_userinfo_qrcode)
    TextView tvUserinfoQrcode;
    @BindView(R.id.tv_userinfo_address)
    TextView tvUserinfoAddress;
    @BindView(R.id.tv_userinfo_sex)
    TextView tvUserinfoSex;
    @BindView(R.id.layout_userinfo_sex)
    LinearLayout layoutUserinfoSex;
    @BindView(R.id.tv_userinfo_area)
    TextView tvUserinfoArea;
    @BindView(R.id.layout_userinfo_area)
    LinearLayout layoutUserinfoArea;
    @BindView(R.id.tv_userinfo_sign)
    TextView tvUserinfoSign;
    @BindView(R.id.layout_userinfo_sign)
    LinearLayout layoutUserinfoSign;

    UserProfileActivity userProfileActivity;
    @BindView(R.id.iv_userinfo_avatar)
    ImageView ivUserinfoAvatar;
    @BindView(R.id.layout_userinfo_name)
    LinearLayout layoutUserinfoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);
        DisplayUtils.initBackWithTitle(this, "个人信息");
        initData();
        userProfileActivity = new UserProfileActivity();
    }

    private void initData() {
        String username = PreferenceManager.getInstance().getCurrentUsername();
        L.e(TAG, ">>>>>>>>>>>>>>>>>用户名" + username);
//        这里的nick第一是从内存中取数据，第二才是从数据库中取数据
        tvUserinfoName.setText(username);
        EaseUserUtils.setAppUserNick(username, tvUserinfoNick);
        EaseUserUtils.setAppUserAvatar(this, username, ivUserinfoAvatar);
    }

    @OnClick({R.id.layout_userinfo_avatar, R.id.layout_userinfo_name, R.id.layout_userinfo_nick, R.id.layout_userinfo_sex, R.id.layout_userinfo_area, R.id.layout_userinfo_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_userinfo_avatar:
                //         头像
//                跳转到个人资料界面
                startActivity(new Intent(this, UserProfileActivity.class).putExtra("setting", true)
                        .putExtra("username", EMClient.getInstance().getCurrentUser()));
//                由于私有了，所以只能采用反射来解决
//                userProfileActivity.uploadHeadPhoto();
                break;
            case R.id.layout_userinfo_nick:
                //         昵称

                break;
            case R.id.layout_userinfo_sex:
//                性别
                break;
            case R.id.layout_userinfo_area:
//                地区
                break;
            case R.id.layout_userinfo_sign:
//                个性签名
                break;
        }
    }
}
