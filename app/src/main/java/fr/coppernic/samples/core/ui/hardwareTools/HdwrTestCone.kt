package fr.coppernic.samples.core.ui.hardwareTools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentManager
import fr.coppernic.sdk.hdk.cone.GpioPort
import fr.coppernic.sdk.serial.SerialCom
import fr.coppernic.sdk.serial.SerialFactory
import fr.coppernic.sdk.utils.core.CpcResult.RESULT
import fr.coppernic.sdk.utils.io.InstanceListener
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.Semaphore


class ExpansionPortTest(ctx: Context?, man: FragmentManager,
                        notifier: HdwrTestResultInterface?) : HdwrTestDialogBase(ctx, man, notifier) {
    override val tag = "ExpansionPortTest"
    private val SERIAL_PORT = "/dev/ttyHSL1"
    private val context = ctx
    private val semUsb = Semaphore(0)
    private lateinit var directSerial: SerialCom
    private lateinit var ftdiSerial: SerialCom
    private lateinit var gpioPort: GpioPort

    private val mUsbDevicePermissions: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
//                if (FtdiSerialCommunication.USB_INTENT_FILTER.equals(action)) {
//                    val device = intent
//                            .getParcelableExtra<Parcelable>("device") as UsbDevice
//                    if (!intent.getBooleanExtra("permission", false)) {
//                        Log.d(tag, "permission denied for device "
//                                + device)
//                        postError("[FAILED] : Permission denied for USB device")
//                    } else {
//                        mResult = RESULT.OK
//                    }
//                    semUsb.release()
//                }
        }
    }

    @Throws(Exception::class)
    override fun startTest(): RESULT {
        Timber.d("Test Gpio 3")
        gpioPort.setPin3(false)
        isGpio3Down
        gpioPort.setPin3(true)

//            // semUsb.acquire();
//            if (false == semUsb.tryAcquire(10, TimeUnit.SECONDS)) {
//                postErrorAndFail("Usb was not activated on time")
//            }
//            // Return error is no permission to access to FTDI
//            if (mResult === RESULT.ERROR) {
//                // postError("[FAIL] Test 1 : Output 3 is LOW or USB is unavailable");
//                postErrorAndFail("[FAIL] Test 1 : Output 3 is LOW or USB is unavailable")
//            }
        isGpio3Up
        Timber.d("Test Com")
        isComOk.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    waitOnError(it.message)
                }
                )


        Log.d(tag, "Test Gpio 1 et 2")
        areGpio1and2Ok()
        Log.d(tag, "Test Input")
        isInput4OK
        return mResult
    }

    override fun setUp(): Completable? {
        Log.d(tag, "setUp")
        super.setUp()
        return Completable.create { emitter ->
            val g = GpioPort.GpioManager.get()
                    .getGpioSingle(context)
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
                    val d = context?.let {
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
                    val f = context?.let {
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
//            }
        }
    }

    override fun tearDown() {
        Log.d(tag, "tearDown")
        context?.applicationContext?.unregisterReceiver(
                mUsbDevicePermissions)
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
        gpioPort.setPin1(false)
        gpioPort.setPin2(false)
        gpioPort.setPin3(false)
        gpioPort.setPin4(false)
    }

    @get:Throws(Exception::class)
    private val isGpio3Down: Boolean
        private get() {
            var ret = true
            val devices: Array<String> = ftdiSerial.listDevices()
            if (devices != null && devices.isNotEmpty()) {
                Log.e(tag, "Ftdi is on")
                Timber.e("[FAIL] Test 1 : Gpio 3 is up instead of down")
                waitOnError("[FAIL] Test 1 : Gpio 3 is up instead of down")
                ret = false
            }
            return ret
        }

    // postError("[FAIL] Test 1 : Gpio 3 is down instead of up");
    @get:Throws(TestFailException::class)
    private val isGpio3Up: Boolean
        private get() {
            var ret = true
            val devices: Array<String> = ftdiSerial.listDevices()
            if (devices.isEmpty()) {
                Log.e(tag, "Ftdi is off")
                //postErrorAndFail("[FAIL] Test 1 : Gpio 3 is down instead of up")
                Timber.e("[FAIL] Test 1 : Gpio 3 is down instead of up")
                postError("[FAIL] Test 1 : Gpio 3 is down instead of up");
                ret = false
            }
            return ret
        }

//    // Get device port
//    @get:Throws(Exception::class)
//    private val isComOk:
//
//    // clear buffer
//            Boolean
//        private get() {
//            val ret = true
//            val bufferOut = ByteArray(32)
//            val bufferIn = ByteArray(32)
//            populateBuffer(bufferOut)
//            // Get device port
//            val devices: Array<String> = ftdiSerial.listDevices()
//            if (!devices.isNullOrEmpty()) {
//                ftdiSerial.open(devices[0], 115200)
//            }
//            if (!ftdiSerial.isOpened || !directSerial.isOpened) {
//                Timber.e("[FAIL] Test 2 : Port com and Ftdi are not opened")
//                waitOnError("[FAIL] Test 2 : Port com and Ftdi are not opened")
//            }
//            Log.d(tag, "Com are opened")
//
//            // clear buffer
//            directSerial.flush()
//            ftdiSerial.send(bufferOut, bufferOut.size)
//            ftdiSerial.flush()
//            directSerial.receive(200, bufferIn.size, bufferIn)
//            Log.d(tag, "compare")
//            if (!bufferIn.contentEquals(bufferOut)) {
//                Timber.e("[FAIL] Test 2 : Com from FTDI to Serial went wrong")
//                waitOnError("[FAIL] Test 2 : Com from FTDI to Serial went wrong")
//            }
//            populateBuffer(bufferOut)
//            Log.d(tag, "Second test")
//            ftdiSerial.flush()
//            directSerial.send(bufferOut, bufferOut.size)
//            directSerial.flush()
//            ftdiSerial.receive(200, bufferIn.size, bufferIn)
//            if (!bufferIn.contentEquals(bufferOut)) {
//                Timber.e("[FAIL] Test 2 : Com from Serial to FTDI went wrong")
//                waitOnError("[FAIL] Test 2 : Com from Serial to FTDI went wrong")
//            }
//            Log.d(tag, " test end")
//            return ret
//        }

    // Get device port
    @get:Throws(Exception::class)
    private val isComOk:

    // clear buffer
            Completable
        private get() {
            return Completable.create { emitter ->
                val ret = true
                val bufferOut = ByteArray(32)
                val bufferIn = ByteArray(32)
                populateBuffer(bufferOut)
                // Get device port
                val devices: Array<String> = ftdiSerial.listDevices()
                if (!devices.isNullOrEmpty()) {
                    ftdiSerial.open(devices[0], 115200)
                }
                if (!ftdiSerial.isOpened || !directSerial.isOpened) {
                    Timber.e("[FAIL] Test 2 : Port com and Ftdi are not opened")
//                    emitter.onError(Exception("[FAIL] Test 2 : Port com and Ftdi are not opened"))
                    waitOnError("[FAIL] Test 2 : Port com and Ftdi are not opened")
                }
                Log.d(tag, "Com are opened")

                // clear buffer
                directSerial.flush()
                ftdiSerial.send(bufferOut, bufferOut.size)
                ftdiSerial.flush()
                directSerial.receive(200, bufferIn.size, bufferIn)
                Log.d(tag, "compare")
                if (!bufferIn.contentEquals(bufferOut)) {
                    Timber.e("[FAIL] Test 2 : Com from FTDI to Serial went wrong")
                    waitOnError("[FAIL] Test 2 : Com from FTDI to Serial went wrong")
                }
                populateBuffer(bufferOut)
                Log.d(tag, "Second test")
                ftdiSerial.flush()
                directSerial.send(bufferOut, bufferOut.size)
                directSerial.flush()
                ftdiSerial.receive(200, bufferIn.size, bufferIn)
                if (!bufferIn.contentEquals(bufferOut)) {
                    Timber.e("[FAIL] Test 2 : Com from Serial to FTDI went wrong")
                    waitOnError("[FAIL] Test 2 : Com from Serial to FTDI went wrong")
                }
                Log.d(tag, " test end")
                emitter.onComplete()
            }
        }

    @Throws(Exception::class)
    private fun areGpio1and2Ok() {
        gpioPort.setPin1(false)
        gpioPort.setPin2(false)
        Log.d(tag, "1 et 2 LOW")
        if (ftdiSerial.getCts() === false) {
            waitOnError("[FAIL] Test 3 : Output 1 or 2 is HIGH instead of LOW")
        }
        gpioPort.setPin1(true)
        gpioPort.setPin2(false)
        Thread.sleep(100)
        Log.d(tag, "1 HIGH")
        if (ftdiSerial.getCts() === true) {
            waitOnError("[FAIL] Test 3 : Output 1 is LOW instead of HIGH")
        }
        gpioPort.setPin1(false)
        gpioPort.setPin2(true)
        Thread.sleep(100)
        Log.d(tag, "2 HIGH")
        if (ftdiSerial.getCts() === true) {
            waitOnError("[FAIL] Test 3 : Output 2 is LOW instead of HIGH")
        }
        Log.d(tag, "Test ok")
    }

    @get:Throws(Exception::class)
    private val isInput4OK: Unit
        private get() {
            gpioPort.setPin1(false)
            gpioPort.setPin2(false)
            ftdiSerial.setRts(false)
            Thread.sleep(100)
            //TODO control test ExpansionPort.getPin(4) == 0
            if (!gpioPort.outPin4) {
                waitOnError("[FAIL] Test 4 : Pin 4 is LOW instead of HIGH")
            }
            ftdiSerial.setRts(true)
            Thread.sleep(100)
            //TODO control test ExpansionPort.getPin(4) == 1
            if (gpioPort.outPin4) {
                waitOnError("[FAIL] Test 4 : Pin 4 is HIGH instead of LOW")
            }
        }

    private fun populateBuffer(buf: ByteArray) {
        val ran = Random()
        ran.nextBytes(buf)
    }
}
