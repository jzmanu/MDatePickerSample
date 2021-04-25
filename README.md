[![](https://jitpack.io/v/jzmanu/MDatePickerSample.svg)](https://jitpack.io/#jzmanu/MDatePickerSample)

#### 基本属性


MDatePicker 基本属性如下：

设置 |设置方法|默认值
---|---|---
标题|setTitle(String mTitle) |日期选择
显示位置 |setGravity(int mGravity)|Gravity.CENTER
时候支持点击外部区域取消|setCanceledTouchOutside(boolean canceledTouchOutside)|false
是否支持时间|setSupportTime(boolean supportTime)|false
是否支持12小时制|setTwelveHour(boolean twelveHour)|false
是否仅显示年月|setOnlyYearMonth(boolean onlyYearMonth)|false

### 使用

1.在 build.gradle 文件中引入：


```java
implementation 'com.github.jzmanu:MDatePickerSample:v1.0.3'
```

2.MDatePicker 的使用和普通的 Dialog 一样，参考如下：

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
### 显示效果

1.MPickView

![MPickView](https://cdn.nlark.com/yuque/0/2021/gif/644330/1616259412628-34df5914-095e-4ef6-8afe-4d9fb128054e.gif)

2.MDatePicker

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

- 【修复】确定、取消位置显示异常
- 【新增】仅显示年月功能

欢迎在 [issue](https://github.com/jzmanu/MDatePickerSample/issues) 上反映问题，以便及时修复。

### 联系作者

个人微信公众号 **躬行之** 。

![image](https://cdn.nlark.com/yuque/0/2021/png/644330/1616259548010-7b8a24c3-393f-4b26-aa26-d01c28d0f538.png)
