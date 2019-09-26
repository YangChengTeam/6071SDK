package com.game.sdk.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author janecer 2014-3-17下午2:53:51
 */
public class ImageCache {

	// 图片内存引用
	private static HashMap<String, SoftReference> bitmapCaches = new HashMap<String, SoftReference>(20);

	/**
	 * 获取图片思路： 1：根据imgurl从内存中获取 2：根据imgurl从sdcard中获取【sdcard缓存的图片命名为imgurl md5获取】
	 * 3：根据imgurl地址从网络中获取，获取后存到内存中，再存到sdcard中
	 * 
	 * @param imgurl
	 * @param ctx
	 * @param imagecallback
	 * @return bitmap
	 */
	public static Bitmap getBitmap(final String imgurl, final Context ctx, final ImageCallBack imagecallback) {
		// 从内存中获取
		if (bitmapCaches.containsKey(imgurl)) {
			SoftReference soft = bitmapCaches.get(imgurl);
			Bitmap bm = (Bitmap) soft.get();
			if (null != bm) {
				return bm;
			}
		}
		// 从sdcard上获取
		Bitmap bm = getBitmapFromSdcard(imgurl, ctx);
		if (null != bm) {
			return bm;
		}

		// 从网络上获取，获取后将图片保存到内存与sdcard中
		new AsyncTask<String, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(String... params) {
				InputStream is = null;
				try {
					//is = GetDataImpl.getInstance(ctx).getImgFromNet(imgurl);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}

				Bitmap bm_net;
				if (null != is) {
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inPreferredConfig = Bitmap.Config.RGB_565;
					opt.inPurgeable = true;
					opt.inInputShareable = true;
					opt.inSampleSize = computeSampleSize(opt, -1, 128 * 128); // 计算出图片使用的inSampleSize
					opt.inJustDecodeBounds = false;
					bm_net = BitmapFactory.decodeStream(is, null, opt);
				} else {
					// 返回的输入流为null 将从资源文件中读取默认图片
					bm_net = BitmapFactory.decodeResource(ctx.getResources(),
							MResource.getIdByName(ctx, "drawable", "user_default_icon"));
				}
				return bm_net;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (null != result) {
					imagecallback.LoadImageFromIntenet(result);// 图片加载回调
					bitmapCaches.put(imgurl, new SoftReference(result));
					// 将图片写入sdcard
					write2sdcard(imgurl, result, ctx);
				}
			}
		}.execute();
		return null;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128
				: (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 获取图片思路： 1：根据imgurl从内存中获取 2：根据imgurl从sdcard中获取【sdcard缓存的图片命名为imgurl md5获取】
	 * 3：根据imgurl地址从网络中获取，获取后存到内存中，再存到sdcard中
	 * 
	 * 适用于使用子线程中使用
	 * 
	 * @param imgurl
	 * @param ctx
	 * @param imagecallback
	 * @return bitmap
	 */
	public static Bitmap getBitmap(final String imgurl, final Context ctx) {
		// 从内存中获取
		if (bitmapCaches.containsKey(imgurl)) {
			SoftReference soft = bitmapCaches.get(imgurl);
			Bitmap bm = (Bitmap) soft.get();
			if (null != bm) {
				return bm;
			}
		}
		// 从sdcard上获取
		Bitmap bm = getBitmapFromSdcard(imgurl, ctx);
		if (null != bm) {
			return bm;
		}

		// 从网络上获取，获取后将图片保存到内存与sdcard中
		InputStream is = null;
		Bitmap bm_net;
		if (null != is) {
			bm_net = BitmapFactory.decodeStream(is);
		} else {
			// 返回的输入流为null 将从资源文件中读取默认图片
			bm_net = BitmapFactory.decodeResource(ctx.getResources(),
					MResource.getIdByName(ctx, "drawable", "ttw_defalut_name"));
		}
		bitmapCaches.put(imgurl, new SoftReference(bm_net));
		// 将图片写入sdcard
		write2sdcard(imgurl, bm_net, ctx);
		return bm_net;
	}

	/**
	 * 从sdcard中获取bitmap
	 * 
	 * @param imgurl
	 * @param ctx
	 * @return Bitmap
	 */
	public static Bitmap getBitmapFromSdcard(String imgurl, Context ctx) {

		if (TextUtils.isEmpty(imgurl)) {
			return null;
		}

		String imgpath = Environment.getExternalStorageDirectory().getPath() + File.separator + ctx.getPackageName()
				+ File.separator + "image" + File.separator + Md5Util.md5(imgurl);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(imgpath);
			if (file.exists()) {
				return BitmapFactory.decodeFile(imgpath);
			}
		}
		return null;
	}

	/**
	 * 将bitmap写入到sdcard
	 * 
	 * @param imgurl
	 * @param ctx
	 */
	public static void write2sdcard(String imgurl, Bitmap bm, Context ctx) {

		if (TextUtils.isEmpty(imgurl) || null == bm) {
			return;
		}

		String imgpath = Environment.getExternalStorageDirectory().getPath() + File.separator + ctx.getPackageName()
				+ File.separator + "image" + File.separator + Md5Util.md5(imgurl);

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(imgpath);
			File fileParent = file.getParentFile();
			if (!fileParent.exists()) {
				fileParent.mkdirs();
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				bm.compress(CompressFormat.PNG, 100, fos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != fos) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public interface ImageCallBack {
		/**
		 * 从网络成功获取图片的回调
		 * 
		 * @param bm
		 */
		void LoadImageFromIntenet(Bitmap bm);
	}
	
	
	public static Bitmap loadImageByUrl(Context context, String imageUrl) {
		
		OkHttpClient mOkHttpClient = new OkHttpClient();
		Bitmap tempBitmap = FileUtil.getImageFromSDCard(context, PathUtil.getThemeDir(), imageUrl);
		
		if (tempBitmap != null) {
			return tempBitmap;
		} else {
			try {
				// 创建OkHttpClient针对某个imageUrl的数据请求
				Request request = new Request.Builder().url(imageUrl).build();
				Call call = mOkHttpClient.newCall(request);
				Response response;

				response = call.execute();
				byte[] bytes = response.body().bytes();
				// 把byte字节组装成图片
				tempBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

				// 图片缓存到SD卡
				FileUtil.writeImageInSDCard(context, tempBitmap, PathUtil.getThemeDir(), imageUrl);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return tempBitmap;
	}

}
