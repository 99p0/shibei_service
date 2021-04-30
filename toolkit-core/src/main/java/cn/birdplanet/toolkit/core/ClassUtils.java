/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ClassUtils {

  private static final String proxyClazzField = "$$";

  public static final String getRealClassName(Class<?> clazz) {
    return getRealClass(clazz).getName();
  }

  public static final Class<?> getRealClass(Class<?> clazz) {
    String clazzName = clazz.getName();
    return (clazzName.indexOf(proxyClazzField) > 0) ?
        clazz.getSuperclass() : clazz;
  }

  public static final Class<?> getRealClass(Object obj) {
    return getRealClass(obj.getClass());
  }

  /**
   * 比较两个类是否一样<br/> 考虑了hibernate缓存代理对象的因素
   *
   * @author fengyuan
   */
  public static final boolean equals(Class<?> clazz, Class<?> clazzOther) {
    if (clazz == clazzOther) return true;
    return getRealClassName(clazz).equals(getRealClassName(clazzOther));
  }

  /**
   * 搜索class path下的类的名称集合（包名+类名）
   *
   * @param packageName 包名(com.tiger.domain) 如果从根目录查找则输入""
   * @param recursive 是否递归搜索
   * @author fengyuan
   */
  public static List<String> getClassPathClasseNames(String packageName,
      boolean recursive) {
    List<String> classNames = new ArrayList<String>();
    String packageDirName = packageName.replace('.', '/');
    try {
      Enumeration<URL> urls =
          Thread.currentThread().getContextClassLoader().getResources(packageDirName);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        String protocol = url.getProtocol();
        if ("file".equalsIgnoreCase(protocol)) {
          String absolutePackagePath = URLDecoder.decode(url.getFile(), "UTF-8");
          addClassFile(packageName, absolutePackagePath, recursive, classNames);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("package not found : " + packageName, e);
    }
    return classNames;
  }

  /**
   * 搜索class path下的类的class集合（包名+类名）
   *
   * @param packageName 包名(com.tiger.domain) 如果从根目录查找则输入""
   * @param recursive 是否递归搜索
   * @author fengyuan
   */
  public static List<Class<?>> getClassPathClasses(String packageName, boolean recursive) {
    List<String> classNames = getClassPathClasseNames(packageName, recursive);
    List<Class<?>> classes = new ArrayList<Class<?>>();
    for (String className : classNames) {
      try {
        classes.add(Class.forName(className));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("class not found : " + className, e);
      }
    }
    return classes;
  }

  /**
   * 搜索class path下的类的class集合（包名+类名）
   *
   * @param packageName 包名(com.tiger.domain) 如果从根目录查找则输入""
   * @param recursive 是否递归搜索
   * @param filter class过滤器,当过滤器为null 或accept方法返回true时才添加到集中
   * @author fengyuan
   */

  public static List<Class<?>> getClassPathClasses(String packageName, boolean recursive,
      ClassFilter filter) {
    List<String> classNames = getClassPathClasseNames(packageName, recursive);
    List<Class<?>> classes = new ArrayList<Class<?>>();
    for (String className : classNames) {
      try {
        Class<?> clazz = Class.forName(className);
        if (filter == null || filter.accept(clazz)) {
          classes.add(clazz);
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("class not found : " + className, e);
      } catch (Exception e) {
        throw new RuntimeException("class not found : " + className, e);
      }
    }
    return classes;
  }

  /**
   * 以文件的形式来获取包下的所有Class
   */
  private static void addClassFile(String packageName,
      String absolutePackagePath, final boolean recursive,
      List<String> classNames) {
    File dir = new File(absolutePackagePath);
    if (!dir.exists() || !dir.isDirectory()) {
      return;
    }
    File[] dirAndfiles = dir.listFiles(new FileFilter() {
      public boolean accept(File file) {
        return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
      }
    });
    for (File file : dirAndfiles) {
      if (file.isDirectory()) {
        addClassFile(
            getClassOrPackageName(packageName, file.getName()),
            file.getAbsolutePath(), recursive, classNames);
      } else {
        if (file.getName().indexOf("$") > -1) {
          continue;
        }
        String classFileName = file.getName().substring(0, file.getName().length() - 6);
        classNames.add(getClassOrPackageName(packageName, classFileName));
      }
    }
  }

  private static String getClassOrPackageName(String packageName,
      String classOrSubPackageName) {
    return (packageName != null && packageName.length() > 0) ? packageName
        + "."
        + classOrSubPackageName : classOrSubPackageName;
  }

  /**
   * 得到指定类下的公开方法和方法的基本命名<br/> 如： getType()的基本基本命名为：type
   *
   * @param clazz 类 只返回java基本类型、基本类型的包装类以及BigDecimal,String 对应的方法
   * @author fengyuan
   */
  public static Map<Method, String> paraserGet(Class<?> clazz) {
    Map<Method, String> map = new HashMap<Method, String>();
    String[] typeArray = {"String", "Long", "Integer", "int", "long", "Float", "float",
        "Double", "double", "Boolean", "boolean", "BigDecimal", "Date", "Calendar"};
    String[] excludeMethods = {"equals", "hashCode", "toString"};
    Method[] methods = clazz.getDeclaredMethods();
    for (Method method : methods) {
      //			if(ArrayUtils.contains(excludeMethods, method.getName()))
      //				continue;
      //			if (!ArrayUtils.contains(typeArray, method.getReturnType().getSimpleName()))
      //				continue;
      if (method.getGenericParameterTypes().length > 0) {
        continue;
      }
      method.setAccessible(true);
      String name = method.getName();
      if (name.startsWith("get")) {
        name = name.substring(3, 4).toLowerCase() + name.substring(4);
      }
      if (name.startsWith("is")) {
        name = name.substring(2, 3).toLowerCase() + name.substring(3);
      }
      map.put(method, name);
    }
    return map;
  }

  /**
   * 得到指定类下的 set方法 和方法对应的属性
   *
   * @param clazz 类 只返回java基本类型、基本类型的包装类以及BigDecimal,String,Date 对应的方法
   * @author fengyuan
   */
  public static Map<Method, Field> paraserSet(Class<?> clazz) {
    Map<Method, Field> map = new HashMap<Method, Field>();
    Method[] methods = clazz.getDeclaredMethods();
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      Method m = getMethod(field, methods);
      if (m == null) continue;
      map.put(m, field);
    }
    return map;
  }

  private static Method getMethod(Field field, Method[] methods) {
    String[] typeArray = {"String", "Long", "long", "Integer", "int", "Float", "float",
        "Double", "double", "Boolean", "boolean", "BigDecimal", "Date", "Calendar"};
    String fieldTypeName = field.getType().getSimpleName();
    //		if (!ArrayUtils.contains(typeArray,fieldTypeName))return null;
    for (Method method : methods) {
      if (method.getParameterTypes().length != 1) continue;
      Class<?> parameterClazz = method.getParameterTypes()[0];
      if (!parameterClazz.getSimpleName().equals(fieldTypeName)) {
        continue;
      }
      method.setAccessible(true);
      String methodName = method.getName();
      String fieldName = field.getName();
      String propertieName =
          "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      if (methodName.equals(propertieName)) return method;
    }
    return null;
  }

  /**
   * 根据类型返回改类型的对象 改类型必须有默认的构造方法
   *
   * @author fengyuan
   */
  public static <T> T instanceObject(Class<T> clazz) {
    return instanceObject(clazz, null);
  }

  /**
   * 根据类型返回改类型的对象
   *
   * @param <T> 类型
   * @param clazz 构造方法参数
   * @author fengyuan
   */
  public static <T> T instanceObject(Class<T> clazz, Object[] parameters) {
    try {
      Constructor<T> con = clazz.getDeclaredConstructor(objectConvertClass(parameters));
      con.setAccessible(true);
      return con.newInstance(parameters);
    } catch (Exception e) {
      throw new RuntimeException("init Object error : " + clazz.getName(), e);
    }
  }

  private static Class<?>[] objectConvertClass(Object... parameters) {
    if (parameters == null || parameters.length == 0) return null;
    Class<?>[] types = new Class<?>[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      Object parameter = parameters[i];
      types[i] = parameter.getClass();
    }
    return types;
  }

  /**
   * 取得某个接口下所有实现这个接口的类
   */
  public static List<Class<?>> getAllClassByInterface(Class c) {
    List<Class<?>> returnClassList = null;
    if (c.isInterface()) {
      // 获取当前的包名
      String packageName = c.getPackage().getName();
      // 获取当前包下以及子包下所以的类
      List<Class<?>> allClass = getClasses(packageName);
      if (allClass != null) {
        returnClassList = new ArrayList<>();
        for (Class classes : allClass) {
          // 判断是否是同一个接口
          if (c.isAssignableFrom(classes)) {
            // 本身不加入进去
            if (!c.equals(classes)) {
              returnClassList.add(classes);
            }
          }
        }
      }
    }
    return returnClassList;
  }

  /*
   * 取得某一类所在包的所有类名 不含迭代
   */
  public static String[] getPackageAllClassName(String classLocation, String packageName) {
    //将packageName分解
    String[] packagePathSplit = packageName.split("[.]");
    String realClassLocation = classLocation;
    int packageLength = packagePathSplit.length;
    for (int i = 0; i < packageLength; i++) {
      realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
    }
    File packeageDir = new File(realClassLocation);
    if (packeageDir.isDirectory()) {
      String[] allClassName = packeageDir.list();
      return allClassName;
    }
    return null;
  }

  /**
   * 从包package中获取所有的Class
   */
  public static List<Class<?>> getClasses(String packageName) {

    //第一个class类的集合
    List<Class<?>> classes = new ArrayList<Class<?>>();
    //是否循环迭代
    boolean recursive = true;
    //获取包的名字 并进行替换
    String packageDirName = packageName.replace('.', '/');
    //定义一个枚举的集合 并进行循环来处理这个目录下的things
    Enumeration<URL> dirs;
    try {
      dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
      //循环迭代下去
      while (dirs.hasMoreElements()) {
        //获取下一个元素
        URL url = dirs.nextElement();
        //得到协议的名称
        String protocol = url.getProtocol();
        //如果是以文件的形式保存在服务器上
        if ("file".equals(protocol)) {
          //获取包的物理路径
          String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
          //以文件的方式扫描整个包下的文件 并添加到集合中
          findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
        } else if ("jar".equals(protocol)) {
          //如果是jar包文件
          //定义一个JarFile
          JarFile jar;
          try {
            //获取jar
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            //从此jar包 得到一个枚举类
            Enumeration<JarEntry> entries = jar.entries();
            //同样的进行循环迭代
            while (entries.hasMoreElements()) {
              //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
              JarEntry entry = entries.nextElement();
              String name = entry.getName();
              //如果是以/开头的
              if (name.charAt(0) == '/') {
                //获取后面的字符串
                name = name.substring(1);
              }
              //如果前半部分和定义的包名相同
              if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                //如果以"/"结尾 是一个包
                if (idx != -1) {
                  //获取包名 把"/"替换成"."
                  packageName = name.substring(0, idx).replace('/', '.');
                }
                //如果可以迭代下去 并且是一个包
                if ((idx != -1) || recursive) {
                  //如果是一个.class文件 而且不是目录
                  if (name.endsWith(".class") && !entry.isDirectory()) {
                    //去掉后面的".class" 获取真正的类名
                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    try {
                      //添加到classes
                      classes.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                      e.printStackTrace();
                    }
                  }
                }
              }
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return classes;
  }

  /**
   * 以文件的形式来获取包下的所有Class
   */
  public static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
      final boolean recursive, List<Class<?>> classes) {
    //获取此包的目录 建立一个File
    File dir = new File(packagePath);
    //如果不存在或者 也不是目录就直接返回
    if (!dir.exists() || !dir.isDirectory()) {
      return;
    }
    //如果存在 就获取包下的所有文件 包括目录
    File[] dirfiles = dir.listFiles(new FileFilter() {
      //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
      public boolean accept(File file) {
        return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
      }
    });
    //循环所有文件
    for (File file : dirfiles) {
      //如果是目录 则继续扫描
      if (file.isDirectory()) {
        findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
            file.getAbsolutePath(),
            recursive,
            classes);
      } else {
        //如果是java类文件 去掉后面的.class 只留下类名
        String className = file.getName().substring(0, file.getName().length() - 6);
        try {
          //添加到集合中去
          classes.add(Class.forName(packageName + '.' + className));
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * class过滤器
   *
   * @author tiger
   */
  public interface ClassFilter {
    boolean accept(Class<?> clazz);
  }
}
