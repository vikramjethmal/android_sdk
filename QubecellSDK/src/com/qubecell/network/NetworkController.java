/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.text.TextUtils;

import com.qubecell.constants.HttpConstant;
import com.qubecell.constants.NetworkResponse;
import com.qubecell.constants.NetworkResponseCode;
import com.qubecell.constants.NetworkURL;
import com.qubecell.constants.ServerCommand;
import com.qubecell.elogger.ELogger;

/**
 * The NetworkAsyncTask class is used to perform network related operations
 * @author Eninov
 *
 */
public class NetworkController 
{
	private ELogger eLogger = null;
	private String logTag = "Network::";
	protected Context appContext = null;
	protected HttpPost httpPost = null;
	HttpURLConnection httpConn = null;
	protected boolean initFlag = false;
	private static NetworkController nwObj =  null;
	
	public boolean init()
	{
		eLogger = new ELogger();
		eLogger.setTag(logTag);
		initFlag = true;
		nwObj = getInstance();
		return initFlag;
	}

	public static NetworkController getInstance() //pass
	{
		if(nwObj == null)
			nwObj = new NetworkController();
		return nwObj;
	}
	
	/**
	 * This method is used to send http post request to server.
	 * @param reqJson
	 * @return
	 */
	public NetworkResponse httpPost(List<NameValuePair> reqNameValuePair, int requestType) 
	{
		if(eLogger == null)
		{
			init();
		}
		
		String httpUrl = getNetworkURL(requestType);
		
		NetworkResponse netResp = null;
		if(requestType != ServerCommand.FETCH_OPR_CMD && reqNameValuePair == null || TextUtils.isEmpty(httpUrl)) 
		{
			eLogger.error("httpPost() : req json null or or null url");
		}
		else
		{
			eLogger.info("httpPost() : Request String : "+ reqNameValuePair.toString());
			netResp = new NetworkResponse();
			httpPost = null;
			try
			{
				httpPost = new HttpPost(httpUrl);
			}
			catch (IllegalArgumentException e) 
			{
				eLogger.error("httpPost() : IllegalArgumentException "+e);
				netResp.netRespCode = NetworkResponseCode.INVALID_URL;
				httpPost = null;
			}
			if(httpPost != null)
			{
				BasicHttpParams basicHttpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(basicHttpParams,HttpConstant.HTTP_REQUEST_TIMEOUT);
				HttpConnectionParams.setSoTimeout(basicHttpParams, HttpConstant.SOCKET_REQUEST_TIMEOUT);
				
				try
				{ 
					if(requestType != ServerCommand.FETCH_OPR_CMD)
					{
						httpPost.setEntity(new UrlEncodedFormEntity(reqNameValuePair));
					}
				} 
				catch (UnsupportedEncodingException e) 
				{
					eLogger.error("httpPost : UnsupportedEncodingException"+e);
					httpPost = null;
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				}

				DefaultHttpClient httpClient = new DefaultHttpClient(basicHttpParams);
				HttpResponse response = null;
				try 
				{
					eLogger.debug("httpPost : sending request to network ");
					response = httpClient.execute(httpPost);
					eLogger.debug("httpPost : response from network  : "+response);
				} 
				catch (ClientProtocolException e)
				{
					eLogger.error("httpPost : ClientProtocolException"+e);
					e.printStackTrace();
					closeHttpPost(httpPost);
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				} 
				catch(ConnectTimeoutException e)
				{
					netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
					eLogger.error("httpPost : ConnectTimeoutException"+e);
					closeHttpPost(httpPost);
					return netResp;
				} 
				catch(SocketTimeoutException e)
				{
					netResp.netRespCode = NetworkResponseCode.NET_REQ_TIMEOUT;
					eLogger.error("httpPost : SocketTimeoutException : "+e);
					closeHttpPost(httpPost);
					return netResp;
				}
				catch (Exception e) 
				{
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					eLogger.error("httpPost : Network Exception"+e);
					closeHttpPost(httpPost);
					return netResp;
				}

				StringBuffer resposebuf = new StringBuffer();
				StatusLine status = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					InputStreamReader inputStreamReader = null;
					BufferedReader bufferedReader = null;
					try
					{
						inputStreamReader = new InputStreamReader(entity.getContent());
						bufferedReader = new BufferedReader(inputStreamReader);
						String str;
						while ((str = bufferedReader.readLine()) != null)
						{
							resposebuf.append(str);
						}
					}
					catch (OutOfMemoryError e) 
					{
						eLogger.error("httpPost : OutOfMemoryError"+e);
						e.printStackTrace();
						netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
						return netResp;
					}
					catch (IllegalStateException e) 
					{
						eLogger.error("httpPost : IOException"+e);
						e.printStackTrace();
						netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
						return netResp;
					} 
					catch (IOException e)
					{
						eLogger.error("httpPost : IOException"+e);
						e.printStackTrace();
						netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
						return netResp;
					}
					finally
					{
						closeHttpPost(httpPost);
						closeBufferedReader(bufferedReader);
						closeInputStream(inputStreamReader);
					}
				}
				if (status.getStatusCode() >= 300) 
				{
					eLogger.error("httpPost : invalid status code HTTP:" + status.getStatusCode() + " "+ status.getReasonPhrase() + "\n" + resposebuf.toString());
					netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
					return netResp;
				} 
				else 
				{
					netResp.netRespCode = NetworkResponseCode.NET_RESP_SUCCESS;
					netResp.respStr = resposebuf.toString();
				}
			}
			else
			{
				netResp.netRespCode = NetworkResponseCode.NET_EXCEPTION;
				return netResp;
			}
		}
		eLogger.info("httpPost() : Response String : "+ netResp.respStr);
		netResp.requestType = requestType;
		return netResp;
	}
	
	/**
	 * This method is used to get the network URL for making request to the network for give request type.
	 * @param requestType
	 * @return
	 */
	private String getNetworkURL(int requestType) 
	{
		String httpUrl = null;
		switch (requestType) {
		case ServerCommand.MSISDN_CMD:
		{
			httpUrl = NetworkURL.MSISDN_URL;
		}
		break;
		case ServerCommand.FETCH_OPR_CMD:
		{
			httpUrl = NetworkURL.OPERATOR_URL;
		}
		break;
		case ServerCommand.SUBSCRIBE_CMD:
		{
			//httpUrl = NetworkURL.SUBSCRIPTION_URL;
		}
		break;
		case ServerCommand.CHECK_STATUS_CMD:
		{
			//httpUrl = NetworkURL.SUBSCRIPTION_URL;
		}
		break;
		case ServerCommand.UNSUBSCRIBE_CDM:
		{
			//httpUrl = NetworkURL.SUBSCRIPTION_URL;
		}
		break;
		case ServerCommand.GETLASTSTATUS_CDM:
		{
			httpUrl = NetworkURL.API_URL;
		}
		break;
		default:
		{
			httpUrl = NetworkURL.API_URL;
		}
		break;
		}
		return httpUrl;
	}

	/**
	 * This method is used to close all the http post request.
	 * @param httpPost
	 * @return
	 */
	private boolean closeHttpPost(HttpPost httpPost)
	{
		boolean rslt = false;
		if(httpPost != null)
		{
			httpPost.abort();
			httpPost = null;
			rslt = true;
		}
		else
		{
			 rslt = false;
		}
		return rslt;
	}
	
	/**
	 * This method is used to close input streams.
	 * @param inputStream
	 * @return
	 */
	private boolean closeInputStream(InputStreamReader inputStream)
	{
		boolean rslt = false;
		if(inputStream != null)
		{
			try 
			{
				inputStream.close();
				rslt = true;
			} catch (IOException e) 
			{
				eLogger.error("closeInputStream() : exception while closing input stream : "+e);
				e.printStackTrace();
				rslt = false;
			}
			inputStream = null;
		}
		else
		{
			rslt = false;
		}
		return rslt;
	}
	
	/**
	 * This method is used to close all the buffered readers.
	 * @param bufReader
	 * @return
	 */
	private boolean closeBufferedReader(BufferedReader bufReader)
	{
		boolean rslt = false;
		if(bufReader != null)
		{
			try 
			{
				bufReader.close();
				 rslt = true;
			}
			catch (IOException e) 
			{
				e.printStackTrace();
				 rslt = false;
			}
		}
		else
		{
			 rslt = false;
		}
		return rslt;
	}
}
