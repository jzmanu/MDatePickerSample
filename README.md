[ ![Download](https://api.bintray.com/packages/jzman/maven/MDatePicker/images/download.svg?version=1.0.1) ](https://bintray.com/jzman/maven/MDatePicker/1.0.1/link)

MDatePickerDialog 常用设置如下：

设置| 对应属性 |设置方法|默认值
---|---|---|---
标题|mTitle|setTitle(String mTitle) |日期选择
显示位置 | mGravity|setGravity(int mGravity)|Gravity.CENTER
时候支持点击外部区域取消|isCanceledTouchOutside|setCanceledTouchOutside(boolean canceledTouchOutside)|false
是否支持时间 | isSupportTime|setSupportTime(boolean supportTime)|false
是否支持12小时制 |isTwelveHour|setTwelveHour(boolean twelveHour)|false

### 使用


##### gradle


```java
compile 'com.manu:MDatePicker:1.0.1'
```

##### maven

```java
<dependency>
	<groupId>com.manu</groupId>
	<artifactId>MDatePicker</artifactId>
	<version>1.0.1</version>
	<type>pom</type>
</dependency>
```

##### ivy


```java
<dependency org="com.manu" name="MDatePicker" rev="1.0.1">
	<artifact name="MDatePicker" ext="pom"></artifact>
</dependency>
```

MDatePickerDialog 的使用和普通的 Dialog 一样，参考如下：

```java
MDatePickerDialog dialog = new MDatePickerDialog.Builder(this)
    //附加设置(非必须,有默认值)
    .setCanceledTouchOutside(true)
    .setGravity(Gravity.BOTTOM)
    .setSupportTime(false)
    .setTwelveHour(true)
    .setCanceledTouchOutside(false)
    //结果回调(必须)
    .setOnDateResultListener(new MDatePickerDialog.OnDateResultListener() {
        @Override
        public void onDateResult(long date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
            dateFormat.applyPattern("yyyy-MM-dd HH:mm");
            Toast.makeText(MainActivity.this, dateFormat.format(new Date(date)), Toast.LENGTH_SHORT).show();
        }
    })
    .build();
dialog.show();
```
### 显示效果

MPickView|MDatePickerDialog
---|---
![MPickView](https://github.com/jzmanu/MDatePickerSample/blob/master/screenshot/MPickView.gif)|![MDatePickerDialog](https://github.com/jzmanu/MDatePickerSample/blob/master/screenshot/MDatePickerDialog.gif)

### 版本记录

#### 1.0.0

草稿版本，可以使用不过有 bug.

#### 1.0.1

- 修复日期回调月份错误 Bug
- 修年月日时分秒都显示时。低屏幕密度手机显示不全的问题
- 其他可能影响使用的问题

欢迎在 [issue](https://github.com/jzmanu/MDatePickerSample/issues) 上反映问题，以便及时修复。

### 作者

可以关注公众号：jzman-blog，一起交流学习。
![image](https://upload-images.jianshu.io/upload_images/2494569-f1ae978df99e1007.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/600/format/webp)
