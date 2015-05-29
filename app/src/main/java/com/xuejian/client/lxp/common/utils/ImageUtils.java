/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

public class ImageUtils {
//	public static String getThumbnailImagePath(String imagePath) {
//		String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
//		path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
//		EMLog.d("msg", "original image path:" + imagePath);
//		EMLog.d("msg", "thum image path:" + path);
//		return path;
//	}
	
	public static String getImagePath(String remoteUrl)
	{
		String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ imageName;
        EMLog.d("msg", "image path:" + path);
        return path;
		
	}
	
	
	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }

    public static Bitmap getMaskImage(Bitmap mask,Bitmap source){
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(source, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        return result ;
    }
	
	
}
