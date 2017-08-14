package com.itheima17.mobileguard.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.dao.AntiVirusDao;
import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.utils.AppUtils;
import com.itheima17.mobileguard.utils.MD5Utils;
import com.itheima17.mobileguard.utils.PrintLog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * @author Administrator
 * @date 2015-12-3
 * @pagename com.itheima17.mobileguard.activity
 * @desc 病毒查杀的界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-03 16:41:16 +0800 (周四, 03 十二月 2015) $
 * @Id $Id: AntiVirusActivity.java 90 2015-12-03 08:41:16Z goudan $
 * @version $Rev: 90 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/AntiVirusActivity.java $
 * 
 */
public class AntiVirusActivity extends Activity {

	protected static final int SCANNING = 1;
	protected static final int FINISH = 3;
	protected static final int BEGINSCAN = 6;
	private CircleProgress mPb_scanprogress;
	private TextView mTv_scaninfo;
	private LinearLayout mLl_scanresult;
	private boolean isAnimatorInit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		// startScan();// 开始扫描

		// 检测是否有新病毒
		checkVirusVersion();
		// startScan();
	}

	private String getPoints(int pointCounts) {
		String points = "";
		for (int i = 0; i < pointCounts % 6 + 1; i++) {
			points += ".";
		}
		return points;
	}
	boolean connectionPoints = true;
	int pointCounts = 1;
	private void checkVirusVersion() {

		// 没有网络
		// 显示访问网络的进度条 ......

		startpointsView("正在玩命联网中");//对话框显示 点的切换 

		HttpUtils mHttpUtils = new HttpUtils();
		mHttpUtils.configTimeout(7000);// 设置超时时间为5秒
		mHttpUtils.send(HttpMethod.GET,
				getResources().getString(R.string.getvirusversion),
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "联网失败", 1)
								.show();
						// 关闭对话框
						closeNetDialog();
						startScan();
					}

					

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						closeNetDialog();
						
						//或版本号判断是否有新版本
						String serverCode = arg0.result;
						//客户端版本
						
						String currentCode = AntiVirusDao.getVirusVersion();
						
						if (serverCode.equals(currentCode)) {
							Toast.makeText(getApplicationContext(), "病毒库是最新的", 1).show();
							//开始扫描
							startScan();
						} else {
							//提示用户有新病毒 是否下载更新
							showConfirmDialog(serverCode);
						}
					}

					private void showConfirmDialog(final String serverCode) {
						AlertDialog.Builder ab = new AlertDialog.Builder(AntiVirusActivity.this);
						ab.setTitle("提醒")
						  .setMessage("有新病毒 是否下载更新？")
						  .setPositiveButton("下载", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//下载
								getNewVirus();
								
							}

							private void getNewVirus() {
								//下载新病毒信息
								startpointsView("正在玩命获取新病毒并更新中");//对话框显示 点的切换 
								
								HttpUtils mHttpUtils = new HttpUtils();
								mHttpUtils.send(HttpMethod.GET, getResources().getString(R.string.getnewvirus),
										new RequestCallBack<String>() {

											@Override
											public void onFailure(
													HttpException arg0,
													String arg1) {
												// TODO Auto-generated method stub
												closeNetDialog();
												//继续扫描
												startScan();
											}

											@Override
											public void onSuccess(
													ResponseInfo<String> arg0) {
												// TODO Auto-generated method stub
												closeNetDialog();
												try {
													//下载成功
													String json = arg0.result;
													//解析病毒
													
													JSONObject jsonObj = new JSONObject(json);
													String md5 = jsonObj.getString("md5");
													String desc = jsonObj.getString("desc");
													//插入本地数据库中
													AntiVirusDao.insert(md5, desc);
													//更新病毒库版本
													AntiVirusDao.updateVersion(serverCode);
													//继续扫描
													startScan();
												} catch (Exception e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										});
								
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//继续扫描
								startScan();
								
							}
						}).create().show();
						
					}
				});

	}
	
	private void closeNetDialog() {
		mConnectDialog.dismiss();
		connectionPoints = false ;//关闭点的线程
	}

	private void startpointsView(final String mess_dialog) {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("注意");// 设置对话框的标题
		ab.setMessage(mess_dialog + "......");
		mConnectDialog = ab.create();
		mConnectDialog.show();

		
		
		new Thread() {
			public void run() {
				while (connectionPoints) {
					String points = getPoints(pointCounts);

					pointCounts++;
					final String  mess = mess_dialog + points;
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mConnectDialog.setMessage(mess);// 更新ui
						}
					});
					
					SystemClock.sleep(500);
				}
			};
		}.start();
	}

	private void initEvent() {
		// 初始化重新扫描的事件
		mBt_rescan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PrintLog.print("重新扫描 ");
				// 先播放关闭的动画
				closeResultAnimator();
				// 动画结束 继续扫描
			}
		});

		mMAnimatorsClose.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				// 动画结束 继续扫描
				startScan();
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		});
		mAnimatorsOpen.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				mBt_rescan.setEnabled(false); // 动画开始禁用按钮

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				mBt_rescan.setEnabled(true);// 动画结束才可以使用

			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case BEGINSCAN:
				// 开始扫描 ，处理ui的显示与否
				mLl_scanprogress.setVisibility(View.VISIBLE);
				mLl_scanresultafterAnimator.setVisibility(View.GONE);
				mLl_scanAnimatorResult.setVisibility(View.GONE);

				// 清空扫描结果信息
				mLl_scanresult.removeAllViews();
				break;
			case SCANNING:
				// 正在扫描

				// 处理消息
				ScanInfo info = (ScanInfo) msg.obj;

				// 设置进度条的显示

				mPb_scanprogress
						.setProgress((int) (info.currentProgress * 100.0 / info.totalProgress));

				// 进度条下面文字的显示
				mTv_scaninfo.setText("正在扫描:" + info.appName);

				// 扫描结果
				View view = View.inflate(getApplicationContext(),
						R.layout.item_antivirus_ll, null);

				ImageView iv_icon = (ImageView) view
						.findViewById(R.id.iv_item_antivirus_ll_icon);
				ImageView iv_isVirus = (ImageView) view
						.findViewById(R.id.iv_item_antivirus_ll_isVirus);
				TextView tv_appName = (TextView) view
						.findViewById(R.id.tv_item_antivirus_ll_name);

				iv_icon.setImageDrawable(info.icon);
				tv_appName.setText(info.appName);

				if (info.isVirus) {
					iv_isVirus.setImageResource(R.drawable.admin_inactivated);
				} else {
					iv_isVirus.setImageResource(R.drawable.admin_activated);
				}

				// 添加到LinearLayout中

				mLl_scanresult.addView(view, 0);// 每次添加到0位置,后面view 依次往后瞬移

				break;
			case FINISH:
				// 扫描完成
				// 1. 对扫描完成的ui拍照
				// a,给mLl_scanprogress拍照

				Bitmap progressBitmap = getProgressBitmap();

				// b.生成两张图片（左，右）
				Bitmap leftBitmap = getLeftBitmap(progressBitmap);
				Bitmap rightBitmap = getRightBitmap(progressBitmap);
				// 设置左边的图片
				mIv_left.setImageBitmap(leftBitmap);
				mIv_right.setImageBitmap(rightBitmap);

				// 2. 设置显示和隐藏
				mLl_scanprogress.setVisibility(View.GONE);
				mLl_scanAnimatorResult.setVisibility(View.VISIBLE);
				mLl_scanresultafterAnimator.setVisibility(View.VISIBLE);
				if (!isAnimatorInit) {
					initOpenResultAnimtor();// 初始化动画 只初始化一次
					initCloseResultAnimtor();// 初始化关闭的动画
					initEvent();// 事件只初始化一次
					isAnimatorInit = true;
				}
				openResultAnimator();

				// 3. 设置结果的文字显示
				boolean is_Virus = (Boolean) msg.obj;
				if (is_Virus) {
					mTv_isvirusMessage.setTextColor(Color.RED);
					mTv_isvirusMessage.setText("您中毒很深，请看下面结果清理");
				} else {
					mTv_isvirusMessage.setTextColor(Color.WHITE);
					mTv_isvirusMessage.setText("您很健康，黑马保护放心使用");
				}

				break;
			default:
				break;
			}

		};
	};
	private LinearLayout mLl_scanresultafterAnimator;
	private LinearLayout mLl_scanAnimatorResult;
	private LinearLayout mLl_scanprogress;
	private TextView mTv_isvirusMessage;
	private Button mBt_rescan;
	private ImageView mIv_left;
	private ImageView mIv_right;
	private AnimatorSet mAnimatorsOpen;
	private AnimatorSet mMAnimatorsClose;
	private boolean mIsScanning;
	private AlertDialog mConnectDialog;

	private void startScan() {
		mIsScanning = true;// 开始扫描
		// 耗时
		new Thread() {
			public void run() {

				// 发送开始扫描的信息
				mHandler.obtainMessage(BEGINSCAN).sendToTarget();

				// 开始扫描
				// 获取所有安装的App
				List<AppBean> allInstalledAppInfos = AppUtils
						.getAllInstalledAppInfos(getApplicationContext());
				int progress = 0;
				int totalProgress = allInstalledAppInfos.size();
				boolean result_isVirus = false;// 记录扫描完成，是否有病毒
				for (AppBean bean : allInstalledAppInfos) {
					if (!mIsScanning) { // 扫描过程中中断处理
						return;
					}
					String md5 = MD5Utils.getFile(bean.getSourceDir());
					boolean isVirus = AntiVirusDao.isVirus(md5);
					if (isVirus) {
						// 只要有病毒 记录有病毒
						result_isVirus = true;
					}
					// 信息数据
					ScanInfo info = new ScanInfo();
					info.isVirus = isVirus;
					info.icon = bean.getIcon();
					info.appName = bean.getAppName();

					progress++;
					info.currentProgress = progress;
					info.totalProgress = totalProgress;

					// 发送进度的消息
					Message message = mHandler.obtainMessage(SCANNING);
					message.obj = info;

					mHandler.sendMessage(message);

					SystemClock.sleep(100);
				}

				// 扫描完毕
				Message message = mHandler.obtainMessage(FINISH);
				message.obj = result_isVirus;
				mHandler.sendMessage(message);

			};
		}.start();

	}

	@Override
	protected void onDestroy() {
		mIsScanning = false;
		super.onDestroy();
	}

	private void initCloseResultAnimtor() {
		// 经过测量 才能获取 0 随意 布局属性取测（wrapcontent fillparrent）
		mLl_scanAnimatorResult.measure(0, 0);
		int width = mLl_scanAnimatorResult.getMeasuredWidth();
		PrintLog.print(width + "测量");
		// 1. 左边image 位移动画 0 ll_
		// mIv_left.setTranslationX(translationX);
		// 第二个参数 标准的javabean赋值 setName 属性名： 把set后面的字母全部取出 ，并且把第一个字母改成小写 如：name
		ObjectAnimator lm_tranAnimator = ObjectAnimator.ofFloat(mIv_left,
				"translationX", -width / 2, 0f);// 从0 到-width/2值之间变化组成的动画

		// 2. 左边image 渐变的动画
		// mIv_left.setAlpha(float);
		ObjectAnimator lm_alphaAnimator = ObjectAnimator.ofFloat(mIv_left,
				"alpha", 0f, 1.0f);

		// 3. 右边image 位移动画
		ObjectAnimator rm_tranAnimator = ObjectAnimator.ofFloat(mIv_right,
				"translationX", width / 2, 0f);

		// 4. 右边image 渐变
		ObjectAnimator rm_alphaAnimator = ObjectAnimator.ofFloat(mIv_right,
				"alpha", 0, 1.0f);

		// 5.结果显示动画 渐变 0 1.0
		ObjectAnimator result_alphaAnimator = ObjectAnimator.ofFloat(
				mLl_scanresultafterAnimator, "alpha", 1.0f, 0);

		// 同时播放

		mMAnimatorsClose = new AnimatorSet();

		mMAnimatorsClose.playTogether(lm_tranAnimator, lm_alphaAnimator,
				rm_tranAnimator, rm_alphaAnimator, result_alphaAnimator);
		mMAnimatorsClose.setDuration(1000);// 设置动画时间
	}

	private void initOpenResultAnimtor() {
		// 经过测量 才能获取 0 随意 布局属性取测（wrapcontent fillparrent）
		mLl_scanAnimatorResult.measure(0, 0);
		int width = mLl_scanAnimatorResult.getMeasuredWidth();
		PrintLog.print(width + "测量");
		// 1. 左边image 位移动画 0 ll_
		// mIv_left.setTranslationX(translationX);
		// 第二个参数 标准的javabean赋值 setName 属性名： 把set后面的字母全部取出 ，并且把第一个字母改成小写 如：name
		ObjectAnimator lm_tranAnimator = ObjectAnimator.ofFloat(mIv_left,
				"translationX", 0f, -width / 2);// 从0 到-width/2值之间变化组成的动画

		// 2. 左边image 渐变的动画
		// mIv_left.setAlpha(float);
		ObjectAnimator lm_alphaAnimator = ObjectAnimator.ofFloat(mIv_left,
				"alpha", 1.0f, 0f);

		// 3. 右边image 位移动画
		ObjectAnimator rm_tranAnimator = ObjectAnimator.ofFloat(mIv_right,
				"translationX", 0f, width / 2);

		// 4. 右边image 渐变
		ObjectAnimator rm_alphaAnimator = ObjectAnimator.ofFloat(mIv_right,
				"alpha", 1.0f, 0);

		// 5.结果显示动画 渐变 0 1.0
		ObjectAnimator result_alphaAnimator = ObjectAnimator.ofFloat(
				mLl_scanresultafterAnimator, "alpha", 0, 1.0f);

		// 同时播放

		mAnimatorsOpen = new AnimatorSet();

		mAnimatorsOpen.playTogether(lm_tranAnimator, lm_alphaAnimator,
				rm_tranAnimator, rm_alphaAnimator, result_alphaAnimator);
		mAnimatorsOpen.setDuration(1000);// 设置动画时间
	}

	/**
	 * 打开结果的属性动画
	 */
	private void openResultAnimator() {

		mAnimatorsOpen.start();// 开始

	}

	private void closeResultAnimator() {

		mMAnimatorsClose.start();// 开始

	}

	/**
	 * 获取图片的右半
	 * 
	 * @param progressBitmap
	 * @return
	 */
	protected Bitmap getRightBitmap(Bitmap progressBitmap) {
		// TODO Auto-generated method stub
		// 绘制图片
		// 画纸（照片）
		Bitmap leftBitmap = Bitmap.createBitmap(progressBitmap.getWidth() / 2,
				progressBitmap.getHeight(), progressBitmap.getConfig());
		// 画板
		Canvas canvas = new Canvas(leftBitmap);

		Matrix matrix = new Matrix();
		matrix.setTranslate(-progressBitmap.getWidth() / 2, 0);
		Paint paint = new Paint();
		canvas.drawBitmap(progressBitmap, matrix, paint);
		return leftBitmap;
	}

	/**
	 * 获取图片的左半
	 * 
	 * @param progressBitmap
	 * @return
	 */
	protected Bitmap getLeftBitmap(Bitmap progressBitmap) {
		// TODO Auto-generated method stub
		// 绘制图片
		// 画纸（照片）
		Bitmap leftBitmap = Bitmap.createBitmap(progressBitmap.getWidth() / 2,
				progressBitmap.getHeight(), progressBitmap.getConfig());
		// 画板
		Canvas canvas = new Canvas(leftBitmap);

		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		canvas.drawBitmap(progressBitmap, matrix, paint);
		return leftBitmap;
	}

	/**
	 * 把一个布局拍成一张照片
	 * 
	 * @param ll_scanprogress
	 * @return
	 */
	protected Bitmap getProgressBitmap() {
		mLl_scanprogress.setDrawingCacheEnabled(true);// true 才可以拍照
														// .getDrawingCache()才有图片
		// 设置图片的质量优
		mLl_scanprogress
				.setDrawingCacheQuality(LinearLayout.DRAWING_CACHE_QUALITY_HIGH);

		Bitmap drawingCache = mLl_scanprogress.getDrawingCache();// 把mLl_scanprogress转换成bitmap
		return drawingCache;
	}

	private class ScanInfo {
		Drawable icon;
		String appName;
		boolean isVirus;
		int currentProgress;
		int totalProgress;
	}

	private void initView() {
		setContentView(R.layout.activity_antivirus);

		// 扫描结果所有app信息的显示
		mLl_scanresult = (LinearLayout) findViewById(R.id.ll_antivirus_results);

		// 扫描结果的布局（ 是否有病毒和重新扫描按钮）
		mLl_scanresultafterAnimator = (LinearLayout) findViewById(R.id.ll_antivirus_scanresult);

		mTv_isvirusMessage = (TextView) findViewById(R.id.tv_antivirus_result);
		mBt_rescan = (Button) findViewById(R.id.bt_antivirus_rescan);

		// 扫描动画的布局
		mLl_scanAnimatorResult = (LinearLayout) findViewById(R.id.ll_antivirus_animatorresult);

		mIv_left = (ImageView) findViewById(R.id.iv_antivirus_leftimage);
		mIv_right = (ImageView) findViewById(R.id.iv_antivirus_rightimage);

		// 扫描进度的布局
		mLl_scanprogress = (LinearLayout) findViewById(R.id.ll_antivirus_scanProgress);

		// 圆形进度条
		mPb_scanprogress = (CircleProgress) findViewById(R.id.pb_antivirus_scanprogress);
		// 扫描app信息显示
		mTv_scaninfo = (TextView) findViewById(R.id.tv_antivirus_scanapps);

	}

}
