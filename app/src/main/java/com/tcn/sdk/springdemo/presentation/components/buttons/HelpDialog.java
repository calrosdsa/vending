package com.tcn.sdk.springdemo.presentation.components.buttons;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.presentation.ResourceUtil;
import com.tcn.sdk.springdemo.presentation.Resources;

public class HelpDialog extends Dialog {
	private Context m_Context = null;
	private TextView tv;
	private TextView load_text;




	public HelpDialog(Context context, String load, String tv) {
		super(context, R.style.ui_base_Dialog_bocop);
		m_Context = context;
		init(load, tv);
	}

	private void init(String load, String tv1) {
		View contentView = View.inflate(m_Context, ResourceUtil.getLayoutId(m_Context, "help_dialog"), null);
		//contentView.setBackgroundResource(R.drawable.dialoback);
		setContentView(contentView);
		findViewById(R.id.close_help_dialog).setOnClickListener(view -> cancel());
//		tv = (TextView) findViewById(ResourceUtil.getId(m_Context, "tv"));
//		tv.setText(tv1);
//		load_text=(TextView) findViewById(ResourceUtil.getId(m_Context, "load_text"));
//		load_text.setText(load);
//		initAnim();
		getWindow().setWindowAnimations(Resources.getAnimResourceID(R.anim.ui_base_alpha_in));
	}


	@Override
	public void show() {
		super.show();
	}



	@Override
	public void cancel() {
		super.cancel();
	}








}
