/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.ui;

import java.util.List;

import org.apache.http.NameValuePair;

import com.qubecell.constants.NetworkResponse;
import com.qubecell.network.NetworkController;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

/**
 * This Fragment manages a single background task and retains 
 * itself across configuration changes.
 */
public class TaskFragment extends Fragment {


	private boolean isTaskRunning = false;
	/**
	 * Callback interface through which the fragment will report the
	 * task's progress and results back to the Activity.
	 */
	public static interface TaskCallbacks {
		void onPreExecute();
		void onProgressUpdate(int percent);
		void onCancelled();
		void onPostExecute(NetworkResponse result);
	}

	private TaskCallbacks mCallbacks;
	private DummyTask mTask;

	/**
	 * Hold a reference to the parent Activity so we can report the
	 * task's current progress and results. The Android framework 
	 * will pass us a reference to the newly created Activity after 
	 * each configuration change.
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}

	/**
	 * This method will only be called once when the retained
	 * Fragment is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);

	}

	/**
	 * Set the callback to null so we don't accidentally leak the 
	 * Activity instance.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	/**
	 * A dummy task that performs some (dumb) background work and
	 * proxies progress updates and results back to the Activity.
	 *
	 * Note that we need to check if the callbacks are null in each
	 * method in case they are invoked after the Activity's and
	 * Fragment's onDestroy() method have been called.
	 */
	private class DummyTask extends AsyncTask<Object[], Object, NetworkResponse> {

		@Override
		protected void onPreExecute() {
			isTaskRunning = true;
			if (mCallbacks != null) {
				mCallbacks.onPreExecute();
			}
		}

		/**
		 * Note that we do NOT call the callback object's methods
		 * directly from the background thread, as this could result 
		 * in a race condition.
		 */
		@Override
		protected NetworkResponse doInBackground(Object[]... reqParam) {
			isTaskRunning = true;
			Object[] requestParam = reqParam[0];
			List<NameValuePair> requestP = (List<NameValuePair>) requestParam[0];
			int requestType = (Integer) requestParam[1];
			NetworkController nwObj = new NetworkController();
			NetworkResponse netresp = nwObj.httpPost(requestP, requestType);
			return netresp;
		}

		/*@Override
    protected void onProgressUpdate(Integer... percent) {
      if (mCallbacks != null) {
        mCallbacks.onProgressUpdate(percent[0]);
      }
    }*/

		@Override
		protected void onCancelled() {
			if (mCallbacks != null) {
				mCallbacks.onCancelled();
			}
		}

		@Override
		protected void onPostExecute(NetworkResponse result) {
			if (mCallbacks != null) {
				mCallbacks.onPostExecute(result);
				isTaskRunning = false;
				mTask = null;
			}
		}
	}

	public void initTaskFlag()
	{
		isTaskRunning = false;
		mTask = null;
	}
	
	public void executeRequest(List<NameValuePair> requestParam, int requestType) {
		Object[] reqParam = new Object[2];
		reqParam[0] = requestParam;
		reqParam[1] = requestType;
		if(!isTaskRunning)
		{
			if(mTask == null)
				mTask = new DummyTask();
			mTask.execute(reqParam);
		}
	}
}