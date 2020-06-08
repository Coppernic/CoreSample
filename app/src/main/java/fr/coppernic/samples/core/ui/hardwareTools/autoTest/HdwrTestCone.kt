package fr.coppernic.samples.core.ui.hardwareTools.autoTest

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.FragmentManager
import fr.coppernic.sdk.hdk.cone.GpioPort
import fr.coppernic.sdk.serial.SerialCom
import fr.coppernic.sdk.serial.SerialFactory
import fr.coppernic.sdk.utils.core.CpcBytes
import fr.coppernic.sdk.utils.core.CpcResult.RESULT
import fr.coppernic.sdk.utils.io.InstanceListener
import fr.coppernic.sdk.utils.usb.UsbHelper
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

private const val TAG = "ExpansionPortTest"

private const val SERIAL_PORT = "/dev/ttyHSL1"
private const val FTX230X_VID = 1027
private const val FTX230X_PID = 24597

class ExpansionPortTest(ctx: Context?, man: FragmentManager,
                        notifier: HdwrTestResultInterface?) : HdwrTestDialogBase(ctx, man, notifier) {


    private val semUsb = Semaphore(0)

    private lateinit var directSerial: SerialCom
    private lateinit var ftdiSerial: SerialCom
    private lateinit var gpioPort: GpioPort

//        private BroadcastReceiver mUsbDevicePermissions = new BroadcastReceiver() {
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (UsbHelper.ACTION_USB_PERMISSION.equals(action)) {
//                    UsbDevice device = intent
//                            .getParcelableExtra("device");
//                    if (!intent.getBooleanExtra("permission", false)) {
//                        Timber.d("permission denied for device %s", device);
//                        postError("[FAILED] : Permission denied for USB device");
//                    } else {
//                        mResult = RESULT.OK;
//                    }
//                    semUsb.release();
//                }
//            }
//        };

    override fun setUp(): Completable? {
        Timber.d("setUp")
        super.setUp()
        return Completable.create { emitter ->
            val g = GpioPort.GpioManager.get()
                    .getGpioSingle(mCtx)
                    .observeOn(Schedulers.io())
                    .subscribe({
                        gpioPort = it
                        Timber.d("gpio open")
                        emitter.onComplete()
                    }, {
                        emitter.onError(it)
                    })
        }.andThen(
                Completable.create { emit ->
                    val d = mCtx?.let {
                        SerialFactory.getInstance(it, SerialCom.Type.DIRECT, object : InstanceListener<SerialCom> {
                            override fun onDisposed(p0: SerialCom?) {

                            }

                            override fun onCreated(direct: SerialCom) {
                                Timber.d("onCreated serial direct")
                                directSerial = direct
                                emit.onComplete()
                            }
                        })
                    }
                }
        ).andThen(
                Completable.create { emit ->
                    val f = mCtx?.let {
                        SerialFactory.getInstance(it, SerialCom.Type.FTDI, object : InstanceListener<SerialCom> {
                            override fun onDisposed(p0: SerialCom?) {

                            }

                            override fun onCreated(ftdi: SerialCom) {
                                Timber.d("onCreated serial FTDI")
                                ftdiSerial = ftdi
                                emit.onComplete()
                            }
                        })
                    }
                }
        ).doFinally {
            // RxUsbHelper.disableUsbDialog(getContext());
            resetGpio()
            var res = SerialCom.ERROR_OK
            try {
                res = directSerial.open(SERIAL_PORT, 115200)
            } catch (e: Exception) {
                Log.e(tag, e.toString())
            }
            if (res != SerialCom.ERROR_OK) {
                throw UnsupportedOperationException("Open " + SERIAL_PORT
                        + " failed")
            }
//            val mPermissionFilter = IntentFilter(
//                    FtdiSerialCommunication.USB_INTENT_FILTER)
//            mCtx.getApplicationContext().registerReceiver(
//                    mUsbDevicePermissions, mPermissionFilter)

            // Bug case where several permission windows are shown
//            while (true == semUsb.tryAcquire()) {
//                // acquire all sem available
//           }
        }
        Timber.d("setup end")
    }

    @Throws(Exception::class)
    override fun startTest(): RESULT? {
        Log.d(TAG, "Test Gpio 3")
        gpioPort.setPin3(false)
        isGpio3Down()
        gpioPort.setPin3(true)
        isGpio3Up()
        Timber.d("Test Com")
        isComOk()
        Timber.d("Test Gpio 1 et 2")
        areGpio1and2Ok()
        Timber.d("Test Input")
        isInput4OK()
        return mResult
    }

    override fun tearDown() {
        Timber.d("tearDown")

//            mCtx.getApplicationContext().unregisterReceiver(
//                    mUsbDevicePermissions);
        if (directSerial.isOpened) {
            directSerial.close()
        }
        if (ftdiSerial.isOpened) {
            ftdiSerial.close()
        }
        resetGpio()
        super.tearDown()
    }

    private fun resetGpio() {
        Log.d(TAG, "resetGpio")
        gpioPort.setPin1(false)
        gpioPort.setPin2(false)
        gpioPort.setPin3(false)
        gpioPort.setPin4(false)
        gpioPort.setPinEn(false)
        gpioPort.setPinUsbEn(false)
        gpioPort.setPinUsbIdSw(false)
    }

    @Throws(Exception::class)
    private fun isGpio3Down() {
        Log.d(TAG, "isGpio3Down")
        val devices = ftdiSerial.listDevices()
        if (devices != null && devices.isNotEmpty()) {
            Log.e(TAG, "Ftdi is on")
            Timber.e("Ftdi is on")
            waitOnError("[FAIL] Test 1 : Gpio 3 is up instead of down")
        }
    }

    @Throws(TestFailException::class)
    private fun isGpio3Up() {
        Log.d(TAG, "isGpio3Up")
        gpioPort.setPinEn(true)
        gpioPort.setPinUsbEn(true)
        gpioPort.setPinUsbIdSw(true)
        val deviceConnected = UsbHelper.waitingForDeviceConnected(mCtx, FTX230X_VID, FTX230X_PID, 3, TimeUnit.SECONDS)
        Log.d(TAG, "usb device " + UsbHelper.getUsbDeviceListLog(mCtx))
        when (deviceConnected) {
            RESULT.OK -> {
                SystemClock.sleep(2000)
                val devices = ftdiSerial.listDevices()
                if (devices == null || devices.isEmpty()) {
                    Log.e(TAG, "Ftdi is off")
                    Timber.e("Ftdi is off")
                    postErrorAndFail("[FAIL] Test 1 : Gpio 3 is down instead of up")
                }
            }
            else -> postErrorAndFail("Usb was not activated on time")
        }
    }

    @Throws(Exception::class)
    private fun isComOk() {
        Log.d(TAG, "isComOk")
        val bufferOut = ByteArray(32)
        val bufferIn = ByteArray(32)
        populateBuffer(bufferOut)
        // Get device port
        val devices = ftdiSerial.listDevices()
        if (devices != null) {
            ftdiSerial.open(devices[0], 9600)
        }
        if (!ftdiSerial.isOpened || !directSerial.isOpened) {
            waitOnError("[FAIL] Test 2 : Port com and Ftdi are not opened")
        }
        Timber.d("Com are opened")

        // clear buffer
        directSerial.flush()
        ftdiSerial.send(bufferOut, bufferOut.size)
        Log.d(TAG, "items length send " + CpcBytes.byteArrayToString(bufferOut))
        directSerial.receive(200, bufferIn.size, bufferIn)
        Log.d(TAG, "items length receive " + CpcBytes.byteArrayToString(bufferIn))
        Timber.d("compare")
        if (!Arrays.equals(bufferIn, bufferOut)) {
            waitOnError("[FAIL] Test 2 : Com from FTDI to Serial went wrong")
        }
        populateBuffer(bufferOut)
        Timber.d("Second test")
        ftdiSerial.flush()
        directSerial.send(bufferOut, bufferOut.size)
        ftdiSerial.receive(200, bufferIn.size, bufferIn)
        if (!Arrays.equals(bufferIn, bufferOut)) {
            waitOnError("[FAIL] Test 2 : Com from Serial to FTDI went wrong")
        }
        Timber.d(" test end")
    }

    @Throws(Exception::class)
    private fun areGpio1and2Ok() {
        Log.d(TAG, "areGpio1and2Ok")
        gpioPort.setPin1(false)
        gpioPort.setPin2(false)
        Timber.d("1 et 2 LOW")
        if (!ftdiSerial.cts) {
            waitOnError("[FAIL] Test 3 : Output 1 or 2 is HIGH instead of LOW")
        }
        gpioPort.setPin1(true)
        gpioPort.setPin2(false)
        SystemClock.sleep(300)
        Timber.d("1 HIGH")
        if (ftdiSerial.cts) {
            waitOnError("[FAIL] Test 3 : Output 1 is LOW instead of HIGH")
        }
        gpioPort.setPin1(false)
        gpioPort.setPin2(true)
        SystemClock.sleep(300)
        Timber.d("2 HIGH")
        if (ftdiSerial.cts) {
            waitOnError("[FAIL] Test 3 : Output 2 is LOW instead of HIGH")
        }
        Timber.d("Test ok")
    }

    @Throws(Exception::class)
    private fun isInput4OK() {
        Log.d(TAG, "isInput4OK")
        gpioPort.setPin1(false)
        gpioPort.setPin2(false)
        ftdiSerial.setRts(false)
        SystemClock.sleep(200)
        if (!gpioPort.inPinInput) {
            waitOnError("[FAIL] Test 4 : Pin 4 is LOW instead of HIGH")
        }
        ftdiSerial.setRts(true)
        SystemClock.sleep(200)
        if (gpioPort.inPinInput) {
            waitOnError("[FAIL] Test 4 : Pin 4 is HIGH instead of LOW")
        }
    }

    private fun populateBuffer(buf: ByteArray) {
        Log.d(TAG, "populateBuffer")
        val ran = Random()
        ran.nextBytes(buf)
    }
}
