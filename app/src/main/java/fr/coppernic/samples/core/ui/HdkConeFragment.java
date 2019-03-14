package fr.coppernic.samples.core.ui;


import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.coppernic.samples.core.R;
import fr.coppernic.sdk.hdk.cone.GpioPort;
import fr.coppernic.sdk.hdk.cone.UsbGpioPort;
import fr.coppernic.sdk.utils.core.CpcResult;
import fr.coppernic.sdk.utils.core.CpcResult.RESULT;
import fr.coppernic.sdk.utils.debug.L;
import fr.coppernic.sdk.utils.usb.RxUsbHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class HdkConeFragment extends Fragment {
    public static final String TAG = "HdkConeFragment";
    private final RxUsbHelper rxUsbHelper = new RxUsbHelper();
    @BindView(R.id.togglePin1)
    ToggleButton tbPin1;
    @BindView(R.id.togglePin2)
    ToggleButton tbPin2;
    @BindView(R.id.togglePin3)
    ToggleButton tbPin3;
    @BindView(R.id.togglePin4)
    ToggleButton tbPin4;
    @BindView(R.id.togglePolling)
    ToggleButton tbPolling;
    @BindView(R.id.radioInput4)
    RadioButton radioInput4;
    @BindView(R.id.toggleBarcodePower)
    ToggleButton tbBarcodePower;
    @BindView(R.id.toggleWakeUp)
    ToggleButton tbBarcodeWakeUp;
    @BindView(R.id.toggleTrigger)
    ToggleButton tbBarcodeTrigger;
    @BindView(R.id.toggleUsb1)
    ToggleButton tbUsb1;
    @BindView(R.id.toggleUsb2)
    ToggleButton tbUsb2;
    @BindView(R.id.toggleUsb3)
    ToggleButton tbUsb3;
    @BindView(R.id.toggleUsb4)
    ToggleButton tbUsb4;
    @BindView(R.id.edtVid)
    EditText edtVid;
    @BindView(R.id.edtPid)
    EditText edtPid;
    private Disposable inputDisposable;
    private GpioPort gpioPort;

    private final Consumer<Throwable> onError = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) {
            if(throwable instanceof CpcResult.ResultException) {
                CpcResult.ResultException e = (CpcResult.ResultException) throwable;
                if(e.getResult() == RESULT.SERVICE_NOT_FOUND) {
                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.error)
                        .content(R.string.error_service_not_found)
                        .build();
                    dialog.show();
                }
            }
        }
    };

    public HdkConeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hdk_cone, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        RxUsbHelper.disableUsbDialog(getContext());
        Disposable d = GpioPort.GpioManager.get()
            .getGpioSingle(getContext())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<GpioPort>() {
                @Override
                public void accept(GpioPort g) throws Exception {
                    gpioPort = g;
                }
            }, onError);
    }

    @Override
    public void onStop() {
        if(gpioPort != null) {
            gpioPort.close();
        }
        super.onStop();
    }

    @OnClick(R.id.togglePolling)
    void togglePolling() {
        if (tbPolling.isChecked()) {
            inputDisposable = GpioPort.observeInputPin4(getContext(), 200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        radioInput4.setChecked(aBoolean);
                    }
                }, onError);
        } else {
            if (inputDisposable != null && !inputDisposable.isDisposed()) {
                inputDisposable.dispose();
            }
        }
    }

    @OnClick(R.id.togglePin1)
    void togglePin1() {
        if (gpioPort != null) {
            showErr(gpioPort.setPin1(tbPin1.isChecked()));
        }
    }

    @OnClick(R.id.togglePin2)
    void togglePin2() {
        if (gpioPort != null) {
            showErr(gpioPort.setPin2(tbPin2.isChecked()));
        }
    }

    @OnClick(R.id.togglePin3)
    void togglePin3() {
        if (gpioPort != null) {
            showErr(gpioPort.setPinEn(tbPin3.isChecked()));
            showErr(gpioPort.setPin3(tbPin3.isChecked()));
            showErr(gpioPort.setPinUsbEn(tbPin3.isChecked()));
        }
    }

    @OnClick(R.id.togglePin4)
    void togglePin4() {
        if (gpioPort != null) {
            showErr(gpioPort.setPin4(tbPin4.isChecked()));
        }
    }

    @OnClick(R.id.toggleBarcodePower)
    void toggleBarcodePower() {
        if (gpioPort != null) {
            showErr(gpioPort.setBarcodeScanPower(tbBarcodePower.isChecked()));
        }
    }

    @OnClick(R.id.toggleWakeUp)
    void toggleBarcodeWakeUp() {
        if (gpioPort != null) {
            showErr(gpioPort.setBarcodeScanWakeup(tbBarcodeWakeUp.isChecked()));
        }
    }

    @OnClick(R.id.toggleTrigger)
    void toggleBarcodeTrigger() {
        if (gpioPort != null) {
            showErr(gpioPort.setBarcodeScanTrigger(tbBarcodeTrigger.isChecked()));
        }
    }

    @OnClick(R.id.toggleUsb1)
    void toggleUsb1() {
        showErr(UsbGpioPort.get().setUsbPin1(getContext(), tbUsb1.isChecked()));
    }

    @OnClick(R.id.toggleUsb2)
    void toggleUsb2() {
        showErr(UsbGpioPort.get().setUsbPin2(getContext(), tbUsb2.isChecked()));

    }

    @OnClick(R.id.toggleUsb3)
    void toggleUsb3() {
        showErr(UsbGpioPort.get().setUsbPin3(getContext(), tbUsb3.isChecked()));
    }

    @OnClick(R.id.toggleUsb4)
    void toggleUsb4() {
        showErr(UsbGpioPort.get().setUsbPin4(getContext(), tbUsb4.isChecked()));
    }

    @OnClick(R.id.btnAskUsbPerm)
    void askUsbAuth() {
        int vid = integerFromCS(edtVid.getText(), -1);
        int pid = integerFromCS(edtPid.getText(), -1);
        rxUsbHelper.getUsbPermission(getContext(), vid, pid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableSingleObserver<UsbDevice>() {
                @Override
                public void onSuccess(UsbDevice usbDevice) {
                    Toast.makeText(getContext(), "Permission gotten for " + usbDevice, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getContext(), "Permission error : " + e, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void showErr(RESULT res) {
        if (res != RESULT.OK) {
            Toast.makeText(getContext(), L.getMethodName(3) + " : " + res, Toast.LENGTH_SHORT).show();
        }
    }

    private int integerFromCS(CharSequence s, int def) {
        int ret = def;
        try {
            ret = Integer.parseInt(s.toString());
        } catch (NumberFormatException ignore) {
            ignore.printStackTrace();
        }
        return ret;
    }

}
