package fr.coppernic.samples.core.ui;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.coppernic.samples.core.R;
import fr.coppernic.samples.core.power.PowerMgmtActivity;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @OnClick(R.id.button)
    void lauchPowerMgmtActivity() {
        startActivity(new Intent(getContext(), PowerMgmtActivity.class));
    }
}
