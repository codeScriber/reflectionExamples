package com.avg.loadExample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Menu;
import android.widget.ArrayAdapter;
import dalvik.system.DexClassLoader;

public class MainActivity extends ListActivity implements IThreadNotifier{

	private LoadExternalAPK mWorker;
	private Dialog mWaitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWorker = new LoadExternalAPK(this, this);
		mWorker.start();
		mWaitDialog = ProgressDialog.show(this, "please wait loading...", "Now Loading External Class");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	protected void onDestroy() {
		if( mWorker != null ){
			mWorker.sombodyStopMe();
			mWorker = null;
		}
		if( mWaitDialog != null && mWaitDialog.isShowing()){
			mWaitDialog.dismiss();
			mWaitDialog = null;
		}
		super.onDestroy();
	}

	@Override
	public void OnFinished() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mWaitDialog.dismiss();
				mWaitDialog = null;
				SparseIntArray arr = mWorker.getExternalData();
				String[] enumNames = mWorker.getEnumConstNames();
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
						android.R.layout.simple_list_item_1);
				adapter.setNotifyOnChange(false);
				setListAdapter(adapter);
				int size = arr.size();
				for(int i = 0; i < size; i++){
					int key = arr.keyAt(i);
					int value = arr.get(key);
					String stringValue = enumNames[value];
					adapter.add(String.format("Carrier: %s, mnc,mcc: %d",stringValue, key));
				}
				adapter.setNotifyOnChange(true);
				adapter.notifyDataSetChanged();
			}
		});
	}


	private static class LoadExternalAPK extends Thread{
		private WeakReference<Context> mContext;
		private volatile boolean mIsStop;
		private SparseIntArray mReturnedObject;
		private IThreadNotifier mNotifier;
		private String[] mEnumConstNames;

		private static final String APK_PKG_NAME = "com.imdb.mobile";
		private static final String TARGET_CLASS = "com.imdb.mobile.net.NetTools";
		private static final String TARGET_ENUM_CLASS = "com.imdb.mobile.net.NetTools$Carrier";
		private static final String TARGET_METHOD = "getCarrierMap";
		private static final String APK_LOCAL_FILE= "myApk.apk";

		public LoadExternalAPK(Context context, IThreadNotifier notifier){
			mContext = new WeakReference<Context>(context);
			mIsStop = false;
			mNotifier = notifier;
		}

		public void sombodyStopMe(){
			mIsStop = true;
		}

		@Override
		public void run() {
			super.run();
			if( ! mIsStop ){
				copyApk();
			}
			if( ! mIsStop ){
				loadExternalClass();
			}
			if( ! mIsStop ){
				mNotifier.OnFinished();
			}
			mNotifier = null;
		}

		public SparseIntArray getExternalData(){
			return mReturnedObject;
		}

		public String[] getEnumConstNames(){
			return mEnumConstNames;
		}

		private void loadExternalClass() {
			Context context = mContext.get();
			if( mContext != null ){
				try{
					ClassLoader parent = context.getClassLoader();
					DexClassLoader loader = new DexClassLoader(context.getFilesDir() + File.separator + APK_LOCAL_FILE,
							context.getFilesDir().toString() , null, parent);
					Class<?> temp = loader.loadClass(TARGET_CLASS);
					Class<?> carrierEnum = loader.loadClass(TARGET_ENUM_CLASS);
					if (temp != null ){
						Method m = temp.getDeclaredMethod(TARGET_METHOD, null);
						m.setAccessible(true);
						Object returnValue = m.invoke(null, null);
						Object[] consts = (Object[])carrierEnum.getEnumConstants();
						if( consts != null && consts.length != 0 ){
							mEnumConstNames = new String[consts.length];
							for(int i = 0; i < consts.length; i++){
								mEnumConstNames[i] = consts[i].toString();
							}
						}
						if( returnValue != null && ! mIsStop ){
							mReturnedObject = (SparseIntArray)returnValue;
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}

		}

		private void copyApk() {
			Context context = mContext.get();
			if( mContext != null ){
				try{
					File apkFile = new File("/data/app/"+APK_PKG_NAME+"-1.apk");
					FileOutputStream  localOut = context.openFileOutput(APK_LOCAL_FILE, Context.MODE_PRIVATE);
					byte[] buffer = new byte[1024];
					FileInputStream targetIn = new FileInputStream(apkFile);
					int read = 0;
					do{
						read = targetIn.read(buffer);
						if( read != -1 ){
							localOut.write(buffer, 0, read);
						}
					}while( ! mIsStop && read != -1 );
					if( ! mIsStop ){
						localOut.flush();
						localOut.close();
						targetIn.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}



}
