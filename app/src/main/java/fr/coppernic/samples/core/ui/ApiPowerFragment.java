package fr.coppernic.samples.core.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.coppernic.samples.core.R;
import fr.coppernic.samples.core.power.PowerActivity;
import fr.coppernic.samples.core.utils.Definitions;
import fr.coppernic.sdk.power.api.peripheral.Peripheral;
import fr.coppernic.sdk.power.impl.cizi.CiziPeripheral;
import fr.coppernic.sdk.power.impl.cone.ConePeripheral;
import fr.coppernic.sdk.power.impl.idplatform.IdPlatformPeripheral;
import fr.coppernic.sdk.utils.helpers.CpcOs;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApiPowerFragment extends Fragment {
    public static final String TAG = "ApiPowerFragment";

    @BindView(R.id.spinPower)
    Spinner spinPower;

    public ApiPowerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_api_power, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        setAdapterToPowerSpinner();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setAdapterToPowerSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                                          android.R.layout.simple_list_item_1,
                                                          getPeripheralList());
        spinPower.setAdapter(adapter);
    }

    private String[] getPeripheralList() {
        List<String> list = new ArrayList<>();
        if (CpcOs.isCone()) {
            for (ConePeripheral p : ConePeripheral.values()) {
                list.add(p.toString());
            }
        } else if (CpcOs.isCizi()) {
            for (CiziPeripheral p : CiziPeripheral.values()) {
                list.add(p.toString());
            }
        } else if (CpcOs.isIdPlatform()) {
            for (IdPlatformPeripheral p : IdPlatformPeripheral.values()) {
                list.add(p.toString());
            }
        }
        return list.toArray(new String[]{});
    }

    private Peripheral peripheralFromString(String s) {
        if (CpcOs.isCone()) {
            return ConePeripheral.valueOf(s);
        } else if (CpcOs.isCizi()) {
            return CiziPeripheral.valueOf(s);
        } else if (CpcOs.isIdPlatform()) {
            return IdPlatformPeripheral.valueOf(s);
        }
        return null;
    }

    @OnClick(R.id.btnActivityLaunch)
    void launchActivity() {
        String s = (String) spinPower.getSelectedItem();
        Peripheral p = peripheralFromString(s);
        //Log.v(TAG, p.toString());
        if (p != null) {
            Intent intent = new Intent(getContext(), PowerActivity.class);
            intent.putExtra(Definitions.KEY_PERIPHERAL, p);
            startActivity(intent);
        }
    }
}
