package com.tcn.sdk.springdemo.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.tcn.springboard.control.TcnVendIF;

import controller.VendService;

public class TcnMainActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setFullScreen(this);
		super.onCreate(savedInstanceState);


		if (TcnVendIF.getInstance().isServiceRunning()) {

		} else {
			startService(new Intent(getApplication(), VendService.class));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		FullScreencall();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void setFullScreen(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public void FullScreencall() {
		//for new api versions.
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		decorView.setSystemUiVisibility(uiOptions);
	}


//	@Override // android.app.Activity, android.view.Window.Callback
//	public boolean dispatchTouchEvent(MotionEvent motionEvent) {
//		if (motionEvent.getAction() == 0) {
//			View currentFocus = getCurrentFocus();
//
//				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				if (inputMethodManager != null) {
//					assert currentFocus != null;
//					inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
//				}
//
//			return super.dispatchTouchEvent(motionEvent);
//		}
//		if (getWindow().superDispatchTouchEvent(motionEvent)) {
//			return true;
//		}
//		return onTouchEvent(motionEvent);
//	}

}
