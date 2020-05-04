
# 好营养SDK使用说明 - Android

[![](https://jitpack.io/v/elinkthings/PabulumSDKRepositoryAndroid.svg)](https://jitpack.io/#elinkthings/PabulumSDKRepositoryAndroid)

[aar包下载地址](https://github.com/elinkthings/PabulumSDKRepositoryAndroid/releases)

[English documentation](README.md)



## 使用条件
1. 最低版本 android4.4（API 19）

2. 设备所使用的蓝牙版本需要4.0及以上

3. 依赖环境androidx


## 导入SDK


```
repositories {
    flatDir {
        dirs 'libs'
    }
}


步骤1.将JitPack存储库添加到您的构建文件中
将其添加到存储库末尾的root build.gradle中：
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

步骤2.添加依赖项
	dependencies {
	        implementation 'com.github.inet2018:PabulumSDKRepositoryAndroid:1.2.6'
	}

也可以使用aar包依赖,请自行下载放到项目的libs中


```

## 权限设置

```
<!--In most cases, you need to ensure that the device supports BLE.-->
<uses-feature
    android:name="android.hardware.bluetooth_le"
    android:required="true"/>

<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>

<!--Android 6.0 and above. Bluetooth scanning requires one of the following two permissions. You need to apply at run time.-->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

<!--Optional. If your app need dfu function.-->
<uses-permission android:name="android.permission.INTERNET"/>
```

>  6.0及以上系统必须要定位权限，且需要手动获取权限

## 开始接入

- 首先给SDK配置key和secret，[申请地址](http://sdk.aicare.net.cn)
```
 //在主项目的application中调用
PabulumSDK.getInstance().init(this,"key","secret");
```



- 在AndroidManifest.xml application标签下面增加
```
<application>
    ...

    <service android:name="cn.net.aicare.pabulumlibrary.pabulum.PabulumService" />

</application>

```


- 你可以直接让你自己的`Activity`类继承`BleProfileServiceReadyActivity`

```
public class MyActivity extends BleProfileServiceReadyActivity
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断手机设备是否支持Ble
        if (!ensureBLESupported()) {
            T.showShort(this, R.string.not_support_ble);
            finish();
        }
        //判断是否有定位权限,此方法没有进行封装具体代码可在demo中获得，也可按照自己方式去调用请求权限方法
        initPermissions();
        //判断蓝牙是否打开，若需要换样式，可自己去实现
        if (!isBLEEnabled()) {
            showBLEDialog();
        }
    }


```

## 扫描设备，停止扫描设备,查看扫描状态
与扫描相关的API如下，详情参考BleProfileServiceReadyActivity类，具体使用参考demo

```
  //调用startScan方法开启扫描
  startScan();
  //onLeScanCallback(BluetoothDevice device, int rssi)接口会回调获取到符合协议的设备
    @Override
    protected void onLeScanCallback(BluetoothDevice device, int rssi) {
               //符合协议的设备


    }
//调用stopScan方法停止扫描
 stopScan();


```



## 连接设备，断开设备

与连接相关的API如下，详情参考BleProfileServiceReadyActivity类，具体使用参考demo。

```
//调用connectDevice(BluetoothDevice device)方法去连接设备 需要传入BluetoothDevice对象,可以在onLeScanCallback（）回调方法中获得 。

connectDevice(BluetoothDevice device)
//在onStateChanged可以获取到连接状态
@Override
    public void onStateChanged(String deviceAddress, int state) {
        super.onStateChanged(deviceAddress, state);
        //state 具体状态看类说明
        //连接成功：(state = BleProfileService.STATE_CONNECTED)
        //断开连接：(state = BleProfileService.STATE_DISCONNECTED)
        //校验成功：(state = BleProfileService.STATE_INDICATION_SUCCESS)
    }
//调用PabulumService.PabulumBinder类中disconnect方法去断开连接
 binder.disconnect()
```

使用`connectDevice`方法连接，，使用`onStateChanged`方法监听连接的状态，使用`onError`方法监听连接过程中的异常，以便于进行额外的处理和问题排查。使用`isConnected`方法判断连接是否已经建立。

## 连接成功，接受秤返回的数据
以下方法或接口可直接在继承BleProfileServiceReadyActivity类后自动获得

```
//onServiceBinded方法中获得PabulumService.PabulumBinder的实例
    @Override
    protected void onServiceBinded(BleProfileService.LocalBinder binder) {
        this.binder = (PabulumService.PabulumBinder) binder;

   }
    //设备返回数据
    @Override
    protected void getFoodData(FoodData foodData) {
         //FoodData(String data, byte unit, byte deviceType, double  weight);
         //data(重量, 对应当前单位),
         //unit(单位，同上 unitType）,
         //deviceType(0x04: 无小数点，0x05:大量程),
         //weight(重量，以 g 为单位)
    }
    //支持的单位列表
    @Override
    protected void getUnits(int[] units) {
       //PabulumBleConfig 中定义的单位列表
       UNIT_G = 0x00; //克
       UNIT_ML = 0x01; //毫升
       UNIT_LB = 0x02; //磅
       UNIT_OZ = 0x03; //盎司
       UNIT_KG = 0x04; //千克
       UNIT_FG = 0x05; //斤=500g
       UNIT_ML_MILK = 0x06; //牛奶 ml
       UNIT_ML_WATER = 0x07; //水 ml
       UNIT_FL_OZ_MILK = 0x08; //牛奶 floz
       UNIT_FL_OZ_WATER = 0x09; //水 floz
    }
    //当前单位
    @Override
    protected void getUnit(byte unitType) {

    }
    //设备返回的计时操作指令
    @Override
    protected void getTimeStatus(int status) {
        //status 状态(开=1,关=2,重置=3)
    }

    //设备返回的倒计时开始指令
   @Override
    protected void getCountdownStart(int time) {
        //time 秒
    }

    //设备返回的同步时间指令
    @Override
    protected void getSynTime(byte cmdType, int timeS) {
        //timeS 秒
        //cmdType 为指令类型, 范围
        //PabulumBleConfig.SYN_TIME -->正计时同步时间
        //PabulumBleConfig.SYN_TIME_LESS -->倒计时同步时间
        //PabulumBleConfig.TIMING_PAUSE -->正计时暂停同步时间
        //PabulumBleConfig.TIMING_PAUSE_LESS -->倒计时暂停同步时间
    }

    //错误指令
    @Override
    protected void getErrCodes(int[] ints) {
        //err[0]==1 超载,0 正常;err[1]==1 低电,0 正常
    }
    //停止闹铃指令
    @Override
    protected void getStopAlarm() {

    }

    //设备返回的版本信息
    @Override
    protected void getBleVersion(String version) {

    }

    //设备的信号强度
    @Override
    public void onReadRssi(int rssi) {

    }

    //设备返回的透传数据(不能用 0xAC 和 0xAD 开头,避免与 SDK 其他协议冲突)：
    @Override
    protected void getPenetrateData(byte[] bytes) {

    }

```
> 注意：这些接口或方法部分需要APP给体脂下发命令才会有返回数据.
##  APP给设备下发指令
在BleProfileServiceReadyActivity.onServiceBinded(PabulumService.PabulumBinder binder)获得PabulumService.PabulumBinder的实例，调用binder里面方法


```
秤支持的单位:getUnits();

设置单位：setUnit(byte unitType); //unitType 支持的列表从getUnits()中获取

去皮：netWeight();

关机：powerOff();

自定义写数据接口：writeValue(byte[] value);//value 不能为 null， 长度不能大于 20

//---------咖啡秤定时相关功能

开始计时:startTime();

暂停,正计时:pauseTime(int time);

暂停,倒计时:pauseTimeLess(int time);

重置计时:resetTime();

开始倒计时:startTimeLess(int time);//time 为秒

停止闹铃指令:stopAlarm();

```


## 类说明


#### FoodData(重量数据)
| 类型|	参数名|	说明
| ---|---|---
| String | data; | 重量，对应当前单位
| byte | unit; | 单位
| byte | deviceType; | 设备类型：0x04(无小数点), 0x05(大量程)
| double | weight; | 重量，对应单位g

## 版本历史
|版本号|更新时间|作者|更新信息|
|:----|:---|:-----|-----|
| 1.0 	| 2016/04/15 | Suzy 	| 初步版本
| 1.0.1 | 2016/09/08 | Suzy 	| 兼容新协议
| 1.0.2 | 2017/01/14 | Suzy 	| 增加透传接口
| 1.0.3 | 2017/01/19 | Suzy 	| 增加设置重量接口
| 1.0.4 | 2017/03/31 | Suzy 	| 调换设置重量高低字节
| 1.0.7 | 2017/06/28 | Suzy 	| 增加关机指令接口
| 1.0.8 | 2017/09/01 | Suzy 	| 增加设置（总）卡路里数据接口
| 1.0.9 | 2017/09/20 | Suzy 	| 增加设置（总）脂肪数据等接口
| 1.1.0 | 2018/05/12 | Suzy 	| 增加透传接口
| 1.1.1 | 2018/08/15 | Suzy 	| So 库更新
| 1.1.2 | 2018/12/03 | 陈福行 	| 增加获取 DID 指令
| 1.1.3 | 2019/01/24 | 陈福行 	| 修复蓝牙数据错误导致闪退问题
| 1.1.4 | 2019/03/07 | 陈福行 	| 增加千克/斤,增加动态权限提示
| 1.1.5 | 2019/04/26 | 陈福行 	| 增加 get 方法获取类型,修复校验问题
| 1.1.6 | 2019/04/29 | 陈福行 	| 增加咖啡秤同步时间
| 1.1.7 | 2019/05/15 | 陈福行 	| 修复注销广播可能会出现异常的问题
| 1.1.8 | 2019/05/22 | 陈福行 	| 增加倒计时功能
| 1.1.9 | 2019/06/26 | 陈福行 	| 修改咖啡秤暂停指令,增加时间校验
| 1.2.0 | 2019/07/16 | 陈福行 	| 修改咖啡功能操作指令
| 1.2.1 | 2019/08/12 | 陈福行 	| 修改 jar 中的 bug
| 1.2.2 | 2019/10/08 | 陈福行 	| 增加错误指令和单位
| 1.2.3 | 2019/11/28 | 陈福行 	| 增加闹铃停止指令
| 1.2.4 | 2020/03/03 | 陈福行 	| 增加透传数据接口

## FAQ

- 扫描不到蓝牙设备？

1.查看App权限是否正常,6.0及以上系统必须要定位权限，且需要手动获取权限
2.查看手机的定位服务是否开启,部分手机可能需要打开GPS
3.拔掉电池重启秤
4.是否被其他手机连接(秤未被连接时，秤盘上蓝牙图标会不断闪烁)
5.PabulumService是否在AndroidManifest中注册

- 协议支持哪些单位？

1.单位最多只支持8种，具体支持什么单位请通过getUnits()方法获取,如无法获取,请参照秤的出厂设置。





## 联系我们
深圳市易连物联网有限公司

电话：0755-81773367

官网：www.elinkthings.com

邮箱：app@elinkthings.com