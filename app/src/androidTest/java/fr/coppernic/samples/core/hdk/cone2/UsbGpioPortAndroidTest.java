package fr.coppernic.samples.core.hdk.cone2;

import android.content.Context;
import androidx.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.coppernic.sdk.hdk.cone.GpioPort;
import fr.coppernic.sdk.hdk.cone.UsbGpioPort;

public class UsbGpioPortAndroidTest {

    private Context context;
    private GpioPort gpioPort;

    @Before
    public void before() {
        context = InstrumentationRegistry.getTargetContext();
        gpioPort = GpioPort.GpioManager.get().getGpioSingle(context).blockingGet();
    }

    @After
    public void after() {
        gpioPort.close();
    }

    @Test
    public void test(){
        gpioPort.setPin3(true);
        gpioPort.setPinEn(true);
        gpioPort.setPinUsbEn(true);
        gpioPort.setPinUsbIdSw(true);

        UsbGpioPort.get().setUsbPin2(context, true);
        UsbGpioPort.get().setUsbPin3(context, true);
    }
}
