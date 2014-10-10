package com.aizou.core.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;


import com.aizou.core.log.LogGloble;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.Map;

/**
 * 位图工具类，该类提供根据不同的资源获取指定尺寸要求的图片
 * 
 * @author xby
 * 
 *         2013-3-26上午10:00:28
 */
public class BitmapTools {
	/**
	 * 将资源文件下的图片转换成String
	 * @param id 图片资源id
	 * @return 转换后的String
	 */
	public String bitmaptoString(Context context,int id) {
		Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(id))
				.getBitmap();
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}
	
	/**
	 * 将Bitmap转换成String
	 * @return 转换后的String
	 */
	public String bitmaptoString(Bitmap bitmap) {
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	/**
	 * 将字符串转换成Bitmap
	 * @param string 原字符串
	 * @return Bitmap
	 */
	public Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}


	/**
	 * 根据图片路径key,获取到对应的BitMap
	 * 
	 * @param bitmapKey
	 * @return
	 */
	public static Bitmap getSoftReferenceMap(Context context,String bitmapKey,
			Map<String, SoftReference<Bitmap>> bitMapCaches) {
		Bitmap bitmap = null;

		// 从内存缓存中获取如果存在就返回
		if (bitMapCaches.containsKey(bitmapKey)) {
			bitmap = bitMapCaches.get(bitmapKey).get();
			// 如果缓存中的数据已经被释放，移除该路径
			if (bitmap == null) {
				bitMapCaches.remove(bitmapKey);
			} else
				return bitmap;
		}

		// 缓存中没有则从文件中读取
		File dir = context.getExternalFilesDir(
				Environment.DIRECTORY_PICTURES);
		// 创建需要的图片路径
		File file = new File(dir, bitmapKey);
		bitmap = getBitmap(file.getAbsolutePath());
		bitMapCaches.put(bitmapKey, new SoftReference<Bitmap>(bitmap));
		// 如果本地存在就从新放回到软引用并返回
		if (bitmap != null) {
			bitMapCaches.put(bitmapKey, new SoftReference<Bitmap>(bitmap));
			return bitmap;
		} else
			return null;
	}

	/**
	 * 保存BitMap到软引用缓存，图片备份到sdcard
	 * 
	 * @param bitmapKey
	 * @param saveBitmap
	 */
	private static void putBitmapToSoft(Context context,String bitmapKey, Bitmap saveBitmap,
			Map<String, SoftReference<Bitmap>> bitMapCaches) {
		bitMapCaches.put(bitmapKey, new SoftReference<Bitmap>(saveBitmap));
		// 向SD卡文件中添加缓存信息
		File dir = context.getExternalFilesDir(
				Environment.DIRECTORY_PICTURES);
		if (!dir.exists())
			dir.mkdirs();
		File file = new File(dir, bitmapKey);
		try {
			saveBitmap(file.getAbsolutePath(), saveBitmap);
		} catch (IOException e) {
			LogGloble.exceptionPrint(e);
		}
		saveBitmap = null;

	}

	/**
	 * 根据输入流获取位图像
	 * 
	 * @param is
	 *            图片输入流
	 * @return 返回输入流对应的图片
	 */
	public static Bitmap getBitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	/**
	 * 根据输入流和缩放比例获得位图像
	 * 
	 * @param is
	 *            图片输入流
	 * @param scale
	 *            缩放比例
	 * @return 返回获取的缩放后的图片
	 */
	public static Bitmap getBitmap(InputStream is, int scale) {
		Bitmap bitmap = null;
		Options opts = new Options();
		opts.inSampleSize = scale;
		bitmap = BitmapFactory.decodeStream(is, null, opts);
		return bitmap;
	}

	/**
	 * 通过字符数组获取指定宽和高的图片
	 * 
	 * @param bytes
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(byte[] bytes, int width, int height) {
		Bitmap bitmap = null;
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		opts.inJustDecodeBounds = false;
		int scaleX = opts.outWidth / width;
		int scaleY = opts.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		opts.inSampleSize = scale;
		bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		return bitmap;
	}


	/**
	 * 根据输入流和宽和高获得位图像
	 * 
	 * @param is
	 *            获取图片的输入流
	 * @param width
	 *            想要获取的图片的宽度
	 * @param height
	 *            想要获取的图片的高度
	 * @return 返回获取到的图片
	 */
	public static Bitmap getBitmap(InputStream is, int width, int height) {
		Bitmap bitmap = null;
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, new Rect(0, 0, 0, 0), opts);
		opts.inJustDecodeBounds = false;
		int saleX = opts.outWidth / width;
		int saleY = opts.outHeight / height;
		int sale = saleX > saleY ? saleX : saleY;
		opts.inSampleSize = sale;
		bitmap = BitmapFactory.decodeStream(is, null, opts);
		return bitmap;
	}

	/**
	 * 从文件中获取位图像
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 保存位图对象到指定位置
	 * 
	 * @param path
	 *            文件保存路径
	 * @param bitmap
	 *            所要保存的图片
	 * @throws java.io.IOException
	 */
	public static void saveBitmap(String path, Bitmap bitmap)
			throws IOException {
		if (path != null && bitmap != null) {
			File file = new File(path);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			OutputStream stream = new FileOutputStream(file);
			String name = file.getName();
			String end = name.substring(name.lastIndexOf('.') + 1);
			if ("png".equals(end)) {
				bitmap.compress(CompressFormat.PNG, 100, stream);
			} else {
				bitmap.compress(CompressFormat.JPEG, 100, stream);
			}
		}

	}

	/**
	 * 读取资源图片返回指定的宽高
	 *
	 * @param context
	 *            上下文对象
	 * @param resId
	 *            资源ID
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId, int width,
			int height) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, opts);
		opts.inJustDecodeBounds = false;
		int scaleX = opts.outWidth / width;
		int scaleY = opts.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		opts.inSampleSize = scale;

		return BitmapFactory.decodeStream(is, null, opts);
	}

	// Read bitmap
	public static Bitmap readBitmap(Context context,Uri selectedImage) {
		Bitmap bm = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 5;
		AssetFileDescriptor fileDescriptor = null;
		try {
			fileDescriptor = context
					.getContentResolver()
					.openAssetFileDescriptor(selectedImage, "r");
		} catch (FileNotFoundException e) {
			LogGloble.exceptionPrint(e);
		} finally {
			try {
				bm = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);
				fileDescriptor.close();
			} catch (IOException e) {
				LogGloble.exceptionPrint(e);
			}
		}
		return bm;
	}

	// Clear bitmap
	public static void clearBitmap(Bitmap bm) {
		bm.recycle();
		System.gc();
	}

	/**
	 * 把传进来的bitmap对象转换为宽度为x,长度为y的bitmap对象
	 * @param b 要更改的原位图
	 * @param x 原位图的宽
	 * @param y 原位图的高
	 * @return
	 */
	public static Bitmap getGivenXYBitmap(Bitmap b, float x, float y) {
		int w = b.getWidth();
		int h = b.getHeight();
		float sx = (float) x / w;// 要强制转换，不转换我的在这总是死掉。
		float sy = (float) y / h;
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
		return resizeBmp;
	}
	
	/**
	 * 获取宽度为x,长度为y的bitmap对象
	 * @param resId 要更改的原位图
	 * @param width 原位图的宽
	 * @param height 原位图的高
	 * @return
	 */
	public static Bitmap getGivenXYBitmap(Context context, int resId, int width,
			int height) {
		Bitmap b = readBitMap(context, resId,
				width, height);
		int w = b.getWidth();
		int h = b.getHeight();
		float sx = (float) width / w;// 要强制转换，不转换我的在这总是死掉。
		float sy = (float) height / h;
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
		return resizeBmp;
	}
	/**
	 * 根据文件路径和图片宽高获取图片
	 * 
	 * @param path
	 *            文件路径
	 * @param width
	 *            想要获取的图片的宽度
	 * @param height
	 *            想要获取的图片的高度
	 * @return 返回获取到的图片
	 */

	public static Bitmap getBitmap(String path, int width, int height) {
		Bitmap bitmap = null;
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inJustDecodeBounds = false;
		int scaleX = opts.outWidth / width;
		int scaleY = opts.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		opts.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(path, opts);
		return bitmap;
	}

	/**
	 * 读取图片度数功能
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
