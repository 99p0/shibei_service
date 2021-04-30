package cn.birdplanet.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: TestGuava
 * @date 2019/9/17 15:14
 */
@Slf4j
public class jvmTest {

  public static int i=1;

  @Test
  public void test1() {
    final long max = Runtime.getRuntime().maxMemory();//jvm的视图使用的最大内存
    final long total = Runtime.getRuntime().totalMemory();//jvm初始化内存
    final int cpu = Runtime.getRuntime().availableProcessors();//cpu核数
    /*-Xms10m  -Xmx10m -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -Xss2m
     * 表示配置jvm初始化内存为10M,最大内存为10M,打印GC详细文件,生成错误文件
     * */
    log.debug("虚拟机获得最大内存 {}m", (max / 1024 / 1024));
    log.debug("初始最大内存 {}m", (total / 1024 / 1024));
    log.debug("本机核数 :: {}", cpu);
    String s = "";
    while (true) {
      final byte[] bytes = new byte[1024 * 1024 * 1024];
    }
  }
}
