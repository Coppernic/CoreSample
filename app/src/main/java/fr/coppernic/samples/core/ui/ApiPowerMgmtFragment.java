package fr.coppernic.samples.core.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.coppernic.samples.core.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApiPowerMgmtFragment extends Fragment {
    public static final String TAG = "ApiPowerMgmtFragment";

    public ApiPowerMgmtFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_api_powermgmt, container, false);
    }

}
