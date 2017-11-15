package fr.coppernic.samples.core.power;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.coppernic.samples.core.R;
import fr.coppernic.samples.core.utils.Definitions;
import fr.coppernic.sdk.power.PowerManager;
import fr.coppernic.sdk.power.api.PowerListener;
import fr.coppernic.sdk.power.api.peripheral.Peripheral;
import fr.coppernic.sdk.utils.core.CpcResult.RESULT;

public class PowerActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    @BindView(R.id.textView9)
    TextView tvName;
    @BindView(R.id.textView10)
    TextView tvStatus;
    private final PowerListener powerListener = new PowerListener() {
        @Override
        public void onPowerUp(RESULT res, final Peripheral peripheral) {
            tvStatus.setText("Powered up : " + res);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText("Powering down...");
                    peripheral.off(PowerActivity.this);
                }
            }, 1000);
        }

        @Override
        public void onPowerDown(RESULT res, Peripheral peripheral) {
            tvStatus.setText("Powered down : " + res);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PowerManager.get().registerListener(powerListener);
        Peripheral peripheral = getPeripheralFromIntent();
        if (peripheral != null) {
            tvName.setText(peripheral.toString());
            tvStatus.setText("Powering up...");
            peripheral.on(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager.get().releaseAndUnregister();
    }

    private Peripheral getPeripheralFromIntent() {
        Intent intent = getIntent();
        Peripheral p = null;
        if (intent.hasExtra(Definitions.KEY_PERIPHERAL)) {
            p = (Peripheral) intent.getSerializableExtra(Definitions.KEY_PERIPHERAL);
        }
        return p;
    }


}
