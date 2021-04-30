package cn.birdplanet.service.posters;

import cn.birdplanet.daka.domain.vo.PosterDataVO;
import cn.birdplanet.daka.infrastructure.service.posters.PostersUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class PostersUtilsTest {

  @Test
  public void testA() throws IOException {
    LocalDate now = LocalDate.now();
    PosterDataVO dataVO = new PosterDataVO();
    dataVO.setCheckin_times(6533);
    dataVO.setIncome_sum(new BigDecimal("17382.88"));
    dataVO.setJoined_days(428L);

    dataVO.setAvatarPath(
        "https://www.zhuomuniaodaka.com/avatar/default.png");
    dataVO.setInvitationCode("666666");
    dataVO.setNickName("小鸟星球007");

    String basePath = "/Users/dwy/Downloads/birdplanet/";
    // 合成海报的路径 ::
    Path path = Paths.get(basePath + "poster/" +
        "10001-poster-" + now.getYear() + now.getMonthValue() + ".png");
    if (!Files.exists(path)) {
      // 未处理海报的路径
      Path posterPath = Paths.get(basePath + "poster/" + "default.png");
      if (!Files.exists(posterPath)) {
        log.error("预设海报不存在");
      }
      // 二维码的路径
      Path qrcodePath =
          Paths.get(basePath + "poster/" + "app_download_qr.png");
      if (!Files.exists(qrcodePath)) {
        PostersUtils.buildQrcode("http://d.7short.com/18k", qrcodePath.toString(), 140, 140);
        if (!Files.exists(qrcodePath)) {
          log.error("二维码信息量太大，目前配置参数放不下，需手动调节，请联系业务人员");
        }
      }
      // 合成的海报是否存在， 不存在 则生成
      PostersUtils.buildPosterWithData(dataVO, qrcodePath.toString(), posterPath.toString(),
          path.toString());
    } else {
      System.out.println("存在");
    }
  }
}