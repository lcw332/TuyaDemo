package com.gantch.www;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.tuya.sdk.bluemesh.bean.SubDevGetDpBean;
import com.tuya.smart.android.device.utils.WiFiUtil;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.builder.ActivatorBuilder;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaActivator;
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener;
import com.tuya.smart.sdk.bean.DeviceBean;
import com.tuya.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ITuyaSmartActivatorListener {

    public EditText username, pwd, code, et_wifi_name, et_wifi_pwd;
    public Button btn_get_code, btn_login, btn_getToken, btn_add_home,btn_open,btn_connect;
    public Button btn_reg;
    private Context mContext;
    private String deviceToken = null;
    protected ITuyaActivator mTuyaActivator; //集成配网具体实现接口
    private SubDevGetDpBean deviceBean;
    //根据设备id初始化设备控制类
    private ITuyaDevice mDevice = TuyaHomeSdk.newDeviceInstance("03027124b4e62d147e8c");
    private Map<String,Object> map=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        btn_open = this.findViewById(R.id.btn_open);
        et_wifi_name = this.findViewById(R.id.et_wifi_name);
        et_wifi_pwd = this.findViewById(R.id.et_wifi_pwd);
        btn_add_home = this.findViewById(R.id.btn_add_home);
        btn_getToken = this.findViewById(R.id.btn_connect_token);
        btn_login = this.findViewById(R.id.btn_login);
        btn_get_code = this.findViewById(R.id.btn_get_code);
        username = this.findViewById(R.id.et_user);
        pwd = this.findViewById(R.id.et_pwd);
        code = this.findViewById(R.id.et_code);
        btn_reg = this.findViewById(R.id.btn_reg);
        btn_connect=this.findViewById(R.id.btn_connect);

        btn_connect.setOnClickListener(this);
        btn_open.setOnClickListener(this);
        btn_add_home.setOnClickListener(this);//添加家庭
        btn_getToken.setOnClickListener(this);//获取设备入网Token
        btn_login.setOnClickListener(this);//账号登录
        btn_get_code.setOnClickListener(this);//获取注册的验证码
        btn_reg.setOnClickListener(this);//注册
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TuyaHomeSdk.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                /**
                 * @param homeId(参考家庭管理章节)
                 * @param callback
                 */
                TuyaHomeSdk.getActivatorInstance().getActivatorToken(7451821, new ITuyaActivatorGetToken() {
                    @Override
                    public void onSuccess(String token) {
                        deviceToken = token;
                        Log.i("设备配网token", deviceToken);
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.i("错误信息", "s：" + s + "s1：" + s1);
                    }
                });

                //初始化配网参数 EZ配网
                /**
                 * @param token: 配网所需要的激活key。
                 * @param ssid: 配网之后，设备工作WiFi的名称。（家庭网络）
                 * @param password: 配网之后，设备工作WiFi的密码。（家庭网络）
                 * @param activatorModel: 现在给设备配网有以下两种方式:
                ActivatorModelEnum.TY_EZ: 传入该参数则进行EZ配网
                ActivatorModelEnum.TY_AP: 传入该参数则进行AP配网
                 * @param timeout: 配网的超时时间设置，默认是100s.
                 * @param context: 需要传入activity的context.
                 */

                //获取当前wifi名
                ConnectivityManager ctm = (ConnectivityManager) this.
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = ctm.getActiveNetworkInfo();
                String ssid = networkInfo.getExtraInfo();
                String replceSsid = ssid.replace("\"", "");

                mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newMultiActivator(new ActivatorBuilder()
                        .setSsid(replceSsid)
                        .setContext(this)
                        .setPassword("ghp18310220135")
                        .setActivatorModel(ActivatorModelEnum.TY_EZ)
                        .setTimeOut(100)
                        .setToken(deviceToken)
                        .setListener(this)
                );
                et_wifi_name.setText(replceSsid);
                mTuyaActivator.start(); //开始配网
                break;
            case R.id.btn_open:
                //设备控制必须先初始化数据，即先调用 TuyaHomeSdk.newHomeInstance(homeId).getHomeDetail(ITuyaHomeResultCallback callback)
                ITuyaHomeResultCallback callback4 = null;
                TuyaHomeSdk.newHomeInstance(7451821).getHomeDetail(callback4);
                map.put("1",true);
                map.put("10",5);
                map.put("11",8);
                final String value = JSONObject.toJSONString(map);
                mDevice.publishDps(value, new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {

                    }

                    @Override
                    public void onSuccess() {
                        Log.i("用户打开开关", "成功");
                    }
                });


                break;

            //注册获取验证码
            case R.id.btn_get_code:
                //注册获取邮箱验证码
                TuyaHomeSdk.getUserInstance().getRegisterEmailValidateCode("86", username.getText().toString(), new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(MainActivity.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            //注册
            case R.id.btn_reg:
                //邮箱密码注册
                TuyaHomeSdk.getUserInstance().registerAccountWithEmail("86", username.getText().toString(), pwd.getText().toString(), code.getText().toString(), new IRegisterCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d("注册成功信息", user.toString());
                        Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(MainActivity.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            //账户登录
            case R.id.btn_login:
                //uid登陆
                TuyaHomeSdk.getUserInstance().loginOrRegisterWithUid("86", username.getText().toString(), pwd.getText().toString(), new ILoginCallback() {
                    @Override
                    public void onSuccess(User user) {
                        String result = user.getSid();
                        Log.d("登录用户信息", result);
                        Toast.makeText(MainActivity.this, "登录成功，用户名：", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(MainActivity.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            //添加家庭
            case R.id.btn_add_home:
                /**
                 *
                 * @param name     家庭名称
                 * @param lon      经度
                 * @param lat      纬度
                 * @param geoName  家庭地理位置名称
                 * @param rooms    房间列表
                 * @param callback
                 */
                List<String> rooms = null;
                rooms = new ArrayList<>();
                rooms.add("testRoom");
                ITuyaHomeResultCallback callback = null;
                TuyaHomeSdk.getHomeManagerInstance().createHome("测试房间", 116.40, 39.90, "beijing", rooms, callback);
                break;
            //获取设备入网Token
            case R.id.btn_connect_token:


                ITuyaGetHomeListCallback callback1 = null;
                TuyaHomeSdk.getHomeManagerInstance().queryHomeList(callback1);



        }
    }

    @Override
    public void onError(String errorCode, String errorMsg) {
        mTuyaActivator.stop(); //停止配网
        mTuyaActivator.onDestroy(); //退出页面销毁一些缓存和监听
        Log.i("配网失败信息", "errorCode: " + errorCode + "errorMsg：" + errorMsg);
    }

    @Override
    public void onActiveSuccess(DeviceBean devResp) {
        Log.i("配网成功", "onActiveSuccess");
    }

    @Override
    public void onStep(String step, Object data) {

    }


}
