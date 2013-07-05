package com.avg.reflection.pluginUser;

import com.avg.reflection.pluggable.R;
import com.avg.reflection.pluggable.R.id;
import com.avg.reflection.pluggable.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PluginUserActivity extends Activity {
	
	private TextView mPluginOutputTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mPluginOutputTV = (TextView)findViewById(R.id.plugin_output_id);
	}

}
