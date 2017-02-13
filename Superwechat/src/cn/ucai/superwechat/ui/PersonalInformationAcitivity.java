package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWechatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.db.net.IModelUser;
import cn.ucai.superwechat.db.net.ModelUser;
import cn.ucai.superwechat.db.net.OnCompleteListener;
import cn.ucai.superwechat.utils.CommonUtils;
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
    updatenickReceiver mReceiver;
    PopupWindow mpopupWindow;
    private ProgressDialog dialog;
    IModelUser model;
    static final int ACTION_CAPTURE = 0;//拍照的动作
    static final int ACTION_CHOOSE = 1;//相册选择的动作
    static final int ACTION_CROP = 2;//参见的动作
    File mFile;
    TextView photo;
    TextView choosep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);
        DisplayUtils.initBackWithTitle(this, "个人信息");
        initData();
        initPopupWindow();
        userProfileActivity = new UserProfileActivity();
        ivUserinfoAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow();
            }
        });
    }

    private void initPopupWindow() {
        View layout = View.inflate(this, R.layout.popup_window, null);
        photo = (TextView) layout.findViewById(R.id.photo);
        choosep = (TextView) layout.findViewById(R.id.choosePicture);
        //        创建PopupWindow对象，设置布局
        mpopupWindow = new PopupWindow();
        mpopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mpopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mpopupWindow.setContentView(layout);
//        上面三条等价于mpopupWindow = new PopupWindow(layout，ViewGroup.LayoutParams.MATCH_PARENT，ViewGroup.LayoutParams.MATCH_PARENT);
        mpopupWindow.setFocusable(true);
        mpopupWindow.setOutsideTouchable(true);//点击外部关闭
        mpopupWindow.setAnimationStyle(R.style.style_popup_window);//有进入属性，和出去属性
        mpopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Toast.makeText(PersonalInformationAcitivity.this, "关闭弹出框", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupWindow() {
        View rootView = LayoutInflater.from(this).inflate(R.layout.profile_activity, null);
        mpopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
                mpopupWindow.dismiss();
            }
        });
        choosep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosefromPhoto();
                mpopupWindow.dismiss();
            }
        });
    }

    private void initData() {
        String username = PreferenceManager.getInstance().getCurrentUsername();
        model = new ModelUser();
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

    @Override
    protected void onStart() {
        super.onStart();
        mReceiver = new updatenickReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(I.REQUEST_UPDATE_USER_NICK);
        registerReceiver(mReceiver, filter);//注册
    }


    private void choosefromPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);//"android.intent.action.PICK";选取图片
        //MediaStore这个类是Android系统提供的一个多媒体数据库，android中多媒体信息都可以从这里提取。
        // 这个MediaStore包括了多媒体数据库的所有信息，包括音频，视频和图像，可以出现所有sd卡中的音频、视屏、图像，
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, ACTION_CHOOSE);
    }

    private void capture() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//         System.currentTimeMillis——获取当前时间
        mFile = new File(dir, System.currentTimeMillis() + ".jpg");
//        启动系统拍照的Activity，即照相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);Uri.fromFile(mFile);存储的位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));//输出图片存储的位置，将路劲加进去
        //可以自定义裁切输出的图片存储位置。利用这一点，就可以规避Intent携带信息的不靠谱所造成的吃饭不香。
        startActivityForResult(intent, ACTION_CAPTURE);//启动相机，类似于activity的跳转，这里是跳转到相机Activity，处理相机返回的结果
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {//默认是Result_Ok,
            return;
        }
        switch (requestCode) {
            case ACTION_CAPTURE://拍照
                Log.i("main", "拍照返回结果");
//                进行剪切
                startCrop(Uri.fromFile(mFile), 200, 200);//Uri.fromFile(mFile)将文件转化为Uri类型
                break;
            case ACTION_CHOOSE:
                Log.i("main", "相册选取返回的结果");
//                startCrop(data.getData(), 200, 200);
//                showAvatar(data);
//                Uri uri = data.getData();
//                Bitmap bitmap = PictureUtil.decodeUriAsBitmap(this, uri);
//                ivUserinfoAvatar.setImageBitmap(bitmap);
                update_UserAvatar(data);
                break;
            case ACTION_CROP:
                Log.i("main", "裁剪返回的结果");
                showAvatar(data);//显示出来，
                break;
        }
    }

    //启动裁剪功能的Activity
    private void startCrop(Uri uri, int outputX, int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");//隐式模式开启，com.android.camera.action.CROP图片裁剪
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("outputX", outputX);//图片宽————固定用法，
        intent.putExtra("outputY", outputY);//高

        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());//输出格式Bitmap.CompressFormat.
        startActivityForResult(intent, ACTION_CROP);//剪切，返回裁剪结果
    }

    private void showAvatar(Intent data) {
//         //从裁剪的Activity返回的intent中取出裁剪后的图片
        Bitmap bitmap = data.getParcelableExtra("data");//data为默认name//Bitmap implements Parcelable
        /*
        try {
            FileOutputStream fos = new FileOutputStream(mFile);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);//bitmap输出格式，
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        if (bitmap == null) {
            return;
        }
        ivUserinfoAvatar.setImageBitmap(bitmap);
    }

    class updatenickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String nickname = intent.getStringExtra("nick");
            tvUserinfoNick.setText(nickname);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);//注销接收器
        }
    }

    private void update_UserAvatar(Intent picdata) {
        String username = PreferenceManager.getInstance().getCurrentUsername();
        File file = savaBitmapFile(picdata);
        L.e(TAG, "update_UserAvatar:>>>>>" + file);
        if (file == null) {
            return;
        }
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        model.updateAvator(this, username, file, new OnCompleteListener<Result>() {
            @Override
            public void onSuccess(Result result) {
                L.e(TAG, "updateAvator:>>>>>" + result);
                if (result != null) {
                    User user = new Gson().fromJson(result.getRetData().toString(), User.class);
                    if (user != null) {
//						保存到集合和数据库中
                        SuperWechatHelper.getInstance().saveAppContact(user);
                        EaseUserUtils.setAppUserAvatar(PersonalInformationAcitivity.this, user.getMUserName(), ivUserinfoAvatar);
                        CommonUtils.showShortToast(R.string.toast_updatenick_success);
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                CommonUtils.showShortToast(R.string.toast_updatenick_fail);
            }
        });
    }
    private File savaBitmapFile(Intent picdata) {
        String username = PreferenceManager.getInstance().getCurrentUsername();
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            String imagePath = EaseImageUtils.getImagePath(username + I.AVATAR_SUFFIX_PNG);
            File file = new File(imagePath);
            L.e("file path=" + file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }
}
