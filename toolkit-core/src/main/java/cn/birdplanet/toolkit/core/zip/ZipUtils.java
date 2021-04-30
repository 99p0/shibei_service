/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ZipUtils {

  private static final String EXT = ".zip";
  private static final String BASE_DIR = "";
  private static final String PATH = "/";
  private static final int BUFFER = 8192;

  private ZipUtils() {
  }

  /**
   * 压缩的当前父文件夹下
   *
   * @param srcFile 需要压缩的文件 | 文件夹
   * @throws Exception
   */
  public static void compress(File srcFile) throws Exception {
    String name = srcFile.getName();
    String destPath = srcFile.getParent() + name + EXT;
    compress(srcFile, destPath);
  }

  /**
   * 压缩
   *
   * @param srcFile 源路径
   * @param destFile 目标路径
   * @throws Exception
   */
  public static void compress(File srcFile, File destFile) throws Exception {
    // 对输出文件做CRC32校验
    CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(destFile), new CRC32());
    ZipOutputStream zos = new ZipOutputStream(cos);
    compress(srcFile, zos, BASE_DIR);
    zos.flush();
    zos.close();
  }

  /**
   * 压缩文件
   *
   * @throws Exception
   */
  public static void compress(File srcFile, String destPath) throws Exception {
    File destFile = new File(destPath);
    if (!destFile.getParentFile().exists()) {
      destFile.getParentFile().mkdir();
    }
    compress(srcFile, destFile);
  }

  /**
   * 压缩
   *
   * @param srcFile 源路径
   * @param zos ZipOutputStream
   * @param basePath 压缩包内相对路径
   * @throws Exception
   */
  private static void compress(File srcFile, ZipOutputStream zos, String basePath)
      throws Exception {
    if (srcFile.isDirectory()) {
      compressDir(srcFile, zos, basePath);
    } else {
      compressFile(srcFile, zos, basePath);
    }
  }

  /**
   * 压缩
   *
   * @throws Exception
   */
  public static void compress(String srcPath) throws Exception {
    File srcFile = new File(srcPath);
    compress(srcFile);
  }

  /**
   * 文件压缩
   *
   * @param srcPath 源文件路径
   * @param destPath 目标文件路径
   */
  public static void compress(String srcPath, String destPath) throws Exception {
    File srcFile = new File(srcPath);
    compress(srcFile, destPath);
  }

  /**
   * 压缩目录
   *
   * @throws Exception
   */
  private static void compressDir(File dir, ZipOutputStream zos, String basePath) throws Exception {
    File[] files = dir.listFiles();
    if (files.length < 1) {
      ZipEntry entry = new ZipEntry(basePath + dir.getName() + PATH);
      zos.putNextEntry(entry);
      zos.closeEntry();
    }
    for (File file : files) {
      compress(file, zos, basePath + dir.getName() + PATH);
    }
  }

  public static void compressFiles(String destPath, List<File> files) throws Exception {
    File destFile = new File(destPath);
    if (!destFile.exists()) destFile.mkdirs();
    CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(destFile), new CRC32());
    ZipOutputStream zos = new ZipOutputStream(cos);
    for (File file : files) {
      compress(file, zos, destFile.getName() + PATH);
    }
    compress(destFile, zos, BASE_DIR);
    zos.flush();
    zos.close();
  }

  /**
   * 文件压缩
   *
   * @param file 待压缩文件
   * @param zos ZipOutputStream
   * @param dir 压缩文件中的当前路径
   * @throws Exception
   */
  private static void compressFile(File file, ZipOutputStream zos, String dir) throws Exception {
    ZipEntry entry = new ZipEntry(dir + file.getName());
    zos.putNextEntry(entry);
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    int count;
    byte[] data = new byte[BUFFER];
    while ((count = bis.read(data, 0, BUFFER)) != -1) {
      zos.write(data, 0, count);
    }
    bis.close();
    zos.closeEntry();
  }

  /**
   * 实现将多个文件进行压缩，生成指定目录下的指定名字的压缩文件
   * <p>
   *
   * @param filename ：指定生成的压缩文件的名称
   * @param temp_path ：指定生成的压缩文件所存放的目录
   * @param list ：List集合：用于存放多个File（文件）
   */
  public static void compressFiles(String filename, String temp_path, List<File> list) {
    File file = new File(temp_path);
    if (!file.exists()) file.mkdirs();
    File zipFile = new File(temp_path + File.separator + filename);
    InputStream input;
    try {
      ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
      if (file.isDirectory()) {
        for (int i = 0; i < list.size(); ++i) {
          input = new FileInputStream(list.get(i));
          zipOut.putNextEntry(
              new ZipEntry(file.getName() + File.separator + list.get(i).getName()));
          int temp;
          while ((temp = input.read()) != -1) {
            zipOut.write(temp);
          }
          input.close();
        }
      }
      zipOut.close();
    } catch (Exception e) {
      e.getMessage();
    }
  }
}
