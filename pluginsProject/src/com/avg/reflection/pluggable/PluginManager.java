package com.avg.reflection.pluggable;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

import android.content.Context;
import android.util.SparseArray;

public class PluginManager {
	private SparseArray<ISuperHero> mPlusings;
	private Context mContext;
	private File mDexCacheDir;

	public PluginManager(Context context){
		mPlusings = new SparseArray<ISuperHero>();
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
			try{
				for(File dexFile: dexFiles){
					loadFromSingleDexFile(dexFile);
				}
			}catch(IOException e){
				e.printStackTrace();
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
				mPlusings.put(plugin.getSuperHeroID(), plugin);
			}
		}

	}


	public ISuperHero getPlugin(int id){
		return null;
	}

}
