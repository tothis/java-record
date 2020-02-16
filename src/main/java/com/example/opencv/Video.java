package com.example.opencv;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 李磊
 * @datetime 2020/2/16 13:30:25
 * @description opencv video相关 参考项目https://github.com/wade410/javavideo
 */
public class Video {

    public static void camera() {
        try {
            // VideoInputFrameGrabber grabber = VideoInputFrameGrabber.createDefault(0);
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start(); // 开始获取摄像头数据
            CanvasFrame canvas = new CanvasFrame("摄像头"); // 新建一个窗口
            canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            canvas.setAlwaysOnTop(true);
            while (true) {
                if (!canvas.isDisplayable()) { // 窗口是否关闭
                    grabber.stop(); // 停止抓取
                    System.exit(-1); // 退出
                }
                // frame是一帧视频图像
                Frame frame = grabber.grab();
                // 获取摄像头图像并放到窗口上显示
                canvas.showImage(frame);
                Thread.sleep(50); // 50毫秒刷新一次图像
            }
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按帧录制本机摄像头视频 边预览边录制 停止预览即停止录制
     *
     * @param outputFile -录制的文件路径 也可以是rtsp或者rtmp等流媒体服务器发布地址
     * @param frameRate  - 视频帧率
     * @throws Exception
     * @throws InterruptedException
     * @throws FrameRecorder.Exception
     * @author eguid
     */
    public static void recordCamera(String outputFile, double frameRate)
            throws FrameGrabber.Exception, FrameRecorder.Exception, InterruptedException {
        Loader.load(opencv_objdetect.class);
        // 抓取参数
        FrameGrabber grabber = FrameGrabber.createDefault(0); // 本机摄像头默认0 这里使用javacv的抓取器 至于使用的是ffmpeg还是opencv 请自行查看源码
        grabber.start(); // 开启抓取器

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage(); // 转换器
        IplImage grabbedImage = converter.convert(grabber.grab()); // 抓取一帧视频并将其转换为图像 至于用这个图像用来做什么？加水印 人脸识别等等自行添加
        int width = grabbedImage.width();
        int height = grabbedImage.height();
        // 录制参数
        FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264 编码
        recorder.setFormat("flv"); // 封装格式 如果是推送到rtmp就必须是flv封装格式
        recorder.setFrameRate(frameRate);

        recorder.start(); // 开启录制器
        long startTime = 0;
        long videoTS = 0;
        CanvasFrame frame = new CanvasFrame("直播间", CanvasFrame.getDefaultGamma() / grabber.getGamma()); // 这里只是抓取视频
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        Frame rotatedFrame;
        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            rotatedFrame = converter.convert(grabbedImage);
            frame.showImage(rotatedFrame);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            recorder.setTimestamp(videoTS);
            recorder.record(rotatedFrame);
            Thread.sleep(40);
        }
        frame.dispose();
        recorder.stop();
        recorder.release();
        grabber.stop();

    }

    /**
     * 推送/录制本机的音/视频(Webcam/Microphone)到流媒体服务器(Stream media server)
     *
     * @param WEBCAM_DEVICE_INDEX - 视频设备 本机默认是0
     * @param AUDIO_DEVICE_INDEX  - 音频设备 本机默认是4
     * @param outputFile          - 输出文件/地址(可以是本地文件 也可以是流媒体服务器地址)
     * @param captureWidth        - 摄像头宽
     * @param captureHeight       - 摄像头高
     * @param FRAME_RATE          - 视频帧率 最低 25(即每秒25张图片,低于25就会出现闪屏)
     * @throws FrameGrabber.Exception
     */

    public static void recordWebcamAndMicrophone(
            int WEBCAM_DEVICE_INDEX, final int AUDIO_DEVICE_INDEX, String outputFile,
            int captureWidth, int captureHeight, final int FRAME_RATE)
            throws FrameGrabber.Exception {
        long startTime = 0;
        long videoTS;
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
        grabber.setImageWidth(captureWidth);
        grabber.setImageHeight(captureHeight);
        System.out.println("开始抓取摄像头...");
        int isTrue = 0; // 摄像头开启状态
        try {
            grabber.start();
            isTrue += 1;
        } catch (FrameGrabber.Exception e2) {
            if (grabber != null) {
                try {
                    grabber.restart();
                    isTrue += 1;
                } catch (FrameGrabber.Exception e) {
                    isTrue -= 1;
                    try {
                        grabber.stop();
                    } catch (FrameGrabber.Exception e1) {
                        isTrue -= 1;
                    }
                }
            }
        }
        if (isTrue < 0) {
            System.err.println("摄像头首次开启失败 尝试重启也失败");
            return;
        } else if (isTrue < 1) {
            System.err.println("摄像头开启失败");
            return;
        } else if (isTrue == 1) {
            System.err.println("摄像头开启成功");
        } else if (isTrue == 1) {
            System.err.println("摄像头首次开启失败 重新启动成功");
        }

        final FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, captureWidth, captureHeight, 2);
        recorder.setInterleaved(true);
        recorder.setVideoOption("tune", "zerolatency");
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("crf", "25");
        // 2000 kb/s 720P视频的合理比特率范围
        recorder.setVideoBitrate(2000000);
        // h264编/解码器
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        // 封装格式flv
        recorder.setFormat("flv");
        // 视频帧率(保证视频质量的情况下最低25 低于25会出现闪屏)
        recorder.setFrameRate(FRAME_RATE);
        // 关键帧间隔 一般与帧率相同或者是视频帧率的两倍
        recorder.setGopSize(FRAME_RATE * 2);
        // 不可变(固定)音频比特率
        recorder.setAudioOption("crf", "0");
        // 最高质量
        recorder.setAudioQuality(0);
        // 音频比特率
        recorder.setAudioBitrate(192000);
        // 音频采样率
        recorder.setSampleRate(44100);
        // 双通道(立体声)
        recorder.setAudioChannels(2);
        // 音频编/解码器
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        System.out.println("开始录制...");

        try {
            recorder.start();
        } catch (FrameRecorder.Exception e2) {
            if (recorder != null) {
                System.out.println("关闭失败 尝试重启");
                try {
                    recorder.stop();
                    recorder.start();
                } catch (FrameRecorder.Exception e) {
                    try {
                        System.out.println("开启失败 关闭录制");
                        recorder.stop();
                        return;
                    } catch (FrameRecorder.Exception e1) {
                        return;
                    }
                }
            }

        }
        // 音频捕获
        new Thread(() -> {
            AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);

            // 通过AudioSystem获取本地音频混合器信息
            Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
            // 通过AudioSystem获取本地音频混合器
            Mixer mixer = AudioSystem.getMixer(minfoSet[AUDIO_DEVICE_INDEX]);
            // 通过设置好的音频编解码器获取数据线信息
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            try {
                // 打开并开始捕获音频
                // 通过line可以获得更多控制权
                // 获取设备 TargetDataLine line
                // =(TargetDataLine)mixer.getLine(dataLineInfo);
                final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                line.open(audioFormat);
                line.start();
                // 获得当前音频采样率
                final int sampleRate = (int) audioFormat.getSampleRate();
                // 获取当前音频通道数量
                final int numChannels = audioFormat.getChannels();
                // 初始化音频缓冲区(size是音频采样率*通道数)
                int audioBufferSize = sampleRate * numChannels;
                final byte[] audioBytes = new byte[audioBufferSize];

                ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
                exec.scheduleAtFixedRate(() -> {
                    try {
                        // 非阻塞方式读取
                        int nBytesRead = line.read(audioBytes, 0, line.available());
                        // 因为我们设置的是16位音频格式 所以需要将byte[]转成short[]
                        int nSamplesRead = nBytesRead / 2;
                        short[] samples = new short[nSamplesRead];
                        /**
                         * ByteBuffer.wrap(audioBytes)-将byte[]数组包装到缓冲区
                         * ByteBuffer.order(ByteOrder)-按little-endian修改字节顺序 解码器定义的
                         * ByteBuffer.asShortBuffer()-创建一个新的short[]缓冲区
                         * ShortBuffer.get(samples)-将缓冲区里short数据传输到short[]
                         */
                        ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                        // 将short[]包装到ShortBuffer
                        ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
                        // 按通道录制shortBuffer
                        recorder.recordSamples(sampleRate, numChannels, sBuff);
                    } catch (FrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                }, 0, (long) 1000 / FRAME_RATE, TimeUnit.MILLISECONDS);
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            }
        }).start();

        // javaCV提供了优化非常好的硬件加速组件来帮助显示我们抓取的摄像头视频
        CanvasFrame cFrame = new CanvasFrame("Capture Preview", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        Frame capturedFrame = null;
        // 执行抓取 capture 过程
        while ((capturedFrame = grabber.grab()) != null) {
            if (cFrame.isVisible()) {
                // 本机预览要发送的帧
                cFrame.showImage(capturedFrame);
            }
            // 定义我们的开始时间 当开始时需要先初始化时间戳
            if (startTime == 0)
                startTime = System.currentTimeMillis();

            // 创建一个timestamp用来写入帧中
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            // 检查偏移量
            if (videoTS > recorder.getTimestamp()) {
                System.out.println("Lip-flap correction:" + videoTS + ":" + recorder.getTimestamp() + "->"
                        + (videoTS - recorder.getTimestamp()));
                // 告诉录制器写入这个timestamp
                recorder.setTimestamp(videoTS);
            }
            // 发送帧
            try {
                recorder.record(capturedFrame);
            } catch (FrameRecorder.Exception e) {
                System.out.println("录制帧发生异常");
            }
        }

        cFrame.dispose();
        try {
            if (recorder != null) {
                recorder.stop();
            }
        } catch (FrameRecorder.Exception e) {
            System.out.println("关闭录制器失败");
            try {
                if (recorder != null) {
                    grabber.stop();
                }
            } catch (FrameGrabber.Exception e1) {
                System.out.println("关闭摄像头失败");
                return;
            }
        }
        try {
            if (recorder != null) {
                grabber.stop();
            }
        } catch (FrameGrabber.Exception e) {
            System.out.println("关闭摄像头失败");
        }
    }

    public static void main(String[] args) throws Exception {
        recordWebcamAndMicrophone(0, 4, "D:/data/output.mp4", 640, 480, 25);
        // camera();
        // recordCamera("D:/data/output.mp4", 25);
        recordCamera("rtmp://192.168.1.10:8080/live/app", 25);
    }
}