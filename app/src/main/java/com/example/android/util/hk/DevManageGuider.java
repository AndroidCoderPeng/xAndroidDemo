package com.example.android.util.hk;

import android.util.Log;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import hcnetsdk.jna.HCNetSDKByJNA;
import hcnetsdk.jna.HCNetSDKJNAInstance;

public class DevManageGuider implements Serializable {
    private static final String TAG = "DevManageGuider";

    /**
     * 设备状态对象
     */
    public class DeviceState implements Serializable {
        public int loginState = 0; // 0-offline, 1-online, 2-dropoff
        public int alarmState = 0; // 0-alarmclosed, 1-alarmopen

        public void reset() {
            loginState = 0;
            alarmState = 0;
        }
    }

    public class DevNetInfo implements Serializable {
        public String szIp;
        public String szPort;
        public String szUserName;
        public String szPassword;

        public DevNetInfo(String szIp, String szPort, String szUserName, String szPassWorld) {
            this.szIp = szIp;
            this.szPort = szPort;
            this.szUserName = szUserName;
            this.szPassword = szPassWorld;
        }

        /**
         * 判断IP格式和范围
         */
        public boolean checkIp() {
            if ("".equals(szIp)) {
                return false;
            }
            if (szIp.length() < 7 || szIp.length() > 15) {
                return false;
            }
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            return szIp.matches(regex);
        }

        public boolean checkPort() {
            Pattern pattern = Pattern.compile("[1-9][0-9]*");
            return pattern.matcher(szPort).matches();
        }

        public boolean checkNetInfo() {
            return checkIp() && checkPort() && !szUserName.isEmpty() && !szPassword.isEmpty();
        }
    }

    /**
     * 设备信息对象
     */
    public class DeviceItem implements Serializable {
        public String szUuid;
        public String szDevName;
        public int szUserId = -1;
        public byte loginFlag = -1; // 设备登录方式，0-jni, 1-jna
        public DeviceState devState = new DeviceState();
        public DevNetInfo devNetInfo;
        public HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40 deviceInfoV40_jna;
        public NET_DVR_DEVICEINFO_V30 deviceInfoV30_jni;

        public DeviceItem() {
            szUuid = UUID.randomUUID().toString();
        }

        public DeviceItem(String szUUID) {
            szUuid = szUUID;
        }
    }

    private ExceptionCallBack getExceptionCb() {
        return new ExceptionCallBack() {
            @Override
            public void fExceptionCallBack(int type, int userId, int handle) {
                Log.d(TAG, "fExceptionCallBack: " + type);
                if (type == 0x8044) {
                    getDevByUserID(userId).devState.loginState = 2;
                }
            }
        };
    }

    private ArrayList<DeviceItem> devList = new ArrayList<>();
    private int currSelectDevIndex = -1;

    /**
     * 设置当前选中设备的列表序号.
     */
    public void setCurrSelectDevIndex(int iCurrSelectDevIndex) {
        currSelectDevIndex = iCurrSelectDevIndex;
    }

    /**
     * @return 设备索引号
     * 获取当前选中的设备在设备列表中的索引号.
     */
    public int getCurrSelectDevIndex() {
        return currSelectDevIndex;
    }

    /**
     * @return 设备列表
     * 获取设备列表
     */
    public ArrayList<DeviceItem> getDevList() {
        return devList;
    }

    /**
     * @return [DeviceItem] 设备信息对象
     * 获取当前选中设备的信息对象
     */
    public DeviceItem getCurrSelectDev() {
        if (currSelectDevIndex < 0 || currSelectDevIndex >= devList.size()) {
            return null;
        }
        return devList.get(currSelectDevIndex);
    }

    /**
     * @param lUserID 设备登陆ID
     * @return [DeviceItem] 设备信息对象
     * 使用登陆id查找设备信息对象
     */
    public DeviceItem getDevByUserID(int lUserID) {
        if (lUserID < 0) {
            return null;
        }
        for (DeviceItem item : devList) {
            if (item.szUserId == lUserID) {
                return item;
            }
        }
        return null;
    }

    public void setDevList(ArrayList<DeviceItem> alDevList) {
        this.devList = alDevList;
    }

    /**
     * @param szDevName      设备名称
     * @param struDevNetInfo 设备登陆的网络参数
     * @return 登陆成功返回true, 否则false
     * jna方式登陆设备
     */
    public boolean login_v40_jna(String szDevName, DevNetInfo struDevNetInfo) {
        // 验证参数有效性
        if (!struDevNetInfo.checkIp() || !struDevNetInfo.checkPort() || struDevNetInfo.szUserName.isEmpty() || struDevNetInfo.szPassword.isEmpty()) {
            Log.d(TAG, "login_v40_jna: login_v40_jna failed with error param");
            return false;
        }

        HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO();
        System.arraycopy(struDevNetInfo.szIp.getBytes(), 0, loginInfo.sDeviceAddress, 0, struDevNetInfo.szIp.length());
        System.arraycopy(struDevNetInfo.szUserName.getBytes(), 0, loginInfo.sUserName, 0, struDevNetInfo.szUserName.length());
        System.arraycopy(struDevNetInfo.szPassword.getBytes(), 0, loginInfo.sPassword, 0, struDevNetInfo.szPassword.length());
        loginInfo.wPort = (short) Integer.parseInt(struDevNetInfo.szPort);
        HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40 deviceInfo = new HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40();
        loginInfo.write();
        int lUserID = HCNetSDKJNAInstance.getInstance().NET_DVR_Login_V40(loginInfo.getPointer(), deviceInfo.getPointer());
        Log.d(TAG, "login_v40_jna: " + lUserID);
        if (lUserID < 0) {
            Log.d(TAG, "NET_DVR_Login_V40 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            return false;
        }

        deviceInfo.read();
        DeviceItem devItem = new DeviceItem();
        devItem.loginFlag = 1;
        devItem.szUserId = lUserID;
        if (szDevName.isEmpty()) {
            devItem.szDevName = struDevNetInfo.szIp;
        }
        devItem.devState.loginState = 1;
        devItem.devNetInfo = struDevNetInfo;
        devItem.deviceInfoV40_jna = deviceInfo;
        if (!devList.isEmpty()) {
            devList.clear();
        }
        devList.add(devItem);
        Log.d(TAG, "NET_DVR_Login_V40 succ with:" + lUserID);
        return true;
    }

    /**
     * @param iDevIndex 设备列表中的索引号
     * @return 登陆成功返回true, 否则false
     * 当设备已经添加到设备列表之后，可以通过在列表中的索引号进行登陆
     */
    public boolean login_v40_jna_with_isapi(int iDevIndex) {
        // 验证参数有效性
        if (iDevIndex < 0 || iDevIndex >= devList.size()) {
            Log.d(TAG, "logout_jna failed with error param");
            return false;
        }
        DeviceItem devItem = devList.get(iDevIndex);
        if (devItem.devState.loginState == 1) {
            return true;
        }

        HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO();
        System.arraycopy(devItem.devNetInfo.szIp.getBytes(), 0, loginInfo.sDeviceAddress, 0, devItem.devNetInfo.szIp.length());
        System.arraycopy(devItem.devNetInfo.szUserName.getBytes(), 0, loginInfo.sUserName, 0, devItem.devNetInfo.szUserName.length());
        System.arraycopy(devItem.devNetInfo.szPassword.getBytes(), 0, loginInfo.sPassword, 0, devItem.devNetInfo.szPassword.length());
        loginInfo.wPort = 80;
        loginInfo.byLoginMode = 1; // isapi login
        HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40 deviceInfo = new HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40();
        loginInfo.write();
        int lUserID = HCNetSDKJNAInstance.getInstance().NET_DVR_Login_V40(loginInfo.getPointer(), deviceInfo.getPointer());
        if (lUserID < 0) {
            Log.d(TAG, "NET_DVR_Login_V40 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            return false;
        }

        deviceInfo.read();
        devItem.szUserId = lUserID;
        devItem.devState.loginState = 1;
        devItem.deviceInfoV40_jna = deviceInfo;
        Log.d(TAG, "NET_DVR_Login_V40 succ with:" + lUserID);
        return true;
    }

    /**
     * @param iDevIndex 设备列表中的索引号
     * @return 登陆成功返回true, 否则false
     * 当设备已经添加到设备列表之后，可以通过在列表中的索引号进行登陆
     */
    public boolean login_v40_jna_with_index(int iDevIndex) {
        // 验证参数有效性
        if (iDevIndex < 0 || iDevIndex >= devList.size()) {
            Log.d(TAG, "logout_jna failed with error param");
            return false;
        }
        DeviceItem devItem = devList.get(iDevIndex);
        if (devItem.devState.loginState == 1) {
            return true;
        }

        HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO();
        System.arraycopy(devItem.devNetInfo.szIp.getBytes(), 0, loginInfo.sDeviceAddress, 0, devItem.devNetInfo.szIp.length());
        System.arraycopy(devItem.devNetInfo.szUserName.getBytes(), 0, loginInfo.sUserName, 0, devItem.devNetInfo.szUserName.length());
        System.arraycopy(devItem.devNetInfo.szPassword.getBytes(), 0, loginInfo.sPassword, 0, devItem.devNetInfo.szPassword.length());
        loginInfo.wPort = (short) Integer.parseInt(devItem.devNetInfo.szPort);
        HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40 deviceInfo = new HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40();
        loginInfo.write();
        int lUserID = HCNetSDKJNAInstance.getInstance().NET_DVR_Login_V40(loginInfo.getPointer(), deviceInfo.getPointer());
        if (lUserID < 0) {
            Log.d(TAG, "NET_DVR_Login_V40 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            return false;
        }

        deviceInfo.read();
        devItem.szUserId = lUserID;
        devItem.devState.loginState = 1;
        devItem.deviceInfoV40_jna = deviceInfo;
        Log.d(TAG, "NET_DVR_Login_V40 succ with:" + lUserID);
        return true;
    }

    /**
     * @param iDevIndex 设备索引号
     * @return 注销成功true, 否则false
     * 注销设备
     */
    public boolean logout_jna(int iDevIndex) {
        if (iDevIndex < 0 || iDevIndex >= devList.size()) {
            Log.d(TAG, "logout_jna failed with error param");
            return false;
        }
        DeviceItem devItem = devList.get(iDevIndex);
        boolean ret = HCNetSDKJNAInstance.getInstance().NET_DVR_Logout(devItem.szUserId);
        if (!ret) {
            Log.d(TAG, "NET_DVR_Logout failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
        }
        devItem.devState.reset();
        devItem.deviceInfoV30_jni = null;
        devItem.deviceInfoV40_jna = null;
        return true;
    }

    public boolean login_v30_jni(String szDevName, DevNetInfo struDevNetInfo) {
        if (!struDevNetInfo.checkIp() || !struDevNetInfo.checkPort() || struDevNetInfo.szUserName.isEmpty() || struDevNetInfo.szPassword.isEmpty()) {
            System.out.println("login_v40_jna failed with error param");
            return false;
        }

        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        String strIP = struDevNetInfo.szIp;
        int nPort = Integer.parseInt(struDevNetInfo.szPort);
        String strUser = struDevNetInfo.szUserName;
        String strPsd = struDevNetInfo.szPassword;

        int lUserID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
        if (lUserID < 0) {
            Log.d(TAG, "NET_DVR_Login is failed!Err:" + MessageCodeHub.INSTANCE.getErrorCode());
            return false;
        }

        DeviceItem devItem = new DeviceItem();
        devItem.loginFlag = 0;
        devItem.szUserId = lUserID;
        if (szDevName.isEmpty()) {
            devItem.szDevName = struDevNetInfo.szIp;
        } else {
            devItem.szDevName = szDevName;
        }
        devItem.devState.loginState = 1;
        devItem.devNetInfo = struDevNetInfo;
        devItem.deviceInfoV30_jni = m_oNetDvrDeviceInfoV30;
        devList.add(devItem);

        ExceptionCallBack exceptionCb = getExceptionCb();
        if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exceptionCb)) {
            Log.d(TAG, "NET_DVR_SetExceptionCallBack is failed!");
            return false;
        }
        Log.d(TAG, "NET_DVR_Login is Successful!");
        return true;
    }

    public boolean login_v30_jni_with_index(int iDevIndex) {
        // 验证参数有效性
        if (iDevIndex < 0 || iDevIndex >= devList.size()) {
            Log.d(TAG, "logout_jna failed with error param");
            return false;
        }
        DeviceItem devItem = devList.get(iDevIndex);
        if (devItem.devState.loginState == 1) {
            return true;
        }
        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        String strIP = devItem.devNetInfo.szIp;
        int nPort = Integer.parseInt(devItem.devNetInfo.szPort);
        String strUser = devItem.devNetInfo.szUserName;
        String strPsd = devItem.devNetInfo.szPassword;

        int lUserID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
        if (lUserID < 0) {
            Log.d(TAG, "NET_DVR_Login is failed!Err:" + MessageCodeHub.INSTANCE.getErrorCode());
            return false;
        }

        devItem.loginFlag = 0;
        devItem.szUserId = lUserID;
        devItem.devState.loginState = 1;
        devItem.deviceInfoV30_jni = m_oNetDvrDeviceInfoV30;

        ExceptionCallBack exceptionCbf = getExceptionCb();
        if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exceptionCbf)) {
            Log.d(TAG, "NET_DVR_SetExceptionCallBack is failed!");
            return false;
        }
        Log.d(TAG, "NET_DVR_Login is Successful!");
        return true;
    }

    public boolean logout_jni(int iDevIndex) {
        if (iDevIndex < 0 || iDevIndex >= devList.size()) {
            Log.d(TAG, "logout_jna failed with error param");
            return false;
        }
        DeviceItem devItem = devList.get(iDevIndex);
        boolean ret = HCNetSDK.getInstance().NET_DVR_Logout_V30(devItem.szUserId);
        if (!ret) {
            Log.d(TAG, "NET_DVR_Logout failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
        }
        devItem.devState.reset();
        devItem.deviceInfoV30_jni = null;
        devItem.deviceInfoV40_jna = null;
        return true;
    }
}
