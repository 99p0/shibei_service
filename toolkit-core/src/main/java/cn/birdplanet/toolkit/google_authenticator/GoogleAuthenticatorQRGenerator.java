//package cn.birdplanet.toolkit.google_authenticator;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.MultiFormatWriter;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//public class GoogleAuthenticatorQRGenerator {
//
//  public static void createQRCode(String barCodeData, String filePath,
//      int height, int width) throws WriterException, IOException {
//
//    BitMatrix matrix = new MultiFormatWriter().encode(barCodeData,
//        BarcodeFormat.QR_CODE, width, height);
//    try (FileOutputStream out = new FileOutputStream(filePath)) {
//      MatrixToImageWriter.writeToStream(matrix, "png", out);
//    }
//  }
//}
