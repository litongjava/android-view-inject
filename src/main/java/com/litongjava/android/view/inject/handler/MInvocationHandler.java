package com.litongjava.android.view.inject.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MInvocationHandler implements InvocationHandler {
  // 这里我们到时候回传入activity或者fragment
  private Object target;
  // 用户自定义view 的点击事件方法
  private Method method;

  public MInvocationHandler(Object target, java.lang.reflect.Method method) {
    super();
    this.target = target;
    this.method = method;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 调用用户自定义方法的点击事件
    // 调用button的onClick方法,不需要传入参数
    return this.method.invoke(target);
  }
}