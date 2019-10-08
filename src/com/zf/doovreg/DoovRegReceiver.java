package com.zf.doovreg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DoovRegReceiver extends BroadcastReceiver {
	private static final String TAG = "DoovReg/DoovRegReceiver";
	
	 @Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        String intentAction = null;
        intentAction = intent.getAction();
        Log.d(TAG, "The intent is " + intentAction);
        PackageManager manager = context.getPackageManager();
          
        if (intentAction.equals(DoovRegConst.ACTION_PHONE_STATE)) {
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			Log.d(TAG, "The phone status is " + state);
			if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
				writeRegStatus(context, DoovRegConst.REG_ALLOW, 1);
				Intent callIdleIntent = new Intent();
				callIdleIntent.setAction(DoovRegConst.ACTION_PHONE_STATE);
				callIdleIntent.setClass(context, DoovRegService.class);
				context.startService(callIdleIntent);
			}
        }else if (intentAction.equals(DoovRegConst.ACTION_SMS_RECEIVED)) {
        	writeRegStatus(context, DoovRegConst.REG_ALLOW, 1);
            Intent smsReceivedIntent = new Intent();
            smsReceivedIntent.setAction(DoovRegConst.ACTION_SMS_RECEIVED);
            smsReceivedIntent.setClass(context, DoovRegService.class);
            context.startService(smsReceivedIntent);
        }else if (intentAction.equals(DoovRegConst.ACTION_CONNECTIVITY)) {
            Intent connectivityIntent = new Intent();
            connectivityIntent.setAction(DoovRegConst.ACTION_CONNECTIVITY);
            connectivityIntent.setClass(context, DoovRegService.class);
            context.startService(connectivityIntent);
        }
        // add by huangjiawei 静默安装 begin
        else if (intentAction.equals(DoovRegConst.ACTION_REPLACE)) {
			String packageName = intent.getData().getSchemeSpecificPart();
        	Log.i("DoovRegInstall", "i can get the packageInstall replace== " + packageName);
        	if (DoovRegConst.PACKAGE_NAME.equals(packageName)) {
        		Log.i("DoovRegInstall","install is success--");
		    	FileUtil fileUtil = new FileUtil();
		    	//安装成功过后删除apk
		    	if(fileUtil.isFileExist()){
		    		fileUtil.clearFile();
		    	}
			}
		}
        
        // add by huangjiawei 静默安装 end
	}
	public void writeRegStatus(Context context,String key, int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(DoovRegConst.REG_CONFIG_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
}
