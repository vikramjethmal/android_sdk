package com.qubecell.beans;

/**
 * This LastAPIStatusRespBean class is used to hold the response data of GETLASTSTATUS_CDM from server.
 * @author Eninov
 *
 */
public class LastAPIStatusRespBean extends ResponseBaseBean {

	private String apiname = "";
	private long requestid;
	private int responsecode;
	private long apirequestid;
	private int apiresponsecode;
	private String apimessage = "";
	private String apitxnid;
	private String apiproductid;
	private String apimsisdn;
	private float apiamount;
	private String apioperatorname;
	private float actualprice;
	private String key;
	
	public String getApiproductid() {
		return apiproductid;
	}
	public void setApiproductid(String apiproductid) {
		this.apiproductid = apiproductid;
	}
	public String getApimsisdn() {
		return apimsisdn;
	}
	public void setApimsisdn(String apimsisdn) {
		this.apimsisdn = apimsisdn;
	}
	public float getApiamount() {
		return apiamount;
	}
	public void setApiamount(float apiamount) {
		this.apiamount = apiamount;
	}
	public String getApioperatorname() {
		return apioperatorname;
	}
	public void setApioperatorname(String apioperatorname) {
		this.apioperatorname = apioperatorname;
	}
	public float getActualprice() {
		return actualprice;
	}
	public void setActualprice(float actualprice) {
		this.actualprice = actualprice;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getApiname() {
		return apiname;
	}
	public void setApiname(String apiname) {
		this.apiname = apiname;
	}
	public int getResponsecode() {
		return responsecode;
	}
	public void setResponsecode(int responsecode) {
		this.responsecode = responsecode;
	}
	
	public int getApiresponsecode() {
		return apiresponsecode;
	}
	public void setApiresponsecode(int apiresponsecode) {
		this.apiresponsecode = apiresponsecode;
	}
	public String getApimessage() {
		return apimessage;
	}
	public void setApimessage(String apimessage) {
		this.apimessage = apimessage;
	}
	public long getRequestid() {
		return requestid;
	}
	public void setRequestid(long requestid) {
		this.requestid = requestid;
	}
	public long getApirequestid() {
		return apirequestid;
	}
	public void setApirequestid(long apirequestid) {
		this.apirequestid = apirequestid;
	}
	public String getApitxnid() {
		return apitxnid;
	}
	public void setApitxnid(String apitxnid) {
		this.apitxnid = apitxnid;
	}
	
}
