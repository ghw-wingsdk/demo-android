package com.wa.sdk.demo.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAPlayer;
import com.wa.sdk.social.model.WAUpdateAchievementResult;

public class GoogleGameActivity extends BaseActivity {

    private ImageView mIvAvatar;
    private TextView mTvName;
    private Button mBtnSignIn;

    private EditText mEtUnlockAchievementId;
    private EditText mEtIncreaseAchievementId;
    private EditText mEtIncreaseSteps;

    private boolean mSignIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_game);

        initView();

        if(!mSignIn) {
            signIn();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_google_game_sign:
                if(mSignIn) {
                    signOut();
                } else {
                    signIn();
                }
                break;
            case R.id.btn_google_game_display_achievements:
                if(WASocialProxy.isGameSignedIn(WAConstants.CHANNEL_GOOGLE)) {
                    int result = WASocialProxy.displayAchievement(this, WAConstants.CHANNEL_GOOGLE, new WACallback<WAResult>() {
                        @Override
                        public void onSuccess(int code, String message, WAResult result) {

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(int code, String message, WAResult result, Throwable throwable) {
                            if(WACallback.CODE_GAME_NEED_SIGN == code) {
                                showSignOutView();
                                mSignIn = false;
                            }
                        }
                    });
                    if(WACallback.CODE_GAME_NEED_SIGN == result) {
                        showShortToast("You need sign in game first");
                    }
                } else {
                    showShortToast("You need sign in game first");
                }
                break;
            case R.id.btn_google_game_unlock_achievements:
                if(WASocialProxy.isGameSignedIn(WAConstants.CHANNEL_GOOGLE)) {
                    unlockAchievement();
                } else {
                    showShortToast("You need sign in game first");
                }
                break;
            case R.id.btn_google_game_increase_achievements:
                if(WASocialProxy.isGameSignedIn(WAConstants.CHANNEL_GOOGLE)) {
                    increaseAchievement();
                } else {
                    showShortToast("You need sign in game first");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void exit() {
        super.exit();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_google_game);
        titleBar.setTitleText(R.string.google_game_service);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);

        mIvAvatar = (ImageView) findViewById(R.id.iv_google_game_avatar);
        mTvName = (TextView) findViewById(R.id.tv_google_game_name);
        mBtnSignIn = (Button) findViewById(R.id.btn_google_game_sign);
        mEtUnlockAchievementId = (EditText) findViewById(R.id.et_google_game_unlock_achievement_id);
        mEtIncreaseAchievementId = (EditText) findViewById(R.id.et_google_game_increase_achievement_id);
        mEtIncreaseSteps = (EditText) findViewById(R.id.et_google_game_increase_num_steps);
        mEtIncreaseAchievementId.setText("CgkIjKzo4o0fEAIQCw");
        mEtIncreaseSteps.setText("1");
    }

    private void signIn() {
        showLoadingDialog("Sign in to Play Game", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(!mSignIn) {
                    showSignOutView();
                }
            }
        });
        WASocialProxy.signInGame(this, WAConstants.CHANNEL_GOOGLE, new WACallback<WAPlayer>() {
            @Override
            public void onSuccess(int code, String message, WAPlayer result) {
                showSignInView(result);
                mSignIn = true;
                dismissLoadingDialog();
            }

            @Override
            public void onCancel() {
                showSignOutView();
                mSignIn = false;
                dismissLoadingDialog();
            }

            @Override
            public void onError(int code, String message, WAPlayer result, Throwable throwable) {
                showSignOutView();
                mSignIn = false;
                dismissLoadingDialog();
            }
        });
    }

    private void signOut() {
        WASocialProxy.signOutGame(WAConstants.CHANNEL_GOOGLE);
        showSignOutView();
        mSignIn = false;
    }

    /**
     * 显示登入后的视图
     */
    private void showSignInView(WAPlayer player) {
        mBtnSignIn.setText(R.string.sign_out);
        String icon = player.getIconImageUrl();
        if(!StringUtil.isEmpty(icon)) {
            Picasso.with(GoogleGameActivity.this)
                    .load(icon)
                    .placeholder(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .resize(100, 100)
                    .into(mIvAvatar);
        }
        mTvName.setText(player.getName());
    }

    /**
     * 显示登出后的视图
     */
    private void showSignOutView() {
        mIvAvatar.setImageResource(R.drawable.ic_launcher);
        mTvName.setText(R.string.need_sign_in);
        mBtnSignIn.setText(R.string.sign_in);
    }

    private void unlockAchievement() {
        String achievementId = mEtUnlockAchievementId.getText().toString().trim();
        if(StringUtil.isEmpty(achievementId)) {
            showShortToast("Achievement id is null");
            return;
        }

        showLoadingDialog("Unlocking achievement", null);
        WASocialProxy.unlockAchievement(WAConstants.CHANNEL_GOOGLE, achievementId, new WACallback<WAUpdateAchievementResult>() {
            @Override
            public void onSuccess(int code, String message, WAUpdateAchievementResult result) {
                dismissLoadingDialog();
                String text = "Unlock achievement success!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + result.getAchievementId();
                showShortToast(text);
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();

            }

            @Override
            public void onError(int code, String message, WAUpdateAchievementResult result, Throwable throwable) {
                dismissLoadingDialog();
                String text = "Unlock achievement failed!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + (null == result ? "" : result.getAchievementId());
                showShortToast(text);
            }
        });
    }

    private void increaseAchievement() {
        String achievementId = mEtIncreaseAchievementId.getText().toString().trim();
        if(StringUtil.isEmpty(achievementId)) {
            showShortToast("Achievement id is null");
            return;
        }

        String stepStr = mEtIncreaseSteps.getText().toString().trim();
        if (StringUtil.isEmpty(stepStr)) {
            showShortToast("Please input steps num");
            return;
        }

        int steps = Integer.parseInt(stepStr);

        showLoadingDialog("Increase achievement", null);
        WASocialProxy.increaseAchievement(WAConstants.CHANNEL_GOOGLE, achievementId, steps, new WACallback<WAUpdateAchievementResult>() {
            @Override
            public void onSuccess(int code, String message, WAUpdateAchievementResult result) {
                dismissLoadingDialog();
                String text = "Increase achievement success!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + result.getAchievementId();
                showShortToast(text);
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();

            }

            @Override
            public void onError(int code, String message, WAUpdateAchievementResult result, Throwable throwable) {
                dismissLoadingDialog();
                String text = "Increase achievement failed!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + (null == result ? "" : result.getAchievementId());
                showShortToast(text);
            }
        });
    }

}
