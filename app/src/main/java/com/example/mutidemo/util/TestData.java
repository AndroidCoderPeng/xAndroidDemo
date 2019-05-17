package com.example.mutidemo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.mutidemo.bean.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    //获取手机里面的应用信息列表
    public static List<AppInfoBean> getAppInfo(Context mContext) {
        PackageManager mContextPackageManager = mContext.getPackageManager();
        List<AppInfoBean> infoList = new ArrayList<>();
        List<PackageInfo> installedPackages = mContextPackageManager.getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            AppInfoBean infoBean = new AppInfoBean();

            PackageInfo packageInfo = installedPackages.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(mContextPackageManager).toString();
            String packageName = packageInfo.packageName;
            String versionName = packageInfo.versionName;
            Drawable appIcon = packageInfo.applicationInfo.loadIcon(mContextPackageManager);

            infoBean.setAppName(appName);
            infoBean.setPackageName(packageName);
            infoBean.setVersionName(versionName);
            infoBean.setAppIcon(appIcon);
            infoList.add(infoBean);
        }
        return infoList;
    }
}