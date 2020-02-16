package com.example.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

public class QRcodeUtil {

    // 图片宽度配置
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int HALF_WIDTH = WIDTH / 2;
    private static final int FRAME_WIDTH = 2;
    private static final String FORMAT_NAME = "png";

    // 二维码写码器
    private static MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

    public static void encode(String content, int width, int height, String imagePath, String destImagePath) {

        int[][] srcPixels = null;
        BitMatrix matrix = null;
        try {
            // 读取源图像
            BufferedImage image = scale(imagePath, WIDTH, HEIGHT, true);
            srcPixels = new int[WIDTH][HEIGHT];
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    srcPixels[i][j] = image.getRGB(i, j);
                }
            }
            // 设置二维码的纠错级别 编码
            Map<EncodeHintType, Object> hint = new HashMap<>();
            hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 生成二维码
            matrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hint);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // 二维矩阵转为一维像素数组
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;
        int[] pixels = new int[width * height];

        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                // 读取图片
                if (x > halfW - HALF_WIDTH && x < halfW + HALF_WIDTH && y > halfH - HALF_WIDTH
                        && y < halfH + HALF_WIDTH) {
                    pixels[y * width + x] = srcPixels[x - halfW + HALF_WIDTH][y - halfH + HALF_WIDTH];
                }
                // 在图片四周形成边框
                else if ((x > halfW - HALF_WIDTH - FRAME_WIDTH && x < halfW - HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - HALF_WIDTH - FRAME_WIDTH && y < halfH + HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW + HALF_WIDTH - FRAME_WIDTH && x < halfW + HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - HALF_WIDTH - FRAME_WIDTH
                        && y < halfH + HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - HALF_WIDTH - FRAME_WIDTH && x < halfW + HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - HALF_WIDTH - FRAME_WIDTH
                        && y < halfH - HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - HALF_WIDTH - FRAME_WIDTH && x < halfW + HALF_WIDTH + FRAME_WIDTH
                        && y > halfH + HALF_WIDTH - FRAME_WIDTH
                        && y < halfH + HALF_WIDTH + FRAME_WIDTH)) {
                    pixels[y * width + x] = 0xfffffff;
                } else {
                    // 此处可以修改二维码的颜色 可以分别制定二维码和背景的颜色
                    pixels[y * width + x] = matrix.get(x, y) ? 0xff000000 : 0xfffffff;
                }
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, pixels);

        try {
            ImageIO.write(image, FORMAT_NAME, new File(destImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把传入的原始图像按高度和宽度进行缩放 生成符合要求的图标
     *
     * @param imagePath  源文件地址
     * @param height     目标高度
     * @param width      目标宽度
     * @param fillerFlag 比例不对时是否需要补白 true为补白 false为不补白
     * @throws IOException
     */
    private static BufferedImage scale(String imagePath, int width, int height, boolean fillerFlag) {
        double ratio; // 缩放比例
        File file = new File(imagePath);
        BufferedImage image1 = null;
        try {
            image1 = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image result = image1.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        // 计算比例
        if ((image1.getHeight() > height) || (image1.getWidth() > width)) {
            if (image1.getHeight() > image1.getWidth()) {
                ratio = (new Integer(height)).doubleValue() / image1.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue() / image1.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            result = op.filter(image1, null);
        }
        if (fillerFlag) { // 补白
            BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphic = image2.createGraphics();
            graphic.setColor(Color.white);
            graphic.fillRect(0, 0, width, height);
            if (width == result.getWidth(null))
                graphic.drawImage(result, 0, (height - result.getHeight(null)) / 2, result.getWidth(null),
                        result.getHeight(null), Color.white, null);
            else
                graphic.drawImage(result, (width - result.getWidth(null)) / 2, 0, result.getWidth(null),
                        result.getHeight(null), Color.white, null);
            graphic.dispose();
            result = image2;
        }
        return (BufferedImage) result;
    }

    public static void encode(String content, int width, int height, String filePath) {
        try {
            MatrixToImageWriter.writeToPath(
                    new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
                    , FORMAT_NAME, FileSystems.getDefault().getPath(filePath)
            );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url = "baidu.com";
        String path = "D:/data/";
        // 将1.png作为logo放在2.png中间 生成扫码图片2.png变成二维码
        QRcodeUtil.encode(url
                , 300, 300
                , path + "1.png", path + "QRcode.png");

        QRcodeUtil.encode(url, 300, 300, path + "test.png");
    }
}