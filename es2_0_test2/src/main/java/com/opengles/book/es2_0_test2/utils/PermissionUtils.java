package com.opengles.book.es2_0_test2.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * author: ycl
 * date: 2018-11-11 23:35
 * desc:
 */
public class PermissionUtils {
    public static void requestPermissions(Activity context, String[] permissions, int reqCode, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 被用户拒绝的权限列表
            List<String> mPermissionList = new ArrayList<>();
            for (String p : permissions) {
                if (ActivityCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(p);
                }
            }
            // 只申请被用户拒绝过了的权限
            if (!mPermissionList.isEmpty()) {
                String[] permission = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(context, permission, reqCode);
            } else {
                runnable.run();
            }
        } else {
            runnable.run();
        }
    }

    public static void onRequestPermissionsResult(boolean isReq, int[] grantResults, Runnable okRun, Runnable deniRun) {
        if (isReq) {
            if (grantResults.length > 0) {
                for (int results : grantResults) {
                    if (results != PackageManager.PERMISSION_GRANTED) {
                        deniRun.run();
                        return;
                    }
                }
                okRun.run();
            } else {
                deniRun.run();
            }
        }
    }

}
