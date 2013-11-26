/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.xmlparser;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.text.TextUtils;

import com.qubecell.beans.CheckStatusRespBean;
import com.qubecell.beans.EventChargeRespBean;
import com.qubecell.beans.MsisdnRespBean;
import com.qubecell.beans.OperatorDetails;
import com.qubecell.beans.OperatorsRespBean;
import com.qubecell.beans.ResponseBaseBean;
import com.qubecell.beans.SendOTPRespBean;
import com.qubecell.beans.ValidateOTPRespBean;
import com.qubecell.constants.MsisdnServerRespCode;
import com.qubecell.constants.ServerCommand;
import com.qubecell.elogger.ELogger;

/**
 * The XMLParser class is used to parse XML received as response from server.
 * @author Eninov
 *
 */
public class XMLParser 
{
	private static ELogger log = null;
	private String logTag = "XMLParser";
	private static XMLParser parserObj = null;

	public boolean init()
	{
		boolean initResult = false;
		log = new ELogger();
		log.setTag(logTag);
		parserObj = getInstance();
		return initResult;
	}

	public static XMLParser getInstance() 
	{
		if(parserObj == null)
			parserObj = new XMLParser();
		return parserObj;
	}

	/**
	 * This method is used to create response bean from server response string. 
	 * @param respStr response String from server
	 * @param serverCmd server command type
	 * @return
	 */
	public ResponseBaseBean getResponseBean(String respStr, int serverCmd)
	{
		if(TextUtils.isEmpty(respStr))
		{
			log.error("getResponseBean() : Server response is null");
			return null;
		}

		respStr = respStr.replaceAll("\\s+", " ");
		respStr = respStr.trim();
		Document doc = XMLfunctions.XMLfromString(respStr);
		if(doc == null)
		{
			log.error("getResponseBean() : Document object is null");
			return null;
		}

		NodeList sectionObject = null;
		if(serverCmd == ServerCommand.MSISDN_CMD)
		{
			sectionObject = doc.getElementsByTagName("detect");
		}
		else if(serverCmd == ServerCommand.FETCH_OPR_CMD)
		{
			sectionObject = doc.getElementsByTagName("operators");
		}
		else
		{
			sectionObject = doc.getElementsByTagName("transaction");
		}

		if(sectionObject == null)
		{
			log.error("getResponseBean() : Node List object is null");
			return null;
		}
		Element uiHeaderElement = (Element) sectionObject.item(0);
		if(uiHeaderElement == null)
		{
			log.error("getResponseBean() : Element object is null");
			return null;
		}
		String requestId = XMLfunctions.getValue(uiHeaderElement, "requestid");
		String responseCode = XMLfunctions.getValue(uiHeaderElement, "responsecode");
		int respCode = 0;
		if(!TextUtils.isEmpty(responseCode))
		{
			try 
			{
				respCode = Integer.parseInt(responseCode);
			}
			catch (NumberFormatException e) 
			{
				log.error("getResponseBean() : Number format exception : "+ e);
			}
		}
		String message = XMLfunctions.getValue(uiHeaderElement, "message");
		String msisdn = XMLfunctions.getValue(uiHeaderElement, "msisdn");
		String operator = XMLfunctions.getValue(uiHeaderElement, "operator");
		String transactionId = XMLfunctions.getValue(uiHeaderElement, "txnid");
		String key = XMLfunctions.getValue(uiHeaderElement, "key");
		String amount = XMLfunctions.getValue(uiHeaderElement, "amount");
		String productid = XMLfunctions.getValue(uiHeaderElement, "productid");

		switch (serverCmd) 
		{
		case ServerCommand.MSISDN_CMD:
		{
			MsisdnRespBean bean = new MsisdnRespBean();
			bean.setRequestid(requestId);
			bean.setResponsecode(respCode);
			bean.setMsisdn(msisdn);
			bean.setOperator(operator);
			bean.setMessage(message);
			bean.setTxnid(transactionId);
			return bean;
		}

		case ServerCommand.SENDOTP_CMD:
		{
			SendOTPRespBean bean = new SendOTPRespBean();
			bean.setRequestid(requestId);
			bean.setResponsecode(respCode);
			bean.setMessage(message);
			bean.setTxnid(transactionId);
			bean.setKey(key);
			return bean;
		}

		case ServerCommand.VALIDATEOTP_CMD:
		{
			ValidateOTPRespBean bean = new ValidateOTPRespBean();
			bean.setRequestid(requestId);
			bean.setResponsecode(respCode);
			bean.setMsisdn(msisdn);
			bean.setOperator(operator);
			bean.setMessage(message);
			bean.setTxnid(transactionId);
			bean.setKey(key);
			return bean;
		}

		case ServerCommand.EVENTCHARGE_CMD:
		{
			EventChargeRespBean bean = new EventChargeRespBean();
			bean.setRequestid(requestId);
			bean.setResponsecode(respCode);
			bean.setMsisdn(msisdn);
			bean.setOperator(operator);
			bean.setMessage(message);
			bean.setAmount(amount);
			bean.setTxnid(transactionId);
			bean.setProductid(productid);
			return bean;
		}

		case ServerCommand.FETCH_OPR_CMD:
		{
			sectionObject = doc.getElementsByTagName("operator");
			if(sectionObject == null)
				return null;

			OperatorsRespBean oprBean = new OperatorsRespBean();
			ArrayList<OperatorDetails> oprDetails = new ArrayList<OperatorDetails>();
			for (int i = 0; i < sectionObject.getLength(); i++) 
			{
				Element header = (Element) sectionObject.item(i);
				OperatorDetails oprDetailObj = new OperatorDetails();
				oprDetailObj.setId(header.getAttribute("id"));
				oprDetailObj.setOperatorName(XMLfunctions.getValue(header, "productid"));
				log.info("OperatorId : "+ oprDetailObj.getId()+" , Operator Name : "+oprDetailObj.getOperatorName());
				oprDetails.add(oprDetailObj);
			}
			oprBean.setOperators(oprDetails);
			oprBean.setResponsecode(MsisdnServerRespCode.SUCCESS);
			return oprBean;
		}

		case ServerCommand.CHECK_STATUS_CMD:
		{
			CheckStatusRespBean checkRespBean = new CheckStatusRespBean();
			checkRespBean.setResponsecode(respCode);
			checkRespBean.setMessage(message);
			checkRespBean.setKey(key);
			checkRespBean.setOperator(operator);
			checkRespBean.setMsisdn(msisdn);
			checkRespBean.setRequestid(requestId);
			checkRespBean.setTxnid(transactionId);
			checkRespBean.setAmount(amount);

			return checkRespBean;
		}


		default:
		{
			log.error("getResponseBean() : Unknown switch case");
		}
		break;
		}
		return null;
	}
}
