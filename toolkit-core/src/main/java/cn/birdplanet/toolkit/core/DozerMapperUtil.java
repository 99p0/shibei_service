package cn.birdplanet.toolkit.core;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DozerMapperUtil {

  private DozerMapperUtil() {
  }

  private static Mapper MAPPER = DozerBeanMapperBuilder.buildDefault();

  public static <T> T map(Object source, Class<T> destinationClass) {
    if (source == null) {
      return null;
    }
    return MAPPER.map(source, destinationClass);
  }

  public static void map(Object source, Object destination) {
    MAPPER.map(source, destination);
  }

  public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
    List<T> destinationList = new ArrayList<>();
    for (Object sourceObject : sourceList) {
      destinationList.add(MAPPER.map(sourceObject, destinationClass));
    }
    return destinationList;
  }
}
