package aicare.net.cn.sdk.pabulumsdkrepositoryandroid;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.base.BaseActivity;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.databinding.MainBinding;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.AppUtils;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.Config;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.SPUtils;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.T;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.view.SetRssiDialog;
import cn.net.aicare.pabulumlibrary.PabulumSDK;
import cn.net.aicare.pabulumlibrary.bleprofile.BleProfileService;
import cn.net.aicare.pabulumlibrary.entity.FoodData;
import cn.net.aicare.pabulumlibrary.pabulum.PabulumService;
import cn.net.aicare.pabulumlibrary.utils.L;
import cn.net.aicare.pabulumlibrary.utils.PabulumBleConfig;
import cn.net.aicare.pabulumlibrary.utils.ParseData;

public class MainActivity extends BaseActivity implements SetRssiDialog.OnQueryListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ActivityResultLauncher<String[]> mActivityResultLauncher;

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_title_right:
                new SetRssiDialog(this, defaultRssi, this).show();
                break;
            case R.id.tv_show_state:
                if (binder != null) {
                    binder.disconnect();
                }
                break;
            case R.id.btn_set_weight:
                String weight = mainBinding.etSetWeight.getText().toString().trim();
                if (TextUtils.isEmpty(weight)) {
                    T.showShort(this, R.string.pls_input_weight);
                } else {
                    int wei = Integer.valueOf(weight);
                    if (binder != null) {
                        binder.setWeight(wei);
                    }
                }
                break;
            case R.id.btn_tare:
                if (binder != null) {
                    binder.netWeight();
                }
                break;
            case R.id.btn_power_off:
                if (binder != null) {
                    binder.powerOff();
                    handler.postDelayed(disconnectRunnable, 1000);
                }
                break;
            //获取DID  2018-12-3
            case R.id.btn_did:
                if (binder != null) {
                    L.i(TAG, "点击请求获取did");
                    binder.getDid();
                }
                break;
            //2019/4/29
            case R.id.btn_start:
                if (binder != null) {
                    L.i(TAG, "开始计时");
                    binder.startTime();
                }
                break;    //2019/5/22
            case R.id.btn_start_less:
                if (binder != null) {
                    L.i(TAG, "倒计时开始");
                    binder.startTimeLess(180);
                }
                break;
            //2019/6/25
            case R.id.btn_pause:
                if (binder != null) {
                    L.i(TAG, "正计时暂停");
                    binder.pauseTime(80);
                }
                break;
            //2019/6/25
            case R.id.btn_pause_less:
                if (binder != null) {
                    L.i(TAG, "倒计时暂停");
                    binder.pauseTimeLess(90);
                }
                break;
            //2019/4/29
            case R.id.btn_reset:
                if (binder != null) {
                    L.i(TAG, "重置计时");
                    binder.resetTime();
                }
                break;
            case R.id.btn_write_value:
                if (binder != null) {
                    //透传数据测试
                    byte[] value = initRandomByteArr(new Random().nextInt(21));
                    L.e(TAG, "value: " + ParseData.arr2Str(value));
                    binder.writeValue(value);
                }
                break;
            case R.id.btn_get_version:
                if (binder != null) {
                    L.i(TAG, "点击请求获取版本号");
                    binder.getVersion();

                }
                break;
            case R.id.btn_get_units:
                if (binder != null) {
                    L.i(TAG, "点击请求获取单位列表");
                    binder.getUnits();

                }
                break;
            default:
                setData(view.getId());
                break;
        }
    }

    private byte[] initRandomByteArr(int count) {
        byte[] bytes = new byte[count];
        Random random = new Random();
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = Integer.valueOf(random.nextInt(256)).byteValue();
        }

        return bytes;
    }

    private void setData(int id) {
        String data = mainBinding.etSetWeight.getText().toString().trim();
        if (TextUtils.isEmpty(data)) {
            T.showShort(this, R.string.pls_input_weight);
        } else {
            int wei = Integer.parseInt(data);
            if (binder != null) {
                switch (id) {
                    case R.id.btn_cal:
                        binder.setCal(wei);
                        break;
                    case R.id.btn_all_cal:
                        binder.setAllCal(wei);
                        break;
                    case R.id.btn_fat:
                        binder.setFat(wei);
                        break;
                    case R.id.btn_all_fat:
                        binder.setAllFat(wei);
                        break;
                    case R.id.btn_pro:
                        binder.setPro(wei);
                        break;
                    case R.id.btn_all_pro:
                        binder.setAllPro(wei);
                        break;
                    case R.id.btn_car:
                        binder.setCar(wei);
                        break;
                    case R.id.btn_all_car:
                        binder.setAllCar(wei);
                        break;
                    case R.id.btn_fib:
                        binder.setFib(wei);
                        break;
                    case R.id.btn_all_fib:
                        binder.setAllFib(wei);
                        break;
                    case R.id.btn_cho:
                        binder.setCho(wei);
                        break;
                    case R.id.btn_all_cho:
                        binder.setAllCho(wei);
                        break;
                    case R.id.btn_sod:
                        binder.setSod(wei);
                        break;
                    case R.id.btn_all_sod:
                        binder.setAllSod(wei);
                        break;
                    case R.id.btn_sug:
                        binder.setSug(wei);
                        break;
                    case R.id.btn_all_sug:
                        binder.setAllSug(wei);
                        break;
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Byte unit;
        switch (checkedId) {
            case R.id.rb_g:
                unit = PabulumBleConfig.UNIT_G;
                break;
            case R.id.rb_ml:
                unit = PabulumBleConfig.UNIT_ML;
                break;
            case R.id.rb_lb:
                unit = PabulumBleConfig.UNIT_LB;
                break;
            case R.id.rb_oz:
                unit = PabulumBleConfig.UNIT_OZ;
                break;
            case R.id.rb_kg:
                unit = PabulumBleConfig.UNIT_KG;
                break;
            case R.id.rb_fg:
                unit = PabulumBleConfig.UNIT_FG;
                break;
            case R.id.rb_ml_milk:
                unit = PabulumBleConfig.UNIT_ML_MILK;
                break;
            case R.id.rb_ml_water:
                unit = PabulumBleConfig.UNIT_ML_WATER;
                break;
            case R.id.rb_floz_milk:
                unit = PabulumBleConfig.UNIT_FL_OZ_MILK;
                break;
            case R.id.rb_floz_water:
                unit = PabulumBleConfig.UNIT_FL_OZ_WATER;
                break;
            case R.id.rb_lb_lb:
                unit = PabulumBleConfig.UNIT_LB_LB;
                break;
            default:
                unit = null;
                break;
        }
        if (unit != null && binder != null) {
            binder.setUnit(unit);
        }
    }


    private PabulumService.PabulumBinder binder;
    private int defaultRssi;

    private final static int DEFAULT_RSSI = -70;

    private String preWeight = "0";

    private MainBinding mainBinding;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = MainBinding.inflate(getLayoutInflater());
        L.isDebug = true;
        setContentView(mainBinding.getRoot());
//        PabulumSDK.getInstance().init(this, "66617c04a3bbc7d2", "001814ae6212dd8c4657444c4b");
        PabulumSDK.getInstance().init(this);
        initData();
        initViews();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && !AppUtils.isLocServiceEnable(this)) {
            T.showShort(this, this.getString(R.string.permissions_server));
        }
        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), mActivityResultCallback);
        if (ensureBLESupported()) {//判断设备是否支持BLE，true（支持），反之则反。
            initPermissions();
        }
        reset();
    }

    private void initData() {
        defaultRssi = (int) SPUtils.get(this, Config.DEFAULT_RSSI, DEFAULT_RSSI);
    }

    private void initViews() {
        setTitleRight(Math.abs(defaultRssi));
        // 点击事件
        mainBinding.layoutTitle.btnTitleRight.setOnClickListener(this);
        mainBinding.tvShowState.setOnClickListener(this);
        mainBinding.btnSetWeight.setOnClickListener(this);
        mainBinding.btnTare.setOnClickListener(this);
        mainBinding.btnPowerOff.setOnClickListener(this);
        mainBinding.btnCal.setOnClickListener(this);
        mainBinding.btnAllCal.setOnClickListener(this);
        mainBinding.btnFat.setOnClickListener(this);
        mainBinding.btnAllFat.setOnClickListener(this);
        mainBinding.btnPro.setOnClickListener(this);
        mainBinding.btnAllPro.setOnClickListener(this);
        mainBinding.btnCar.setOnClickListener(this);
        mainBinding.btnAllCar.setOnClickListener(this);
        mainBinding.btnFib.setOnClickListener(this);
        mainBinding.btnAllFib.setOnClickListener(this);
        mainBinding.btnCho.setOnClickListener(this);
        mainBinding.btnAllCho.setOnClickListener(this);
        mainBinding.btnSod.setOnClickListener(this);
        mainBinding.btnAllSod.setOnClickListener(this);
        mainBinding.btnSug.setOnClickListener(this);
        mainBinding.btnAllSug.setOnClickListener(this);
        mainBinding.btnWriteValue.setOnClickListener(this);
        mainBinding.btnDid.setOnClickListener(this);
        mainBinding.btnGetVersion.setOnClickListener(this);
        mainBinding.btnStart.setOnClickListener(this);
        mainBinding.btnStartLess.setOnClickListener(this);
        mainBinding.btnPause.setOnClickListener(this);
        mainBinding.btnReset.setOnClickListener(this);
        mainBinding.btnPauseLess.setOnClickListener(this);
        mainBinding.btnGetUnits.setOnClickListener(this);

        // 开关事件
//        @OnCheckedChanged({R.id.rb_g, R.id.rb_lb, R.id.rb_ml, R.id.rb_oz, R.id.rb_kg, R.id.rb_fg, R.id.rb_ml_milk,
//        R.id.rb_ml_water, R.id.rb_floz_milk, R.id.rb_floz_water, R.id.rb_lb_lb})
//        mainBinding.rbG.setOnCheckedChangeListener(this);
//        mainBinding.rbLb.setOnCheckedChangeListener(this);
//        mainBinding.rbMl.setOnCheckedChangeListener(this);
//        mainBinding.rbOz.setOnCheckedChangeListener(this);
//        mainBinding.rbKg.setOnCheckedChangeListener(this);
//        mainBinding.rbFg.setOnCheckedChangeListener(this);
//        mainBinding.rbMlMilk.setOnCheckedChangeListener(this);
//        mainBinding.rbMlWater.setOnCheckedChangeListener(this);
//        mainBinding.rbFlozMilk.setOnCheckedChangeListener(this);
//        mainBinding.rbFlozWater.setOnCheckedChangeListener(this);
//        mainBinding.rbLbLb.setOnCheckedChangeListener(this);
        mainBinding.rgUnit.setOnCheckedChangeListener(this);
        mainBinding.rgUnitTwo.setOnCheckedChangeListener(this);
    }

    private void reset() {
        setCurrentRssi(null);
        setBleVersion(getResources().getString(R.string.no_version));
        mainBinding.tvShowResult.setText(String.valueOf(preWeight));
        mainBinding.tvShowResult.setTextColor(getResources().getColor(R.color.black_theme));
    }


    /**
     * 设置状态信息
     *
     * @param object
     */
    private void setState(Object object) {
        if (object instanceof Integer) {
            mainBinding.tvShowState.setText((Integer) object);
        } else if (object instanceof String) {
            mainBinding.tvShowState.setText((String) object);
        }
    }

    /**
     * 设置信号显示
     *
     * @param object
     */
    private void setCurrentRssi(Object object) {
        if (object == null) {
            mainBinding.tvShowRssi.setText(R.string.no_rssi);
        } else {
            if (object instanceof Integer) {
                mainBinding.tvShowRssi.setText(String.format(getResources().getString(R.string.current_rssi), (Integer) object));
            }
        }
    }

    private void setBleVersion(String version) {
        mainBinding.tvShowVersion.setText(version);

    }


    @Override
    protected void getTimeStatus(int status) {
        //2019/4/29
        L.i(TAG, "获取操作状态:" + status);
        Toast.makeText(MainActivity.this, "操作状态:" + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void getCountdownStart(int time) {
        //2019/5/22
        L.i(TAG, "倒计时开始:" + time);
        Toast.makeText(MainActivity.this, "倒计时开始:" + time, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void getSynTime(byte cmdType, int timeS) {
        //2019/5/22
        String tyepName = "";
        switch (cmdType) {

            case PabulumBleConfig.SYN_TIME:
                //时间同步
                tyepName = "时间同步";
                break;
            case PabulumBleConfig.SYN_TIME_LESS:
                //倒计时时间同步
                tyepName = "倒计时时间同步";
                break;
            case PabulumBleConfig.TIMING_PAUSE:
                //正计时暂停时间同步
                tyepName = "正计时暂停时间同步";
                Toast.makeText(MainActivity.this, "正计时暂停", Toast.LENGTH_SHORT).show();
                break;
            case PabulumBleConfig.TIMING_PAUSE_LESS:
                //倒计时暂停时间同步
                tyepName = "倒计时暂停时间同步";
                Toast.makeText(MainActivity.this, "倒计时暂停", Toast.LENGTH_SHORT).show();
                break;

        }
        L.i(TAG, "获取时间同步:" + timeS + "||" + tyepName);
        mainBinding.tvShowTime.setText("TIME:" + timeS);
    }

    @Override
    protected void getPenetrateData(byte[] data) {
        L.i(TAG, "透传数据:" + ParseData.arr2Str(data));

    }

    @Override
    protected void getBleDID(int did) {
        L.i(TAG, "获取did成功:" + did);
        mainBinding.tvShowDid.setText("DID:" + did);
    }

    /**
     * 设置右边title
     *
     * @param rssi
     */
    private void setTitleRight(int rssi) {
        mainBinding.layoutTitle.btnTitleRight.setText(String.format(getResources().getString(R.string.default_rssi), rssi));
    }

    @Override
    protected void onServiceBinded(BleProfileService.LocalBinder binder) {//成功绑定服务
        /*
        获取服务的binder通过binder执行蓝牙操作
         */
        this.binder = (PabulumService.PabulumBinder) binder;
        this.binder.getDeviceAddress();//获取当前连接设备的蓝牙地址
        this.binder.getDeviceName();//获取当前连接设备的名称
        L.e(TAG, "onServiceBinded绑定服务成功:" + binder);
    }

    @Override
    public void onStateChanged(int state) {
        super.onStateChanged(state);
        switch (state) {
            case BleProfileService.STATE_CONNECTED:
                L.e(TAG, "onDeviceConnected");
                if (binder != null) {
                    setState(String.format(getResources().getString(R.string.current_device), binder.getDeviceAddress()));
                }
                break;
            case BleProfileService.STATE_DISCONNECTED:
                L.e(TAG, "onDeviceDisconnected");
                setState(R.string.disconnected);
                preWeight = "0";
                reset();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startScan();
                    }
                }, 1000);
                break;
            case BleProfileService.STATE_INDICATION_SUCCESS://订阅蓝牙服务成功
                L.e(TAG, "onIndicationSuccess");
                if (binder != null) {
                    binder.setUnit(preUnit);//设置单位（订阅成功后，同步APP单位到蓝牙，保持两端单位一致）
                }
                mainBinding.rgUnit.check(R.id.rb_g);
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(String msg, int errorCode) {
        T.showLong(this, "msg = " + msg + "; code = " + errorCode);
    }

    private int countRssi = 0;//判断是否需要断开

    @Override
    public void onReadRssi(int rssi) {
        L.e(TAG, "onReadRssi rssi: " + rssi);
        setCurrentRssi(Math.abs(rssi));
        if (Math.abs(rssi) > Math.abs(defaultRssi)) {
            countRssi += 1;
        } else {
            countRssi = 0;
        }
        if (countRssi >= 20) {
            if (binder != null) {
                binder.disconnect();
            }
        }
    }

    private int countWei = 0;//判断数据是否稳定

    @Override
    protected void getUnit(byte unitType) {//秤返回的单位信息
        L.e(TAG, "unitType = " + unitType);
        preUnit = unitType;
        showUnit(preUnit);
    }


    @Override
    protected void getUnits(int[] units) {//支持的单位列表
        L.e(TAG, "支持的单位列表 = " + Arrays.toString(units));
        T.showShort(this, "支持的单位列表 = " + Arrays.toString(units));
    }

    @Override
    protected void getBleVersion(String version) {
        L.e(TAG, "version = " + version);
        T.showShort(this, "获取版本号成功");
        setBleVersion(String.format(getResources().getString(R.string.ble_version), version));
    }

    @Override
    protected void onLeScanCallback(BluetoothDevice device, int rssi) {
        if (rssi >= defaultRssi) {
            connectDevice(device);
        }
    }

    @Override
    protected void onStartScan() {
        setState(R.string.scan_ing);
    }

    @Override
    protected void bluetoothStateOn() {//蓝牙已开启
        super.bluetoothStateOn();
        setState(R.string.ble_state_on);
        L.e(TAG, "bluetoothStateOn");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startScan();
            }
        }, 500);
    }

    private byte preUnit = PabulumBleConfig.UNIT_G;
    private boolean isBleChangeUnit = false;

    @Override
    protected void getFoodData(FoodData foodData) {
        if (foodData == null) {//2017-06-16为空直接返回
            return;
        }
        L.e(TAG, "weight = " + foodData.getData());
        String curWeight = foodData.getData();
        if (TextUtils.equals(curWeight, preWeight)) {
            countWei += 1;
        } else {
            countWei = 0;
            preWeight = curWeight;
        }
        if (countWei >= 5) {
            mainBinding.tvShowResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            mainBinding.tvShowResult.setTextColor(getResources().getColor(R.color.black_theme));
        }
        if (foodData.getUnit() != preUnit) {
            preUnit = foodData.getUnit();
            isBleChangeUnit = true;
            L.e("PabulumService", "isBleChangeUnit = true");
            showUnit(preUnit);
        }
        String unitStr = getUnitStr(preUnit);

        mainBinding.tvShowResult.setText(preWeight + " " + unitStr + "\nType:" + foodData.getDeviceType() + "\n" + foodData.getWeight() + "g");
    }


    private void showUnit(int preUnit) {
        L.e("PabulumService", "showUnit = " + preUnit);
        mainBinding.rgUnit.setOnCheckedChangeListener(null);
        mainBinding.rgUnitTwo.setOnCheckedChangeListener(null);
        switch (preUnit) {
            case PabulumBleConfig.UNIT_G:
                mainBinding.rgUnit.check(R.id.rb_g);
                break;
            case PabulumBleConfig.UNIT_ML:
                mainBinding.rgUnit.check(R.id.rb_ml);
                break;
            case PabulumBleConfig.UNIT_LB:
                mainBinding.rgUnit.check(R.id.rb_lb);
                break;
            case PabulumBleConfig.UNIT_OZ:
                mainBinding.rgUnit.check(R.id.rb_oz);
                break;
            case PabulumBleConfig.UNIT_KG:
                mainBinding.rgUnit.check(R.id.rb_kg);
                break;
            case PabulumBleConfig.UNIT_FG:
                mainBinding.rgUnit.check(R.id.rb_fg);
                break;
            case PabulumBleConfig.UNIT_ML_MILK:
                mainBinding.rgUnitTwo.check(R.id.rb_ml_milk);
                break;
            case PabulumBleConfig.UNIT_ML_WATER:
                mainBinding.rgUnitTwo.check(R.id.rb_ml_water);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_MILK:
                mainBinding.rgUnitTwo.check(R.id.rb_floz_milk);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_WATER:
                mainBinding.rgUnitTwo.check(R.id.rb_floz_water);
                break;
            case PabulumBleConfig.UNIT_LB_LB:
                mainBinding.rgUnitTwo.check(R.id.rb_lb_lb);
                break;
            default:
                break;
        }
        mainBinding.rgUnit.setOnCheckedChangeListener(this);
        mainBinding.rgUnitTwo.setOnCheckedChangeListener(this);
    }


    private String getUnitStr(int preUnit) {
        String unitStr = getString(R.string.unit_g);
        switch (preUnit) {
            case PabulumBleConfig.UNIT_G:
                unitStr = getString(R.string.unit_g);
                break;
            case PabulumBleConfig.UNIT_ML:
                unitStr = getString(R.string.unit_ml);
                break;
            case PabulumBleConfig.UNIT_LB:
                unitStr = getString(R.string.unit_lb_oz);
                break;
            case PabulumBleConfig.UNIT_OZ:
                unitStr = getString(R.string.unit_oz);
                break;
            case PabulumBleConfig.UNIT_KG:
                unitStr = getString(R.string.unit_kg);
                break;
            case PabulumBleConfig.UNIT_FG:
                unitStr = getString(R.string.unit_fg);
                break;
            case PabulumBleConfig.UNIT_ML_MILK:
                unitStr = getString(R.string.unit_ml_milk);
                break;
            case PabulumBleConfig.UNIT_ML_WATER:
                unitStr = getString(R.string.unit_ml_water);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_MILK:
                unitStr = getString(R.string.unit_oz_milk);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_WATER:
                unitStr = getString(R.string.unit_oz_water);
                break;
            case PabulumBleConfig.UNIT_LB_LB:
                unitStr = getString(R.string.unit_lb);
                break;
        }
        return unitStr;
    }

    @Override
    protected void bluetoothStateOff() {//蓝牙已关闭
        super.bluetoothStateOff();
        setState(R.string.ble_state_off);
        L.e(TAG, "bluetoothStateOff");
    }

    @Override
    protected void bluetoothTurningOff() {//蓝牙正在关闭
        super.bluetoothTurningOff();
        L.e(TAG, "bluetoothTurningOff");
    }

    @Override
    protected void bluetoothTurningOn() {//蓝牙正在打开
        super.bluetoothTurningOn();
        L.e(TAG, "bluetoothTurningOn");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e(TAG, "onDestroy");
        stopScan();
        if (binder != null) {
            binder.disconnect();//若Activity关闭时，不需要继续保持连接，可以在此断开连接
        }
    }

    private Handler handler = new Handler();

    private Runnable disconnectRunnable = new Runnable() {
        @Override
        public void run() {
            binder.disconnect();
        }
    };

    @Override
    public void query(int rssi) {
        defaultRssi = rssi;
        setTitleRight(Math.abs(defaultRssi));
        if (binder != null) {
            binder.disconnect();
        } else {
            startScan();
        }
    }

    @Override
    protected void onWriteSuccess(byte[] value) {
        L.e(TAG, "onWriteSuccess: " + ParseData.arr2Str(value));
    }

    /**
     * 需要申请的权限
     */
    private final static String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private final static String[] BLUETOOTH_PERMISSION = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT};

    private String[] permissions;

    /**
     * 初始化请求权限
     */
    private void initPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = BLUETOOTH_PERMISSION;
        } else {
            permissions = LOCATION_PERMISSION;
        }
        if (checkPermissionIsOk(this, permissions)) {
            startScan();
        } else {
            mActivityResultLauncher.launch(permissions);
        }
    }


    private ActivityResultCallback<Map<String, Boolean>> mActivityResultCallback = new ActivityResultCallback<Map<String, Boolean>>() {

        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            // 权限状态
            boolean permissionState = true;
            for (String s : permissions) {
                Boolean aBoolean = result.get(s);
                if (aBoolean == null || Boolean.FALSE.equals(aBoolean)) {
                    permissionState = false;
                    break;
                }
            }
            if (permissionState) {
                startScan();
            } else {
                new AlertDialog.Builder(MainActivity.this).setTitle(MainActivity.this.getString(R.string.hint)).setMessage(MainActivity.this.getString(R.string.permissions))
                        .setPositiveButton(MainActivity.this.getString(R.string.query), (dialog, which) -> {
                            //引导用户至设置页手动授权
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }).setNegativeButton(MainActivity.this.getString(R.string.cancel), (dialog, which) -> {
                            if (dialog != null) {
                                dialog.cancel();
                            }
                        }).show();
            }
        }
    };

    /**
     * 检查权限
     *
     * @return boolean
     */
    public static boolean checkPermissionIsOk(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (context == null) {
            return false;
        }
        if (permissions == null || permissions.length <= 0) {
            return true;
        }
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
