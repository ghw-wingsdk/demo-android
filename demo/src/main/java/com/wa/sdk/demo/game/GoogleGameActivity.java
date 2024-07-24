package com.wa.sdk.demo.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAAchievement;
import com.wa.sdk.social.model.WALoadAchievementResult;
import com.wa.sdk.social.model.WAPlayer;
import com.wa.sdk.social.model.WAUpdateAchievementResult;
import com.wa.sdk.wa.common.utils.ImageUtils;

public class GoogleGameActivity extends BaseActivity {

    private ImageView mIvAvatar;
    private TextView mTvName;
    private EditText mEtUnlockAchievementId;
    private EditText mEtStepsAchievementId;
    private EditText mEtSteps;
    private Button mBtnLoadAchievement;
    private TextView mTvDataText;
    private EditText mEtRevealAchievement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_game);

        initView();

        checkSignInGame();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_google_game_sign) {
            signIn();
        } else if (id == R.id.btn_google_game_display_achievements) {
            displayAchievement();
        } else if (id == R.id.btn_google_game_reveal_achievement) {
            revealAchievement();
        } else if (id == R.id.btn_google_game_unlock_achievements) {
            unlockAchievement();
        } else if (id == R.id.btn_google_game_increase_achievements) {
            increaseAchievement();
        } else if (id == R.id.btn_google_game_set_steps_achievements) {
            setStepsAchievement();
        } else if (id == R.id.load_achievement) {
            loadAchievements();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        TitleBar titleBar = findViewById(R.id.tb_google_game);
        titleBar.setTitleText(R.string.google_game_service);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, v -> finish());
        titleBar.setTitleTextColor(R.color.color_white);

        mIvAvatar = findViewById(R.id.iv_google_game_avatar);
        mTvName = findViewById(R.id.tv_google_game_name);
        mEtUnlockAchievementId = findViewById(R.id.et_google_game_unlock_achievement_id);
        mEtRevealAchievement = findViewById(R.id.et_google_game_reveal_achievement);
        mEtStepsAchievementId = findViewById(R.id.et_google_game_steps_achievement_id);
        mEtSteps = findViewById(R.id.et_google_game_num_steps);
        mEtStepsAchievementId.setText("CgkIjKzo4o0fEAIQCw");
        mEtSteps.setText("1");

        mBtnLoadAchievement = findViewById(R.id.load_achievement);
        mTvDataText = findViewById(R.id.tv_achievement_data);
    }

    private void checkSignInGame() {
        WASocialProxy.isGameSignedIn(this, WAConstants.CHANNEL_GOOGLE, new WACallback<WAResult>() {
            @Override
            public void onSuccess(int code, String message, WAResult result) {
                WASocialProxy.getPlayerInfo(GoogleGameActivity.this, WAConstants.CHANNEL_GOOGLE, new WACallback<WAPlayer>() {
                    @Override
                    public void onSuccess(int code, String message, WAPlayer result) {
                        showSignInView(result);
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(int code, String message, WAPlayer result, Throwable throwable) {

                    }
                });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                // 未登录，进行登录
                signIn();
            }
        });
    }

    private void signIn() {
        showLoadingDialog("Sign in to Play Game", false, false, null);
        WASocialProxy.signInGame(this, WAConstants.CHANNEL_GOOGLE, new WACallback<WAPlayer>() {
            @Override
            public void onSuccess(int code, String message, WAPlayer result) {
                showSignInView(result);
                dismissLoadingDialog();
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();
            }

            @Override
            public void onError(int code, String message, WAPlayer result, Throwable throwable) {
                showShortToast(message);
                dismissLoadingDialog();
            }
        });
    }

    /**
     * 显示登入后的视图
     */
    private void showSignInView(WAPlayer player) {
        String icon = player.getIconImageUrl();
        if (!StringUtil.isEmpty(icon)) {
            ImageUtils.loadImage(GoogleGameActivity.this, icon, mIvAvatar, 100, 100, R.drawable.ic_launcher, R.drawable.ic_launcher);
        }
        mTvName.setText(player.getName());
    }

    private void displayAchievement() {
        WASocialProxy.displayAchievement(GoogleGameActivity.this, WAConstants.CHANNEL_GOOGLE, new WACallback<WAResult>() {
            @Override
            public void onSuccess(int code, String message, WAResult result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                handleNeedSign(code);
            }
        });
    }

    private void revealAchievement() {
        String achievementId = mEtRevealAchievement.getText().toString().trim();
        if (StringUtil.isEmpty(achievementId)) {
            showShortToast("Hidden Achievement id is null");
            return;
        }

        showLoadingDialog("Reveal achievement", null);
        WASocialProxy.revealAchievement(GoogleGameActivity.this, WAConstants.CHANNEL_GOOGLE, achievementId, new WACallback<WAUpdateAchievementResult>() {
            @Override
            public void onSuccess(int code, String message, WAUpdateAchievementResult result) {
                dismissLoadingDialog();
                String text = "Reveal achievement success!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + result.getAchievementId();
                Log.d("WASDK", text);
                showShortToast(text);
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();
            }

            @Override
            public void onError(int code, String message, WAUpdateAchievementResult result, Throwable throwable) {
                dismissLoadingDialog();
                message = code == WACallback.CODE_GAME_NEED_SIGN ? "You need sign in game first" : message;

                String text = "Unlock achievement failed!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + (null == result ? achievementId : result.getAchievementId());
                Log.d("WASDK", text);
                showShortToast(text);
                handleNeedSign(code);
            }
        });
    }

    private void unlockAchievement() {
        String achievementId = mEtUnlockAchievementId.getText().toString().trim();
        if (StringUtil.isEmpty(achievementId)) {
            showShortToast("Achievement id is null");
            return;
        }

        showLoadingDialog("Unlocking achievement", null);
        WASocialProxy.unlockAchievement(GoogleGameActivity.this, WAConstants.CHANNEL_GOOGLE, achievementId, new WACallback<WAUpdateAchievementResult>() {
            @Override
            public void onSuccess(int code, String message, WAUpdateAchievementResult result) {
                dismissLoadingDialog();
                String text = "Unlock achievement success!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + result.getAchievementId();
                Log.d("WASDK", text);
                showShortToast(text);
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();

            }

            @Override
            public void onError(int code, String message, WAUpdateAchievementResult result, Throwable throwable) {
                dismissLoadingDialog();
                message = code == WACallback.CODE_GAME_NEED_SIGN ? "You need sign in game first" : message;

                String text = "Unlock achievement failed!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + (null == result ? achievementId : result.getAchievementId());
                Log.d("WASDK", text);
                showShortToast(text);
                handleNeedSign(code);
            }
        });
    }

    private void increaseAchievement() {
        String achievementId = mEtStepsAchievementId.getText().toString().trim();
        if (StringUtil.isEmpty(achievementId)) {
            showShortToast("Achievement id is null");
            return;
        }

        String stepStr = mEtSteps.getText().toString().trim();
        if (StringUtil.isEmpty(stepStr)) {
            showShortToast("Please input steps num");
            return;
        }

        int steps = Integer.parseInt(stepStr);

        showLoadingDialog("Increase achievement", null);
        WASocialProxy.increaseAchievement(GoogleGameActivity.this, WAConstants.CHANNEL_GOOGLE, achievementId, steps, new WACallback<WAUpdateAchievementResult>() {
            @Override
            public void onSuccess(int code, String message, WAUpdateAchievementResult result) {
                dismissLoadingDialog();
                String text = "Increase achievement success!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + result.getAchievementId();
                Log.d("WASDK", text);
                showShortToast(text);
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();

            }

            @Override
            public void onError(int code, String message, WAUpdateAchievementResult result, Throwable throwable) {
                dismissLoadingDialog();
                message = code == WACallback.CODE_GAME_NEED_SIGN ? "You need sign in game first" : message;

                String text = "Increase achievement failed!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + (null == result ? achievementId : result.getAchievementId());
                Log.d("WASDK", text);
                showShortToast(text);
                handleNeedSign(code);
            }
        });
    }

    private void setStepsAchievement() {
        String achievementId = mEtStepsAchievementId.getText().toString().trim();
        if (StringUtil.isEmpty(achievementId)) {
            showShortToast("Achievement id is null");
            return;
        }

        String stepStr = mEtSteps.getText().toString().trim();
        if (StringUtil.isEmpty(stepStr)) {
            showShortToast("Please input steps num");
            return;
        }

        int steps = Integer.parseInt(stepStr);

        showLoadingDialog("Set Steps achievement", null);
        WASocialProxy.setStepsAchievement(GoogleGameActivity.this, WAConstants.CHANNEL_GOOGLE, achievementId, steps, new WACallback<WAUpdateAchievementResult>() {
            @Override
            public void onSuccess(int code, String message, WAUpdateAchievementResult result) {
                dismissLoadingDialog();
                String text = "Set Steps achievement success!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + result.getAchievementId();
                Log.d("WASDK", text);
                showShortToast(text);
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();

            }

            @Override
            public void onError(int code, String message, WAUpdateAchievementResult result, Throwable throwable) {
                dismissLoadingDialog();
                message = code == WACallback.CODE_GAME_NEED_SIGN ? "You need sign in game first" : message;

                String text = "Set Steps achievement failed!"
                        + "code: " + code + "\n"
                        + "message: " + message + "\n"
                        + "achievementId: " + (null == result ? achievementId : result.getAchievementId());
                Log.d("WASDK", text);
                showShortToast(text);
                handleNeedSign(code);
            }
        });
    }

    private void loadAchievements() {
        mBtnLoadAchievement.setEnabled(false);
        mTvDataText.setText("loading...");
        WASocialProxy.loadAchievements(GoogleGameActivity.this, WAConstants.CHANNEL_GOOGLE, true, new WACallback<WALoadAchievementResult>() {
            @Override
            public void onSuccess(int code, String message, WALoadAchievementResult result) {
                LogUtil.e(WAConstants.TAG, "Load achievement onSuccess, achievement array size: " + result.getAchievements().size());
                StringBuilder sb = new StringBuilder();
                for (WAAchievement achievement : result.getAchievements()) {
                    sb.append("Id: ")
                            .append(achievement.getAchievementId()).append("\n")
                            .append("Name: ").append(achievement.getName()).append("\n")
                            .append("Description: ").append(achievement.getDescription()).append("\n")
                            .append("Type: ");

                    switch (achievement.getType()) {
                        case WAAchievement.TYPE_STANDARD:
                            sb.append("Standard");
                            break;
                        case WAAchievement.TYPE_INCREMENTAL:
                            sb.append("Incremental").append("\n")
                                    .append("Step: ").append(achievement.getCurrentSteps());
                            break;
                        default:
                            break;
                    }

                    sb.append("\n")
                            .append("State: ");

                    switch (achievement.getState()) {
                        case WAAchievement.STATE_HIDDEN:
                            sb.append("Hidden");
                            break;
                        case WAAchievement.STATE_REVEALED:
                            sb.append("Revealed");
                            break;
                        case WAAchievement.STATE_UNLOCKED:
                            sb.append("Unlocked");
                            break;
                        default:
                            break;
                    }
                    sb.append("\n-----------------\n");

                    LogUtil.w(WAConstants.TAG, achievement.toString());
                }
                mTvDataText.setText(sb.toString());
                mBtnLoadAchievement.setEnabled(true);
            }

            @Override
            public void onCancel() {
                LogUtil.e(WAConstants.TAG, "Load achievement onCancel");
                mTvDataText.setText("Load achievement canceled");
                mBtnLoadAchievement.setEnabled(true);
            }

            @Override
            public void onError(int code, String message, WALoadAchievementResult result, Throwable throwable) {
                LogUtil.e(WAConstants.TAG, String.format("Load achievement onError: code=%d, message=%s", code, message));
                mTvDataText.setText(String.format("Load achievement error: code=%d, message=%s", code, message));
                mBtnLoadAchievement.setEnabled(true);
                handleNeedSign(code);
            }
        });
    }

    private void handleNeedSign(int code) {
        if (code == WACallback.CODE_GAME_NEED_SIGN) {
            new AlertDialog.Builder(this)
                    .setMessage("You need sign in game first")
                    .setPositiveButton(R.string.sign_in, (dialog, which) -> signIn())
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }
}
