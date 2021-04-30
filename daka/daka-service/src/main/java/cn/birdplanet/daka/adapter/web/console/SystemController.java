/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter.web.console;

import cn.birdplanet.daka.domain.dto.RespDto;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "系统参数 :: 相关操作")
@RequestMapping("console/system")
@RestController("consoleSystemController")
public class SystemController {

  @ApiOperation(value = "系统参数", notes = "系统参数")
  @PostMapping("property")
  public RespDto execute() {
    Map<String, String> map = Maps.newLinkedHashMapWithExpectedSize(30);
    map.put("Java运行时环境版本", System.getProperty("java.version"));
    map.put("Java运行时环境供应商", System.getProperty("java.vendor"));
    map.put("Java安装目录", System.getProperty("java.home"));
    map.put("Java虚拟机规范版本", System.getProperty("java.vm.specification.version"));
    map.put("Java虚拟机规范供应商", System.getProperty("java.vm.specification.vendor"));
    map.put("Java虚拟机规范名称", System.getProperty("java.vm.specification.name"));
    map.put("Java虚拟机实现版本", System.getProperty("java.vm.version"));
    map.put("Java虚拟机实现供应商", System.getProperty("java.vm.vendor"));
    map.put("Java虚拟机实现名称", System.getProperty("java.vm.name"));
    map.put("Java运行时环境规范版本", System.getProperty("java.specification.version"));
    map.put("Java运行时环境规范供应商", System.getProperty("java.specification.vendor"));
    map.put("Java运行时环境规范名称", System.getProperty("java.specification.name"));
    map.put("Java类格式版本号", System.getProperty("java.class.version"));
    //map.put("Java类路径", System.getProperty("java.class.path"));
    map.put("加载库时搜索的路径列表", System.getProperty("java.library.path"));
    map.put("临时文件路径", System.getProperty("java.io.tmpdir"));
    map.put("JIT编译器的名称", System.getProperty("java.compiler"));
    //map.put("扩展目录的路径", System.getProperty("java.ext.dirs"));
    map.put("操作系统的名称", System.getProperty("os.name"));
    map.put("操作系统的架构", System.getProperty("os.arch"));
    map.put("操作系统的版本", System.getProperty("os.version"));
    map.put("文件分隔符（在 UNIX 系统中是“/”）", System.getProperty("file.separator"));
    map.put("路径分隔符（在 UNIX 系统中是“:”）", System.getProperty("path.separator"));
    map.put("行分隔符（在 UNIX 系统中是“/n”）", System.getProperty("line.separator"));
    map.put("用户的账户名称", System.getProperty("user.name"));
    map.put("用户的主目录", System.getProperty("user.home"));
    map.put("用户的当前工作目录", System.getProperty("user.dir"));
    return RespDto.succData(map);
  }
}
