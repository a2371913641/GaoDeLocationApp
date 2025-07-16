package com.example.locationapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;

public class CustomMarker {

    public static Bitmap createCustomMarker(Context context, String text, int iconResId){
        View markerView= LayoutInflater.from(context).inflate(R.layout.custom_marker_layout,null);
        ImageView imageView=(ImageView) markerView.findViewById(R.id.account_image);
        TextView textView=(TextView) markerView.findViewById(R.id.account_text);

        //设置图片和文字
        imageView.setImageBitmap(ImageCircleProcessor.processToCircle(context,iconResId));
        textView.setText(text);

        // 测量并布局View
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());

        //将View转化为Bitmap
        Bitmap bitmap=Bitmap.createBitmap(markerView.getMeasuredWidth(),markerView.getMeasuredHeight(),Bitmap.Config.ARGB_8888);

        Canvas canvas=new Canvas(bitmap);

        markerView.draw(canvas);

        return bitmap;
    }

}
