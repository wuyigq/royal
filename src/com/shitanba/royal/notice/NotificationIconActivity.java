package com.shitanba.royal.notice;


import com.shitanba.royal.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class NotificationIconActivity extends Activity {
	private final static String TAG = "NotifacationIconActivity";
//	private final static String app_name = "NotifacationIconActivity";
	private final static int NOTIFICATION_CONTACT_ID=1;
//	private ImageView mImageView;
	private NotificationManager nm;
	//处理过的加上联系人数量的图标
//	private Bitmap contactCountIcon;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mImageView=(ImageView)findViewById(R.id.icon);
        //优先采用联系人的图标，如果不存在则采用该应用的图标
        Drawable contactIcon;
		try {
			contactIcon = getPackageManager().getApplicationIcon("com.android.contacts");
		} catch (NameNotFoundException e) {
			contactIcon=null;
		}
//		Bitmap icon;
//        if(contactIcon instanceof BitmapDrawable){
//        	icon=((BitmapDrawable)contactIcon).getBitmap();
//        }else{
//        	icon=getResIcon(getResources(), R.id.icon);
//        }
//        contactCountIcon = generatorContactCountIcon(icon);
//        mImageView.setImageBitmap(contactCountIcon);
        nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        showNotifacation(generatorContactCountIcon(((BitmapDrawable)contactIcon).getBitmap()), "Royal提示", "aaaaaa");
    }
    

	@Override
	protected void onDestroy() {
		super.onDestroy();
		nm.cancel(NOTIFICATION_CONTACT_ID);
	}

	/**
     * 根据id获取一个图片
     * @param res
     * @param resId
     * @return
     */
    private Bitmap getResIcon(Resources res,int resId){
    	Drawable icon=res.getDrawable(resId);
    	if(icon instanceof BitmapDrawable){
    		BitmapDrawable bd=(BitmapDrawable)icon;
    		return bd.getBitmap();
    	}else{
    		return null;
    	}
    }
    
    /**
     * 在给定的图片的右上角加上联系人数量。数量用红色表示
     * @param icon 给定的图片
     * @return 带联系人数量的图片
     */
    private Bitmap generatorContactCountIcon(Bitmap icon){
    	//初始化画布
    	int iconSize = (int)getResources().getDimension(android.R.dimen.app_icon_size);
    	Log.d(TAG, "the icon size is "+iconSize);
    	Bitmap contactIcon=Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);
    	Canvas canvas=new Canvas(contactIcon);
    	
    	//拷贝图片
    	Paint iconPaint=new Paint();
    	iconPaint.setDither(true);//防抖动
    	iconPaint.setFilterBitmap(true);//用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
    	Rect src=new Rect(0, 0, icon.getWidth(), icon.getHeight());
    	Rect dst=new Rect(0, 0, iconSize, iconSize);
    	canvas.drawBitmap(icon, src, dst, iconPaint);
    	
    	//在图片上创建一个覆盖的联系人个数
    	int contacyCount=getContactCount();
    	//启用抗锯齿和使用设备的文本字距
    	Paint countPaint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DEV_KERN_TEXT_FLAG);
    	countPaint.setColor(Color.RED);
    	countPaint.setTextSize(20f);
    	countPaint.setTypeface(Typeface.DEFAULT_BOLD);
    	canvas.drawText(String.valueOf(contacyCount), iconSize-18, 25, countPaint);
    	return contactIcon;
    }
    /**
     * 获取联系人的个数
     * @return 手里通讯录中联系人的个数
     */
    private int getContactCount(){
    	Cursor c=getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._COUNT}, null, null, null);
    	try{
    		c.moveToFirst();
    		return c.getInt(0);
    	}catch(Exception e){
    		return 0;
    	}finally{
    		c.close();
    	}
    }
    
    /**
     * 显示状态栏通知
     * @param icon 通知内容图标
     */
    private void showNotifacation(Bitmap icon, String title, String dec){
    	Notification notification=new  Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
    	//使用RemoteView自定义通知视图
    	RemoteViews contentView=new RemoteViews(getPackageName(), R.layout.notification);
    	contentView.setImageViewBitmap(R.id.image, icon);
    	contentView.setTextViewText(R.id.text, dec);
    	notification.contentView=contentView;
    	Intent notificationIntent=new Intent(this, NotificationIconActivity.class);
    	PendingIntent contentIntent=PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	notification.contentIntent=contentIntent;
    	nm.notify(NOTIFICATION_CONTACT_ID, notification);
    }
}