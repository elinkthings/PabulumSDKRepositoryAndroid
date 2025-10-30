package aicare.net.cn.sdk.pabulumsdkrepositoryandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.base.BaseActivity;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.AppUtils;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.T;


/**
 * Created by Suzy on 2016/5/10.
 */
public class WelcomeActivity extends BaseActivity {
    TextView tv_show_version;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initViews();

        if (ensureBLESupported()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openActivity(MainActivity.class);
                    WelcomeActivity.this.finish();
                }
            }, 3000);
        } else {
            T.showLong(this, R.string.not_support_ble);
            this.finish();
        }
    }

    protected void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void initViews() {
        tv_show_version = (TextView) findViewById(R.id.tv_show_version);
        tv_show_version.setText(getString(R.string.current_version, AppUtils.getVersionName(this)));
    }
}
