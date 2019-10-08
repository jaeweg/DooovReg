package com.zf.doovreg;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MobileInfo {

	/**
	 * 参数
	 * QueryMobile 
	 * MobileType
	 * QueryIMEI
	 * BuildVersion
	 * PkgName
	 * VersionCode
	 * ClientVersion
	 * @return
	 */
	
	public static final String QUERYMOBILE = "QueryMobile";
	public static final String MOBILETYPE = "MobileType";
	public static final String QUERYIMEI = "QueryIMEI";
	public static final String BUILDVERSION = "BuildVersion";
	public static final String PKGNAME = "PkgName";
	public static final String VERSIONCODE = "VersionCode";
	public static final String CLIENTVERSION = "ClientVersion";
	private Context mContext;
	private static MobileInfo mobileInfo;
	
	private MobileInfo(Context context) {
		mContext = context;
	}

	public static MobileInfo getInstance(Context context) {
		if (mobileInfo == null) {
			mobileInfo = new MobileInfo(context);
		}
		return mobileInfo;
	}
	
	
	public String handParams(String key , String values){
		String temp = key + "=" + values + "&";
		return temp;
	}
	
	
	/**
	 * 请求的基本参数
	 * */
	public StringBuilder getHttpGetParams(String path,String imei) {
		StringBuilder builder = new StringBuilder();
		builder.append(path).append("?").append(handParams(QUERYMOBILE, getPhoneNumber()))
		.append(handParams(MOBILETYPE, getMobileType())).append(handParams(QUERYIMEI, imei))
		.append(handParams(BUILDVERSION, getBuildVersion())).append(handParams(PKGNAME, getPagName(mContext)))
		.append(handParams(VERSIONCODE, getAppVersionCode(mContext))).append(handParams(CLIENTVERSION, getmAppVersionName(mContext)));
		return builder;
	}
	
	
	
	/**
	 * 获取电话号码
	 * 
	 * @return
	 */
	public String getPhoneNumber() {
		String nb = null;
		if (mContext != null) {
			try {
				TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
				nb = tm.getLine1Number();
				if (nb == null) {
					return nb = "00000000001";
				} else if (nb.equals("") || nb.length() < 11) {
					return nb = "00000000001";
				} else if (nb.length() > 11) {
					nb = nb.substring(nb.length() - 11);
					return nb;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return nb;
		} else {
			return "00000000001";
		}
	}
	
	/**
	 * 获取手机型号
	 */
	public static String getMobileType() {
		return android.os.Build.MODEL.replace(" ", "_");
	}
	
	/** 
     * 返回当前程序versionCode 
     */  
    public static String getAppVersionCode(Context context) {  
        String versioncode = 0 + "";
        try {  
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
            versioncode = pi.versionCode + "";
        } catch (Exception e) {  
            Log.e("VersionInfo", "Exception", e);  
        }  
        return versioncode;  
    }
    
    /** 
     * 返回当前程序versionName 
     */  
    public static String getmAppVersionName(Context context) {  
        String versionName = ""; 
        try {  
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
            versionName = pi.versionName;  
        } catch (Exception e) {  
            Log.e("VersionInfo", "Exception", e);  
        }  
        return versionName;  
    }
    
    /**
	 * 获取系统版本号
	 */
	public String getBuildVersion() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	/**
	 * 获取包名
	 */
	public String getPagName(Context context) {
		return context.getPackageName();
	}
	

	
}
