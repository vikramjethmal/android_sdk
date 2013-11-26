/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.constants;

/**
 * The NetworkResponseCode class has network response error code information.
 * @author Eninov
 *
 */
public class NetworkResponseCode {

	public static final int NET_RESP_SUCCESS = 1;
	public static final int NET_RESP_FAILURE = -1000;
	public static final int NET_REQ_TIMEOUT = NET_RESP_FAILURE -1;
	public static final int INVALID_URL = NET_RESP_FAILURE -2;
	public static final int NET_EXCEPTION = NET_RESP_FAILURE -3;
	public static final int FILE_NOT_FOUND = NET_RESP_FAILURE -4;
}
