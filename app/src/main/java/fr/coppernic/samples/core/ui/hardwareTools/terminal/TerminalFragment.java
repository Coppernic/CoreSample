package fr.coppernic.samples.core.ui.hardwareTools.terminal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.coppernic.samples.core.R;
import fr.coppernic.samples.core.ui.hardwareTools.OptionDialogTerminal;
import fr.coppernic.samples.core.ui.hardwareTools.autoTest.HdwrFragmentBase;
import fr.coppernic.sdk.hdk.cone.GpioPort;
import fr.coppernic.sdk.serial.SerialCom;
import fr.coppernic.sdk.serial.SerialFactory;
import fr.coppernic.sdk.serial.direct.ServiceConnector;
import fr.coppernic.sdk.utils.core.CpcBytes;
import fr.coppernic.sdk.utils.io.InstanceListener;
import fr.coppernic.sdk.utils.usb.UsbHelper;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TerminalFragment extends Fragment implements HdwrFragmentBase,
        SharedPreferences.OnSharedPreferenceChangeListener, OptionDialogTerminal.OptListener {

    public static final String TAG = "Terminal2Fragment";

    private final static String KEY_PREF_RTS = "pref_rts";
    private final static String KEY_PREF_CTS = "pref_cts";
    private final static String KEY_PREF_ASCII = "pref_ascii";
    private final static String KEY_PREF_W_LINE_ENDING = "pref_wline_ending";
    private final static String KEY_PREF_U_LINE_ENDING = "pref_uline_ending";
    private final static String KEY_PREF_R_LINE_ENDING = "pref_rline_ending";

    private SharedPreferences prefs = null;

    private SerialCom serialCom = null;
    private SerialCom directHsl1 = null;
    private GpioPort gpioPort = null;

    private Button btnOpenClose = null;
    private Spinner spBaudRate = null;
    private Spinner spDevices = null;
    private AutoCompleteTextView tvInput = null;
    private RecyclerView rvLogs = null;

    private RecyclerView.Adapter logAdapter;
    private ArrayAdapter<String> devicesAdapter = null;

    private List<String> listLogs = new ArrayList<>();
    private List<String> listDevices = new ArrayList<>();

    private Thread listeningThread = null;
    private Context context;

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                if (serialCom.isOpened()) {
                    closeCom(serialCom);
                }
            }
            if (intent.getAction().equals(
                    UsbHelper.ACTION_USB_PERMISSION)) {
                UsbDevice device = intent
                        .getParcelableExtra("device");
                if (!intent.getBooleanExtra("permission", false)) {
                    Log.d(TAG, "permission denied for device " + device);
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.v("onCreateView");

        View view = inflater.inflate(R.layout.fragment_hyper_terminal,
                container,
                false);

        context = getContext();

        btnOpenClose = view.findViewById(R.id.btnOpenClose3);
        spBaudRate = view.findViewById(R.id.spBaudRate);
        spDevices = view.findViewById(R.id.spDevices);

        ImageButton mBtnExpand = view.findViewById(R.id.btnExpand);
        mBtnExpand.setOnClickListener(v -> {
            OptionDialogTerminal optDialog = OptionDialogTerminal
                    .newInstance(prefs.getBoolean(KEY_PREF_RTS, false),
                            prefs.getBoolean(KEY_PREF_CTS, false), false,
                            false, false);
            optDialog.show(getFragmentManager(), OptionDialogTerminal.TAG);
        });

        tvInput = view.findViewById(R.id.autoCompleteTv);
        tvInput.setOnEditorActionListener((v, actionId, event) -> {
            if (v.getText().length() == 0) {
                return false;
            }
            sendCmd(v.getText());
            addLog(">> " + v.getText().toString());
            v.setText("");
            return true;
        });
        rvLogs = view.findViewById(R.id.rvLogs);
        btnOpenClose.setOnClickListener(v -> {
            if (isComOpened(serialCom)) {
                close();
            } else {
                open();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Timber.v("onActivityCreated");

        if (getActivity() != null) {
            prefs = getActivity().getSharedPreferences(context.getPackageName() + "_preferences",
                    Context.MODE_PRIVATE);
        }

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvLogs.setLayoutManager(layoutManager);
        // specify an adapter
        logAdapter = new LogAdapter(listLogs);
        rvLogs.setAdapter(logAdapter);

        ArrayAdapter<String> baudRatesAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                Arrays.asList(context.getResources().getStringArray(R.array.baudrates)));
        baudRatesAdapter.setDropDownViewResource(R.layout.spinner_item);
        spBaudRate.setAdapter(baudRatesAdapter);

        devicesAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, listDevices);
        devicesAdapter.setDropDownViewResource(R.layout.spinner_item);
        spDevices.setAdapter(devicesAdapter);

        changeInputType();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbHelper.ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbReceiver,
                filter);
        super.onStart();
    }

    @Override
    public void onResume() {
        initializeDevices();
        super.onResume();
    }

    @Override
    public void onStop() {
        context.unregisterReceiver(mUsbReceiver);
        super.onStop();
    }

    private void initializeDevices() {
        listDevices.clear();
        GpioPort.GpioManager.get().getGpioSingle(context)
                .map(gpio -> gpioPort = gpio)
                .flatMap(g -> getSerial("USB"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableSingleObserver<SerialCom>() {

            @Override
            public void onSuccess(SerialCom serial) {
                powerUsb();
                serialCom = serial;
                String[] devices = serialCom.listDevices();
                loadDevicesSpinner(context.getResources().getStringArray(R.array.port_names));
                if (devices != null && devices.length > 0) {
                    loadDevicesSpinner(devices);
                }
            }

            @Override
            public void onError(Throwable e) {
                Timber.d("onError");
            }
        });
    }

    private void powerUsb() {
        gpioPort.setPin3(true);
        gpioPort.setPinEn(true);
        gpioPort.setPinUsbEn(true);
        gpioPort.setPinUsbIdSw(true);
    }

    private void loadDevicesSpinner(String[] devices) {
        listDevices.addAll(Arrays.asList(devices));
        devicesAdapter.notifyDataSetChanged();
    }

    private void addLog(String log) {
        if (listLogs.size() < Integer.parseInt(prefs.getString(
                "pref_nb_item_log", "10"))) {
            listLogs.add(log);
        } else {
            listLogs.remove(0);
            listLogs.add(log);
        }
        getActivity().runOnUiThread(() -> {
            logAdapter.notifyDataSetChanged();
        });
    }

    private void sendCmd(CharSequence cmd) {

        if (!isComOpened(serialCom) || cmd.length() == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder(cmd);
        byte[] tx;

        if (prefs.getBoolean(KEY_PREF_ASCII, false)) {
            if (prefs.getBoolean(KEY_PREF_W_LINE_ENDING, false)) {
                sb.append("\r\n");
            } else if (prefs.getBoolean(KEY_PREF_U_LINE_ENDING, false)) {
                sb.append("\n");
            } else if (prefs.getBoolean(KEY_PREF_R_LINE_ENDING, false)) {
                sb.append("\r");
            }
            // no else
            tx = sb.toString().getBytes();
        } else { // hexadecimal display
            String[] bytes = cmd.toString().split(" ");
            tx = new byte[bytes.length];

            for (int i = 0; i < bytes.length; i++) {
                try {
                    tx[i] = (byte) Integer.parseInt(bytes[i], 16);
                } catch (NumberFormatException e) {
                    Timber.e(e.toString());
                }
            }
        }

        try {
            CpcBytes.printLine(TAG, tx, tx.length);
            serialCom.send(tx, tx.length);
        } catch (Exception e) {
            Timber.e("ERROR : %s", e.getLocalizedMessage());
        }
    }

    private boolean isComOpened(SerialCom pSerialCom) {
        boolean b;
        synchronized (pSerialCom) {
            b = pSerialCom.isOpened();
        }
        return b;
        //  return pSerialCom.isOpened();
    }

    private void closeCom(SerialCom pSerialCom) {
        if (isComOpened(pSerialCom)) {
            pSerialCom.close();
            listeningThread.interrupt();
        }
    }

    private void open() {
        getSerial(spDevices.getSelectedItem().toString())
                .map(s -> serialCom = s)
                .flatMapCompletable(m -> openPortAndListen())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.d("completable  onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        Timber.d("completable onComplete");
                        btnOpenClose.setText(R.string.btn_close);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("completable onError");
                        Toast.makeText(context,
                                "Error oppening com",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onDestroy() {
        Timber.v("onDestroy");
        close();
        super.onDestroy();
    }

    private void close() {
        closeCom(serialCom);
        if (directHsl1 != null) {
            closeCom(directHsl1);
        }
        btnOpenClose.setText(R.string.btn_open);
    }


    private Single<SerialCom> getSerial(String port) {
        return Single.create(emitter -> SerialFactory.getInstance(context, port.startsWith("/dev") ?
                        SerialCom.Type.DIRECT : SerialCom.Type.FTDI,
                new InstanceListener<SerialCom>() {
                    @Override
                    public void onCreated(SerialCom serial) {
                        Timber.d("serial open");
                        emitter.onSuccess(serial);
                    }

                    @Override
                    public void onDisposed(SerialCom serial) {
                        Timber.v("serial disposed");
                    }
                }));
    }

    private Completable openPortAndListen() {
        return Completable.create(emitter -> {
            if (serialCom instanceof ServiceConnector) {
                Timber.v("Open com with direct serial com");
            } else {
                Timber.v("Open com with Ftdi");
            }

            if (!isComOpened(serialCom)) {
                Timber.d(spDevices.getSelectedItem().toString());
                Timber.d(spBaudRate.getSelectedItem().toString());
                if (serialCom.open(spDevices.getSelectedItem().toString(),
                        Integer.parseInt(spBaudRate.getSelectedItem().toString())) != 0) {
                    emitter.onError(new Throwable());
                } else {

                    serialCom.setRts(prefs.getBoolean(KEY_PREF_RTS, false));

                    if (serialCom instanceof ServiceConnector) {
                        Timber.v("Open com with direct serial com");
                        listeningThread = new Thread(new ListeningRunnable(serialCom));
                        listeningThread.start();
                    } else {
                        Timber.v("Open com with Ftdi");
                        getSerial("/dev/ttyHSL1").subscribe(new DisposableSingleObserver<SerialCom>() {
                            @Override
                            public void onSuccess(SerialCom serialCom) {
                                directHsl1 = serialCom;
                                directHsl1.open("/dev/ttyHSL1",
                                        Integer.parseInt(spBaudRate.getSelectedItem().toString()));
                                listeningThread = new Thread(new ListeningRunnable(directHsl1));
                                listeningThread.start();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e);
                            }
                        });
                    }
                    emitter.onComplete();
                }
            }
        });
    }

    @Override
    public boolean isHardwareSupported() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Terminal";
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.contentEquals(KEY_PREF_ASCII)) {
            changeInputType();
        }
    }

    private void changeInputType() {
        tvInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        if (!prefs.getBoolean(KEY_PREF_ASCII, false)) {
            tvInput.setFilters(new InputFilter[]{hexadecimalFilter});
        } else {
            tvInput.setFilters(new InputFilter[]{});
        }
    }

    private InputFilter hexadecimalFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dStart, int dEnd) {
            if (source.length() == 0) {
                return null;
            } else {
                StringBuilder sb = new StringBuilder();

                for (int i = start; i < end; i++) {
                    if (Character.isDigit(source.charAt(i))
                            || Character.isWhitespace(source.charAt(i))
                            || isHexadecimalLetter(source.charAt(i))) {
                        sb.append(source.charAt(i));
                    } else {
                        Timber.w("Wrong character supplied : "
                                + source.charAt(i) + " at index "
                                + i);
                    }
                }
                return sb.toString();
            }
        }

        private boolean isHexadecimalLetter(char c) {
            if (c >= 'a' && c <= 'f') {
                return true;
            } else return c >= 'A' && c <= 'F';
        }
    };

    private boolean getPrefBoolean() {
        return prefs.getBoolean(TerminalFragment.KEY_PREF_ASCII, false);
    }

    @Override
    public void setOpt(boolean[] checkedOpt) {

        prefs.edit()
                .putBoolean(KEY_PREF_RTS, checkedOpt[OptionDialogTerminal.RTS])
                .putBoolean(KEY_PREF_CTS, checkedOpt[OptionDialogTerminal.CTS])
                .apply();

        if (isComOpened(serialCom)) {
            serialCom.setRts(checkedOpt[OptionDialogTerminal.RTS]);
        }

        if (checkedOpt[OptionDialogTerminal.REFRESH]) {
            initializeDevices();
        }

        if (checkedOpt[OptionDialogTerminal.CLEAR]) {
            Timber.i("Clear term");
            listLogs.clear();
            logAdapter.notifyDataSetChanged();
        }
    }

    private class ListeningRunnable implements Runnable {

        private SerialCom com;

        private ListeningRunnable(SerialCom pSerialCom) {
            com = pSerialCom;
        }

        @Override
        public void run() {
            while (isComOpened(com)) {
                if (this.com.getQueueStatus() > 0) {
                    int dataLength = this.com.getQueueStatus();
                    // final Bundle pos = msg.getData();
                    byte[] data = new byte[dataLength];

                    this.com.receive(100, dataLength, data);

                    StringBuilder sb = new StringBuilder();

                    if (getPrefBoolean()) {
                        for (int i = 0; i < dataLength; i++) {
                            sb.append((char) data[i]);
                        }
                    } else {
                        for (int i = 0; i < dataLength; i++) {
                            sb.append(String.format("%02x ", data[i]));
                        }
                    }
                    sb.append("\n");
                    addLog("<< " + sb.toString());
                }
            }
        }
    }
}
