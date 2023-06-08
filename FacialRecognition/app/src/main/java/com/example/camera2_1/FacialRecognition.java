package com.example.camera2_1;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class FacialRecognition extends AppCompatActivity {
    OneNetAPIOrder oneNetAPIOrder = new OneNetAPIOrder();
    ApiOrderResponse apiOrderResponse = new ApiOrderResponse();
    ApiOrderResponse.Data data = new ApiOrderResponse.Data();
    ApiResponse.ApiData apiData = new ApiResponse.ApiData();
    ApiResponse apiResponse = new ApiResponse();
    FaceRecognitionResponse faceRecognitionResponse = new FaceRecognitionResponse();
    FaceRecognitionResponse.FaceRecognitionData faceRecognitionData = new FaceRecognitionResponse.FaceRecognitionData();
    CameraActivity cameraActivity = new CameraActivity();
    public String filepath = cameraActivity.filePath;

    public int Flag = 1;
    public int accessTokenFlag = 1;
    public int stm32Flag = 0;
    public String Message = "";
    public String name = "";
    public int count_bj=0;//报警标志
    public int rl=0;
    int sendflag=1; //不能随便改


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 在主线程中处理消息
            try {
                System.out.println("单片机这里");
                if (msg.what == 1){
                    String[] stringArray = (String[]) msg.obj;
                    Message = stringArray[0];
                    name =stringArray[1];
                    System.out.println(Message);
                    System.out.println(name);
                    if (Message.equals("success")){
                        //更改单片机表示为允许
                        stm32Flag = 1;
                        rl++;
                    }
                    else if(Message.equals("未匹配到图片")){
                        sendflag=1; //识别成功就发送一次，直到未匹配才不发
                        //人脸识别失败多次报警
                        count_bj++;
                        if (count_bj>2){
                            PostOrder("bj");
                           // Thread.sleep(1000*3);//休眠三秒
                            //   cameraActivity.MyToast("人脸识别失败多次报警");
                            count_bj=0;
                        }

                    }
                    else if(Message.equals("未检测到人脸")){
                        sendflag=1; //识别成功就发送一次，直到未匹配才不发！！！！这个逻辑非常棒 避免了多次发信息
                      // cameraActivity.MyToast("人脸识别失败");
                    }
                }
                if(stm32Flag == 1){  //防止多次发送命令
                    if (sendflag==1 ) {
                        pinyin();
                        PostOrder(name);
                        //Thread.sleep(1000*10);
                        //  cameraActivity.MyToast("人脸识别成功\n欢迎"+name);
                        //编写函数发送数据给单片机
                        rl = 0;
                        sendflag = 0;
                    }
                }

                stm32Flag = 0;
                Flag = 1;
            }catch (Exception e){
                System.out.println("3"+e);
                e.printStackTrace();
            }
            return true;
        }
    });




    public void getData() {
        if (FacialRecognition.this.Flag == 1){
            String[] messageback = new String[2];
            System.out.println("这是标志位为1执行");
            System.out.println(filepath);
            //更改标志位
            FacialRecognition.this.Flag = 0;
            //创建新的线程
            Thread thread = null;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (FacialRecognition.this.accessTokenFlag == 1){
                        String accessToken = getAccessToken();
                        parseJson(accessToken);
                        }
                        String face = getPhoto(apiData.getAccessToken());
                        getJson(face);

                        messageback[0] = faceRecognitionResponse.getMessage();
                        Message message = new Message();
                        message.what = 1;
                        message.obj = messageback;
                        if (Objects.equals(faceRecognitionResponse.getMessage(),"success")){
                            messageback[1] = faceRecognitionData.getName();
                        }else {
                            messageback[1] = faceRecognitionResponse.getMessage();
                        }
                        mHandler.sendMessage(message);
                    } catch (Exception e) {
                        System.out.println("1:" + e);
                        e.printStackTrace();
                        FacialRecognition.this.Flag = 1;
                    }finally {
                        //中断当前线程
                        Thread.currentThread().interrupt();
                    }
                }
            });

            thread.start();

            //thread.interrupt();
        }
        else {
            System.out.println("这是标志位为0不执行");
        }
    }

    //获取api的accessToken
    public String getAccessToken() throws IOException {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
        Request request = new Request.Builder()
                .url("http://ai.heclouds.com:9090/v1/user/app/accessToken?aiKey=72c16741b3d944f59fd39e4500336317&secretKey=8a969e16d520447fb1721132c37dcc20&expireTime=525600")//请求接口。如果需要传参拼接到接口后面。
                .build();
        //创建Request 对象
        Response response = null;
        response = client.newCall(request).execute();//得到Response 对象
        if (response.isSuccessful()) {
            return response.body().string();
        }
        return null;
    }

    //解析api返回的JSON数据，使其可以直接获取字符串形式的accessToken
    private void parseJson(String json) throws JSONException {
        //解析JSON类
        JSONObject jsonObject = new JSONObject(json);
        String stateCode = jsonObject.getString("stateCode");
        String message = jsonObject.getString("message");
        JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
        String accessToken = jsonObject1.getString("accessToken");
        int expireTime = jsonObject1.getInt("expireTime");

        //将解析后的数据绑定到ApiResponse类上
        apiResponse.setStateCode(stateCode);
        apiResponse.setMessage(message);
        apiData.setExpireTime(expireTime);
        apiData.setAccessToken(accessToken);
        apiResponse.setData(apiData);
    }

    //调用人脸搜索api，获取返回的JSON数据
    public String getPhoto(String taken){
        try {
            String path = "http://ai.heclouds.com:9090/v1/aiApi/picture/FACE_RECO_LIB";
            Map<String,Object> params = new HashMap<>();
            String pic = Base64Img.getImageStrFromPath(FacialRecognition.this.filepath);
            String[] value = {pic};
            params.put("param", "1105813901863301120,0.6");
            params.put("picture",value);
            try {
                //调用
                String result = HttpUtil.post(path, taken, params);
                System.out.println("result:" + result);
                return result;
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }catch (Exception e){
            System.out.println("2:" + e);
            e.printStackTrace();
        }
        return null;
    }


    //解析返回的人脸识别的JSON数据
    public  void getJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String stateCode = jsonObject.getString("stateCode");
        String message = jsonObject.getString("message");
        faceRecognitionResponse.setStateCode(stateCode);
        faceRecognitionResponse.setMessage(message);

        if (message.equals("success")) {
            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
            String confidence = jsonObject1.getString("confidence");
            String url = jsonObject1.getString("url");
            String name = jsonObject1.getString("name");
            faceRecognitionData.setConfidence(confidence);
            faceRecognitionData.setUrl(url);
            faceRecognitionData.setName(name);
            faceRecognitionResponse.setData(faceRecognitionData);
        }
    }

    //发送命令API相关：
    public void PostOrder(String ml){
        Thread thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String orderBack = "";
                    orderBack = oneNetAPIOrder.sendOrder(ml);
                    parseOrderJson(orderBack);
                    System.out.println(orderBack);
                    System.out.println(apiOrderResponse.getError());
                }catch (Exception e){
                    System.out.println("1"+e);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void parseOrderJson (String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String errno = jsonObject.getString("errno");
        String error = jsonObject.getString("error");

        apiOrderResponse.SetApiOrderResponse(errno,error);
        if (errno.equals("0")){
            JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
            String cmd_uuid = jsonObject1.getString("cmd_uuid");
            data.setCmd_uuid(cmd_uuid);
            apiOrderResponse.SetApiOrderResponseData(data);
        }
    }

    //转为拼音
    public void pinyin(){
        if(Objects.equals(name, "戴伟")){
            name="dw";
        }
        if(Objects.equals(name, "屈城霖")){
            name="qcl";
        }
        if(Objects.equals(name, "杨树桓")){
            name="ysh";
        }
        if(Objects.equals(name, "刘龙友")){
            name="lly";
        }

    }

}