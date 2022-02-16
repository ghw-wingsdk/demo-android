package com.wa.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wa.sdk.csc.WACscProxy;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.wa.core.WASdkOnlineParameter;
import com.wa.sdk.wa.core.model.WAParameterResult;

import net.aihelp.init.AIHelpSupport;

import java.util.HashMap;


/**
 * 测试客服系統
 * 按照說明對應著來
 */
public class CscActivity extends BaseActivity {

    private TitleBar mTitlebar;
    private String faqId = "";
    private String sectionPublishId = "";
    private EditText mEtFaq;
    private EditText mEtSection;
    private Button mBtnSwitchAiHelpVip;
    private boolean mIsVip = true;
    private TextView mTvAiHelpConfig;
    private int mOpenConfig = 1;
    private String mLanguage = "";
    private boolean enableLog = false;
    private final HashMap<String, Object> customData = new HashMap<>();
    private EditText mEdtCustomKey;
    private EditText mEdtCustomValue;
    private TextView mTvCustomData;
    private TextView mTvLanguage;
    private Button mBtnEnableLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);

        setContentView(R.layout.activity_csc);

        initView();

    }

    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (WACommonProxy.onRequestPermissionsResult(this, requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelLoadingDialog();
    }

    String showConversationFlag = "1";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_elva:
                WACscProxy.showElva(showConversationFlag, customData);
                break;
            case R.id.btn_show_elva_op:
                WACscProxy.showElvaOP(showConversationFlag, customData);
                break;
            case R.id.btn_show_faqs:
                WACscProxy.showFAQs(customData);
                break;
            case R.id.btn_show_conversation:
                WACscProxy.showConversation(customData);
                break;
            case R.id.btn_show_single_faq:
                faqId = mEtFaq.getText().toString().trim();
                if (TextUtils.isEmpty(faqId)) {
                    showShortToast("faqId不能为空");
                } else {
                    WACscProxy.showSingleFAQ(faqId, customData);
                }
                break;
            case R.id.btn_show_faq_section:
                sectionPublishId = mEtSection.getText().toString().trim();
                if (TextUtils.isEmpty(sectionPublishId)) {
                    showShortToast("sectionPublishId不能为空");
                } else {
                    WACscProxy.showFAQSection(sectionPublishId, customData);
                }
                break;
            case R.id.btn_openAiHelp: {
                if (WACscProxy.isOpenAiHelp()) {
                    WACscProxy.openAiHelp(null, mIsVip);
                }
                break;
            }
            case R.id.btn_isOpenAiHelp: {
                String tip = WACscProxy.isOpenAiHelp() ? "已开启" : "未开启";
                showShortToast(tip);
                break;
            }
            case R.id.btn_switchAiHelpVip: {//切换VIP
                switchVip(v);
                break;
            }
            case R.id.btn_switchConfig: {//切换wa后台aihelp配置
                switchOpenConfig();
                break;
            }
            case R.id.btn_add_custom_data: {//添加自定义参数
                addCustomParams();
                break;
            }
            case R.id.btn_reset_custom_data: {//重置自定义参数
                customData.clear();
                mTvCustomData.setText(null);
                break;
            }
            case R.id.btn_switch_language: {//切换语言
                switchLanguage();
                break;
            }
            case R.id.btn_enable_log: {//开关AiHelp日志
                enableLogging();
                break;
            }
            default:
                break;
        }
    }

    private void enableLogging() {
        enableLog = !enableLog;
        AIHelpSupport.enableLogging(enableLog);
        mBtnEnableLog.setText("日志："+(enableLog?"开启":"关闭"));
    }

    private void switchLanguage() {
        if ("zh-CN".equals(mLanguage)) {
            mLanguage = "en";
        } else if ("en".equals(mLanguage)) {
            mLanguage = "pt";
        } else {
            mLanguage = "zh-CN";
        }
        mTvLanguage.setText("当前语言："+mLanguage);
        WACscProxy.setSDKLanguage(mLanguage);
    }

    private void addCustomParams() {
        String key = mEdtCustomKey.getText().toString().trim();
        String value = mEdtCustomValue.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            showShortToast("Key不能为空");
            return;
        }
        customData.put(key, value);
        mTvCustomData.setText(customData.toString());
    }

    private void switchOpenConfig() {
        WAParameterResult waParameterResult = WASdkOnlineParameter.getInstance().getClientParameter();
        waParameterResult.setIsOpenKefu(mOpenConfig);
        //0-关闭；1-启动机器人客服；2-启动人工客服；3-启动运营界面；4-展示全部FAQ菜单；5-展示全部FAQ菜单(无机器人客服)；
        String msg = "AiHelp 本地配置：";
        if (mOpenConfig == 0) {
            msg += "0-关闭";
        } else if (mOpenConfig == 1) {
            msg += "1-启动机器人客服";
        } else if (mOpenConfig == 2) {
            msg += "2-启动人工客服";
        } else if (mOpenConfig == 3) {
            msg += "3-启动运营界面";
        } else if (mOpenConfig == 4) {
            msg += "4-展示全部FAQ菜单";
        } else if (mOpenConfig == 5) {
            msg += "5-展示全部FAQ菜单(无机器人客服)";
        } else {
            msg += (mOpenConfig + "-未识别");
        }
        mTvAiHelpConfig.setText(msg);
        mOpenConfig++;
        if (mOpenConfig > 5)
            mOpenConfig = 0;
    }

    private void switchVip(View view) {
        Button btn = (Button) view;
        if (mIsVip) {
            mIsVip = false;
            btn.setText("VIP:0\n设置VIP");
            showShortToast("您处于非Vip状态");
            showConversationFlag = "0";
        } else {
            mIsVip = true;
            btn.setText("VIP:1\n设置非VIP");
            showShortToast("您处于Vip状态");
            showConversationFlag = "1";
        }
    }

    private void initView() {
        mEtFaq = findViewById(R.id.et_show_single_faq);
        mEtSection = findViewById(R.id.et_show_faq_section);
        mBtnSwitchAiHelpVip = findViewById(R.id.btn_switchAiHelpVip);
        mTitlebar = (TitleBar) findViewById(R.id.tb_csc);
        mTvAiHelpConfig = findViewById(R.id.tv_openAiHelp_config);
        mTitlebar.setTitleText(R.string.csc);
        mEdtCustomKey = findViewById(R.id.edt_custom_key);
        mEdtCustomValue = findViewById(R.id.edt_custom_value);
        mTvCustomData = findViewById(R.id.tv_custom_data);
        mTvLanguage = findViewById(R.id.tv_language);
        mBtnEnableLog = findViewById(R.id.btn_enable_log);
        mTitlebar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        mTitlebar.setTitleTextColor(R.color.color_white);
        mBtnSwitchAiHelpVip.setText("VIP:1\n设置非VIP");
    }

    public void exit() {
        finish();
    }

}
