

package com.example.camera2_1;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera2_1.Camera2BasicFragment;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;

public class CameraActivity extends AppCompatActivity {

    private Camera2BasicFragment mCameraFragment;
    private Handler mHandler;
    private FacialRecognition facialRecognition;
    int i =0;
    String filePath="/storage/emulated/0/Android/data/com.example.camera2_1/files/pic.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (savedInstanceState == null) {
            mCameraFragment = Camera2BasicFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mCameraFragment)
                    .commit();
        } else {
            mCameraFragment = (Camera2BasicFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.container);
        }

        // 在 onCreate() 方法中创建 Handler 对象
        mHandler = new Handler();

        // 在 onCreate() 方法中创建 FacialRecognition 对象
        facialRecognition= new FacialRecognition();


        //获取文件保存路径
       // mFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
        File internalStorageDir = getExternalFilesDir(null);
        String fileName = "pic.jpg";
        File file = new File(internalStorageDir, fileName);

        if (file.exists()) {
             filePath = file.getAbsolutePath();
            // 文件已存在，返回它的保存路径
        } else {
            // 文件不存在
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在 onResume() 方法中启动定时拍照
        startAutoCapture();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在 onPause() 方法中停止定时拍照
        stopAutoCapture();
    }

    // 启动定时拍照
    //就是这种加上，置位，实现主线程拍照后调用子线程进行API人脸识别处理
    // 子线程运行结束自动死去，未结束会一直运行且不会影响主线程运行
    //且新创建的子线程会与旧的并发执行
    // 如果不行则用开始的线程方法（不杀死那个）重新试试
    private void startAutoCapture() {

        class MyThread extends Thread {
            private boolean mIsRunning = true;

            public void run() {
                //while (mIsRunning && !isInterrupted()) {  //这里while表示一直执行，并不是执行一次！！！到时候调用qcl的两个都试一试
                    // 执行任务
                    doSomethingElse();
               // }
                // 退出线程执行
                return;
            }

            public void stopRunning() {
                System.out.println("子线程关闭"+i);
                //System.out.println(filePath);
                mIsRunning = false;
                interrupt();
            }
        }

        Runnable captureRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCameraFragment != null) {
                    // 在主线程中调用 takePicture() 方法进行拍照操作
                    mCameraFragment.takePicture();



                    // 创建一个新的线程执行其他操作函数
                    MyThread thread = new MyThread();
                    thread.start();

                    // 定时检查子线程是否执行完毕，利用Handler？
                    mHandler.postDelayed(new Runnable() {//通过Habdler从后台线程发送给前台线程发送给
                        @Override
                        public void run() {
                            if (thread.isAlive()) {
                                // 如果子线程还在执行，就等待下一次检查
                                mHandler.postDelayed(this, 500);
                            } else {
                                // 如果子线程执行完毕，就杀死子线程
                                thread.stopRunning();
                            }
                        }
                    }, 500);
                }
                // 延迟 5 秒后再次调用自身
                mHandler.postDelayed(this, 1000);
            }
        };

// 启动定时拍照
        mHandler.postDelayed(captureRunnable, 1000);
    }

    // 定义一个函数，用于执行其他操作函数
    public void doSomethingElse() {
        i++;
        //Toast.makeText(CameraActivity.this,"正在执行操作", Toast.LENGTH_SHORT).show();  //Toast 消息只能在应用程序的主线程上创建。
        System.out.println("正在执行子线程操作"+i);
        // 在这里执行其他操作函数
        //filepath是文件路径
        //FacialRecognition facialRecognition= new FacialRecognition();
        facialRecognition.getData();

    }

    // 停止定时拍照
    private void stopAutoCapture() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public void MyToast(String flag){
        mCameraFragment.showToast(flag);

    }

}


