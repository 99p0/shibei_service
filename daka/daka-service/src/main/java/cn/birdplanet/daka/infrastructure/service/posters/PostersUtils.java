package cn.birdplanet.daka.infrastructure.service.posters;

import cn.birdplanet.daka.domain.vo.PosterDataVO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostersUtils {

  private PostersUtils() {
  }

  //public static String drawImage(String posterImgUrl, String tempQrCodeImgUrl, String headImgUrl,
  //    String nameText, String cqText, String cqTextx) throws IOException {
  //
  //  int width = 620;
  //  int height = 1004;
  //  // RGB形式
  //  BufferedImage bgBufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  //  Graphics2D bgBufImageGraphics = bgBufImage.createGraphics();
  //  // 设置背景色
  //  bgBufImageGraphics.setBackground(Color.WHITE);
  //  // 通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
  //  bgBufImageGraphics.clearRect(0, 0, width, height);
  //
  //  // bgBufImageGraphics.setBackground(new Color(255,255,255));
  //  // 设置画笔,设置Paint属性
  //  bgBufImageGraphics.setPaint(Color.black);
  //  Font font = new Font("宋体", Font.PLAIN, 28);
  //  bgBufImageGraphics.setFont(font);
  //  // 抗锯齿
  //  bgBufImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
  //      RenderingHints.VALUE_ANTIALIAS_ON);
  //  // 计算文字长度，计算居中的x点坐标
  //  FontMetrics fm = bgBufImageGraphics.getFontMetrics(font);
  //  int textWidth = fm.stringWidth(nameText);
  //  // int widthX = (width - textWidth) / 2;
  //  int widthX = 128;
  //  // 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
  //  bgBufImageGraphics.drawString(nameText, widthX, 926);
  //  // 计算文字长度，计算居中的x点坐标
  //  // int widthX = (width - textWidth) / 2;
  //  int cqCodeWidthX = 372;
  //  // 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
  //  bgBufImageGraphics.drawString(cqText, cqCodeWidthX, 958);
  //
  //  // BufferedImage posterBufImage = ImageIO.read(new URL(posterImgUrl));
  //  // //直接使用图片做背景，自定义背景使用上面方式
  //  // Graphics2D posterBufImageGraphics = posterBufImage.createGraphics();
  //
  //  BufferedImage posterBufImage = ImageIO.read(new URL(posterImgUrl));
  //  BufferedImage qrCodeImage = ImageIO.read(new URL(tempQrCodeImgUrl));
  //  BufferedImage headImage = ImageIO.read(new URL(headImgUrl));
  //
  //  // 设置圆形头像
  //  BufferedImage roundHeadImg = new BufferedImage(headImage.getWidth(), headImage.getHeight(),
  //      BufferedImage.TYPE_INT_RGB);
  //
  //  Graphics2D roundHeadGraphics = roundHeadImg.createGraphics();
  //  Ellipse2D.Double shape =
  //      new Ellipse2D.Double(0, 0, roundHeadImg.getWidth(), roundHeadImg.getHeight());
  //  roundHeadGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
  //      RenderingHints.VALUE_ANTIALIAS_ON);
  //  roundHeadImg =
  //      roundHeadGraphics.getDeviceConfiguration().createCompatibleImage(headImage.getWidth(),
  //          headImage.getHeight(), Transparency.TRANSLUCENT);
  //  roundHeadGraphics = roundHeadImg.createGraphics();
  //  // 使用 setRenderingHint 设置抗锯齿
  //  roundHeadGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
  //      RenderingHints.VALUE_ANTIALIAS_ON);
  //  roundHeadGraphics.setClip(shape);
  //  roundHeadGraphics.drawImage(headImage, 0, 0, null);
  //  roundHeadGraphics.dispose();
  //
  //  // bgBufImageGraphics.drawImage(qrCodeImage, (posterBufImage.getWidth()
  //  // - qrCodeImage.getWidth()), 10, qrCodeImage.getWidth(),
  //  // qrCodeImage.getHeight(), null);
  //  // posterBufImageGraphics.drawImage(roundHeadImg, 50, 100,
  //  // HEAD_URL_WIDTH, HEAD_URL_HEIGHT, null);
  //  bgBufImageGraphics.drawImage(qrCodeImage, 444, 848, 72, 72, null);
  //  bgBufImageGraphics.drawImage(roundHeadImg, 32, 876, 80, 80, null);
  //  bgBufImageGraphics.drawImage(posterBufImage, 0, 0, 620, 826, null);
  //  bgBufImageGraphics.dispose();
  //
  //  ByteArrayOutputStream bs = new ByteArrayOutputStream();
  //  ImageOutputStream imgOut = ImageIO.createImageOutputStream(bs);
  //  ImageIO.write(bgBufImage, "png", imgOut);
  //  // 上传到服务器上
  //  // InputStream inSteam = new ByteArrayInputStream(bs.toByteArray());
  //  // String url = OSSFactory.build().uploadSuffix(inSteam, ".png");
  //  // 返回合成的图片地址url
  //  return "url";
  //}

  public final static void buildQrcode(String url, String targetPathName, int width, int height) {
    try {
      File file = new File(targetPathName);
      if (!file.getParentFile().exists()) {
        // 如果目标文件所在的目录不存在，则创建父目录
        if (!file.getParentFile().mkdirs()) {
          log.error("生成下载地址的二维码 创建文件所在目录失败:: {}", file.getPath());
        }
      }
      // 二维码微调
      Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
      //指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
      //内容所使用字符集编码
      hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
      //设置二维码边的空度，非负数
      hints.put(EncodeHintType.MARGIN, 1);
      // 二维码版本， 数字越大 存储信息越多
      hints.put(EncodeHintType.QR_VERSION, 5);

      BitMatrix bitMatrix =
          new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
      MatrixToImageWriter.writeToPath(bitMatrix, "png", file.toPath());
    } catch (Exception e) {
      log.error("生成h5投保的二维码出错 {}", e);
    }
  }

  /**
   * ` 海报合成二维码图片
   *
   * @param dataVO         需要添加的文本
   * @param iconPathName   投保二维码图片的存储路径
   * @param srcImgPathName 海报样式图片的存储路径
   * @param targetPathName 合成图片后的存储路径
   */
  public final static void buildPosterWithData(PosterDataVO dataVO, String iconPathName,
      String srcImgPathName, String targetPathName) {
    log.debug("dataVO:: {}, iconPath::{}, srcImgPath::{}, targetPath::{}", dataVO, iconPathName,
        srcImgPathName, targetPathName);
    OutputStream os = null;
    try {
      //
      Image srcImg = ImageIO.read(new File(srcImgPathName));
      //
      BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
          srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
      // 得到画笔对象
      Graphics2D g = buffImg.createGraphics();
      // 设置对线段的锯齿状边缘处理
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      //
      g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg
          .getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);

      // 下载地址的二维码
      Image qrImg = ImageIO.read(new File(iconPathName));
      g.drawImage(qrImg, 480, 1095, null);
      // 头像 url
      BufferedImage avatarImgIcon = ImageIO.read(new URL(dataVO.getAvatarPath()));
      g.drawImage(avatarImgIcon.getScaledInstance(83, 83, Image.SCALE_SMOOTH), 95, 145, null);

      // 查看系统可用的字体
      //GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      //Font[] fonts = ge.getAllFonts();
      //log.debug("系统共{}个可用的字体 --", fonts.length);
      //Arrays.stream(fonts).forEach(font -> log.debug("FontFamilyName :: {}", font));

      // 字体 样式 字号
      Font useFont = new Font("思源宋体", Font.PLAIN, 26);
      Font useFontNum = new Font("思源宋体", Font.BOLD, 48);
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1));
      // 昵称
      g.setFont(useFont);
      g.drawString(dataVO.getNickName(), 218, 165);
      g.setColor(Color.WHITE);
      g.setFont(useFont);
      g.setColor(Color.WHITE);
      g.drawString(dataVO.getInvitationCode(), 315, 205);
      //
      g.setFont(useFontNum);
      g.setColor(Color.WHITE);
      g.drawString(dataVO.getCheckin_times().toString(), 95, 340);
      g.setFont(useFontNum);
      g.setColor(Color.WHITE);
      g.drawString(dataVO.getIncome_sum().toBigInteger().toString(), 310, 340);
      g.setFont(useFontNum);
      g.setColor(Color.WHITE);
      g.drawString(dataVO.getJoined_days().toString(), 540, 340);
      //
      g.dispose();
      os = new FileOutputStream(targetPathName);
      // 生成图片
      ImageIO.write(buffImg, "png", os);
    } catch (Exception e) {
      log.error("合成h5投保的海报 出错", e);
    } finally {
      try {
        if (null != os) {
          os.close();
        }
      } catch (Exception e) {
        log.error("合成h5投保的海报 关闭流出错", e);
      }
    }
  }
}
