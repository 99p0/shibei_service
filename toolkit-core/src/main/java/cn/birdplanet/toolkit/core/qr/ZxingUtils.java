//package cn.birdplanet.toolkit.core.qr;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.EncodeHintType;
//import com.google.zxing.MultiFormatWriter;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
//import lombok.extern.slf4j.Slf4j;
//
//import java.awt.image.BufferedImage;
//import java.util.Hashtable;
//
//@Slf4j
//public final class ZxingUtils {
//
//  private ZxingUtils() {
//  }
//
//  /**
//   * 生成二维码
//   *
//   * @param content 二维码内容
//   * @param width 宽度
//   * @param height 高度
//   * @return BufferedImage
//   */
//  public static BufferedImage build(String content, int width, int height) {
//    MultiFormatWriter mfw = new MultiFormatWriter();
//    Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
//    // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%） 默认L
//    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//    // 内容所使用字符集编码
//    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//    // 设置二维码边的空度，非负数 默认是4
//    hints.put(EncodeHintType.MARGIN, 0);
//    try {
//      BitMatrix bitMatrix = mfw.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
//      return MatrixToImageWriter.toBufferedImage(bitMatrix);
//    } catch (WriterException e) {
//      log.error("生成二维码 >>> content > {}, ex > {}", content, e);
//      return null;
//    }
//  }
//}
