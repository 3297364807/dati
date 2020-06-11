package com.example.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.wenzi.AuthService;
import com.example.test.wenzi.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by branch on 2016-5-25.
 * <p>
 * 启动悬浮窗界面
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FloatWindowsService extends Service {
    public static Intent newIntent(Context context, Intent mResultData) {
        Intent intent = new Intent(context, FloatWindowsService.class);
        if (mResultData != null) {
            intent.putExtras(mResultData);
        }
        return intent;
    }

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private static Intent mResultData = null;
    private ImageView jietu, image_tp,choice;
    private WindowManager wm;
    private LinearLayout gaodu;
    private ImageReader mImageReader;
    private GestureDetector mGestureDetector;
    private WindowManager.LayoutParams params;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private List<Tools> list = new ArrayList<>();
    private int height_view;
    private Handler handler = new Handler();
    private View view1;
    private String key = null;
    private RecyclerView ry;

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();//没得问题这个
    }

    public static Intent getResultData() {
        return mResultData;
    }

    public static void setResultData(Intent mResultData) {
        FloatWindowsService.mResultData = mResultData;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {
        wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        //初始化桌面需显示的视图
        View view = LayoutInflater.from(this).inflate(R.layout.xiala_juxing, null, false);
        jietu = view.findViewById(R.id.iv);
        image_tp = view.findViewById(R.id.image);
        gaodu = view.findViewById(R.id.gaodu);
        ry = view.findViewById(R.id.ry);
        choice=view.findViewById(R.id.choice);
        choice.setOnClickListener(v -> {
            if(ry.getVisibility()==View.GONE){
                ry.setVisibility(View.VISIBLE);
                choice.setBackgroundResource(R.drawable.jiantou2);
            }else {
                ry.setVisibility(View.GONE);
                choice.setBackgroundResource(R.drawable.jiantou1);
            }
        });
        //获取layout params对象 设置view的桌面显示的样式
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置视图的宽高
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置透明度
        params.alpha = 5;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        //以屏幕左上角为原点，设置x,y初始值
        params.x = 0;
        params.y = 0;
        mGestureDetector = new GestureDetector(getApplicationContext(), new FloatGestrueTouchListener());
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        wm.addView(view, params);
        jietu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                gaodu.setVisibility(View.GONE);
                return mGestureDetector.onTouchEvent(event);
            }
        });
        gaodu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);
    }


    private class FloatGestrueTouchListener implements GestureDetector.OnGestureListener {
        int lastX, lastY;
        int paramX, paramY;

        @Override
        public boolean onDown(MotionEvent event) {
            lastX = (int) event.getRawX();
            lastY = (int) event.getRawY();
            paramX = params.x;
            paramY = params.y;
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            startScreenShot();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int dx = (int) e2.getRawX() - lastX;
            int dy = (int) e2.getRawY() - lastY;
            params.x = paramX + dx;
            params.y = paramY + dy;
            // 更新悬浮窗位置
//            wm.updateViewLayout(gaodu, params);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    private void startScreenShot() {
        gaodu.setVisibility(View.GONE);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                //start virtual
                startVirtual();
            }
        }, 5);

        handler1.postDelayed(new Runnable() {
            public void run() {
                //capture the screen
                startCapture();
            }
        }, 30);
    }


    private void createImageReader() {

    }

    public void startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    public void setUpMediaProjection() {
        if (mResultData == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        } else {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData);
        }
    }

    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            Log.e(TAG, "1");
            startScreenShot();
        } else {
            Log.e(TAG, "2");
            SaveTask mSaveTask = new SaveTask();
            AsyncTaskCompat.executeParallel(mSaveTask, image);
        }
    }


    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            Image image = params[0];
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            try {
                new Thread(() -> {
                    Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    Log.e(TAG, String.valueOf(height_view));
                    ScreenUtils screenUtils = new ScreenUtils();
                    bitmap = Bitmap.createBitmap(bitmap, 0, screenUtils.getStatusHeight(getApplicationContext()), width, 101);
                    String data = null;
                    data = accurateBasic(bitmap);
                    try {
                        StringBuffer sb=new StringBuffer();
                        JSONObject jsonObject = new JSONObject(data);
                        for(int i=0;i<jsonObject.getJSONArray("words_result").length();i++){
                            String text = jsonObject.getJSONArray("words_result").getJSONObject(i).getString("words");
                            sb.append(text);
                        }
                        if (key != null) {
                            send_request(key, sb.toString());//秘钥题目复制过去
                        } else {
                            key = send();//授权用户
                            send_request(key, sb.toString());//秘钥题目复制过去
                        }
//                        String finalData = text;
//                        handler.post(() -> {
//                            Toast.makeText(FloatWindowsService.this, finalData, Toast.LENGTH_SHORT).show();
//                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    save_file(bitmap);
                }).start();
                image.close();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void send_request(String key, String timu) {
            Log.e(TAG, key );
            Log.e(TAG, timu );
            list.clear();
            JSONObject jsonObject = null;
            try {
                if (key != null) {
                    jsonObject = new JSONObject(key);
                    String api_token = jsonObject.getJSONObject("data").getString("api_token");
                    String user_id = jsonObject.getJSONObject("data").getString("userid");
                    if (timu.length() > 0) {
                        String data = get(api_token, timu);//获取题目
                        JSONObject jsonObject1 = new JSONObject(data);
                        for (int i = 0; i < jsonObject1.getJSONArray("data").length(); i++) {
                            String title = decode(jsonObject1.getJSONArray("data").getJSONObject(i).getString("q"));
                            String info = decode(jsonObject1.getJSONArray("data").getJSONObject(i).getString("a"));
                            list.add(new Tools("题目：" + title, "答案：" + info));
                        }
                    } else {
                        handler.post(() -> {
                            Toast.makeText(FloatWindowsService.this, "请输入题目", Toast.LENGTH_SHORT).show();
                        });
                    }
                    handler.post(() -> {
                        ry.setLayoutManager(new LinearLayoutManager(FloatWindowsService.this));
                        Ry ry1 = new Ry(list);
                        ry.setAdapter(ry1);
//                        dialog.dismiss();
                        Toast.makeText(FloatWindowsService.this, "点击题目即可查看答案", Toast.LENGTH_LONG).show();
                    });
                } else {
                    handler.post(() -> {
                        Toast.makeText(FloatWindowsService.this, "没有拿到题目", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (JSONException e) {
                handler.post(() -> {
//                    dialog.dismiss();
                    Toast.makeText(FloatWindowsService.this, "找不到题目", Toast.LENGTH_SHORT).show();
                });
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //unicode转中文
        public String decode(String unicodeStr) {
            if (unicodeStr == null) {
                return null;
            }
            StringBuffer retBuf = new StringBuffer();
            int maxLoop = unicodeStr.length();
            for (int i = 0; i < maxLoop; i++) {
                if (unicodeStr.charAt(i) == '\\') {
                    if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                        try {
                            retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                            i += 5;
                        } catch (NumberFormatException localNumberFormatException) {
                            retBuf.append(unicodeStr.charAt(i));
                        }
                    else
                        retBuf.append(unicodeStr.charAt(i));
                } else {
                    retBuf.append(unicodeStr.charAt(i));
                }
            }
            return retBuf.toString();
        }

        private String get(String api_token, String s) throws IOException {
            String data = "{\r\n    \"keyword\": \"" + s + "\"\r\n}";
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url("https://app.51xuexiaoyi.com/api/v1/searchQuestion?")
                    .method("POST", body)
                    .addHeader("token", api_token)
                    .addHeader("app-version", "1.0.6")
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        private String send() throws IOException {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"username\": \"17602396448\", \"password\": \"zxytz520\"}");
            Request request = new Request.Builder()
                    .url("https://app.51xuexiaoyi.com/api/v1/login")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        private void save_file(Bitmap bitmap) {
            String galleryPath = Environment.getExternalStorageDirectory()
                    + File.separator + Environment.DIRECTORY_DCIM
                    + File.separator + "Camera" + File.separator;
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(galleryPath, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                //通过io流的方式来压缩保存图片
                boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                Log.e("TAG", "保存图片找不到文件夹");
                e.printStackTrace();
            }
        }

        public byte[] checkFile(Bitmap bitmap) throws URISyntaxException, IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            byte[] bytes = os.toByteArray();
            byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
            return encode;
        }

        public String accurateBasic(Bitmap bitmap) {
            // 请求url
            String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
            try {
                // 本地文件路径
                String encodeStr = new String(checkFile(bitmap));
                String imgParam = URLEncoder.encode(encodeStr, "UTF-8");
                String param = "image=" + imgParam;
                Log.e(TAG, param);
                // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
                AuthService authService = new AuthService();
                String result = HttpUtil.post(url, authService.getAuth(), param);
                System.out.println(result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //预览图片
            if (bitmap != null) {

                ((ScreenCaptureApplication) getApplication()).setmScreenCaptureBitmap(bitmap);
                Log.e("ryze", "获取图片成功");
                startActivity(PreviewPictureActivity.newIntent(getApplicationContext()));
            }
            gaodu.setVisibility(View.VISIBLE);
        }
    }


    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    @Override
    public void onDestroy() {
        // to remove mFloatLayout from windowManager
        super.onDestroy();
        if (gaodu != null) {
            wm.removeView(gaodu);
        }
        stopVirtual();
        tearDownMediaProjection();
    }


}
