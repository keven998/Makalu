
package com.aizou.core.dialog;




import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.R;

public class ToastUtil {
    private Toast mToastInstance;
    private Context mContext;
    private  static ToastUtil sInstance;
    public static ToastUtil getInstance(Context ct) {
    	if(sInstance == null){
    		sInstance = new ToastUtil(ct.getApplicationContext());
    	}
    	return sInstance;
    }

    private ToastUtil(Context context) {
        mContext = context;
    }

    public void showToast(String message) {
        makeText(mContext, message, Toast.LENGTH_SHORT,false).show();
    }
    
    public void showToastCenter(String message) {
        makeText(mContext, message, Toast.LENGTH_SHORT,true).show();
    }

    public void showToast(String message, int showTime) {
        makeText(mContext, message, showTime,false).show();
    }

   

    private Toast makeText(Context context, String message, int showTime, boolean isCenter) {
        if (mToastInstance == null) {

            mToastInstance = new Toast(context);

            LayoutInflater inflate = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = inflate.inflate(R.layout.view_toast_content, null);
            mToastInstance.setView(v);

        }

        ((TextView) mToastInstance.getView().findViewById(R.id.message))
                .setText(message);

        mToastInstance.setDuration(showTime);
        if(isCenter){
        	mToastInstance.setGravity(Gravity.CENTER, 0, 0);
        }

        return mToastInstance;
    }
    
    

}
