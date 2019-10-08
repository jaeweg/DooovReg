package com.zf.doovreg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

public class FileUtil {

	public static final String DIRPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"DoovReg" ;

	public static final String FILENAME = File.separator+"DoovReg.apk";
    public FileUtil() {
    }

    public String getSDPATH() {
        return DIRPATH;
    }

    /**
     * 在本地文件夹上创建文件
     * @param fileName
     * @return
     * @throws java.io.IOException
     */
    public File createFile(String fileName) throws IOException {
        File file = new File(DIRPATH + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     * @param dirName 目录名字
     * @return 文件目录
     */
    public File createDir(){
        File dir = new File(DIRPATH);
        if (!dir.exists()) {
        	dir.mkdir();
		}
        
        return dir;
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExist(){
        File file = new File(DIRPATH + FILENAME);
        return file.exists();
    }
    
    /**
     * 删除文件
     */
    public void clearFile(){
    	File file = new File(DIRPATH +FILENAME);
    	if (file != null) {
    		file.delete();
		}
    	File fileDir = new File(DIRPATH);
    	if (fileDir != null) {
    		if (fileDir.isDirectory()) {
    			fileDir.delete();
			}
		}
    	
    }

    public File write2SDFromInput(InputStream input){
        File file = null;
        OutputStream output = null;

        try {
            createDir();
            file =createFile(FILENAME);
            output = new FileOutputStream(file);
            byte [] buffer = new byte[1024];
            int len = 0;
            while((len = input.read(buffer)) != -1){
                output.write(buffer,0,len);
            }
            
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
            	if (null != output) {
            		output.close();
				}
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    
    /** 
     * 获取指定文件大小  
     * @param f  
     * @return  
     * @throws Exception 　　 
     */  
    @SuppressWarnings("resource")
	public long getFileSize(File file) throws Exception {  
        long size = 0;  
        if (file.exists()) {  
            FileInputStream fis = null;  
            fis = new FileInputStream(file);  
            size = fis.available();  
        } else {  
            file.createNewFile();  
            Log.e("FileUtil", "文件不存在!");  
        }  
        return size;  
    } 
    
    
}
