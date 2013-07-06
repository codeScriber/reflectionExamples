package com.avg.reflection.pluginUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Application;
import android.content.SharedPreferences;

import com.avg.reflection.pluggable.ISuperHero;
import com.avg.reflection.pluggable.PluginManager;

public class PluginUserApp extends Application {
	
	private PluginManager mPluginManager;
	private static final String PLUGINS_SHARED_PREFS = "plugins";
	private static final String IS_PLUGINS_COPIED = "is_plugins_copied";
	
	@Override
	public void onCreate() {
		super.onCreate();
		copyFilesIfFirstTime();
		mPluginManager = new PluginManager(this);
		mPluginManager.init();
	}
	
	private void copyFilesIfFirstTime() {
		SharedPreferences shp = getSharedPreferences(PLUGINS_SHARED_PREFS, MODE_PRIVATE);
		if( ! shp.getBoolean(IS_PLUGINS_COPIED, false)){
			try{
				final String[] allFilesInPackage = getResources().getAssets().list("");
				File pluginsDir = new File(getFilesDir(), "plugins");
				for( String fileName : allFilesInPackage ){
					if( fileName.endsWith("dex") ){
						InputStream in = getResources().getAssets().open(fileName);
						copyFile(fileName, in, pluginsDir);
					}
				}
				shp.edit().putBoolean(IS_PLUGINS_COPIED, true).commit();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public ISuperHero getPLugin(int id){
		return mPluginManager.getPlugin(id);
	}
	
	public int[] getPluginsIDS(){
		return mPluginManager.getPluginsIDS();
	}
	
	private void pump(InputStream from, OutputStream to) throws IOException{
		byte[] buffer = new byte[2048];
		int howManyBytes = from.read(buffer);
		while( howManyBytes >= 0 ){
			to.write(buffer, 0, howManyBytes);
			howManyBytes = from.read(buffer);
		}
		to.flush();
		from.close();
		to.close();
	}
	
	
	
	private void copyFile(String fileName,InputStream fileInputStream, File destDir) throws IOException{
		if( destDir.isDirectory() && destDir.canWrite() ){
			File dest = new File(destDir, fileName);
			FileOutputStream out = new FileOutputStream(dest);
			pump(fileInputStream, out);
		}
	}

}
