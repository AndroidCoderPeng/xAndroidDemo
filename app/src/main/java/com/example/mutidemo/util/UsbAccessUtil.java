package com.example.mutidemo.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UsbAccessUtil {

    private static final String ACTION_USB_PERMISSION = "com.UARTLoopback.USB_PERMISSION";
    private UsbManager usbmanager;
    private PendingIntent mPermissionIntent;
    private ParcelFileDescriptor filedescriptor = null;
    private FileInputStream inputstream;
    private FileOutputStream outputstream;
    private boolean mPermissionRequestPending = false;

    private byte[] usbdata;
    private byte[] writeusbdata;
    private byte[] readBuffer;
    private int readcount;
    private int totalBytes;
    private int writeIndex;
    private int readIndex;
    private byte status;
    private final int maxnumbytes = 65536;

    private boolean READ_ENABLE = false;
    private boolean accessory_attached = false;

    private SharedPreferences sharedPreferences;
    private Context mContext;

    public UsbAccessUtil(Context context, SharedPreferences sharePrefSettings) {
        super();
        mContext = context;
        sharedPreferences = sharePrefSettings;
        usbdata = new byte[1024];
        writeusbdata = new byte[252];
        readBuffer = new byte[maxnumbytes];

        readIndex = 0;
        writeIndex = 0;
        /***********************USB handling******************************************/

        usbmanager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        context.registerReceiver(mUsbReceiver, filter);

        inputstream = null;
        outputstream = null;
    }

    public void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        byte tmp = 0x00;
        byte baudRate_byte;

        writeusbdata[0] = 0x30;

        switch (baud) {
            case 300:
                baudRate_byte = 0x00;
                break;
            case 600:
                baudRate_byte = 0x01;
                break;
            case 1200:
                baudRate_byte = 0x02;
                break;
            case 2400:
                baudRate_byte = 0x03;
                break;
            case 4800:
                baudRate_byte = 0x04;
                break;
            case 9600:
                baudRate_byte = 0x05;
                break;
            case 19200:
                baudRate_byte = 0x06;
                break;
            case 38400:
                baudRate_byte = 0x07;
                break;
            case 57600:
                baudRate_byte = 0x08;
                break;
            case 115200:
                baudRate_byte = 0x09;
                break;
            case 230400:
                baudRate_byte = 0x0A;
                break;
            case 460800:
                baudRate_byte = 0x0B;
                break;
            case 921600:
                baudRate_byte = 0x0C;
                break;
            default:
                baudRate_byte = 0x05;
                break; // default baudRate "9600"
        }
        writeusbdata[1] = baudRate_byte;

        switch (dataBits) {
            case 5:
                tmp |= 0x00;
                break;  //reserve
            case 6:
                tmp |= 0x01;
                break;  //reserve
            case 7:
                tmp |= 0x02;
                break;
            case 8:
                tmp |= 0x03;
                break;
            default:
                tmp |= 0x03;
                break; // default data bit "8"
        }

        switch (stopBits) {
            case 1:
                tmp &= ~(1 << 2);
                break;
            case 2:
                tmp |= (1 << 2);
                break;
            default:
                tmp &= ~(1 << 2);
                break; // default stop bit "1"
        }

        switch (parity) {
            case 0:
                tmp &= ~((1 << 3) | (1 << 4) | (1 << 5));
                break; //none
            case 1:
                tmp |= (1 << 3);
                break; //odd
            case 2:
                tmp |= ((1 << 3) | (1 << 4));
                break; //event
            case 3:
                tmp |= ((1 << 3) | (1 << 5));
                break; //mark
            case 4:
                tmp |= ((1 << 3) | (1 << 4) | (1 << 5));
                break; //space
            default:
                tmp &= ~((1 << 3) | (1 << 4) | (1 << 5));
                break;//default parity "NONE"
        }

        switch (flowControl) {
            case 0:
                tmp &= ~(1 << 6);
                break;
            case 1:
                tmp |= (1 << 6);
                break;
            default:
                tmp &= ~(1 << 6);
                break;
        }
        writeusbdata[2] = tmp;

        writeusbdata[3] = 0x00;
        writeusbdata[4] = 0x00;

        SendPacket(5);
    }

    public byte SendData(int numBytes, byte[] buffer) {
        int have_send = 0;
        status = 0x00; /*success by default*/

        if (numBytes < 1) {
            return status;
        }

        if (numBytes > 252) {
            numBytes = 252;
        }
        if (numBytes >= 64) {
            int retval = (numBytes / 63);
            for (int i = 0; i < retval; i++) {
                for (int count_x = 0; count_x < 63; count_x++) {
                    writeusbdata[count_x] = buffer[have_send++];
                }
                SendPacket(63);
            }
            if ((numBytes - (retval * 63)) > 0) {
                for (int count_y = 0; count_y < (numBytes - (retval * 63)); count_y++) {
                    writeusbdata[count_y] = buffer[have_send++];
                }
                SendPacket(numBytes - (retval * 63));
            }
        } else {
            System.arraycopy(buffer, 0, writeusbdata, 0, numBytes);
            SendPacket(numBytes);
        }
        return status;
    }

    /*read data*/
    public byte ReadData(int numBytes, byte[] buffer, int[] actualNumBytes) {
        status = 0x00;

        if ((numBytes < 1) || (totalBytes == 0)) {
            actualNumBytes[0] = 0;
            status = 0x01;
            return status;
        }

        /*check for max limit*/
        if (numBytes > totalBytes)
            numBytes = totalBytes;

        totalBytes -= numBytes;
        actualNumBytes[0] = numBytes;

        for (int count = 0; count < numBytes; count++) {
            buffer[count] = readBuffer[readIndex];
            readIndex++;
            readIndex %= maxnumbytes;
        }
        return status;
    }

    private void SendPacket(int numBytes) {
        try {
            if (outputstream != null) {
                outputstream.write(writeusbdata, 0, numBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*resume accessory*/
    public int ResumeAccessory() {
        if (inputstream != null && outputstream != null) {
            return 1;
        }
        UsbAccessory[] accessories = usbmanager.getAccessoryList();
        if (accessories != null) {
            Toast.makeText(mContext, "Accessory Attached", Toast.LENGTH_SHORT).show();
        } else {
            accessory_attached = false;
            return 2;
        }

        UsbAccessory accessory = accessories[0];
        if (accessory != null) {
            if (!accessory.toString().contains(Constant.ManufacturerString)) {
                Toast.makeText(mContext, "Manufacturer is not matched!", Toast.LENGTH_SHORT).show();
                return 1;
            }

            if (!accessory.toString().contains(Constant.ModelString)) {
                Toast.makeText(mContext, "Model is not matched!", Toast.LENGTH_SHORT).show();
                return 1;
            }

            if (!accessory.toString().contains(Constant.VersionString)) {
                Toast.makeText(mContext, "Version is not matched!", Toast.LENGTH_SHORT).show();
                return 1;
            }
            Toast.makeText(mContext, "Manufacturer, Model & Version are matched!", Toast.LENGTH_SHORT).show();

            accessory_attached = true;
            if (usbmanager.hasPermission(accessory)) {
                OpenAccessory(accessory);
            } else {
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        Toast.makeText(mContext, "Request USB Permission", Toast.LENGTH_SHORT).show();
                        usbmanager.requestPermission(accessory, mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        }
        return 0;
    }

    /*destroy accessory*/
    public void DestroyAccessory(boolean bConfiged) {

        if (bConfiged) {
            READ_ENABLE = false;
        } else {
            SetConfig(9600, (byte) 8, (byte) 1, (byte) 0, (byte) 0);
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            READ_ENABLE = false;
            if (accessory_attached) {
                saveDefaultPreference();
            }
        }
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CloseAccessory();
    }

    public void OpenAccessory(UsbAccessory accessory) {
        filedescriptor = usbmanager.openAccessory(accessory);
        if (filedescriptor != null) {
            FileDescriptor fd = filedescriptor.getFileDescriptor();
            inputstream = new FileInputStream(fd);
            outputstream = new FileOutputStream(fd);
            if (inputstream == null) {
                return;
            }
            if (!READ_ENABLE) {
                READ_ENABLE = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (READ_ENABLE) {
                            while (totalBytes > (maxnumbytes - 63)) {
                                try {
                                    Thread.sleep(5);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                if (inputstream != null) {
                                    readcount = inputstream.read(usbdata, 0, 64);
                                    if (readcount > 0) {
                                        for (int count = 0; count < readcount; count++) {
                                            readBuffer[writeIndex] = usbdata[count];
                                            writeIndex++;
                                            writeIndex %= maxnumbytes;
                                        }
                                        if (writeIndex >= readIndex)
                                            totalBytes = writeIndex - readIndex;
                                        else
                                            totalBytes = (maxnumbytes - readIndex) + writeIndex;
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }
    }

    private void CloseAccessory() {
        try {
            if (filedescriptor != null) {
                filedescriptor.close();
            }
            if (inputstream != null) {
                inputstream.close();
            }
            if (outputstream != null) {
                outputstream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        filedescriptor = null;
        inputstream = null;
        outputstream = null;

        System.exit(0);
    }

    public void saveDetachPreference() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString("configed", "FALSE").apply();
        }
    }

    protected void saveDefaultPreference() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString("configed", "TRUE").apply();
            sharedPreferences.edit().putInt("baudRate", 9600).apply();
            sharedPreferences.edit().putInt("stopBit", 1).apply();
            sharedPreferences.edit().putInt("dataBit", 8).apply();
            sharedPreferences.edit().putInt("parity", 0).apply();
            sharedPreferences.edit().putInt("flowControl", 0).apply();
        }
    }

    /***********USB broadcast receiver*******************************************/
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(mContext, "Allow USB Permission", Toast.LENGTH_SHORT).show();
                        OpenAccessory(accessory);
                    } else {
                        Toast.makeText(mContext, "Deny USB Permission", Toast.LENGTH_SHORT).show();
                    }
                    mPermissionRequestPending = false;
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                saveDetachPreference();
                DestroyAccessory(true);
            }
        }
    };
}