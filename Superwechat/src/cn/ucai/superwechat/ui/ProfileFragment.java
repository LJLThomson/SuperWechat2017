package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.PreferenceManager;
import cn.ucai.superwechat.video.util.MFGT;

/**
 * Created by Administrator on 2017/2/10 0010.
 */

public class ProfileFragment extends Fragment {
    private static String TAG = ProfileFragment.class.getSimpleName();
    @BindView(R.id.layout_profile_view)
    RelativeLayout layoutProfileView;
    @BindView(R.id.tv_profile_album)
    TextView tvProfileAlbum;
    @BindView(R.id.tv_profile_collect)
    TextView tvProfileCollect;
    @BindView(R.id.tv_profile_money)
    TextView tvProfileMoney;
    @BindView(R.id.tv_profile_smail)
    TextView tvProfileSmail;
    @BindView(R.id.tv_profile_settings)
    TextView tvProfileSettings;
    @BindView(R.id.tv_profile_nickname)
    TextView tvProfileNickname;
    @BindView(R.id.tv_profile_username)
    TextView tvProfileUsername;
    @BindView(R.id.iv_profile_avatar)
    ImageView ivProfileAvatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
//        得到用户名,一开始登录之后，用户名和昵称都被系统自行保存了，不过用户名是以小写的形式保存的
//        所以
//        String username1 = EMClient.getInstance().getCurrentUser();//取出来的都为小写，当保存在数据库时，会出现错误
//        从sharepreference中得到用户名
        String username = PreferenceManager.getInstance().getCurrentUsername();
        L.e(TAG,">>>>>>>>>>>>>>>>>用户名"+ username);
//        这里的nick第一是从内存中取数据，第二才是从数据库中取数据
        EaseUserUtils.setAppUserNick(username, tvProfileNickname);
        EaseUserUtils.setAppUserAvatar(getContext(), username,ivProfileAvatar );
    }

    @OnClick({R.id.layout_profile_view, R.id.tv_profile_album, R.id.tv_profile_collect, R.id.tv_profile_money, R.id.tv_profile_smail, R.id.tv_profile_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_profile_view:
//                进入个人信息界面
                MFGT.gotoPerInformationActivity(getActivity());
                break;
            case R.id.tv_profile_album:
//                相册
                break;
            case R.id.tv_profile_collect:
//                收藏
                break;
            case R.id.tv_profile_money:
//                钱包
                RedPacketUtil.startChangeActivity(getActivity());
                break;
            case R.id.tv_profile_smail:
//                表情
                break;
            case R.id.tv_profile_settings:
//                个人设置
                MFGT.gotoSettingActivity(getActivity());
                break;
        }
    }
}
