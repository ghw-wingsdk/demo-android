package com.wa.sdk.demo;

import android.content.Context;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WALoginResult;

/**
 * 
 */
public class WASdkDemo {

    private Context mAppContext = null;
    private WASharedPrefHelper mDemoSharedPrefHelper = null;

    private boolean mInitialized = false;

    private WALoginResult mLoginAccount = null;

    private static WASdkDemo mInstance = null;

    private WASdkDemo() {

    }

    public static WASdkDemo getInstance() {
        synchronized (WASdkDemo.class) {
            if(null == mInstance) {
                mInstance = new WASdkDemo();
            }
            return mInstance;
        }
    }

    public void initialize(Context context) {
        if(mInitialized) {
            return;
        }
        mAppContext = context.getApplicationContext();
        mDemoSharedPrefHelper = WASharedPrefHelper.newInstance(mAppContext, WADemoConfig.SP_CONFIG_FILE_DEMO);
        mInitialized = true;
    }

    /**
     * 更新登录账号信息
     * @param loginResult
     */
    public void updateLoginAccount(WALoginResult loginResult) {
        this.mLoginAccount = loginResult;
    }

    /**
     * 获取当前登录账号信息
     * @return
     */
    public WALoginResult getLoginAccount() {
        return mLoginAccount;
    }

    /**
     * 是否已经登录
     * @return
     */
    public boolean isLogin() {
        return null != mLoginAccount
                && !StringUtil.isEmpty(mLoginAccount.getUserId())
                && !StringUtil.isEmpty(mLoginAccount.getToken());
    }

    public void logout() {
        mLoginAccount = null;
    }

    /**
     * 获取当前的登录流程类型
     * @return
     */
    public int getLoginFlowType() {
        return mDemoSharedPrefHelper.getInt(WADemoConfig.SP_KEY_LOGIN_FLOW_TYPE, WAConstants.LOGIN_FLOW_TYPE_DEFAULT);
    }

    /**
     * 设置登录流程类型
     * @param flowType
     */
    public void setLoginFlowType(int flowType) {
        if(WAConstants.LOGIN_FLOW_TYPE_DEFAULT == flowType || WAConstants.LOGIN_FLOW_TYPE_REBIND == flowType) {
            mDemoSharedPrefHelper.saveInt(WADemoConfig.SP_KEY_LOGIN_FLOW_TYPE, flowType);
            WAUserProxy.setLoginFlowType(flowType);
        }
    }
}
