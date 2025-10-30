package aicare.net.cn.sdk.pabulumsdkrepositoryandroid;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.Random;

import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.base.BaseActivity;
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

public class MainActivity extends BaseActivity implements SetRssiDialog.OnQueryListener, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private ImageButton ib_title_left;
    private TextView tv_title_middle;
    private Button btn_title_right;
    private TextView tv_show_state;
    private TextView tv_show_rssi;
    private TextView tv_show_version;
    private RadioGroup rg_unit;
    private RadioGroup rg_unit_two;
    private EditText et_set_weight;
    private TextView tv_show_result;
    private TextView tv_show_did;
    private TextView tv_show_time;


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_title_right) {
            new SetRssiDialog(this, defaultRssi, this).show();
        } else if (id == R.id.tv_show_state) {
            if (binder != null) {
                binder.disconnect();
            }
        } else if (id == R.id.btn_set_weight) {
            String weight = et_set_weight.getText().toString().trim();
            if (TextUtils.isEmpty(weight)) {
                T.showShort(this, R.string.pls_input_weight);
            } else {
                int wei = Integer.valueOf(weight);
                if (binder != null) {
                    binder.setWeight(wei);
                }
            }
        } else if (id == R.id.btn_tare) {
            if (binder != null) {
                binder.netWeight();
            }
        } else if (id == R.id.btn_power_off) {
            if (binder != null) {
                binder.powerOff();
                handler.postDelayed(disconnectRunnable, 1000);
            }
            //获取DID  2018-12-3
        } else if (id == R.id.btn_did) {
            if (binder != null) {
                L.i(TAG, "点击请求获取did");
                binder.getDid();
            }
            //2019/4/29
        } else if (id == R.id.btn_start) {
            if (binder != null) {
                L.i(TAG, "开始计时");
                binder.startTime();
            }
            //2019/5/22
        } else if (id == R.id.btn_start_less) {
            if (binder != null) {
                L.i(TAG, "倒计时开始");
                binder.startTimeLess(180);
            }
            //2019/6/25
        } else if (id == R.id.btn_pause) {
            if (binder != null) {
                L.i(TAG, "正计时暂停");
                binder.pauseTime(80);
            }
            //2019/6/25
        } else if (id == R.id.btn_pause_less) {
            if (binder != null) {
                L.i(TAG, "倒计时暂停");
                binder.pauseTimeLess(90);
            }
            //2019/4/29
        } else if (id == R.id.btn_reset) {
            if (binder != null) {
                L.i(TAG, "重置计时");
                binder.resetTime();
            }
        } else if (id == R.id.btn_write_value) {
            if (binder != null) {
                //透传数据测试
                byte[] value = initRandomByteArr(new Random().nextInt(21));
                L.e(TAG, "value: " + ParseData.arr2Str(value));
                binder.writeValue(value);
            }
        } else if (id == R.id.btn_get_version) {
            if (binder != null) {
                L.i(TAG, "点击请求获取版本号");
                binder.getVersion();

            }
        } else if (id == R.id.btn_get_units) {
            if (binder != null) {
                L.i(TAG, "点击请求获取单位列表");
                binder.getUnits();

            }
        } else {
            setData(view.getId());
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
        String data = et_set_weight.getText().toString().trim();
        if (TextUtils.isEmpty(data)) {
            T.showShort(this, R.string.pls_input_weight);
        } else {
            int wei = Integer.parseInt(data);
            if (binder != null) {
                if (id == R.id.btn_cal) {
                    binder.setCal(wei);
                } else if (id == R.id.btn_all_cal) {
                    binder.setAllCal(wei);
                } else if (id == R.id.btn_fat) {
                    binder.setFat(wei);
                } else if (id == R.id.btn_all_fat) {
                    binder.setAllFat(wei);
                } else if (id == R.id.btn_pro) {
                    binder.setPro(wei);
                } else if (id == R.id.btn_all_pro) {
                    binder.setAllPro(wei);
                } else if (id == R.id.btn_car) {
                    binder.setCar(wei);
                } else if (id == R.id.btn_all_car) {
                    binder.setAllCar(wei);
                } else if (id == R.id.btn_fib) {
                    binder.setFib(wei);
                } else if (id == R.id.btn_all_fib) {
                    binder.setAllFib(wei);
                } else if (id == R.id.btn_cho) {
                    binder.setCho(wei);
                } else if (id == R.id.btn_all_cho) {
                    binder.setAllCho(wei);
                } else if (id == R.id.btn_sod) {
                    binder.setSod(wei);
                } else if (id == R.id.btn_all_sod) {
                    binder.setAllSod(wei);
                } else if (id == R.id.btn_sug) {
                    binder.setSug(wei);
                } else if (id == R.id.btn_all_sug) {
                    binder.setAllSug(wei);
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        L.i("点击:" + checkedId+"   isBleChangeUnit="+isBleChangeUnit);
        if (isBleChangeUnit) {
            isBleChangeUnit = false;
            return;
        }

        int id = checkedId;
        if (id == R.id.rb_g) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_G);
            }
        } else if (id == R.id.rb_ml) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_ML);
            }
        } else if (id == R.id.rb_lb) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_LB);
            }
        } else if (id == R.id.rb_oz) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_OZ);
            }
        } else if (id == R.id.rb_kg) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_KG);
            }
        } else if (id == R.id.rb_fg) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_FG);
            }
        } else if (id == R.id.rb_ml_milk) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_ML_MILK);
            }
        } else if (id == R.id.rb_ml_water) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_ML_WATER);
            }
        } else if (id == R.id.rb_floz_milk) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_FL_OZ_MILK);
            }
        } else if (id == R.id.rb_floz_water) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_FL_OZ_WATER);
            }
        } else if (id == R.id.rb_lb_lb) {
            if (binder != null) {
                binder.setUnit(PabulumBleConfig.UNIT_LB_LB);
            }
        }
    }


    private PabulumService.PabulumBinder binder;
    private int defaultRssi;

    private final static int DEFAULT_RSSI = -70;

    private String preWeight = "0";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.isDebug = true;
        setContentView(R.layout.main);
//        PabulumSDK.getInstance().init(this, "66617c04a3bbc7d2", "001814ae6212dd8c4657444c4b");
        PabulumSDK.getInstance().init(this);
        initViews();
        initData();
        initListener();
        initPermissions();
        if (!AppUtils.isLocServiceEnable(this)) {
            T.showShort(this, this.getString(R.string.permissions_server));
        }

        reset();
    }

    private void initListener() {
        ib_title_left.setOnClickListener(this);
        tv_title_middle.setOnClickListener(this);
        btn_title_right.setOnClickListener(this);
        tv_show_state.setOnClickListener(this);
        tv_show_rssi.setOnClickListener(this);
        tv_show_version.setOnClickListener(this);
        tv_show_result.setOnClickListener(this);
        tv_show_did.setOnClickListener(this);
        tv_show_time.setOnClickListener(this);

        rg_unit.setOnCheckedChangeListener(this);
        rg_unit_two.setOnCheckedChangeListener(this);
    }




    private void initData() {
        defaultRssi = (int) SPUtils.get(this, Config.DEFAULT_RSSI, DEFAULT_RSSI);
    }

    private void initViews() {
        ib_title_left = findViewById(R.id.ib_title_left);
        tv_title_middle = findViewById(R.id.tv_title_middle);
        btn_title_right = findViewById(R.id.btn_title_right);
        tv_show_state = findViewById(R.id.tv_show_state);
        tv_show_rssi = findViewById(R.id.tv_show_rssi);
        tv_show_version = findViewById(R.id.tv_show_version);
        rg_unit = findViewById(R.id.rg_unit);
        rg_unit_two = findViewById(R.id.rg_unit_two);
        et_set_weight = findViewById(R.id.et_set_weight);
        tv_show_result = findViewById(R.id.tv_show_result);
        tv_show_did = findViewById(R.id.tv_show_did);
        tv_show_time = findViewById(R.id.tv_show_time);
        setTitleRight(Math.abs(defaultRssi));
    }

    private void reset() {
        setCurrentRssi(null);
        setBleVersion(getResources().getString(R.string.no_version));
        tv_show_result.setText(String.valueOf(preWeight));
        tv_show_result.setTextColor(getResources().getColor(R.color.black_theme));
    }


    /**
     * 设置状态信息
     *
     * @param object
     */
    private void setState(Object object) {
        if (object instanceof Integer) {
            tv_show_state.setText((Integer) object);
        } else if (object instanceof String) {
            tv_show_state.setText((String) object);
        }
    }

    /**
     * 设置信号显示
     *
     * @param object
     */
    private void setCurrentRssi(Object object) {
        if (object == null) {
            tv_show_rssi.setText(R.string.no_rssi);
        } else {
            if (object instanceof Integer) {
                tv_show_rssi.setText(String.format(getResources().getString(R.string.current_rssi), (Integer) object));
            }
        }
    }

    private void setBleVersion(String version) {
        tv_show_version.setText(version);

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
        tv_show_time.setText("TIME:" + timeS);
    }

    @Override
    protected void getPenetrateData(byte[] data) {
        L.i(TAG, "透传数据:" + ParseData.arr2Str(data));

    }

    @Override
    protected void getBleDID(int did) {
        L.i(TAG, "获取did成功:" + did);
        tv_show_did.setText("DID:" + did);
    }

    /**
     * 设置右边title
     *
     * @param rssi
     */
    private void setTitleRight(int rssi) {
        btn_title_right.setText(String.format(getResources().getString(R.string.default_rssi), rssi));
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
                rg_unit.check(R.id.rb_g);
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
        String curWeight = foodData.getData();
        if (TextUtils.equals(curWeight, preWeight)) {
            countWei += 1;
        } else {
            countWei = 0;
            preWeight = curWeight;
        }
        if (countWei >= 5) {
            tv_show_result.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tv_show_result.setTextColor(getResources().getColor(R.color.black_theme));
        }
        if (foodData.getUnit() != preUnit) {
            preUnit = foodData.getUnit();
            isBleChangeUnit = true;
            showUnit(preUnit);
        }
        String unitStr = getUnitStr(preUnit);

        tv_show_result.setText(preWeight + " " + unitStr + "\nType:" + foodData.getDeviceType() + "\n" + foodData.getWeight() + "g");
    }


    private void showUnit(int preUnit) {
        switch (preUnit) {
            case PabulumBleConfig.UNIT_G:
                rg_unit.check(R.id.rb_g);
                break;
            case PabulumBleConfig.UNIT_ML:
                rg_unit.check(R.id.rb_ml);
                break;
            case PabulumBleConfig.UNIT_LB:
                rg_unit.check(R.id.rb_lb);
                break;
            case PabulumBleConfig.UNIT_OZ:
                rg_unit.check(R.id.rb_oz);
                break;
            case PabulumBleConfig.UNIT_KG:
                rg_unit.check(R.id.rb_kg);
                break;
            case PabulumBleConfig.UNIT_FG:
                rg_unit.check(R.id.rb_fg);
                break;
            case PabulumBleConfig.UNIT_ML_MILK:
                rg_unit.check(R.id.rb_ml_milk);
                break;
            case PabulumBleConfig.UNIT_ML_WATER:
                rg_unit.check(R.id.rb_ml_water);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_MILK:
                rg_unit.check(R.id.rb_floz_milk);
                break;
            case PabulumBleConfig.UNIT_FL_OZ_WATER:
                rg_unit.check(R.id.rb_floz_water);
                break;
            case PabulumBleConfig.UNIT_LB_LB:
                rg_unit.check(R.id.rb_lb_lb);
                break;
        }
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
     * 初始化请求权限
     */
    private void initPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT}, 1);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan();//开始扫描
        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //权限请求失败，但未选中“不再提示”选项
                new android.app.AlertDialog.Builder(this).setTitle("提示").setMessage("请求使用定位权限搜索蓝牙设备").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.cancel();
                        }

                    }
                }).show();
            } else {
                //权限请求失败，选中“不再提示”选项
//                T.showShort(MainActivity.this, "获取权限失败");
                new android.app.AlertDialog.Builder(this).setTitle("提示").setMessage("请求使用定位权限搜索蓝牙设备").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.cancel();
                        }

                    }
                }).show();
            }

        }

    }


}
