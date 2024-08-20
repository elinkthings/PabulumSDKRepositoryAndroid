package aicare.net.cn.sdk.pabulumsdkrepositoryandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.base.BaseActivity;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.databinding.ActivityWelcomeBinding;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.AppUtils;
import aicare.net.cn.sdk.pabulumsdkrepositoryandroid.utils.T;


/**
 * Created by Suzy on 2016/5/10.
 */
public class WelcomeActivity extends BaseActivity {
    private Handler mHandler = new Handler();
    private ActivityWelcomeBinding welcomeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        welcomeBinding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(welcomeBinding.getRoot());
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
        welcomeBinding.tvShowVersion.setText(getString(R.string.current_version, AppUtils.getVersionName(this)));
    }
}
