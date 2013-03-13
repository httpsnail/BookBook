package com.android.bookbook.util;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.android.bookbook.BookbookApp;
import com.android.bookbook.R;

/**
 * 图片加载服务
 * 
 * 使用说明： 1、在显示图片的地方调用displayImage()；
 * 2、在Activity#onPause中添加puase()，传入需要忽略的图片url地址； 或传入ListView控件的引用，自动生成需要忽略的图片地址；
 * 3、在Activity#onResume中添加ListView#invalidateViews；
 * 4、在Activity#onStop中添加stop()，中止服务。
 * 
 * @author yueyueniao
 */
public class ImageManager2 {

	/** 单例，与Activity关联，若Activity被回收，该实例也会被回收。 */
	// private static Map<Context, ImageManager2> sInstanceMap = new
	// WeakHashMap<Context, ImageManager2>();

	static ImageManager2 imageManager;
	public LruCache<String, Bitmap> mMemoryCache;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 10MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";
	public DiskLruCache mDiskCache;

	/**
	 * 获取单例，只能在UI线程中使用。
	 * 
	 * @param context
	 * @return
	 */
	public static ImageManager2 from() {

		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Cannot instantiate outside UI thread.");
		}

		if (imageManager == null) {
			imageManager = new ImageManager2(BookbookApp.getInstance());
		}

		return imageManager;
	}

	/** 对应的Context对象 */
	private Context mContext;

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	private ImageManager2(Context context) {
		mContext = context;
		int memClass = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		// ToolUtil.log("memClass" + memClass);
		memClass = memClass > 32 ? 32 : memClass;
		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = 1024 * 1024 * memClass / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in bytes rather than number
				// of items.
				return bitmap.getRowBytes() * bitmap.getHeight();
			}

		};

		File cacheDir = DiskLruCache
				.getDiskCacheDir(context, DISK_CACHE_SUBDIR);
		mDiskCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);

	}

	/** 图片缓存 */
	// private Map<String, SoftReference<Bitmap>> mImageMap = new
	// HashMap<String, SoftReference<Bitmap>>();

	/** 图片加载队列，后进先出 */
	private Stack<ImageRef> mImageQueue = new Stack<ImageRef>();

	/** 图片请求队列，先进先出，用于存放已发送的请求。 */
	private Queue<ImageRef> mRequestQueue = new LinkedList<ImageRef>();

	/** 图片加载线程消息处理器 */
	private Handler mImageLoaderHandler;

	/** 图片加载线程是否就绪 */
	private boolean mImageLoaderIdle = true;

	/** 请求图片 */
	public static final int MSG_REQUEST = 1;
	/** 图片加载完成 */
	public static final int MSG_REPLY = 2;
	/** 中止图片加载线程 */
	public static final int MSG_STOP = 3;

	/**
	 * 存放图片信息
	 */
	class ImageRef {

		/** 图片对应ImageView控件 */
		ImageView imageView;
		/** 图片URL地址 */
		String url;
		/** 图片缓存路径 */
		String filePath;
		/** 默认图资源ID */
		int resId;
		int width = 0;
		int height = 0;

		/**
		 * 构造函数
		 * 
		 * @param imageView
		 * @param url
		 * @param resId
		 * @param filePath
		 */
		ImageRef(ImageView imageView, String url, String filePath, int resId) {
			this.imageView = imageView;
			this.url = url;
			this.filePath = filePath;
			this.resId = resId;
		}

		ImageRef(ImageView imageView, String url, String filePath, int resId,
				int width, int height) {
			this.imageView = imageView;
			this.url = url;
			this.filePath = filePath;
			this.resId = resId;
			this.width = width;
			this.height = height;
		}

	}

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 * @param url
	 * @param resId
	 */
	public void displayImage(ImageView imageView, String url, int resId) {
		// ToolUtil.log("mMemoryCache.size:" + mMemoryCache.size());
		// 检测
		if (imageView == null) {
			return;
		}
		if (imageView.getTag() != null
				&& imageView.getTag().toString().equals(url)) {
			return;
		}
		if (resId >= 0) {
			if (imageView.getBackground() == null) {
				imageView.setBackgroundResource(resId);
			}
			imageView.setImageDrawable(null);

		}
		if (url == null || url.equals("")) {
			return;
		}

		// 添加url tag
		imageView.setTag(url);

		// 读取map缓存
		Bitmap bitmap = mMemoryCache.get(url);
		if (bitmap != null) {
			// ToolUtil.log("从mMemoryCache缓存读取");
			setImageBitmap(imageView, bitmap, false);
			return;
		}

		// 生成文件名
		String filePath = urlToFilePath(url);
		if (filePath == null) {
			return;
		}

		queueImage(new ImageRef(imageView, url, filePath, resId));
	}

	private Bitmap blackBgBmp;

	/**
	 * 大图gallery展示图片该方法包含回收与ImageView相关联bitmap功能
	 * 注意：ImageView禁止绑定资源id,例如：imageView.setImageResource(resId);
	 * 这样会导致这个资源被回收，再次使用时报错，使用BitmapFactory.decodeResource生产一个新的实例。
	 * 
	 * @param imageView
	 *            imageview
	 * @param url
	 *            图片url
	 * @param resId
	 *            默认图id
	 */

	public void displayImage(ImageView imageView, String url, int resId,
			int width, int height) {
		// ToolUtil.log("mMemoryCache.size:" + mMemoryCache.size());
		// 检测
		if (imageView == null) {
			return;
		}
		if (resId >= 0) {

			if (imageView.getBackground() == null) {
				imageView.setBackgroundResource(resId);
			}
			imageView.setImageDrawable(null);

		}
		if (url == null || url.equals("")) {
			return;
		}

		// 添加url tag
		imageView.setTag(url);
		// /mnt/sdcard/DCIM/Camera/IMG_20120602_133142.jpg
		// 读取map缓存
		Bitmap bitmap = mMemoryCache.get(url + width + height);
		if (bitmap != null) {
			Log.e("Image2:", "从mMemoryCache缓存读取" + url);
			setImageBitmap(imageView, bitmap, false);
			return;
		}

		// 生成文件名
		String filePath = urlToFilePath(url);
		if (filePath == null) {
			return;
		}

		queueImage(new ImageRef(imageView, url, filePath, resId, width, height));
	}

	/**
	 * 入队，后进先出
	 * 
	 * @param imageRef
	 */
	public void queueImage(ImageRef imageRef) {

		// 删除已有ImageView
		Iterator<ImageRef> iterator = mImageQueue.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().imageView == imageRef.imageView) {
				iterator.remove();
			}
		}

		// 添加请求
		mImageQueue.push(imageRef);
		sendRequest();
	}

	/**
	 * 发送请求
	 */
	private void sendRequest() {

		// 开启图片加载线程
		if (mImageLoaderHandler == null) {
			HandlerThread imageLoader = new HandlerThread("image_loader");
			imageLoader.start();
			mImageLoaderHandler = new ImageLoaderHandler(
					imageLoader.getLooper());
		}

		// 发送请求
		if (mImageLoaderIdle && mImageQueue.size() > 0) {
			ImageRef imageRef = mImageQueue.pop();
			Message message = mImageLoaderHandler.obtainMessage(MSG_REQUEST,
					imageRef);
			mImageLoaderHandler.sendMessage(message);
			mImageLoaderIdle = false;
			mRequestQueue.add(imageRef);
		}
	}

	private boolean isFromNet = true;

	/**
	 * 图片加载线程
	 */
	class ImageLoaderHandler extends Handler {

		public ImageLoaderHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			if (msg == null)
				return;

			switch (msg.what) {

			case MSG_REQUEST: // 收到请求
				Bitmap bitmap = null;
				Bitmap tBitmap = null;
				if (msg.obj != null && msg.obj instanceof ImageRef) {

					ImageRef imageRef = (ImageRef) msg.obj;
					String url = imageRef.url;
					if (url == null)
						return;

					if (url.toLowerCase().contains("dcim")) {

						Log.i("mnt", "生成缩略图" + url);
						tBitmap = null;
						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inSampleSize = 1;
						opt.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(url, opt);
						int bitmapSize = opt.outHeight * opt.outWidth * 4;
						opt.inSampleSize = bitmapSize / (1000 * 2000);
						opt.inJustDecodeBounds = false;
						bitmap = BitmapFactory.decodeFile(url, opt);
						

					} else
						bitmap = mDiskCache.get(url);

					if (bitmap != null) {
						// ToolUtil.log("从disk缓存读取");
						// 写入map缓存
						if (imageRef.width != 0 && imageRef.height != 0) {
							if (mMemoryCache.get(url + imageRef.width
									+ imageRef.height) == null)
								mMemoryCache.put(url + imageRef.width
										+ imageRef.height, bitmap);
						} else {
							if (mMemoryCache.get(url) == null)
								mMemoryCache.put(url, bitmap);
						}

					} else {
						try {
							byte[] data = loadByteArrayFromNetwork(url);

							if (data != null) {

								BitmapFactory.Options opt = new BitmapFactory.Options();
								opt.inSampleSize = 1;

								opt.inJustDecodeBounds = true;
								BitmapFactory.decodeByteArray(data, 0,
										data.length, opt);
								int bitmapSize = opt.outHeight * opt.outWidth
										* 4;// pixels*3 if it's RGB and pixels*4
											// if it's ARGB
								if (bitmapSize > 1000 * 1200)
									opt.inSampleSize = 2;
								opt.inJustDecodeBounds = false;
								bitmap = BitmapFactory.decodeByteArray(data,
										0, data.length, opt);
						

								if (bitmap != null && url != null) {
									// ToolUtil.log("从网络缓存读取");
									// 写入SD卡
									if (imageRef.width != 0
											&& imageRef.height != 0) {
										mDiskCache.put(url + imageRef.width
												+ imageRef.height, bitmap);
										mMemoryCache.put(url + imageRef.width
												+ imageRef.height, bitmap);
									} else {
										mDiskCache.put(url, bitmap);
										mMemoryCache.put(url, bitmap);
									}
									isFromNet = true;
								}
							}
						} catch (OutOfMemoryError e) {
						}

					}

				}

				if (mImageManagerHandler != null) {
					Message message = mImageManagerHandler.obtainMessage(
							MSG_REPLY, bitmap);
					mImageManagerHandler.sendMessage(message);
				}
				break;

			case MSG_STOP: // 收到终止指令
				Looper.myLooper().quit();
				break;

			}
		}
	}

	/** UI线程消息处理器 */
	private Handler mImageManagerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {

				case MSG_REPLY: // 收到应答

					do {
						ImageRef imageRef = mRequestQueue.remove();

						if (imageRef == null)
							break;

						if (imageRef.imageView == null
								|| imageRef.imageView.getTag() == null
								|| imageRef.url == null)
							break;

						if (!(msg.obj instanceof Bitmap) || msg.obj == null) {
							break;
						}
						Bitmap bitmap = (Bitmap) msg.obj;

						// 非同一ImageView
						if (!(imageRef.url).equals((String) imageRef.imageView
								.getTag())) {
							break;
						}

						setImageBitmap(imageRef.imageView, bitmap, isFromNet);
						isFromNet = false;

					} while (false);

					break;
				}
			}
			// 设置闲置标志
			mImageLoaderIdle = true;

			// 若服务未关闭，则发送下一个请求。
			if (mImageLoaderHandler != null) {
				sendRequest();
			}
		}
	};

	private void setImageBitmap(ImageView imageView, Bitmap bitmap,
			boolean isTran) {
		if (isTran) {
			final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] {
							new ColorDrawable(android.R.color.transparent),
							new BitmapDrawable(bitmap) });
			td.setCrossFadeEnabled(true);
			imageView.setImageDrawable(td);
			td.startTransition(300);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * 从网络获取图片字节数组
	 * 
	 * @param url
	 * @return
	 */
	private byte[] loadByteArrayFromNetwork(String url) {

		try {

			HttpGet method = new HttpGet(url);
			HttpResponse response = HttpUtil.getHttpClient().execute(method);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toByteArray(entity);

		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 根据url生成缓存文件完整路径名
	 * 
	 * @param url
	 * @return
	 */
	public String urlToFilePath(String url) {

		// 扩展名位置
		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}

		StringBuilder filePath = new StringBuilder();

		// 图片存取路径 TODO 缓存目录的选择；目录容量管理。
		filePath.append(mContext.getCacheDir().toString()).append('/');

		// 图片文件名 TODO 是否需要扩展名
		filePath.append(url.substring(index));

		return filePath.toString();
	}

	/**
	 * Activity#onStop后，ListView不会有残余请求。
	 */
	public void stop() {

		// 清空请求队列
		mImageQueue.clear();
		// // 停止图片加载线程
		// if (mImageLoaderHandler != null) {
		// Message message = mImageLoaderHandler.obtainMessage(MSG_STOP);
		// mImageLoaderHandler.sendMessageAtFrontOfQueue(message);
		// mImageLoaderHandler = null;
		// }
	}

	// /**
	// * 暂停服务
	// */
	// public void pause(List<String> urlList) {
	//
	// // 清空请求队列
	// mImageQueue.clear();
	//
	// // 清空缓存
	// // clearCache(urlList);
	//
	// }
}
