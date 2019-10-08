package com.zf.doovreg;

import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class DoovRegInstallInfo {

	String pkgName;
	int fileSize;
	int versionCode;
	String versionName;
	String downloadUrl;
	String signatureMd5;
	String apkMd5;
	int limitNet;
	
	public String getPkgName() {
		return pkgName;
	}
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getSignatureMd5() {
		return signatureMd5;
	}
	public void setSignatureMd5(String signatureMd5) {
		this.signatureMd5 = signatureMd5;
	}
	public String getApkMd5() {
		return apkMd5;
	}
	public void setApkMd5(String apkMd5) {
		this.apkMd5 = apkMd5;
	}
	public int getLimitNet() {
		return limitNet;
	}
	public void setLimitNet(int limitNet) {
		this.limitNet = limitNet;
	}
	
	public static DoovRegInstallInfo json2Obj(String content) {
		DoovRegInstallInfo model = null;
		Gson gson = new Gson();
		try {
			List<DoovRegInstallInfo> models = gson.fromJson(content, new TypeToken<List<DoovRegInstallInfo>>() {
			}.getType());
			
			Log.i("DoovRegInstall", "models size == " + models.size());
			if (models == null || models.size() < 1) {
				return null;
			}
			model = models.get(0);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return model;
	}
	
}
