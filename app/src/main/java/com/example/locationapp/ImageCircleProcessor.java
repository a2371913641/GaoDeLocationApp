package com.example.locationapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

public class ImageCircleProcessor {

    /**
     * 将任意图片缩放并裁剪为50dp的圆形图片
     *
     * @param context 上下文对象，用于获取资源和进行单位转换
     * @param source  原始图片，可以是Bitmap或Drawable类型
     * @return 处理后的圆形图片的Bitmap对象
     */
    public static Bitmap processToCircle(Context context, Object source) {
        // 1. 获取原始图片的Bitmap对象
        Bitmap originalBitmap = getBitmapFromSource(context, source);
        if (originalBitmap == null) {
            return null;
        }

        // 2. 将50dp转换为像素值
        float targetDp = 50;
        int targetPixel = dpToPx(context, targetDp);

        // 3. 缩放图片到目标尺寸
        Bitmap scaledBitmap = scaleBitmap(originalBitmap, targetPixel, targetPixel);

        // 4. 将图片裁剪为圆形
        Bitmap circleBitmap = createCircleBitmap(scaledBitmap);

        // 5. 回收不再使用的Bitmap资源
        if (originalBitmap != circleBitmap) {
            originalBitmap.recycle();
        }
        if (scaledBitmap != circleBitmap) {
            scaledBitmap.recycle();
        }

        return circleBitmap;
    }

    /**
     * 从不同类型的源对象获取Bitmap
     */
    private static Bitmap getBitmapFromSource(Context context, Object source) {
        if (source instanceof Bitmap) {
            return (Bitmap) source;
        } else if (source instanceof Drawable) {
            return drawableToBitmap((Drawable) source);
        } else if (source instanceof Integer) {
            // 假设传入的是资源ID
            return drawableToBitmap(context.getResources().getDrawable((Integer) source));
        }
        return null;
    }

    /**
     * 将Drawable转换为Bitmap
     */
    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将dp值转换为像素值
     */
    private static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    /**
     * 缩放Bitmap到指定尺寸
     */
    private static Bitmap scaleBitmap(Bitmap source, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(
                source,
                targetWidth,
                targetHeight,
                true
        );
    }

    /**
     * 创建圆形Bitmap
     */
    private static Bitmap createCircleBitmap(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        Bitmap squaredBitmap = Bitmap.createBitmap(
                source,
                (source.getWidth() - size) / 2,
                (source.getHeight() - size) / 2,
                size,
                size
        );
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(
                squaredBitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
        );
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }
}