package com.avg.reflection.pluggable;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import dalvik.system.DexFile;

public class PluginManager {
	private SparseArray<ISuperHero> mPlugins;
	private Context mContext;
	private File mDexCacheDir;

	public PluginManager(Context context){
		mPlugins = new SparseArray<ISuperHero>();
		mContext = context;
		mDexCacheDir = new File(mContext.getFilesDir(), "dexCache");
		if( ! mDexCacheDir.exists() ){
			mDexCacheDir.mkdirs();
		}
	}

	public void init(){
		loadPlugins();
	}

	private void loadPlugins()  {
		File pluginsDir = new File(mContext.getFilesDir(), "plugins");
		if( ! pluginsDir.exists() ){
			pluginsDir.mkdirs();
		}
		File[] dexFiles = pluginsDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return filename.indexOf(".dex") >= 0;
			}
		});
		if( dexFiles.length > 0 ){
			for(File dexFile: dexFiles){
				try{
					loadFromSingleDexFile(dexFile);
				}catch(Exception e){
					Log.e("dex loader", "could not load file:" + dexFile.getName());
					e.printStackTrace();
				}
			}
		}
	}

	private void loadFromSingleDexFile(File dexFile)throws 
		IOException, IllegalAccessException,InstantiationException{
		DexFile df = DexFile.loadDex(dexFile.getAbsolutePath(), mDexCacheDir.getAbsolutePath(), 0);
		Enumeration<String> classes = df.entries();
		while(classes.hasMoreElements() ){
			String className = classes.nextElement();
			Class<?> clazz = df.loadClass(className, mContext.getClassLoader());
			if( clazz.isInstance(ISuperHero.class) ){
				ISuperHero plugin = (ISuperHero)clazz.newInstance();
				mPlugins.put(plugin.getSuperHeroID(), plugin);
			}
		}

	}


	public ISuperHero getPlugin(int id){
		return null;
	}

}
