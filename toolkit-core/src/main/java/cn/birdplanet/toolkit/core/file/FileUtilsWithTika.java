//package com.birdplanet.foundation.util.file;
//
//import java.io.File;
//import java.io.IOException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.tika.Tika;
//import org.apache.tika.exception.TikaException;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: FileUtilsWithTika
// * @date 2019/9/22 14:41
// */
//@Slf4j
//public class FileUtilsWithTika {
//
//  private FileUtilsWithTika() {
//  }
//
//  /**
//   * 读取txt内容
//   */
//  public static String readFile2Str(File file) {
//
//    Tika tika = new Tika();
//    String filecontent = null;
//    try {
//      filecontent = tika.parseToString(file);
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (TikaException e) {
//      e.printStackTrace();
//    }
//    return filecontent;
//  }
//}
