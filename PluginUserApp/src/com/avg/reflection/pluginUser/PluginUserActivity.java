package com.avg.reflection.pluginUser;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.avg.reflection.R;
import com.avg.reflection.pluggable.ISuperHero;

public class PluginUserActivity extends Activity {
	
	private TextView mPluginOutputTV;
	private int[] mPluginsIDS;
	private Random mRandom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		mPluginOutputTV = (TextView)findViewById(R.id.plugin_output_id);
		mPluginsIDS = ((PluginUserApp)getApplication()).getPluginsIDS();
		mRandom = new Random(System.currentTimeMillis());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		int idx = mRandom.nextInt(mPluginsIDS.length);
		ISuperHero someLieSuperHero = ((PluginUserApp)getApplication()).getPLugin(mPluginsIDS[idx]);
		String text = someLieSuperHero.doesWhateverSuperHeroDoes();
		mPluginOutputTV.setText(text);
	}

}
