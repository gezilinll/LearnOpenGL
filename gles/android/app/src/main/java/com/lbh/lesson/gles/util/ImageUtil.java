package com.lbh.lesson.gles.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**********************************************************************************************
 * 图片相关操作工具类
 *
 **********************************************************************************************
 * @创建人: zhangliang
 * @创建日期： 2018/1/30 下午2:40
 * @版本： 1.0
 **********************************************************************************************
 * @修改人：
 * @修改时间：
 * @修改描述：
 * @版本：
 **********************************************************************************************
 */

public class ImageUtil {

    public static Bitmap getBitmapFromAssets(Context context, String path) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open(path);

            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
