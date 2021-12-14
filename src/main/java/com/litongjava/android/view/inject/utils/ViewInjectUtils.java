package com.litongjava.android.view.inject.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.annotation.FindViewByIdLayout;
import com.litongjava.android.view.inject.annotation.OnClick;
import com.litongjava.android.view.inject.handler.MInvocationHandler;

public class ViewInjectUtils {
  /**
   * 从fromClass获取Field 从viewClass获取findViewById等等的方法
   */
  // private static Class<?> fromClass;
  // private static Class<?> viewClass;

  /**
   * 初始化activity和所有注解
   *
   * @param obj 你需要初始化的activity
   */
  public static void injectActivity(Object view, Object from) {
    injectContentView(view, from);
    injectView(view, from);
    injectOnClick(view, from);
  }
  
  public static void injectViewAndOnClick(Object view, Object from) {
    injectView(view, from);
    injectOnClick(view, from);
  }


  // 初始化activity布局文件
  private static void injectContentView(Object view, Object from) {
    Class<?> fromClass = from.getClass();
    FindViewByIdLayout annotation = fromClass.getAnnotation(FindViewByIdLayout.class);
    if (annotation != null) {
      // 获取注解中的对应的布局id 因为注解只有个方法 所以@XXX(YYY)时会自动赋值给注解类唯一的方法
      int id = annotation.value();
      try {
        // 得到activity中的方法 第一个参数为方法名 第二个为可变参数 类型为 参数类型的字节码
        Method method = fromClass.getMethod("setContentView", int.class);
        // 调用方法 第一个参数为哪个实例去调用 第二个参数为 参数
        method.invoke(from, id);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 初始化activity中的所有view控件
   * 
   * @param from
   */
  public static void injectView(Object view, Object from) {
    Class<?> fromClass = from.getClass();
    // 获取 对象所有的属性 包含私有 保护 默认 共开 但不包含继承等
    // getFields可以获取到所有公开的包括继承的 但无法获取到私有的属性
    Field[] declaredFields = fromClass.getDeclaredFields();
    // 健壮性
    if (declaredFields != null) {
      // 遍历所有的属性变量
      for (Field field : declaredFields) {
        // 获取属性变量上的注解
        FindViewById annotation = field.getAnnotation(FindViewById.class);
        // 如果此属性变量 包含FMYViewView

        if (annotation != null) {
          findViewById(view, from, field, annotation);
        }
      }
    }
  }

  private static void findViewById(Object view, Object from, Field field, FindViewById annotation) {
    Class<?> viewClass = view.getClass();
    // 获取属性id值
    int id = annotation.value();
    try {
      // 获取activity中方法
      Method findViewByIdMethod = viewClass.getMethod("findViewById", int.class);
      if (findViewByIdMethod == null) {
        return;
      }
      // 在某些情况下invokeResult返回null,原因不明 出现在mPhotoRecyclerView;
      Object invokeResult = findViewByIdMethod.invoke(view, id);
      // 设置属性变量 指向实例
      // 如果修饰符不为公共类 这里注意了 当activity
      // 控件变量为private的时候 我们去访问会失败的 要么打破封装系 要么变量改为public
      // 如 private TextView tv 这种情况 如果不打破封装会直接异常
      if (Modifier.PUBLIC != field.getModifiers()) {
        // 打破封装性
        field.setAccessible(true);
      }
      // 这里相当于 field= acitivity.obj
      field.set(from, invokeResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 初始化所有控件的点击事件 只需要某方法上写上对应注解和id即可
   * 
   * @param from
   *
   * @param
   */
  public static void injectOnClick(Object view, Object from) {
    Class<?> fromClass = from.getClass();
    // 获得所有方法
    Method[] methods = fromClass.getMethods();
    // 遍历所有的activity下的方法
    for (Method onClickMethod : methods) {
      // 获取方法的注解
      OnClick annotation = onClickMethod.getAnnotation(OnClick.class);
      // 如果存在此注解
      if (annotation != null) {
        // 所有注解的控件的id
        int[] ids = annotation.value();
        // 代理处理类
        MInvocationHandler handler = new MInvocationHandler(from, onClickMethod);
        // 代理实例 这里也可以返回 new Class<?>[] { View.OnClickListener.class }中的接口类
        // 第一个参数用于加载其他类 不一定要使用View.OnClickListener.class.getClassLoader() 你可以使用其他的
        // 第二个参数你所实现的接口
        ClassLoader classLoader = ViewInjectUtils.class.getClassLoader();
        Class<?> onClickListenerIntreface = ClickListenerUtils.getOnClickListenerIntreface();
        Class<?>[] interfaces = new Class[] { onClickListenerIntreface };
        Object newProxyInstance = Proxy.newProxyInstance(classLoader, interfaces, handler);

        // 遍历所有的控件id 然后设置代理
        for (int i : ids) {
          setOnClickListner(view, onClickListenerIntreface, newProxyInstance, i);
        }
      }
    }
  }

  /**
   * 设置单单击事件
   * 
   * @param view
   * @param onClickListenerIntreface
   * @param newProxyInstance
   * @param i
   */
  private static void setOnClickListner(Object view, Class<?> onClickListenerIntreface, Object newProxyInstance,
      int i) {
    Class<?> viewClass = view.getClass();
    try {
      // 如果对象是activity
      Method findViewByIdMethod = viewClass.getMethod("findViewById", int.class);

      Object buttonView = findViewByIdMethod.invoke(view, i);
      if (buttonView != null) {
        Method setOnClickListener = buttonView.getClass().getMethod("setOnClickListener", onClickListenerIntreface);
        setOnClickListener.invoke(buttonView, newProxyInstance);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}