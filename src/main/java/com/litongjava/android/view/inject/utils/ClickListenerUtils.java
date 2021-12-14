package com.litongjava.android.view.inject.utils;

public class ClickListenerUtils {
  public static Class<?> getOnClickListenerIntreface() {
    // 不封装成工具类之前使用的代码如下
    // return new Class<?>[]{View.OnClickListener.class};
    // return View.OnClickListener.class;

    /**
     * 测试记录,使用class.forName()获取class类出现错误,使用View.OnClickListener.class获取类名成功
     * 猜测和OnClickListener是个接口有关系 原因是加载内部类使用的语法问题,已经解决
     */

    try {
      return Class.forName("android.view.View$OnClickListener");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;

  }
}
