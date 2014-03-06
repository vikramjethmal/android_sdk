package com.qubecell.constants;

public class LastAPIStatusServerRespCode {

	public static final int SUCCESS = 121;
	public static final int REQUEST_DETAILS_FOUND = 122;
	public static final int NO_DETAILS_FOUND = 123;
	public static final int MANDATORY_PARAM_MISSING = 102;
	public static final int AUTHENTICATION_FALIURE = 103;
	public static final int INVALIDKEY = 104;
	public static final int UNDETERMINED_ERROR = 105;
	public static final int DUPLICATE_REQ_ID = 106;
	
	/**
	 * This method is used to return the server error strings as per given error code.
	 * @param appContext
	 * @param responseCode
	 * @return
	 */
	public static String getResponseString(int responseCode)
	{
		String errorString = null;

		switch (responseCode) {
		case SUCCESS:
			errorString = "Details found and have been passed back in the respective parameters";
			break;
		case REQUEST_DETAILS_FOUND:
			errorString = "Only request details found, transaction did not complete";
			break;
		case NO_DETAILS_FOUND:
			errorString = "No details found for the mentioned API request id";
			break;
		case MANDATORY_PARAM_MISSING:
			errorString = "Mandatory parameters missing";
			break;
		case AUTHENTICATION_FALIURE:
			errorString = "Authentication failure";
			break;
		case UNDETERMINED_ERROR:
			errorString = "Undetermined error";
			break;
		case INVALIDKEY:
			errorString = "Invalid key";
			break;
		case DUPLICATE_REQ_ID:
			errorString = "Duplicate request id";
			break;
			
		default:
			break;
		}
		
		return errorString;
	}
	
}
