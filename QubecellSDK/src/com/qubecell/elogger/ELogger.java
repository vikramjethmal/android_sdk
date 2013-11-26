/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.elogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.util.Log;

/**
 * This class provides the API for saving the log on sdcard and also display the log in logcat.
 * @author Eninov
 *
 */
public class ELogger 
{
	private String tag;
	private static boolean isInit=false;
	private static String fileName=null;
	private static File logFile = null; 
	private static final long FILE_SIZE=100*1024*1024; //1 MB = 1024*1024 byte
	
	public static final int VERBOSE=0;
	public static final int DEBUG=1;
	public static final int INFO=2;
	public static final int WARNING=3;
	public static final int ERROR=4;
	private static int label = ERROR;
	
	/**
	 * This is static method, for initializing for variable of ELogger. This method takes file name as argument. If file name is passed then this class 
	 * saves the log message in given file name on device SDCARD. If file name not provided then this class print the log only in logcat. 
	 * @param logFileName
	 * @return return's true if ELogger successfully initialized otherwise false
	 */
	synchronized public static boolean init(String logFileName)
	{
		if(isInit)
		{
			if(logFile != null)
			{
				fileName = null;
				logFile = null;
			}
			isInit = false;
		}
		
		if(logFileName != null)
		{
			fileName = logFileName;
			String status = Environment.getExternalStorageState();
			if (!status.equals(Environment.MEDIA_MOUNTED))
			{
				System.out.println("init():SD Card is not mounted. Log cannot save");
				return false;
			}
			logFile=new File(Environment.getExternalStorageDirectory(),fileName);
			if(logFile != null)
			{
				if(!logFile.exists())
				{
					try{
						logFile.createNewFile();
						}
						catch(IOException e)
						{
							 logFile=null;
							 System.out.println("saveLogToFile(): Problem in creating File on SD Card");
					         e.printStackTrace();
					         return false;
						}
						isInit=true;
						return true;
				}
				if(logFile.length()>=FILE_SIZE)
				{
					if(!backUpLog()) //Need to review
					{
						return false;
					}
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
		isInit=true;		
		return true;
	}
	
	static public void close()
	{
	    fileName = null;
	    logFile = null;
	    isInit = false;
	}
	
	public void setTag(String tag) //pass
	{
		if(tag != null)
			this.tag = tag;
		else
			this.tag = "UNKNOWN::";
	}
	public void info(String msg)
	{
		Log.i(tag,msg);
		if(logFile != null && label <= INFO) 
		{
			msg = "["+"INFO"+"] " + msg;
			saveLogToFile(msg);
		}
	}
	public void debug(String msg)
	{
		Log.d(tag,msg);
		if(logFile != null && label <= DEBUG) 
		{
			msg = "["+"DEBUG"+"] " + msg;
			saveLogToFile(msg);
		}
	}
	public void error(String msg)
	{
		Log.e(tag,msg);
		if(logFile != null && label <= ERROR) 
		{
			msg = "["+"ERROR"+"] " + msg;
			saveLogToFile(msg);
		}
	}
	public void warn(String msg)
	{
		Log.w(tag,msg);
		if(logFile != null && label <= WARNING) 
		{
			msg = "["+"WARNING"+"] " + msg;
			saveLogToFile(msg);
		}
	}
	public void verbose(String msg)
	{
		Log.v(tag,msg);
		if(logFile != null && label <= VERBOSE) 
		{
			msg = "["+"VERBOSE"+"] " + msg;
			saveLogToFile(msg);
		}
	}	
	/**
	 * This method takes the backup of log file if the log file size is greater than 100MB
	 * @return true if backup taken otherwise false
	 */
	synchronized private static boolean backUpLog()
	{
		String backupFileName=fileName+"_backup";
		File backUpFile=new File(Environment.getExternalStorageDirectory(),	backupFileName);		
		if(backUpFile.exists())
		{
			backUpFile.delete();
		}
		
		logFile.renameTo(backUpFile);
		logFile=new File(Environment.getExternalStorageDirectory(),fileName);
		try {
			logFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * This method saves the log message into specified file name. It takes message as parameter
	 * @param message to be saved
	 */
	synchronized private void saveLogToFile(String msg)
	{      

		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestr=fmt.format(System.currentTimeMillis());
		long threadId = Thread.currentThread().getId();
		String logStr="["+timestr+"]"+" "+"["+threadId+"]"+"  "+tag+"  "+msg;
		
		if(logFile.length()>=FILE_SIZE)
		{
			if(!backUpLog()){
				return;
			}
		}
		
		BufferedWriter buf=null;
	    try
	   {
		   
	      buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true)));//(new FileWriter(logFile, true));
	      if(buf!= null)
	      {
		      buf.append(logStr);
		      buf.newLine();
		      buf.flush();
		      buf.close();     
	      }
	   }
	   catch (Exception e)
	   {
	      // TODO Auto-generated catch block
		   System.out.println("saveLogToFile(): Problem in writing to File on SD Card");
		   e.printStackTrace();
	   }
	}
	
	/**
	 * This method is used to set the log levels.
	 * @param logLevel
	 */
	public static void setLogLevel(int logLevel){
		label = logLevel;
	}
}
//end of class ELogger