//package com.birdplanet.service.impl;
//
//import com.birdplanet.service.IExcelService;
//import com.google.common.collect.Lists;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFCellStyle;
//import org.apache.poi.hssf.usermodel.HSSFFont;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.hssf.util.HSSFColor;
//import org.apache.poi.ss.usermodel.FillPatternType;
//import org.apache.poi.ss.usermodel.HorizontalAlignment;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ExcelServiceImpl extends BaseService implements IExcelService {
//
//  /**
//   * 将分析后的数据转换成excel
//   *
//   * @param data 转换后的数据
//   * @return 字节数组
//   * @throws IOException
//   */
//  public static byte[] exportGameModelData(List<?> data) throws IOException {
//    //
//    HSSFWorkbook workbook = new HSSFWorkbook();
//    HSSFSheet sheet = workbook.createSheet("闯关模式-活动周期");
//    HSSFRow row;
//    HSSFCell cell;
//
//    sheet.setDefaultColumnWidth(25);
//    sheet.setDefaultRowHeightInPoints(28);
//
//    HSSFFont font_hd = workbook.createFont();
//    font_hd.setFontName("微软雅黑");
//    font_hd.setFontHeightInPoints((short) 12);
//    font_hd.setBold(false);
//
//    HSSFFont font_txt = workbook.createFont();
//    font_txt.setFontName("微软雅黑");
//    font_txt.setBold(false);
//    font_txt.setFontHeightInPoints((short) 11);
//
//    HSSFCellStyle cs_header, cs_ok;
//
//    // 标题的样式
//    cs_header = workbook.createCellStyle();
//    cs_header.setFont(font_hd);
//    cs_header.setAlignment(HorizontalAlignment.CENTER_SELECTION);
//    cs_header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//    cs_header.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
//    // 成功的样式
//    cs_ok = workbook.createCellStyle();
//    cs_ok.setFont(font_txt);
//    cs_ok.setAlignment(HorizontalAlignment.LEFT);
//
//    // 设置标题头
//    row = sheet.createRow(0);
//    row.setHeightInPoints(25);
//    // 通用值
//    List<String> header = Lists.newArrayList("序号", "手机号码", "区域", "性别", "年龄"),
//        bodyer = Lists.newArrayList();
//
//    // String[] header = {, "产品1倾向度"}, headerVal;
//    int[] columnWidth =
//        {10, 15, 15, 15, 15, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 10, 20, 20, 20, 20, 20};
//    for (int i = 0, len = header.size(); i < len; i++) {
//      sheet.setColumnWidth(i, columnWidth[i] * 256);
//      cell = row.createCell(i);
//      cell.setCellStyle(cs_header);
//      String val = header.get(i);
//      cell.setCellValue(val);
//    }
//
//    // 设置内容体
//    for (int i = 0, len_d = data.size(); i < len_d; i++) {
//      row = sheet.createRow(i + 1);
//      row.setHeightInPoints(25);
//      //DataAnalysis vo = data.get(i);
//      //if (vo == null) {
//      //  continue;
//      //}
//      //// 通用值
//      //bodyer =
//      //    Lists.newArrayList((i + 1) + "",
//      //        Strings.isNullOrEmpty(vo.getMobile()) ? "--" : vo.getMobile(),
//      //        Strings.isNullOrEmpty(vo.getArea()) ? "--" : vo.getArea(),
//      //        Strings.isNullOrEmpty(vo.getGender()) ? "--" : vo.getGender(),
//      //        Strings.isNullOrEmpty(vo.getAge()) ? "--" : vo.getAge());
//      //// 增加消息体
//      //for (String pro : prod_int) {
//      //  if (pro.equalsIgnoreCase("1")) {
//      //    bodyer.add(Strings.isNullOrEmpty(vo.getC_i_val()) ? "--" : vo.getC_i_val());
//      //    bodyer.add(Strings.isNullOrEmpty(vo.getC_i()) ? "--" : vo.getC_i());
//      //  } else if (pro.equalsIgnoreCase("2")) {
//      //    bodyer.add(Strings.isNullOrEmpty(vo.getM_c_val()) ? "--" : vo.getM_c_val());
//      //    bodyer.add(Strings.isNullOrEmpty(vo.getM_c()) ? "--" : vo.getM_c());
//      //  } else if (pro.equalsIgnoreCase("3")) {
//      //    bodyer.add(Strings.isNullOrEmpty(vo.getL_i_val()) ? "--" : vo.getL_i_val());
//      //    bodyer.add(Strings.isNullOrEmpty(vo.getL_i()) ? "--" : vo.getL_i());
//      //  } else if (pro.equalsIgnoreCase("4")) {
//      //    bodyer.add(Strings.isNullOrEmpty(vo.getO_a_p_val()) ? "--" : vo.getO_a_p_val());
//      //    bodyer.add(Strings.isNullOrEmpty(vo.getO_a_p()) ? "--" : vo.getO_a_p());
//      //  }
//      //}
//      // 循环赋值
//      for (int j = 0, len_b = bodyer.size(); j < len_b; j++) {
//        HSSFCell cell1 = row.createCell(j);
//        cell1.setCellStyle(cs_ok);
//        cell1.setCellValue(bodyer.get(j));
//      }
//    }
//    //虽然HSSFWorkBook提供了getBytes()方法，但获得的字节数组并不符合文档结构，所以可通过该方法来获取到excel文档的自己的数组
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    workbook.write(baos);
//    return baos.toByteArray();
//  }
//}
