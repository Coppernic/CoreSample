package fr.coppernic.samples.core.ui;


import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.coppernic.samples.core.R;
import fr.coppernic.sdk.hdk.cone.GpioPort;
import fr.coppernic.sdk.hdk.cone.UsbGpioPort;
import fr.coppernic.sdk.power.impl.cone.ConePeripheral;
import fr.coppernic.sdk.utils.core.CpcResult.RESULT;
import fr.coppernic.sdk.utils.core.CpcResult.ResultException;
import fr.coppernic.sdk.utils.debug.L;
import fr.coppernic.sdk.utils.usb.RxUsbHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class HdkCone2Fragment extends Fragment {
    public static final String TAG = "HdkCone2Fragment";
    private final RxUsbHelper rxUsbHelper = new RxUsbHelper();
    @BindView(R.id.toggle2Pin1)
    ToggleButton tbPin1;
    @BindView(R.id.toggle2Pin2)
    ToggleButton tbPin2;
    @BindView(R.id.toggle2Pin3)
    ToggleButton tbPin3;
    @BindView(R.id.toggle2Pin4)
    ToggleButton tbPin4;
    @BindView(R.id.togglePolling2)
    ToggleButton tbPolling;
    @BindView(R.id.radio2Input4)
    RadioButton radioInput4;
    @BindView(R.id.toggleBarcodePower2)
    ToggleButton tbBarcodePower;
    @BindView(R.id.toggle2Usb1)
    ToggleButton tbUsb1;
    @BindView(R.id.toggle2Usb2)
    ToggleButton tbUsb2;
    @BindView(R.id.toggle2Usb3)
    ToggleButton tbUsb3;
    @BindView(R.id.toggle2Usb4)
    ToggleButton tbUsb4;
    @BindView(R.id.edtVid2)
    EditText edtVid;
    @BindView(R.id.edtPid2)
    EditText edtPid;
    @BindView(R.id.toggleButtonExternalEn)
    ToggleButton tbExternalEn;
    @BindView(R.id.toggleButtonUsbEn)
    ToggleButton tbUsbEn;
    @BindView(R.id.toggleButtonUsbId)
    ToggleButton tbUsbId;
    private Disposable inputDisposable;
    private GpioPort gpioPort;

    private final Consumer<Throwable> onError = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) {
            if (throwable instanceof ResultException) {
                ResultException e = (ResultException) throwable;
                if (e.getResult() == RESULT.SERVICE_NOT_FOUND) {
                    MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.error)
                            .content(R.string.error_service_not_found)
                            .build();
                    dialog.show();
                }
            }
        }
    };

    public HdkCone2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hdk_cone_2, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // RxUsbHelper.disableUsbDialog(getContext());
        Disposable subscribe = GpioPort.GpioManager.get()
                .getGpioSingle(getContext())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GpioPort>() {
                    @Override
                    public void accept(GpioPort g) {
                        gpioPort = g;
                    }
                }, onError);
    }

    @Override
    public void onStop() {
        if (gpioPort != null) {
            gpioPort.close();
        }
        super.onStop();
    }

    @OnClick(R.id.togglePolling2)
    void togglePolling() {
        if (tbPolling.isChecked()) {
            inputDisposable = GpioPort.observeInputPin4(getContext(), 200, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            radioInput4.setChecked(aBoolean);
                        }
                    }, onError);
        } else {
            if (inputDisposable != null && !inputDisposable.isDisposed()) {
                inputDisposable.dispose();
            }
        }
    }

    @OnClick(R.id.toggle2Pin1)
    void togglePin1() {
        if (gpioPort != null) {
            showErr(gpioPort.setPin1(tbPin1.isChecked()));
        }
    }

    @OnClick(R.id.toggle2Pin2)
    void togglePin2() {
        if (gpioPort != null) {
            showErr(gpioPort.setPin2(tbPin2.isChecked()));
        }
    }

    @OnClick(R.id.toggle2Pin3)
    void togglePin3() {
        if (gpioPort != null) {
            showErr(gpioPort.setPin3(tbPin3.isChecked()));
        }
    }

    @OnClick(R.id.toggle2Pin4)
    void togglePin4() {
        if (gpioPort != null) {
            showErr(gpioPort.setPin4(tbPin4.isChecked()));
        }
    }

    @OnClick(R.id.toggleButtonExternalEn)
    void toggleExternalEn() {
        if (gpioPort != null) {
            showErr(gpioPort.setPinEn(tbExternalEn.isChecked()));
        }
    }

    @OnClick(R.id.toggleButtonUsbEn)
    void toggleUsbEn() {
        if (gpioPort != null) {
            showErr(gpioPort.setPinUsbEn(tbUsbEn.isChecked()));
        }
    }

    @OnClick(R.id.toggleButtonUsbId)
    void toggleUsbId() {
        if (gpioPort != null) {
            showErr(gpioPort.setPinUsbIdSw(tbUsbId.isChecked()));
        }
    }

    @OnClick(R.id.toggleBarcodePower2)
    void toggleBarcodePower() {
        if (gpioPort != null) {
            if (tbBarcodePower.isChecked()) {
                ConePeripheral.FP_IB_COLOMBO_USB.on(getContext());
            } else {
                ConePeripheral.FP_IB_COLOMBO_USB.off(getContext());
            }
            //showErr(gpioPort.setBarcodeScanPower(tbBarcodePower.isChecked()));
        }
    }

    @OnClick(R.id.toggle2Usb1)
    void toggleUsb1() {
        showErr(UsbGpioPort.get().setUsbPin1(getContext(), tbUsb1.isChecked()));
    }

    @OnClick(R.id.toggle2Usb2)
    void toggleUsb2() {
        showErr(UsbGpioPort.get().setUsbPin2(getContext(), tbUsb2.isChecked()));
    }

    @OnClick(R.id.toggle2Usb3)
    void toggleUsb3() {
        showErr(UsbGpioPort.get().setUsbPin3(getContext(), tbUsb3.isChecked()));
    }

    @OnClick(R.id.toggle2Usb4)
    void toggleUsb4() {
        showErr(UsbGpioPort.get().setUsbPin4(getContext(), tbUsb4.isChecked()));
    }

    @OnClick(R.id.btnAskUsbPerm2)
    void askUsbAuth() {
        int vid = integerFromCS(edtVid.getText(), -1);
        int pid = integerFromCS(edtPid.getText(), -1);
        rxUsbHelper.setPermissionTimeout(5, TimeUnit.SECONDS);
        rxUsbHelper.setConnectionTimeout(1, TimeUnit.SECONDS);
        rxUsbHelper.getUsbPermission(getContext(), vid, pid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<UsbDevice>() {
                    @Override
                    public void onSuccess(UsbDevice usbDevice) {
                        Toast.makeText(getContext(), "Permission gotten for " + usbDevice, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
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
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return ret;
    }

}
