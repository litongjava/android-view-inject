# android-view-inject
## 简介
适用与嵌入式设备的控件注入框架
## 概述
传统的项目中，为了从XML文件找到各个控件，需要使用方法findViewById,太多的findViewId,使我们的代码变得繁琐，
获取，强转，千篇一律的重复着某种机制，其实内心也是蛮崩溃的。
当然了，为了解决不必要的findViewById，有很多出色的第三方，如ButterKnife，AndroidAnotations，还有XUtils,等等，这些第三方不可否认，
是特别的优秀，功能也是非常的强大，使用起来也是非常的简单，但是，也有一定的负面影响，
显而可见，这些第三方，不仅仅有注解功能，还有联网，请求数据库等等其它很多功能，
而我们只需要一个注解功能，这不等于，我需要一个苹果，你一下给了我一车水果，无形中增加了内存的容量
其实说的通俗点就是，第三方很多冗余的代码，会占去我们的内存，基于这样的一个原因，不就是一个注解功能吗，我们何不自己实现呢?
## 参考工程
AndroidViewInjectTest
## 主要功能
### 整合Activity
* @FindViewByIdLayout 查找布局文件并注入到当前Activity,用于替换setContentView
* @FindViewById 查看控件id,注入到成员变量
* @OnClick 添加单击事件


测试代码
```
package com.litongjava.android_view_inject_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.annotation.FindViewByIdLayout;
import com.litongjava.android.view.inject.annotation.OnClick;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;

@FindViewByIdLayout(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

  @FindViewById(R.id.tv)
  private TextView mText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setContentView(R.layout.activity_main);
    ViewInjectUtils.injectActivity(this, this);
    mText.setText("Annotation");
  }

  @OnClick(R.id.tv)
  public void onClick(View view) {
    Toast.makeText(this, "on click", Toast.LENGTH_LONG).show();
  }
}
```

### Fragment
布局文件
fragment_async.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:orientation="vertical">

  <Button
    android:id="@+id/btn1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="按钮1"/>
</LinearLayout>
```
测试类如下
```
package com.litongjava.android_view_inject_test.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.annotation.OnClick;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;
import com.litongjava.android_view_inject_test.R;



public class AsyncTaskFragment extends Fragment{

  @FindViewById(R.id.btn1)
  private Button button1;


  public static Fragment newInstance() {
    return new AsyncTaskFragment();
  }


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_async, container, false);
    ViewInjectUtils.injectViewAndOnClick(view,this);
    return view;
  }


  @OnClick(R.id.btn1)
  public void button1OnClick(){
    Toast.makeText(this.getActivity(),"button 1 click",Toast.LENGTH_LONG).show();
  }
}

```
### RecyclerView
fragment_photo_gallery.xml
```
<android.support.v7.widget.RecyclerView xmlns:android="http://schemas.android.com/apk/res/android"
  android:id="@+id/fragment_photo_gallery_recycler_view"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />
```
使用RecyclerView代码
```
package com.litongjava.android_view_inject_test.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litongjava.android.view.inject.annotation.FindViewById;
import com.litongjava.android.view.inject.utils.ViewInjectUtils;
import com.litongjava.android_view_inject_test.R;


public class PhotoGalleryFragment extends Fragment {
  @FindViewById(R.id.fragment_photo_gallery_recycler_view)
  private RecyclerView mPhotoRecyclerView;

  public static Fragment newInstance() {
    return new PhotoGalleryFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
    //mPhotoRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_container);
    ViewInjectUtils.injectView(view, this);
    mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    return view;
  }
}
```
### ViewHolder
在ViewHolder 中使用ViewUtils.inject
```
private class CrimeHolder extends RecyclerView.ViewHolder {

  @FindViewById(R.id.list_item_crime_date_text_view)
  private TextView mTittleTextView;


  public CrimeHolder(View itemView) {
    super(itemView);
    ViewInjectUtils.injectView(itemView,this);
  }
}
```