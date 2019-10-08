package com.zf.doovreg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;

import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
////V3M2 must delete
import android.os.ServiceManager;
import com.mediatek.internal.telephony.ITelephonyEx;

public class DoovRegGenInfo {
	private static final String TAG = "DoovReg/DoovRegGenInfo";
	private DoovRegService service;
	public Double longitude;
	public Double latitude;
    
	public DoovRegGenInfo(DoovRegService service) {
		// TODO Auto-generated constructor stub
		this.service = service;
	}

	public String getImei() {
		TelephonyManager telephonyManager = (TelephonyManager)service.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		Log.d(TAG, "return IMEI " + imei);
        return imei;
	}
	
	//add by CBQ 20160428
	public String getFirstIMEI() {
		String imei = "000000000000000";
		if (service != null) {
			String mphoneType = service.doovRegGen.getPhoneType();
			if (mphoneType.contains("L520") || mphoneType.contains("V15")||mphoneType.contains("V10")){
				imei = getImei();
				if (imei == null){
					imei = "000000000000000";
				}
				Log.i(TAG, "getImei() = " + imei);
				return imei;
			}
//            ITelephonyEx iTelephonyEx = ITelephonyEx.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE_EX));
			ITelephonyEx iTelephonyEx = ITelephonyEx.Stub.asInterface(ServiceManager.getService("phoneEx"));
            Log.i(TAG, "iTelephonyEx= " + iTelephonyEx);
            if (iTelephonyEx != null) {
                try {
                	imei = iTelephonyEx.getSvlteImei(0);
    				if (imei == null){
    					imei = "000000000000000";
    				}
    				Log.i(TAG, "getSvlteImei() = " + imei);
                } catch (RemoteException e) {
                    Log.e(TAG, "getSvlteImei(), RemoteException!");
                    e.printStackTrace();
                }
            }
            return imei;
		}
		return imei;
	}
	
//	public String intentGetImeiValue(){
//		String imei = "000000000000000";
//		String mphoneType = service.doovRegGen.getPhoneType();
//		if (mphoneType.contains("L520") || mphoneType.contains("V15")||mphoneType.contains("V10")){				
//			imei = getImei();
//			Log.i(TAG, "getImei() = " + imei);
//			return imei;
//		}		
//		String[] mImeiArray = null;
////        String meid = null;
//	    Intent intent = service.registerReceiver(null, new IntentFilter("intent_action_imei_meid"));
//	    Log.i(TAG, "[initImeiAndMeid] intent is " + intent);
//	    if (intent != null) {
//	        mImeiArray = intent.getStringArrayExtra("extra_key_imei");
//	        if (mImeiArray != null) {
//	            Log.i(TAG, "[initImeiAndMeid] IMEI length is " + mImeiArray.length);
////	            for (int i = 0; i < mImeiArray.length; ++i) {
////	                Log.i(TAG, "[initImeiAndMeid] IMEI[" + i + "] is " + mImeiArray[i]);
////	            }
////	            mIMEI1.setText(getString(R.string.IMEI1) + mImeiArray[0]);
////	            mIMEI2.setText(getString(R.string.IMEI2) + mImeiArray[1]);
//	            return mImeiArray[0];
//	        } else {
//	            Log.i(TAG, "[initImeiAndMeid] IMEI array is " + null);
//	        }
//	    }
//	    return imei;
//	}
	
	public String getVersion() {
//        Log.d(TAG, "return version " + Build.DISPLAY);
        return Build.DISPLAY;
	}
	
	public String getRelease() {
        Log.d(TAG, "return release " + Build.VERSION.RELEASE);
        return Build.VERSION.RELEASE;
	}
	
	public String getDisplayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = service.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
	    String displayMetrics = screenWidth + "*" + screenHeight;
	    Log.d(TAG, "return displaymetrics =" + displayMetrics);
	    return displayMetrics;
	}
	
	public String getOperatorName() {
		TelephonyManager telephonyManager = (TelephonyManager)service.getSystemService(Context.TELEPHONY_SERVICE);
		String operatorName = telephonyManager.getSimOperator();
		if (TextUtils.isEmpty(operatorName)) {
			operatorName="00000";
		}
		Log.d(TAG, "return operatorname " + operatorName);
		return operatorName;
	}

	public String getLocation() {
		NumberFormat nFormat = NumberFormat.getNumberInstance(); 
	    nFormat.setMaximumFractionDigits(DoovRegConst.LOCATION_POINT_MAX);
		Log.d(TAG, "return location " + nFormat.format(longitude) + "," + nFormat.format(latitude));
		return nFormat.format(longitude) + "," + nFormat.format(latitude);
	}

	public String getDataTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");     
		String datetime = sDateFormat.format(new java.util.Date()); 
		Log.d(TAG, "return datatime " + datetime);
		return datetime;
	}

	public String getNetwork() {
		String network = null;
		ConnectivityManager connectivityManager = (ConnectivityManager)service.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			network = info.getExtraInfo();
		}
		Log.d(TAG, "return network " + network);
		return network;
	}
	
	public String getPhoneType() {
//        Log.d(TAG, "return phonetype " + DoovRegConst.DEFAULT_TYPE);
        return DoovRegConst.DEFAULT_TYPE;
	}
	public String getV15Type(Context context){
		int piexls = getCameraPixels(HasBackCamera());
		Log.d(TAG, "Camera piexls = " + piexls);
		if (!getV15TotalROM()){	//8+1
			Log.d(TAG, "return -a type");
			return DoovRegConst.DEFAULT_TYPE + "-a";   
		}else if (getRAM(context) <= 1){
			Log.d(TAG, "return normal type");
			return DoovRegConst.DEFAULT_TYPE;
		}else if (piexls < 600){
			Log.d(TAG, "return -b type");
			return DoovRegConst.DEFAULT_TYPE+ "-b";
		}else{
			Log.d(TAG, "return -c type");
			return DoovRegConst.DEFAULT_TYPE+ "-c";
		}
	}
	public String getPhoneTypeAndRAM(Context context) {
		String extratype = SystemProperties.get(DoovRegConst.EXTRA_TYPE, "");
		Log.d(TAG, "extratype = " + extratype);
		if (!"".contentEquals(extratype)){
			Log.d(TAG, "return ASD type");
			return DoovRegConst.DEFAULT_TYPE + "-" + extratype;
		}
        Log.d(TAG, "return phonetype " + DoovRegConst.DEFAULT_TYPE);
        Log.d(TAG, "return getRAM = " + getRAM(context));

		if (service.doovRegGen.getPhoneType().contains("V15")||service.doovRegGen.getPhoneType().contains("V11")){
			return getV15Type(context);
		}

        if (service.doovRegGen.getPhoneType().contains("L9 mini")){
        	Log.d(TAG, "return L9 mini");
        	return DoovRegConst.DEFAULT_TYPE;  
        }else if ((getRAM(context) < 4)&&(service.doovRegGen.getPhoneType().contains("L9"))){
        	Log.d(TAG, "return L type");
        	return DoovRegConst.DEFAULT_TYPE + "-L";        
        }else if ((getRAM(context) > 3)&&(service.doovRegGen.getPhoneType().contains("L8"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";
        }else if ((getTotalROM())&&(service.doovRegGen.getPhoneType().contains("A5"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";
        }else if ((getTotalROM())&&(service.doovRegGen.getPhoneType().contains("M2"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";        	
        }else if ((getRAM(context) > 1)&&(service.doovRegGen.getPhoneType().contains("V3"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";
        }else if ((getRAM(context) > 1)&&(service.doovRegGen.getPhoneType().contains("V5"))){
        	Log.d(TAG, "return H type");
        	return DoovRegConst.DEFAULT_TYPE + "-H";            	
        }else{
        	Log.d(TAG, "return normal type");
        	return DoovRegConst.DEFAULT_TYPE;
        }
	}
	//CBQ add 
	public static int getRAM(Context context)
	{
		return (int)Math.ceil(getTotalMemory(context));
	}

	private static float getTotalMemory(Context context){
	        String str1 = "/proc/meminfo";
	        String str2;
	        String[] arrayOfString;
	        float initial_memory = 0;  
	        try 
	        {
	            FileReader localFileReader = new FileReader(str1);
	            BufferedReader localBufferedReader = new BufferedReader(
	            localFileReader, 8192);
	            str2 = localBufferedReader.readLine();
	
	            arrayOfString = str2.split("\\s+");
	            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() ;
	            localBufferedReader.close();

	        } catch (IOException e) {
	        }
	        return initial_memory/(1024*1024);
	    }
	
//	private boolean getRomTotalSize() {
//		StatFs mRomFileStats = new StatFs("/storage/sdcard0");  
//		mRomFileStats.restat("/storage/sdcard0");
//	    long RomTotalSize = mRomFileStats.getTotalBytes();
//	    Log.d(TAG,"RomTotalSize = " + RomTotalSize);
//	    float total = RomTotalSize/1024f/1024f/1024f;
//	    Log.d(TAG,"total = " + total);
//		if(total<=16f){
//			return false;
//		}else{
//			return true;
//		}
//	}
	
	private boolean getTotalROM() {//total_memory
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long getTotalBytes = stat.getTotalBytes();
        Log.d(TAG,"getTotalBytes =" + getTotalBytes);
//        long blockSize = stat.getBlockSize();
//        long availableBlocks = stat.getAvailableBlocks();
//        long totalRam = availableBlocks * blockSize;
//		Log.d(TAG,"setTotalMemory totalRam=" + totalRam);
		float total = getTotalBytes/1024f/1024f/1024f;
		Log.d(TAG,"setRunning totalRam=" +total );
		if(total<=16f){
			return false;
		}else{
			return true;
		}
	}
	private boolean getV15TotalROM() {//total_memory
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long getTotalBytes = stat.getTotalBytes();
        Log.d(TAG,"getTotalBytes =" + getTotalBytes);
		float total = getTotalBytes/1024f/1024f/1024f;
		Log.d(TAG,"setRunning totalRam=" +total );
		if(total<=8f){
			return false;
		}else{
			return true;
		}
	}
	public static final int CAMERA_FACING_BACK = 0;  
    public static final int CAMERA_FACING_FRONT = 1;  
    public static final int CAMERA_NONE = 2;  
      
     @SuppressWarnings("deprecation")
	public static int HasBackCamera()  
      {  
          int numberOfCameras = Camera.getNumberOfCameras();    
            CameraInfo cameraInfo = new CameraInfo();    
            for (int i = 0; i < numberOfCameras; i++) {    
                Camera.getCameraInfo(i, cameraInfo);    
                if (cameraInfo.facing == CAMERA_FACING_BACK) {    
                    return i;    
                }    
            }    
            return 2;  
      }  
     @SuppressWarnings("deprecation")
	public static int getCameraPixels(int paramInt)  
     {  
       if (paramInt == 2)  
         return 0;  
       Camera localCamera = Camera.open(paramInt);  
       Camera.Parameters localParameters = localCamera.getParameters();  
       localParameters.set("camera-id", 1);  
       List<Size> localList = localParameters.getSupportedPictureSizes();  
       if (localList != null)  
       {  
           int heights[] = new int[localList.size()];   
           int widths[] = new int[localList.size()];   
           for (int i = 0; i < localList.size(); i++)   
           {   
	           Size size = (Size) localList.get(i);   
	           int sizehieght = size.height;   
	           int sizewidth = size.width;   
	           heights[i] = sizehieght;   
	           widths[i] =sizewidth;      
           }  
           int pixels = getMaxNumber(heights) * getMaxNumber(widths);  
           localCamera.release();  
           return pixels/10000;  
        
       }  
       else return 0;  
        
     }  
 
     public static int getMaxNumber(int[] paramArray)  
     {  
         int temp = paramArray[0];  
         for(int i = 0;i<paramArray.length;i++)  
         {  
             if(temp < paramArray[i])  
             {  
                 temp = paramArray[i];  
             }  
         }  
         return temp;  
     }  
}
