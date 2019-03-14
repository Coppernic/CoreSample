package fr.coppernic.samples.core.ui;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.coppernic.samples.core.R;
import fr.coppernic.samples.core.ui.adapters.PowerAdapter;
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

    @BindView(R.id.listPower)
    ListView lvPower;

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
        //setAdapterToPowerSpinner();
        setAdapterToList();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setAdapterToList() {
        PowerAdapter powerAdapter = new PowerAdapter(getContext(), getPeripheralList());
        lvPower.setAdapter(powerAdapter);
    }

    private List<Peripheral> getPeripheralList() {
        List<Peripheral> list = new ArrayList<>();
        if (CpcOs.isCone()) {
            list.addAll(Arrays.asList(ConePeripheral.values()));
        } else if (CpcOs.isCizi()) {
            list.addAll(Arrays.asList(CiziPeripheral.values()));
        } else if (CpcOs.isIdPlatform()) {
            list.addAll(Arrays.asList(IdPlatformPeripheral.values()));
        }
        return list;
    }
}
