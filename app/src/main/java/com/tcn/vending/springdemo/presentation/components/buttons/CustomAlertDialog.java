package com.tcn.vending.springdemo.presentation.components.buttons;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tcn.vending.springdemo.R;
import com.tcn.vending.springdemo.presentation.Resources;

public class CustomAlertDialog extends Dialog {
	private Context m_Context = null;
	private TextView tv;
	private TextView load_text;
	private Button cancel_button;
	private Button confirm_button;




	public CustomAlertDialog(Context context, String load, String tv) {
		super(context, R.style.ui_base_Dialog_bocop);
		m_Context = context;
		init(load, tv);
	}

	private void init(String load, String tv1) {
		View contentView = View.inflate(m_Context, R.layout.custom_alert_dialog, null);
		//contentView.setBackgroundResource(R.drawable.dialoback);
		setContentView(contentView);
//		findViewById(R.id.close_help_dialog).setOnClickListener(view -> cancel());
		tv = (TextView) findViewById(R.id.load_title);
		tv.setText(tv1);
		load_text = (TextView) findViewById(R.id.load_text);
		load_text.setText(tv1);
		cancel_button = findViewById(R.id.cancel_ship);
		confirm_button = findViewById(R.id.confirm_ship);
		setCanceledOnTouchOutside(true);
		cancel_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				CustomAlertDialog.this.cancel();
			}
		});

//		tv.setText(m_Context.getString(R.string.confirmar_dipensar_text,"Mouse Rowell"));
//		load_text=(TextView) findViewById(ResourceUtil.getId(m_Context, "load_text"));
//		load_text.setText(load);
//		initAnim();
		getWindow().setWindowAnimations(Resources.getAnimResourceID(R.anim.ui_base_alpha_in));
	}

	public void setHeight(int height){
		getWindow().setLayout(700, height - 50);
	}

	public void setConfirmOnclick(View.OnClickListener onClick){
		confirm_button.setOnClickListener(view -> {
			onClick.onClick(view);
			CustomAlertDialog.this.cancel();
		});
	}

	@Override
	public void setTitle(CharSequence title) {
		tv.setText(title);
	}

	@Override
	public void show() {
		super.show();
	}
	public void setLoadText(String str) {
		if (load_text != null) {
			load_text.setText(str);
		}

	}

	@Override
	public void cancel() {
		super.cancel();
	}








}
