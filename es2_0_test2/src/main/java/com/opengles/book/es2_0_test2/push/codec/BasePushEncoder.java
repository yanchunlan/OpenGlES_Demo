package com.opengles.book.es2_0_test2.push.codec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import com.opengles.book.es2_0_test2.eglUtils.EglHelper;
import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;
import com.opengles.book.es2_0_test2.push.AudioRecordUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;

/**
 * author:  ycl
 * date:  2019/1/7 15:01
 * desc:
 * 内部构件EGL，视频编码，音频编码 3个线程执行
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BasePushEncoder {
    private static final String TAG = "BaseMediaEncoder";

    private Surface surface;
    private EGLContext eglContext;

    private int width;
    private int height;

    private MediaCodec videoEncodec;
    private MediaFormat videoFormat;
    private MediaCodec.BufferInfo videoBufferinfo;

    private MediaCodec audioEncodec;
    private MediaFormat audioFormat;
    private MediaCodec.BufferInfo audioBufferinfo;
    private long audioPts = 0; // 计算当前的时间
    private int sampleRate; // 比特率


    private EglThread eglMediaThread;
    private VideoEncodecThread videoEncodecThread;
    private AudioEncodecThread audioEncodecThread;


    private EglSurfaceView.EglRenderer eglRenderer;

    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;
    private int mRenderMode = RENDERMODE_CONTINUOUSLY;

    private OnMediaInfoListener onMediaInfoListener;

    private AudioRecordUtil audioRecordUtil;


    // -------------------------------  init codec start --------------------------------------
    public void initEncodec(EGLContext eglContext,  // egl 数据共享
                            int width, int height, // 视频编码
                            int sampleRate, int channelCount) { // 音频编码
        this.width = width;
        this.height = height;
        this.eglContext = eglContext;

        initVideoEncodec(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        initAudioEncodec(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
        initPCMRecord(sampleRate, channelCount);
    }


    private void initVideoEncodec(String mimeType, int width, int height) {
        try {
            videoBufferinfo = new MediaCodec.BufferInfo();
            videoFormat = MediaFormat.createVideoFormat(mimeType, width, height);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            videoEncodec = MediaCodec.createEncoderByType(mimeType);
            videoEncodec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            surface = videoEncodec.createInputSurface();
        } catch (IOException e) {
            e.printStackTrace();
            videoEncodec = null;
            videoFormat = null;
            videoBufferinfo = null;
        }
    }

    private void initAudioEncodec(String mimeType, int sampleRate, int channelCount) {
        try {
            this.sampleRate = sampleRate;
            audioBufferinfo = new MediaCodec.BufferInfo();
            audioFormat = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096);

            audioEncodec = MediaCodec.createEncoderByType(mimeType);
            audioEncodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
            audioBufferinfo = null;
            audioFormat = null;
            audioEncodec = null;
        }
    }

    private void initPCMRecord(int sampleRate, int channelCount) {
        audioRecordUtil = new AudioRecordUtil(sampleRate, channelCount);
        audioRecordUtil.setOnRecordLisener(new AudioRecordUtil.OnRecordLisener() {
            @Override
            public void recordByte(byte[] audioData, int readSize) {
                if (audioRecordUtil.isStart()) {
                    // 放置录音数据到编码器
                    putPCMData(audioData, readSize);
                }
            }
        });
    }


    public void startRecord() {
        if (surface != null && eglContext != null) {
            audioPts = 0;

            WeakReference weakReference = new WeakReference<BasePushEncoder>(this);
            eglMediaThread = new EglThread(weakReference);
            videoEncodecThread = new VideoEncodecThread(weakReference);
            audioEncodecThread = new AudioEncodecThread(weakReference);
            eglMediaThread.isCreate = true;
            eglMediaThread.isChange = true;
            eglMediaThread.start();
            videoEncodecThread.start();
            audioEncodecThread.start();
        }
    }


    /**
     * 此处同时停止视频的同时停止了音频，就导致了音频实际上可能并不是视频指定的时间，此处如果需要根据精确
     * 就需要自己做音频大小判断，裁剪指定的音频大小再一起合成
     */
    public void stopRecord() {
        if (eglMediaThread != null && videoEncodecThread != null && audioEncodecThread != null) {
            videoEncodecThread.exit();
            audioEncodecThread.exit();
            eglMediaThread.onDestroy();
            videoEncodecThread = null;
            eglMediaThread = null;
            audioEncodecThread = null;
        }
    }

    // -------------------------------  init codec end --------------------------------------


    // -------------------------------  init video start --------------------------------------

    static class VideoEncodecThread extends Thread {
        private WeakReference<BasePushEncoder> encoder;

        private boolean isExit;

        private MediaCodec videoEncodec;
        private MediaCodec.BufferInfo videoBufferInfo;

        byte[] sps;
        byte[] pps;

        private long pts;
        boolean keyFrame;

        public VideoEncodecThread(WeakReference<BasePushEncoder> encoder) {
            this.encoder = encoder;
            videoEncodec = encoder.get().videoEncodec;
            videoBufferInfo = encoder.get().videoBufferinfo;
        }

        @Override
        public void run() {
            super.run();
            pts = 0;
            isExit = false;
            keyFrame = false;

            videoEncodec.start();// 开启编码
            while (true) {
                if (isExit) {
                    videoEncodec.stop();
                    videoEncodec.release();
                    videoEncodec = null;
                    Log.d(TAG, "run: video isExit 录制完成 ");
                    break;
                }
                // 开始编码
                int outputBufferIndex = videoEncodec.dequeueOutputBuffer(videoBufferInfo, 0);
                Log.d(TAG, "run: outputBufferIndex: " + outputBufferIndex);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) { // 开始获取到sps,pps存储在buffer里面

                    Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED");

                    ByteBuffer spsb = videoEncodec.getOutputFormat().getByteBuffer("csd-0");
                    sps = new byte[spsb.remaining()];
                    spsb.get(sps, 0, sps.length);

                    ByteBuffer ppsb = videoEncodec.getOutputFormat().getByteBuffer("csd-1");
                    pps = new byte[spsb.remaining()];
                    ppsb.get(pps, 0, pps.length);

                    Log.d(TAG, "sps: " + byteToHex(sps));
                    Log.d(TAG, "pps: " + byteToHex(pps));

                } else {
                    while (outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = videoEncodec.getOutputBuffers()[outputBufferIndex];
                        outputBuffer.position(videoBufferInfo.offset);
                        outputBuffer.limit(videoBufferInfo.offset + videoBufferInfo.size);

                        // 如果==0,就代表没值，不处理它
                        if (pts == 0) {
                            pts = videoBufferInfo.presentationTimeUs;
                        }
                        Log.d(TAG, "run: video: pts: " + pts + " before  presentationTimeUs: " + videoBufferInfo.presentationTimeUs);
                        videoBufferInfo.presentationTimeUs = videoBufferInfo.presentationTimeUs - pts;
                        Log.d(TAG, "run: video: pts: " + pts + " presentationTimeUs: " + videoBufferInfo.presentationTimeUs);

                        byte[] data = new byte[outputBuffer.remaining()]; // 存储真是的数据
                        outputBuffer.get(data, 0, data.length);
                        Log.d(TAG, "data:" + byteToHex(data));

                        /**
                         *   需要在每次关键帧的时候加入sps,pps这一帧，因为别人可能中途进来需要数据获取需要sps,pps就可以提供，
                         *   不然就只有初始化的时候，第一次才有，那样明显不合逻辑
                         */
                        if (MediaCodec.BUFFER_FLAG_KEY_FRAME == videoBufferInfo.flags) {
                            keyFrame = true;
                            // 关键帧需要返回sps,pps出去
                            if (encoder.get().onMediaInfoListener != null) {
                                encoder.get().onMediaInfoListener.onSPSPPSInfo(sps, pps);
                            }
                        } else {
                            keyFrame = false;
                        }

                        if (encoder.get().onMediaInfoListener != null) {
                            encoder.get().onMediaInfoListener.onVideoInfo(data, keyFrame);
                            encoder.get().onMediaInfoListener.onMediaTime((int) videoBufferInfo.presentationTimeUs / 1000000); // ms
                        }
                        videoEncodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = videoEncodec.dequeueOutputBuffer(videoBufferInfo, 0); // 循环下一个
                    }
                }
            }
        }

        public void exit() {
            isExit = true;
        }

    }


    // -------------------------------  init video end --------------------------------------

    // -------------------------------  init audio start --------------------------------------

    public void putPCMData(byte[] buffer, int size) {
        if (audioEncodecThread != null && !audioEncodecThread.isExit && buffer != null && size > 0) {

            int inputBufferIndex = audioEncodec.dequeueInputBuffer(0);
            if (inputBufferIndex >= 0) {
                ByteBuffer byteBuffer = audioEncodec.getInputBuffers()[inputBufferIndex];
                byteBuffer.clear();
                byteBuffer.put(buffer);
                long pts = getAudioPts(size, sampleRate);
                Log.d(TAG, "putPCMData:  pts: " + pts);
                audioEncodec.queueInputBuffer(inputBufferIndex, 0, size, pts, 0);
            }
        }
    }

    private long getAudioPts(int size, int sampleRate) {
        audioPts += (long) (1.00 * size / (sampleRate * 2 * 2) * 1000000.00);
        // 时间=大小/44100*2通道*byte单位2
        // 总时间，所以后面直接累加时间是累加的，因为此处直接四舍五人了，所以时间精度有丢失，与真实时间是有点误差的
        return audioPts;
    }


    static class AudioEncodecThread extends Thread {
        private WeakReference<BasePushEncoder> encoder;

        private boolean isExit;

        private MediaCodec audioEncodec;
        private MediaCodec.BufferInfo audioBufferInfo;

        private long pts;


        public AudioEncodecThread(WeakReference<BasePushEncoder> encoder) {
            this.encoder = encoder;
            audioEncodec = encoder.get().audioEncodec;
            audioBufferInfo = encoder.get().audioBufferinfo;
        }

        @Override
        public void run() {
            super.run();
            pts = 0;
            isExit = false;

            audioEncodec.start(); // 开启编码
            while (true) {
                if (isExit) {
                    audioEncodec.stop();
                    audioEncodec.release();
                    audioEncodec = null;
                    Log.d(TAG, "run: audio isExit 录制完成 ");
                    break;
                }
                // 开始编码
                //  java.lang.IllegalStateException  主要是因为 audioEncodec.start 未执行
                int outputBufferIndex = audioEncodec.dequeueOutputBuffer(audioBufferInfo, 0);
                Log.d(TAG, "run: outputBufferIndex: " + outputBufferIndex);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) { // 开始合成
                    // 什么都不做
                } else {
                    while (outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = audioEncodec.getOutputBuffers()[outputBufferIndex];
                        outputBuffer.position(audioBufferInfo.offset);
                        outputBuffer.limit(audioBufferInfo.offset + audioBufferInfo.size);

                        if (pts == 0) {
                            pts = audioBufferInfo.presentationTimeUs;
                        }
                        // 发现传入数据的时间，与此处赋值的时间不一致
                        Log.d(TAG, "run: audio: pts: " + pts + " before  presentationTimeUs: " + audioBufferInfo.presentationTimeUs);
                        audioBufferInfo.presentationTimeUs = audioBufferInfo.presentationTimeUs - pts;
                        Log.d(TAG, "run: audio: pts: " + pts + " after presentationTimeUs: " + audioBufferInfo.presentationTimeUs);

                        byte[] data = new byte[outputBuffer.remaining()]; // 存储真是的数据
                        outputBuffer.get(data, 0, data.length);
                        if (encoder.get().onMediaInfoListener != null) {
                            encoder.get().onMediaInfoListener.onAudioInfo(data);
                        }

                        audioEncodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = audioEncodec.dequeueOutputBuffer(audioBufferInfo, 0); // 循环下一个
                    }
                }
            }
        }

        public void exit() {
            isExit = true;
        }
    }


    // -------------------------------  init audio end --------------------------------------


    // -------------------------------- EGL start ---------------------------------------------
    public void setRenderer(EglSurfaceView.EglRenderer renderer) {
        this.eglRenderer = renderer;
    }

    public void setRenderMode(int renderMode) {
        this.mRenderMode = renderMode;
    }

    static class EglThread extends Thread {
        private WeakReference<BasePushEncoder> eglSurfaceViewWeakReference = null;
        private EglHelper eglHelper = null;
        private Object object = null;

        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        private int width;
        private int height;

        EglThread(WeakReference<BasePushEncoder> glSurfaceViewWeakRef) {
            this.eglSurfaceViewWeakReference = glSurfaceViewWeakRef;
            this.width = glSurfaceViewWeakRef.get().width;
            this.height = glSurfaceViewWeakRef.get().height;
        }

        @Override
        public void run() {
            super.run();

            isExit = false;
            isStart = false;
            object = new Object();
            eglHelper = new EglHelper();
            eglHelper.initEgl(eglSurfaceViewWeakReference.get().surface, eglSurfaceViewWeakReference.get().eglContext);

            while (true) {
                if (isExit) {
                    release(); //释放资源
                    break;
                }

                if (isStart) {
                    Log.d(TAG, "run: " + eglSurfaceViewWeakReference.get().mRenderMode);
                    if (eglSurfaceViewWeakReference.get().mRenderMode == RENDERMODE_WHEN_DIRTY) {
                        // 一次就堵塞
                        synchronized (object) {
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    } else if (eglSurfaceViewWeakReference.get().mRenderMode == RENDERMODE_CONTINUOUSLY) {
                        try {
                            Thread.sleep(1000 / 60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw new RuntimeException("mRenderMode is wrong value");
                    }
                }

                onCreate();
                onChange(width, height);
                onDraw();

                isStart = true;
            }
        }

        private void release() {
            if (eglHelper != null) {
                eglHelper.destroyEgl();
                eglHelper = null;
                object = null;
                eglSurfaceViewWeakReference = null;
            }
        }

        public void onCreate() {
            if (isCreate && eglSurfaceViewWeakReference.get().eglRenderer != null) {
                isCreate = false;
                eglSurfaceViewWeakReference.get().eglRenderer.onSurfaceCreated();
            }
        }

        public void onChange(int width, int height) {
            if (isChange && eglSurfaceViewWeakReference.get().eglRenderer != null) {
                isChange = false;
                eglSurfaceViewWeakReference.get().eglRenderer.onSurfaceChanged(width, height);
            }
        }

        public void onDraw() {
            if (eglSurfaceViewWeakReference.get().eglRenderer != null && eglHelper != null) {
                eglSurfaceViewWeakReference.get().eglRenderer.onDrawFrame();

                // 执行一次无效果，不知道为何，此处多执行一次
                if (!isStart) {
                    eglSurfaceViewWeakReference.get().eglRenderer.onDrawFrame();
                }
                eglHelper.swapBuffers();
            }
        }

        public void onDestroy() {
            isExit = true;
            requestRender();
        }

        private void requestRender() {
            if (object != null) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }

        public EGLContext getEglContext() {
            return eglHelper != null ? eglHelper.getEglContext() : null;
        }
    }
    // -------------------------------- EGL end ---------------------------------------------

    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener) {
        this.onMediaInfoListener = onMediaInfoListener;
    }

    public interface OnMediaInfoListener {
        void onMediaTime(int times);

        void onSPSPPSInfo(byte[] sps, byte[] pps);

        void onVideoInfo(byte[] data, boolean keyFrame);

        void onAudioInfo(byte[] data);
    }

    private static String byteToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i]); // 把byte数据强制转换为16进制的string
            if (hex.length() == 1) {
                stringBuilder.append("0" + hex);
            } else {
                stringBuilder.append(hex);
            }
            if (i > 20) {
                break;
            }
        }
        return stringBuilder.toString();
    }
}
