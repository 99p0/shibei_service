/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.mobile.punch;

import cn.birdplanet.daka.domain.dto.RespDto;
import cn.birdplanet.daka.domain.po.AppVersion;
import cn.birdplanet.daka.domain.po.User;
import cn.birdplanet.daka.domain.vo.PosterDataVO;
import cn.birdplanet.daka.domain.vo.UserDtlVO;
import cn.birdplanet.daka.infrastructure.commons.base.BaseController;
import cn.birdplanet.daka.infrastructure.service.IInviteService;
import cn.birdplanet.daka.infrastructure.service.IUserService;
import cn.birdplanet.daka.infrastructure.service.IVersionService;
import cn.birdplanet.daka.infrastructure.service.posters.PostersUtils;
import cn.birdplanet.toolkit.date.DateUtil;
import cn.birdplanet.toolkit.extra.constant.BirdplanetConstants;
import cn.birdplanet.toolkit.extra.constant.RedisConstants;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邀请
 *
 * @author uncle.yang@outlook.com
 */
@Api(tags = "邀请-海报 :: 相关操作")
@Slf4j
@RestController
@RequestMapping("punch/invite/posters")
public class PostersController extends BaseController {

  @Autowired private IInviteService inviteService;
  @Autowired private IUserService userService;
  @Autowired private IVersionService versionService;

  @ApiOperation(value = "获取海报信息", notes = " ")
  @ApiImplicitParams({
  })
  @PostMapping("data")
  public RespDto getData(@RequestAttribute UserDtlVO currUserDtlVo) {

    User currUser = userService.getByUid(currUserDtlVo.getUid());
    Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
    String apkUrl = (String) redisUtils.get(RedisConstants.APP_URL_KEY);

    // 下载地址
    dataMap.put("apkUrl", apkUrl);
    dataMap.put("firim", apkUrl);
    // 打卡次数：成功
    dataMap.put("joined_days", DateUtil.getDays(currUser.getCreatedAt().toLocalDate().toString(),
        LocalDate.now().toString()));
    String rkey = RedisConstants.TJ_CHECKIN_TIMES_KEY;
    Integer checkin_time = (Integer) redisUtils.hash_get(rkey, currUser.getUid());
    dataMap.put("checkin_times", checkin_time);
    // 闯关收益
    dataMap.put("income_sum", currUser.getIncomeSum());
    return RespDto.succData(dataMap);
  }

  @ApiOperation(value = "获取海报Url", notes = " ")
  @ApiImplicitParams({
  })
  @PostMapping("img")
  public RespDto getPostersUrm(@RequestAttribute UserDtlVO currUserDtlVo,
      @RequestParam(required = false, defaultValue = "android") String os) throws IOException {
    LocalDate now = LocalDate.now();
    String fileName =
        currUserDtlVo.getUid() + "-p-i-" + now.getYear() + now.getMonthValue() + ".png";
    // 合成海报的路径 ::
    Path path = Paths.get(BirdplanetConstants.POSTER_INVITE_PATH + fileName);
    if (!Files.exists(path)) {
      // 未处理海报的路径
      Path posterPath = Paths.get(BirdplanetConstants.POSTER_INVITE_PATH + "default.png");
      if (!Files.exists(posterPath)) {
        log.error("预设海报不存在");
      }
      // 二维码的路径
      Path qrcodePath =
          Paths.get(BirdplanetConstants.POSTER_INVITE_PATH + "app_download_qr.png");
      if (!Files.exists(qrcodePath)) {
        AppVersion currVersion = versionService.getCurrVersion(os);
        PostersUtils.buildQrcode(currVersion.getApkDownloadUrl2(), qrcodePath.toString(), 140, 140);
        if (!Files.exists(qrcodePath)) {
          log.error("二维码信息量太大，目前配置参数放不下，需手动调节，请联系业务人员");
        }
      }
      PosterDataVO dataVO = new PosterDataVO();

      Integer times =
          (Integer) redisUtils.hash_get(RedisConstants.TJ_CHECKIN_TIMES_KEY,
              currUserDtlVo.getUid());

      dataVO.setCheckin_times(times);
      User user = userService.getByUid(currUserDtlVo.getUid());
      dataVO.setIncome_sum(user.getIncomeSum());
      // 间隔时间
      dataVO.setJoined_days(
          DateUtil.getDays(currUserDtlVo.getCreatedAt().toLocalDate(), LocalDate.now()));

      dataVO.setAvatarPath(currUserDtlVo.getAvatarPath());
      dataVO.setInvitationCode(currUserDtlVo.getInvitationCode());
      dataVO.setNickName(currUserDtlVo.getNickName());
      // 合成的海报是否存在， 不存在 则生成
      PostersUtils.buildPosterWithData(dataVO, qrcodePath.toString(), posterPath.toString(),
          path.toString());
    }
    HashMap<String, String> dataMap = Maps.newHashMapWithExpectedSize(2);
    dataMap.put("imgUrl", BirdplanetConstants.POSTER_URL_PREFIX + fileName);
    return RespDto.succData(dataMap);
  }
}
