package com.example.hello.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * 二维码生成工具类
 */
public class QrCodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(QrCodeUtil.class);

    /**
     * 默认宽度
     */
    private static final int DEFAULT_WIDTH = 300;

    /**
     * 默认高度
     */
    private static final int DEFAULT_HEIGHT = 300;

    /**
     * 生成二维码Base64编码字符串
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @return Base64编码的二维码图片
     */
    public static String generateQrCodeBase64(String content, int width, int height) {
        try {
            // 生成BitMatrix
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);

            // 转换为PNG图片字节
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

            // 转换为Base64
            byte[] imageBytes = out.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64Image;
        } catch (WriterException | IOException e) {
            logger.error("生成二维码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成二维码Base64编码字符串（使用默认尺寸）
     *
     * @param content 二维码内容
     * @return Base64编码的二维码图片
     */
    public static String generateQrCodeBase64(String content) {
        return generateQrCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成二维码字节数组
     *
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @return 二维码PNG图片字节数组
     */
    public static byte[] generateQrCodeBytes(String content, int width, int height) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", out);
            return out.toByteArray();
        } catch (WriterException | IOException e) {
            logger.error("生成二维码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成二维码字节数组（使用默认尺寸）
     *
     * @param content 二维码内容
     * @return 二维码PNG图片字节数组
     */
    public static byte[] generateQrCodeBytes(String content) {
        return generateQrCodeBytes(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
