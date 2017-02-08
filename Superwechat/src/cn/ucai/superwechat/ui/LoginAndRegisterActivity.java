package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.video.util.MFGT;

/**
 * Created by Administrator on 2017/2/8 0008.
 */

public class LoginAndRegisterActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.login, R.id.register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                MFGT.gotoLoginActivity(this);
                break;
            case R.id.register:
                MFGT.gotoRegisterActivity(this);
                break;
        }
    }
}
