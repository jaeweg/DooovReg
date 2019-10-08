package com.zf.doovreg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import android.util.Log;

public class DoovRegHttp {
	private static final String TAG = "DoovReg/DoovRegHttp";
	private DoovRegService service;
	private List<NameValuePair> paramList;

	public DoovRegHttp(DoovRegService service) {
		// TODO Auto-generated constructor stub
		this.service = service;
		paramList = new ArrayList<NameValuePair>();
	}

	/*
	 * public String postReg() { String ret = null;
	 * 
	 * HttpClient httpClient = new DefaultHttpClient(); HttpPost post = new
	 * HttpPost(DoovRegConst.REG_URL);
	 * 
	 * try { post.setEntity(new UrlEncodedFormEntity(this.paramList));
	 * Log.d(TAG, "post URL=" + post.getRequestLine());
	 * 
	 * HttpResponse httpResponse = httpClient.execute(post);
	 * 
	 * if (httpResponse.getStatusLine().getStatusCode() == 200) { ret =
	 * EntityUtils.toString(httpResponse.getEntity()); } } catch (Exception e) {
	 * e.printStackTrace(); } finally{ post.abort(); httpClient = null; }
	 * 
	 * Log.d(TAG, "postReg ret=" + ret); return ret; }
	 * 
	 * public void updateParam() { paramList.add(new
	 * BasicNameValuePair("location", service.doovRegGen.getLocation()));
	 * paramList.add(new BasicNameValuePair("imei",
	 * service.doovRegGen.getImei())); paramList.add(new
	 * BasicNameValuePair("metrics", service.doovRegGen.getDisplayMetrics()));
	 * paramList.add(new BasicNameValuePair("phone",
	 * service.doovRegGen.getPhoneType())); paramList.add(new
	 * BasicNameValuePair("version", service.doovRegGen.getVersion()));
	 * paramList.add(new BasicNameValuePair("release",
	 * service.doovRegGen.getRelease())); paramList.add(new
	 * BasicNameValuePair("data", service.doovRegGen.getDataTime()));
	 * paramList.add(new BasicNameValuePair("apn",
	 * service.doovRegGen.getNetwork())); paramList.add(new
	 * BasicNameValuePair("mcc", service.doovRegGen.getOperatorName())); }
	 */

	public String postReg() {
		String ret = null;

		try {
			// Post请求的url，与get不同的是不需要带参数
//			String url = DoovRegConst.REG_URL_QIJI;
//			if (service.doovRegGen.getPhoneType().contains("L5M")) {
//				url = DoovRegConst.REG_URL_JIANAI;
//			}else if (service.doovRegGen.getPhoneType().contains("L5")) {
//				url = DoovRegConst.REG_URL_QINGGUO;
//			}else if(service.doovRegGen.getPhoneType().contains("L6")){
//				url = DoovRegConst.REG_URL_CUICAN;	//CBQ add for L6
//			}
			Log.i(TAG,"PhoneType ="+service.doovRegGen.getPhoneType());
			String url = null;
			String[] device_names = service.getResources().getStringArray(R.array.device);
			String[] url_names = service.getResources().getStringArray(R.array.reg_url);
			for(int i = 0; i<device_names.length; i++){
				if (service.doovRegGen.getPhoneType().contains(device_names[i])) {
					url = url_names[i];
					Log.i(TAG,"url = "+ url);
					break;
				}
			}
			
			URL postUrl = new URL(url);
			// 打开连接
			HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
//			connection.setConnectTimeout(30000);	//CBQ add 
//			connection.setReadTimeout(30000);
//			Log.i(TAG,"setReadTimeout");
			// 设置是否向connection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true
			connection.setDoOutput(true);
			// Read from the connection. Default is true.
			connection.setDoInput(true);
			// Set the post method. Default is GET
			connection.setRequestMethod("POST");
			// Post 请求不能使用缓存
			connection.setUseCaches(false);
			// URLConnection.setFollowRedirects是static函数，作用于所有的URLConnection对象。
			// connection.setFollowRedirects(true);
			// URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
			connection.setInstanceFollowRedirects(true);
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			// 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode
			// 进行编码
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//			connection.setRequestProperty("Content-Type","text/plain");
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行connect。
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			// The URL-encoded contend
			// 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
			String content = service.doovRegGen.getLocation() + ";" +
//					service.doovRegGen.getImei() + ",000000000000000;" +
					service.doovRegGen.getFirstIMEI() + ",000000000000000;" +
//					service.doovRegGen.intentGetImeiValue() + ",000000000000000;" +
					service.doovRegGen.getDisplayMetrics() + ";" +
					service.doovRegGen.getPhoneTypeAndRAM(service) + ";" +
					service.doovRegGen.getVersion() + ";" +
					service.doovRegGen.getRelease() + ";" +
					service.doovRegGen.getDataTime() + ";" +
					service.doovRegGen.getNetwork() + ";" +
					service.doovRegGen.getOperatorName() + ";";
			// URLEncoder.encode(service.doovRegGen.getOperatorName(), "UTF-8")
			// + ";";

			// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
			out.write(content.getBytes());

			out.flush();
			out.close(); // flush and close
//			Log.i(TAG,"connection ="+ connection);
//			Log.i(TAG,"connection.getResponseCode() ="+ connection.getResponseCode());
			Log.i(TAG,"connection!!!!!!!");
			if (connection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				ret = reader.readLine();
				reader.close();
				Log.i(TAG,"ret ="+ ret);
			} else {
				Log.d(TAG, "postReg connection error=" + connection.getResponseCode());
				ret = "0";
			}
			connection.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "IOExcaption");
			e.printStackTrace();
		}
		return ret;
	}
	
	// add by huangjiawei 静默安装 begin
	public String postInstall(){
		String ret = null;
		String pathUrl = DoovRegConst.INSTALL_URL;
		try {
			
			String mImei = service.doovRegGen.getFirstIMEI();
			String url = MobileInfo.getInstance(service).getHttpGetParams(pathUrl, mImei).toString();
			Log.i("huangjiawei", "url == " + url);
			URL postUrl = new URL(url);
			// 打开连接
			HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
			connection.setConnectTimeout(30000);	 
			connection.setReadTimeout(30000);
			connection.setDoOutput(true);
			
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			// URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
			connection.setInstanceFollowRedirects(true);
			
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			
			connection.connect();
			Log.i(TAG,"install -- connection!!!!!!!");
			if (connection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				ret = reader.readLine();
				reader.close();
				Log.i(TAG,"huangjiawei--install--ret ="+ ret);
			} else {
				Log.d(TAG, "huangjiawei --insatll -- postReg connection error=" + connection.getResponseCode());
				ret = "0";
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	// add by huangjiawei 静默安装 end

}
