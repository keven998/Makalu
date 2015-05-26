package com.aizou.core.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

public class UrlUtil {
	/**
	 * Java文件操作 获取文件扩展名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/**
	 * 获取文件的真实路径
	 * 
	 * @param act
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromURI(Activity act, Uri contentUri) {
		try {
			String[] proj = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.DATA };
			CursorLoader loader = new CursorLoader(act, contentUri, proj, null,
					null, null);
			Cursor cursor = loader.loadInBackground();
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			// 系统不能识别的 自己带着后缀
			return contentUri.getPath();
		}
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param act
	 *            当前Activity
	 * @param contentUri
	 *            Uri
	 * @return
	 */
	public static String getExtensionNameFromUri(Activity act, Uri contentUri) {
		String fileName = getRealPathFromURI(act, contentUri);
		return getExtensionName(fileName);
	}

	/**
	 * 在uri中拿到图片
	 * 
	 * @param uri
	 * @return
	 */
	public static Bitmap getBitmapFromUri(Context context, Uri uri) {
		Bitmap bitmap = null;
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(uri, null, null, null, null);// 根据Uri从数据库中找
		if (cursor != null) {
			cursor.moveToFirst();// 把游标移动到首位，因为这里的Uri是包含ID的所以是唯一的不需要循环找指向第一个就是了
			String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路径
			return getBitmapFromUrl(context, filePath);
		}
		return bitmap;
	}

	/**
	 * 根据图片路径获取图片
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapFromUrl(Context context, String filePath) {
		Bitmap bitmap = null;
		int orit = BitmapTools.readPictureDegree(filePath);// // 获取旋转的角度
		String orientation = Integer.toString(orit);
		if (filePath != null) {
			bitmap = BitmapFactory.decodeFile(filePath);// 根据Path读取资源图片
			bitmap = BitmapTools.getBitmap(filePath, 300, 300);
			int x = bitmap.getHeight() < bitmap.getWidth() ? bitmap.getWidth()
					: bitmap.getHeight();
			bitmap = Bitmap.createScaledBitmap(bitmap, x, x, true);
			int angle = 0;
			if (orientation != null && !"".equals(orientation)) {
				angle = Integer.parseInt(orientation);
			}
			if (angle != 0) {
				// 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
				Matrix m = new Matrix();
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				m.setRotate(angle); // 旋转angle度
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m,
                        true);// 从新生成图片
			}
		}
		return bitmap;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
}
