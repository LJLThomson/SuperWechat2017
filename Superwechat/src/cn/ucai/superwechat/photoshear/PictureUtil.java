package cn.ucai.superwechat.photoshear;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2017/1/4 0004.
 */

public class PictureUtil {
    // 通过URI获取图片bitmap
    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        ContentResolver cr = context.getContentResolver();
        try {
//将 Uri转化为 Stream流
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    // 通过URI获取图片路径
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
