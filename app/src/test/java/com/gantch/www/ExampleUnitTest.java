package com.gantch.www;

import android.util.Log;

import com.tuya.smart.android.user.api.IValidateCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test(){
        TuyaHomeSdk.getUserInstance().getValidateCode("86","18581599773", new IValidateCallback() {
            @Override
            public void onSuccess() {
                Log.i("成功", "onSuccess: ");
            }

            @Override
            public void onError(String code, String error) {
                Log.i("errorCode", code);
                Log.i("error", error);
            }
        });
    }
}