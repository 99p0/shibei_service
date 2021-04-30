///*
// *  Birdplanet.com Inc.
// *  Copyright (c) 2019-2019 All Rights Reserved.
// */
//
//package com.birdplanet.console.controller;
//
//import com.birdplanet.core.commons.code.ErrorCodes;
//import com.birdplanet.core.foundation.util.ResponseUtils;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//import javax.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: DownloadController
// * @date 2019-07-05 12:29
// */
//@Slf4j
//@RestController
//@RequestMapping("/api")
//public class DownloadController {
//
//  @GetMapping(value = "/download")
//  public ResponseEntity<StreamingResponseBody> download(final HttpServletResponse response) {
//    response.setContentType("application/zip");
//    response.setHeader(
//        "Content-Disposition",
//        "attachment;filename=sample.zip");
//    StreamingResponseBody stream = out -> {
//      final String home = System.getProperty("user.home");
//      final File directory =
//          new File(home + File.separator + "Documents" + File.separator + "sample");
//      final ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
//      if (directory.exists() && directory.isDirectory()) {
//        try {
//          for (final File file : directory.listFiles()) {
//            final InputStream inputStream = new FileInputStream(file);
//            final ZipEntry zipEntry = new ZipEntry(file.getName());
//            zipOut.putNextEntry(zipEntry);
//            byte[] bytes = new byte[1024];
//            int length;
//            while ((length = inputStream.read(bytes)) >= 0) {
//              zipOut.write(bytes, 0, length);
//            }
//            inputStream.close();
//          }
//          zipOut.close();
//        } catch (final IOException e) {
//          log.error("Exception while reading and streaming data {} ", e);
//        }
//      } else {
//        log.error("directory is not exists or not directory.");
//        ResponseUtils.output(response, ErrorCodes.err);
//      }
//    };
//
//    log.info("steaming response {} ", stream);
//    return new ResponseEntity(stream, HttpStatus.OK);
//  }
//}
