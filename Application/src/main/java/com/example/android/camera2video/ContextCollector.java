package com.example.android.camera2video;

import android.hardware.camera2.CameraCharacteristics;
import android.os.AsyncTask;

import java.io.PrintStream;

public class ContextCollector {
    private final CameraCharacteristics mCameraCharacteristics;
    private PrintStream mContextWriter;

    public ContextCollector(PrintStream contextStream, CameraCharacteristics cameraCharacteristics) {
        mContextWriter = contextStream;
        mCameraCharacteristics = cameraCharacteristics;
    }

    private StringBuilder getOSParams() {
        StringBuilder osInfo = new StringBuilder();
        osInfo.append("[os]\n");
        osInfo.append("version=" + android.os.Build.VERSION.RELEASE + "\n");
        osInfo.append("sdk=" + android.os.Build.VERSION.SDK_INT + "\n");
        osInfo.append("model=" + android.os.Build.MODEL + "\n");
        osInfo.append("id=" + android.os.Build.ID + "\n");

        osInfo.append("\n");
        return osInfo;
    }

    private StringBuilder getBaseCameraParams() {
        StringBuilder cameraInfo = new StringBuilder();
        cameraInfo.append("[camera]\n");

        cameraInfo.append("CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES=");
        int[] dsModes = mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
        for (int i = 0; i < dsModes.length; i++) {
            switch(dsModes[i]) {
                case CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_OFF:
                    cameraInfo.append("OFF,");
                    break;
                case CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_ON:
                    cameraInfo.append("ON,");
                    break;
                default:
                    break;
            }
        }
        cameraInfo.append("\n");

        cameraInfo.append("INFO_SUPPORTED_HARDWARE_LEVEL=");
        switch (mCameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)) {
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                cameraInfo.append("LEGACY");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                cameraInfo.append("LIMITED");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                cameraInfo.append("FULL");
                break;
            case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                cameraInfo.append("LEVEL_3");
                break;
            default:
                break;
        }
        cameraInfo.append("\n");

        cameraInfo.append("FLASH_INFO_AVAILABLE=");
        if (mCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
            cameraInfo.append("yes");
        } else {
            cameraInfo.append("no");
        }
        cameraInfo.append("\n");

        cameraInfo.append("LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION=");
        int[] osModes = mCameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
        if (osModes != null) {
            for (int i = 0; i < osModes.length; i++) {
                cameraInfo.append(osModes[i]).append(",");
            }
        } else {
            cameraInfo.append("not_supported");
        }
        cameraInfo.append("\n");

        cameraInfo.append("SENSOR_INFO_TIMESTAMP_SOURCE=");
        switch (mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE)) {
            case CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME:
                cameraInfo.append("REALTIME");
            case CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_UNKNOWN:
                cameraInfo.append("UNKNOWN");
            default:
                break;
        }
        cameraInfo.append("\n");

        cameraInfo.append("\n");
        return cameraInfo;
    }

    public void gatherInfo() {
        new GatherTask().execute(getOSParams(), getBaseCameraParams());
    }

    private class GatherTask extends AsyncTask<StringBuilder, Integer, Integer> {
        @Override
        protected Integer doInBackground(StringBuilder... stringBuilders) {
            int count = stringBuilders.length;
            for (int i = 0; i < count; i++) {
                mContextWriter.append(stringBuilders[i]);
            }
            mContextWriter.close();
            return 1;
        }
    }
}
