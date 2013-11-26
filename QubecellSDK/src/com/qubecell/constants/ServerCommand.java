/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;

/**
 * The ServerCommand class has the different command types for which a request is made to server. 
 * @author Eninov
 *
 */
public class ServerCommand {

	public static final int MSISDN_CMD = 0;
	public static final int SENDOTP_CMD = 1;
	public static final int VALIDATEOTP_CMD = 2;
	public static final int EVENTCHARGE_CMD = 3;
	public static final int SUBSCRIBE_CMD = 4;
	public static final int FETCH_OPR_CMD = 5;
	public static final int CHECK_STATUS_CMD = 6;
	public static final int MESSAGE_CMD = 7;
	public static final int UNSUBSCRIBE_CDM = 8;
	public static final int NONE_CMD = -1;
}
