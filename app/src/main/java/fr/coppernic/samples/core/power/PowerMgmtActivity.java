package fr.coppernic.samples.core.power;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.coppernic.samples.core.R;
import fr.coppernic.samples.core.utils.Definitions;
import fr.coppernic.sdk.powermgmt.PowerMgmt;
import fr.coppernic.sdk.powermgmt.PowerMgmtFactory;
import fr.coppernic.sdk.powermgmt.PowerUtilsNotifier;
import fr.coppernic.sdk.powermgmt.cone.identifiers.InterfacesCone;
import fr.coppernic.sdk.powermgmt.cone.identifiers.ManufacturersCone;
import fr.coppernic.sdk.powermgmt.cone.identifiers.ModelsCone;
import fr.coppernic.sdk.powermgmt.cone.identifiers.PeripheralTypesCone;
import fr.coppernic.sdk.utils.core.CpcResult.RESULT;

public class PowerMgmtActivity extends AppCompatActivity {

    @BindView(R.id.textView10)
    TextView tvStatus;
    private final PowerUtilsNotifier notifier = new PowerUtilsNotifier() {
        @Override
        public void onPowerUp(final RESULT res, int vid, int pid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText("Powered up " + res);
                }
            });
        }

        @Override
        public void onPowerDown(final RESULT res, int vid, int pid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvStatus.setText("Powered down " + res);
                }
            });
        }
    };
    @BindView(R.id.tgFp)
    ToggleButton tgFp;
    @BindView(R.id.tgElyctis)
    ToggleButton tgElyctis;
    @BindView(R.id.tgCr30)
    ToggleButton tgCr30;
    private PowerMgmt powerMgmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_mgmt);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            powerMgmt = PowerMgmtFactory.get().setContext(this).setNotifier(notifier).build();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getApplicationContext(), "Not supported on " + Definitions.getDeviceName(),
                           Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.tgFp)
    void clickFp() {
        powerMgmt.setPower(PeripheralTypesCone.FingerPrintReader,
                           ManufacturersCone.IntegratedBiometrics,
                           ModelsCone.Columbo,
                           InterfacesCone.UsbGpioPort,
                           tgFp.isChecked());
        updateStatus(tgFp);
    }

    @OnClick(R.id.tgElyctis)
    void clickElyctis() {
        powerMgmt.setPower(PeripheralTypesCone.FingerPrintReader,
                           ManufacturersCone.IntegratedBiometrics,
                           ModelsCone.Columbo,
                           InterfacesCone.UsbGpioPort,
                           tgElyctis.isChecked());
        updateStatus(tgElyctis);
    }

    @OnClick(R.id.tgCr30)
    void clickCr30() {
        powerMgmt.setPower(PeripheralTypesCone.RfidSc,
                           ManufacturersCone.Gemalto,
                           ModelsCone.Cr30,
                           InterfacesCone.UsbGpioPort,
                           tgCr30.isChecked());
        updateStatus(tgCr30);
    }

    private void updateStatus(ToggleButton tg) {
        tvStatus.setText("Powering " + (tg.isChecked() ? "Up" : "Down") + "...");
    }

}
