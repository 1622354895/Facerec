package com.example.camera2_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity {

    OneNetAPIOrder oneNetAPIOrder = new OneNetAPIOrder();
    ApiOrderResponse apiOrderResponse = new ApiOrderResponse();
    ApiOrderResponse.Data data = new ApiOrderResponse.Data();
    private Button open;
    private Button close;
    private Button tz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        open=(Button) findViewById(R.id.open);
        close=(Button) findViewById(R.id.close);
        tz=(Button) findViewById(R.id.rlsb_tz);

        //开闸门
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //向STM32发送消息
                PostOrder("open");
                Toast.makeText(AdminActivity.this,"向STM32发送开闸消息", Toast.LENGTH_SHORT).show();
            }
        });

        //关闸门
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostOrder("close");
                //向STM32发送消息
                Toast.makeText(AdminActivity.this,"向STM32发送关闸消息", Toast.LENGTH_SHORT).show();
            }
        });

        //跳转回人脸识别
        tz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this,CameraActivity.class);
                startActivity(intent);
                finish();
            }
        });
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


}