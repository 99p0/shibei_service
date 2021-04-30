//package com.birdplanet.foundation.util.file;
//
//import java.io.File;
//import java.io.IOException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.tika.Tika;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: MimeTypeUtils
// * @date 2019/9/22 14:21
// */
//@Slf4j
//public class MimeTypeUtils {
//
//  private MimeTypeUtils() {
//  }
//
//  public static String getMimeType(File file) {
//    if (file.isDirectory()) {
//      throw new RuntimeException("这是个文件夹");
//    }
//    Tika tika = new Tika();
//    String mediaType = null;
//    try {
//      mediaType = tika.detect(file);
//    } catch (IOException e) {
//      return "";
//    }
//    return mediaType;
//  }
//}
