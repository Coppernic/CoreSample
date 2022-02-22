package fr.coppernic.samples.core.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.coppernic.samples.core.R;
import fr.coppernic.sdk.hdk.access.GpioPort;
import fr.coppernic.sdk.utils.core.CpcResult.RESULT;
import fr.coppernic.sdk.utils.core.CpcResult.ResultException;
import fr.coppernic.sdk.utils.debug.L;
import fr.coppernic.sdk.utils.usb.RxUsbHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class HdkAccessFragment extends Fragment {
    public static final String TAG = "HdkAccessFragment";
    private final RxUsbHelper rxUsbHelper = new RxUsbHelper();

    @BindView(R.id.toggleVccEn)
    ToggleButton tbVccEn;
    @BindView(R.id.toggleIoEn)
    ToggleButton tbIoEn;
    @BindView(R.id.toggleGpio1)
    ToggleButton tbGpio1;
    @BindView(R.id.toggleGpio2)
    ToggleButton tbGpio2;

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

    public HdkAccessFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hdk_access, container, false);
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

    @OnClick(R.id.toggleVccEn)
    void toggleVccEn() {
        if (gpioPort != null) {
            showErr(gpioPort.setVccEn(tbVccEn.isChecked()));
        }
    }

    @OnClick(R.id.toggleIoEn)
    void toggleIoEn() {
        if (gpioPort != null) {
            showErr(gpioPort.setIoEn(tbIoEn.isChecked()));
        }
    }
    @OnClick(R.id.toggleGpio1)
    void toggleGpio1() {
        if (gpioPort != null) {
            showErr(gpioPort.setGpio1(tbGpio1.isChecked()));
        }
    }
    @OnClick(R.id.toggleGpio2)
    void toggleGpio2() {
        if (gpioPort != null) {
            showErr(gpioPort.setGpio2(tbGpio2.isChecked()));
        }
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
