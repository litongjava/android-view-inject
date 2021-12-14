package com.litongjava.android.view.inject.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClick {
  /**
   * 保存所有需要设置点击事件控件的id
   *
   * @return
   */
  int[] value();
}