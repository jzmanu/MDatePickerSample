[![](https://jitpack.io/v/jzmanu/MDatePickerSample.svg)](https://jitpack.io/#jzmanu/MDatePickerSample)

MDatePicker实现细节见文章：[手把手教你实现实现一个Android日期选择器 ](https://juejin.cn/post/6844904016283975687)

#### 基本功能


MDatePicker 基本属性如下：

设置 |设置方法|默认值
---|---|---
标题|setTitle(String mTitle) |日期选择
显示位置 |setGravity(int mGravity)|Gravity.CENTER
时候支持点击外部区域取消|setCanceledTouchOutside(boolean canceledTouchOutside)|false
是否支持时间|setSupportTime(boolean supportTime)|false
是否支持12小时制|setTwelveHour(boolean twelveHour)|false
是否仅显示年月|setOnlyYearMonth(boolean onlyYearMonth)|false
设置年份默认值|setYearValue(int yearValue)|当前年份
设置月份默认值|setMonthValue(int monthValue)|当前月份
设置天默认值|setDayValue(int dayValue)|当前天数
设置字体大小类型|setFontType(String type)|FlontType.NORMAL
设置确定的颜色|setConfirmTextColor(int confirmTextColor)|默认样式
设置取消的颜色|setCancelTextColor(int cancelTextColor)|默认样式
设置标题的颜色|setTitleTextColor(int titleTextColor)|默认样式
设置日期显示的颜色|setDateNormalTextColor(int normalTextColor)|默认样式
设置选中的颜色|setDateSelectTextColor(int selectTextColor)|默认样式

### 使用说明

1. 在项目根目录下的 build.gradle 文件中添加 jitpack 仓库，如下：

```groovy
allprojects {
	repositories {
		// ...
		maven { url 'https://www.jitpack.io' }
	}
}
```

2. 在 app 下面的 build.gradle 文件中引入 MDatePicker，如下：


```groovy
implementation 'com.github.jzmanu:MDatePickerSample:v1.0.6'
```

3. MDatePicker 的使用和普通的 Dialog 一样，参考如下：

```java
MDatePicker.create(this)
    //附加设置(非必须,有默认值)
    .setCanceledTouchOutside(true)
    .setGravity(Gravity.BOTTOM)
    .setSupportTime(false)
    .setTwelveHour(true)
    //结果回调(必须)
    .setOnDateResultListener(new MDatePickerDialog.OnDateResultListener() {
        @Override
        public void onDateResult(long date) {
            // date
        }
    })
    .build()
    .show();
```
### 演示效果

1. MPickView

![MPickView](https://cdn.nlark.com/yuque/0/2021/gif/644330/1616259412628-34df5914-095e-4ef6-8afe-4d9fb128054e.gif)

2. MDatePicker

![MDatePicker](https://cdn.nlark.com/yuque/0/2021/gif/644330/1616259411686-8f8bec0f-fc98-4cbb-99c2-03ae7b18965b.gif)

### 版本记录

#### v1.0.0

- 发布初始版本

#### v1.0.1

- 【修复】日期回调月份错误
- 【修复】修年月日时分秒都显示时
- 【修复】低屏幕密度手机显示不全的问题
- 【修复】其他可能影响使用的问题

#### v1.0.2

- 【修复】底部显示时不能铺满全屏
- 【优化】优化调用方式

#### v1.0.3

- 【新增】仅显示年月功能
- 【修复】确定、取消位置显示异常

#### v1.0.4

- 【新增】设置年月日初始值
- 【修复】仅显示年月时月份回调错误
- 【优化】添加日志开关

#### v1.0.5

- 【修复】修复选择月份时天数更新异常

##### v1.0.6

- 【适配】切换到AndroidX
- 【新增】设置字体大小
- 【新增】设置文字颜色
- 【优化】微调文字绘制位置

欢迎在 [issue](https://github.com/jzmanu/MDatePickerSample/issues) 上反映问题，以便及时修复。

### 联系作者

个人微信公众号 **躬行之** 。

![image](https://cdn.nlark.com/yuque/0/2021/png/644330/1616259548010-7b8a24c3-393f-4b26-aa26-d01c28d0f538.png)