package com.example.vibrationbra.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.blankj.utilcode.util.ToastUtils;
import com.example.common.base.BaseActivity;
import com.example.common.util.PermissionUtils;
import com.example.vibrationbra.util.BlueUtils;
import com.example.vibrationbra.R;

import androidx.annotation.Nullable;
import io.reactivex.functions.Consumer;

/**
 * @description：蓝牙相关权限检测
 * @author：bux on 2019/9/6 11:59
 * @email: 471025316@qq.com
 */
public abstract class BaseSysBleCheckActivity extends BaseActivity {


    private static final int OPEN_BLUE_REQUEST_CODE = 0X11;
    private OnCheckResult mCheckResult;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_BLUE_REQUEST_CODE
                && resultCode == RESULT_OK) {
            generateLocation(mCheckResult);

        }
    }


    /**
     * 检测蓝牙 是否打开 及定位权限
     */
    protected void checkBlue(OnCheckResult onCheckResult) {
        mCheckResult = onCheckResult;

        if (!BlueUtils.sUtils.isBlueEnable()) {
            ToastUtils.showShort("蓝牙未打开!");
            BlueUtils.sUtils.openBlue(this, OPEN_BLUE_REQUEST_CODE);
        } else {
            generateLocation(onCheckResult);
        }
    }

    private void generateLocation(OnCheckResult onCheckResult) {
        PermissionUtils.checkBlueTooth(this, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    onCheckResult.onSuccess();
                } else {
                    ToastUtils.showShort("扫描蓝牙设备需要定位权限,请打开卫星定位权限!");
                }
            }
        });
    }

    public void showDialog(String title, String message, String cancel, String confirm
            , DialogInterface.OnClickListener cancelListener
            , DialogInterface.OnClickListener confirmListener) {
        //提示断开连接
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialog)
                .setIcon(R.drawable.ic_blue_connected)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(confirm, confirmListener)
                .setNegativeButton(cancel, cancelListener).create();
        alertDialog.show();
    }

    public interface OnCheckResult {
        void onSuccess();
    }
}
