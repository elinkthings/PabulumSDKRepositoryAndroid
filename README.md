[TOC]

# Pabulum SDK Instructions - Android

[![](https://jitpack.io/v/elinkthings/PabulumSDKRepositoryAndroid.svg)](https://jitpack.io/#elinkthings/PabulumSDKRepositoryAndroid)

[aar package download link](https://github.com/elinkthings/PabulumSDKRepositoryAndroid/releases)

[key registration](http://sdk.aicare.net.cn)



## Conditions of Use
1. Minimum version android4.4 (API 19)

2. The Bluetooth version used by the device requires 4.0 and above

3. Depends on the environment androidx

##  Import SDK


```
repositories {
    flatDir {
        dirs 'libs'
    }
}


Step 1. Add the JitPack repository to your build file
Add it to the root build.gradle at the end of the repository:
allprojects {
repositories {
...
maven {url 'https://jitpack.io'}
}
}

Step 2. Add dependencies
dependencies {
 implementation 'com.github.elinkthings:PabulumSDKRepositoryAndroid:1.2.6'
}





You can also use the aar package dependency, please download it yourself and put it in the project's libs



```

##   permission settings

```
<!-In most cases, you need to ensure that the device supports BLE .-->
<uses-feature
    android: name = "android.hardware.bluetooth_le"
    android: required = "true" />

<uses-permission android: name = "android.permission.BLUETOOTH" />
<uses-permission android: name = "android.permission.BLUETOOTH_ADMIN" />

<!-Android 6.0 and above. Bluetooth scanning requires one of the following two permissions. You need to apply at run time .-->
<uses-permission android: name = "android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android: name = "android.permission.ACCESS_FINE_LOCATION" />

<!-Optional. If your app need dfu function .-->
<uses-permission android: name = "android.permission.INTERNET" />
```

> 6.0 and above systems must locate permissions and need to manually obtain permissions

##   Start access
- First configure the key and secret for the SDK, [application address](http://sdk.aicare.net.cn)
```
  // Called in the application of the main project
PabulumSDK.getInstance (). Init (this, "key", "secret");
```

- Add under AndroidManifest.xml application tag
```
<application>
    ...

    <service android: name = "cn.net.aicare.pabulumlibrary.pabulum.PabulumService" />

</ application>

```

- You can directly make your own Activity class inherit BleProfileServiceReadyActivity

```
public class MyActivity extends BleProfileServiceReadyActivity
      @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        // Determine whether the mobile device supports Ble
        if (! ensureBLESupported ()) {
            T.showShort (this, R.string.not_support_ble);
            finish ();
        }
        // Determine whether there is positioning permission, this method does not encapsulate the specific code can be obtained in the demo, you can also call the request permission method in your own way
        initPermissions ();
        // Determine whether Bluetooth is turned on, if you need to change the style, you can implement it yourself
        if (! isBLEEnabled ()) {
            showBLEDialog ();
        }
    }


```

##  scan the device, stop scanning the device, check the scan status
The APIs related to scanning are as follows. For details, refer to the BleProfileServiceReadyActivity class. For specific use, refer to the demo

```
  // Call the startScan method to start scanning
  startScan ();
  // onLeScanCallback (BluetoothDevice device, int rssi) interface will callback to get the device that conforms to the protocol
    @Override
    protected void onLeScanCallback (BluetoothDevice device, int rssi) {
               // equipment conforming to the agreement


    }
// Call the stopScan method to stop scanning
 stopScan ();


```



##   connect the device, disconnect the device

The connection-related APIs are as follows. For details, refer to the BleProfileServiceReadyActivity class. For specific usage, refer to the demo.

```
// Call the connectDevice (BluetoothDevice device) method to connect to the device. You need to pass in the BluetoothDevice object, which can be obtained in the onLeScanCallback () callback method.

connectDevice (BluetoothDevice device)
// The connection state can be obtained in onStateChanged
@Override
    public void onStateChanged (String deviceAddress, int state) {
        super.onStateChanged (deviceAddress, state);
        // state specific state see class description
        // The connection is successful: (state = BleProfileService.STATE_CONNECTED)
        // Disconnect: (state = BleProfileService.STATE_DISCONNECTED)
        // Successful verification: (state = BleProfileService.STATE_INDICATION_SUCCESS)
    }
// Call the disconnect method in the PabulumService.PabulumBinder class to disconnect
 binder.disconnect ()
```

Use the `connectDevice` method to connect, use the` onStateChanged` method to monitor the status of the connection, and use the `onError` method to monitor exceptions during the connection process for additional processing and troubleshooting. Use the `isConnected` method to determine whether the connection has been established.

##   Connect successfully, accept the data returned by the scale
The following methods or interfaces can be automatically obtained directly after inheriting the BleProfileServiceReadyActivity class

```
// Get the instance of PabulumService.PabulumBinder in the onServiceBinded method
    @Override
    protected void onServiceBinded (BleProfileService.LocalBinder binder) {
        this.binder = (PabulumService.PabulumBinder) binder;

   }
    // The device returns data
    @Override
    protected void getFoodData (FoodData foodData) {
         // FoodData (String data, byte unit, byte deviceType, double weight);
         // data (weight, corresponding to current unit),
         // unit (unit, same as above unitType),
         // deviceType (0x04: no decimal point, 0x05: large range),
         // weight (weight in g)
    }
    // List of supported units
    @Override
    protected void getUnits (int [] units) {
       // List of units defined in PabulumBleConfig
       UNIT_G = 0x00; // g
       UNIT_ML = 0x01; // ml
       UNIT_LB = 0x02; // pound
       UNIT_OZ = 0x03; // oz
       UNIT_KG = 0x04; // kg
       UNIT_FG = 0x05; // jin = 500g
       UNIT_ML_MILK = 0x06; // milk ml
       UNIT_ML_WATER = 0x07; // water ml
       UNIT_FL_OZ_MILK = 0x08; // milk floz
       UNIT_FL_OZ_WATER = 0x09; // Water floz
    }
    // Current unit
    @Override
    protected void getUnit (byte unitType) {

    }
    // The timing operation instruction returned by the device
    @Override
    protected void getTimeStatus (int status) {
        // status status (on = 1, off = 2, reset = 3)
    }

    // Countdown start instruction returned by the device
   @Override
    protected void getCountdownStart (int time) {
        // time seconds
    }

    // Sync time instruction returned by the device
    @Override
    protected void getSynTime (byte cmdType, int timeS) {
        // timeS seconds
        // cmdType is the instruction type, range
        //PabulumBleConfig.SYN_TIME-> positive timing synchronization time
        //PabulumBleConfig.SYN_TIME_LESS-> Countdown synchronization time
        //PabulumBleConfig.TIMING_PAUSE-> Pause the synchronization time while timing
        //PabulumBleConfig.TIMING_PAUSE_LESS-> Countdown pauses synchronization time
    }

    // Error instruction
    @Override
    protected void getErrCodes (int [] ints) {
        // err [0] == 1 overload, 0 positiveNormal;
        // err [1] == 1 low power, 0 normal
    }
    // stop alarm instruction
    @Override
    protected void getStopAlarm () {

    }

    // Version information returned by the device
    @Override
    protected void getBleVersion (String version) {

    }

    // The signal strength of the device
    @Override
    public void onReadRssi (int rssi) {

    }

    // Transparent transmission data returned by the device (cannot start with 0xAC and 0xAD to avoid conflict with other SDK protocols):
    @Override
    protected void getPenetrateData (byte [] bytes) {

    }

```
> Note: Some of these interfaces or methods require APP to issue commands to body fat to return data.
##   Give instructions to the device
Get an instance of PabulumService.PabulumBinder in BleProfileServiceReadyActivity.onServiceBinded (PabulumService.PabulumBinder binder), call the method inside binder


```
Units supported by the scale: getUnits ();

Setting unit: setUnit (byte unitType); // The list supported by unitType is obtained from getUnits ()

Tare: netWeight ();

Shutdown: powerOff ();

Custom write data interface: writeValue (byte [] value); // value cannot be null, length cannot be greater than 20

// --------- Timing related functions of coffee scale

Start timing: startTime ();

Pause, timing: pauseTime (int time);

Pause, countdown: pauseTimeLess (int time);

Reset timing: resetTime ();

Start countdown: startTimeLess (int time); // time is seconds

Stop alarm command: stopAlarm ();

```


##   categories
####  FoodData (weight data)
```
Type Parameter name Description
String data; // Weight, corresponding to the current unit
byte unit; // unit
byte deviceType; // Device type: 0x04 (no decimal point), 0x05 (large range)
double weight; // Weight, corresponding unit g
```


## Version History
| Version number | Update time | Author | Update information |
|:----|:---|:-----|-----|
| 1.0 	| 2016/04/15 | Suzy 	| Preliminary version
| 1.0.1 | 2016/09/08 | Suzy 	| Compatible with new protocols
| 1.0.2 | 2017/01/14 | Suzy 	| Add transparent transmission interface
| 1.0.3 | 2017/01/19 | Suzy 	| Increase setting weight interface
| 1.0.4 | 2017/03/31 | Suzy 	| Swap setting weight high and low bytes
| 1.0.7 | 2017/06/28 | Suzy 	| Add shutdown command interface
| 1.0.8 | 2017/09/01 | Suzy 	| Increase the setting (total) calorie data interface
| 1.0.9 | 2017/09/20 | Suzy 	| Increase the interface for setting (total) fat data
| 1.1.0 | 2018/05/12 | Suzy 	| Add transparent transmission interface
| 1.1.1 | 2018/08/15 | Suzy 	| So library update
| 1.1.2 | 2018/12/03 | Xing 	| Add Get DID command
| 1.1.3 | 2019/01/24 | Xing 	| Fix flashback issue caused by Bluetooth data error
| 1.1.4 | 2019/03/07 | Xing 	| Increase kg / kg, increase dynamic permission prompt
| 1.1.5 | 2019/04/26 | Xing 	| Add get method to get type, fix verification problem
| 1.1.6 | 2019/04/29 | Xing 	| Increase coffee scale synchronization time
| 1.1.7 | 2019/05/15 | Xing 	| Fixed an issue where logout broadcast may be abnormal
| 1.1.8 | 2019/05/22 | Xing 	| Add countdown function
| 1.1.9 | 2019/06/26 | Xing 	| Modify the coffee scale pause command to increase time verification
| 1.2.0 | 2019/07/16 | Xing 	| Modify coffee function operation instructions
| 1.2.1 | 2019/08/12 | Xing 	| Fix bugs in jar
| 1.2.2 | 2019/10/08 | Xing 	| Add wrong commands and units
| 1.2.3 | 2019/11/28 | Xing 	| Add alarm stop command
| 1.2.4 | 2020/03/03 | Xing 	| Add transparent data interface




## FAQ

- Can't scan the Bluetooth device?

1. Check whether the permissions of the App are normal. The 6.0 and above systems must locate the permissions and need to manually obtain the permissions
2. Check whether the location service of the mobile phone is turned on, some mobile phones may need to turn on the GPS
3. Unplug the battery and restart the scale
4. Whether it is connected by other mobile phones (when the scale is not connected, the Bluetooth icon on the weighing pan will continue to flash)
5. Is PabulumService registered in AndroidManifest

- What units does the agreement support?

1. Units can only support up to 8 types. For specific units, please obtain them by the getUnits () method. If they cannot be obtained, please refer to the factory settings of the scale.




## Contact Us
Shenzhen elink things Co., Ltd.

Phone: 0755-81773367

Official website: www.elinkthings.com

Email: app@elinkthings.com


