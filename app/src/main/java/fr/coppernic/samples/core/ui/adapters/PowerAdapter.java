package fr.coppernic.samples.core.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.coppernic.samples.core.BuildConfig;
import fr.coppernic.samples.core.R;
import fr.coppernic.samples.core.power.PowerActivity;
import fr.coppernic.samples.core.utils.Definitions;
import fr.coppernic.sdk.power.PowerManager;
import fr.coppernic.sdk.power.api.PowerListener;
import fr.coppernic.sdk.power.api.peripheral.Peripheral;
import fr.coppernic.sdk.utils.core.CpcResult.RESULT;
import fr.coppernic.sdk.utils.debug.L;

/**
 * <p>Created on 20/11/17
 *
 * @author bastien
 */
public class PowerAdapter extends ArrayAdapter<Peripheral> {

    private final LayoutInflater inflater;
    private final List<Peripheral> peripheralList;

    public PowerAdapter(@NonNull Context context,
                        @NonNull List<Peripheral> peripherals) {
        super(context, R.layout.listview_item_power, R.id.tvPowerName, peripherals);
        this.inflater = LayoutInflater.from(context);
        this.peripheralList = peripherals;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Peripheral peripheral = peripheralList.get(position);
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
            holder.update(peripheral);
        } else {
            convertView = inflater.inflate(R.layout.listview_item_power, parent, false);
            holder = new ViewHolder(convertView, peripheral);
            convertView.setTag(holder);
        }

        return super.getView(position, convertView, parent);
    }

    static final class ViewHolder implements PowerListener {
        private static final String TAG = "ViewHolder";
        private static final boolean DEBUG = BuildConfig.DEBUG;
        @BindView(R.id.tvPowerName)
        TextView tv;
        @BindView(R.id.togglePower)
        ToggleButton tg;
        @BindView(R.id.btnLaunchPowerAct)
        ImageButton btn;

        private Peripheral peripheral;

        ViewHolder(View view, @NonNull Peripheral peripheral) {
            ButterKnife.bind(this, view);
            update(peripheral);
        }

        void update(@NonNull Peripheral peripheral){
            this.peripheral = peripheral;
            tv.setText(peripheral.toString());
            PowerManager.get().registerListener(this , peripheral);
        }

        @OnClick(R.id.togglePower)
        void toggle(){
            PowerManager.get().power(tg.getContext(), peripheral, tg.isChecked());
        }

        @OnClick(R.id.btnLaunchPowerAct)
        void launch(){
            Context context = btn.getContext();
            Intent intent = new Intent(context, PowerActivity.class);
            intent.putExtra(Definitions.KEY_PERIPHERAL, peripheral);
            context.startActivity(intent);
        }

        @Override
        public void onPowerUp(RESULT res, Peripheral peripheral) {
            L.m(TAG, DEBUG, res.toString());
            if(res != RESULT.OK){
                tg.setChecked(false);
            }
        }

        @Override
        public void onPowerDown(RESULT res, Peripheral peripheral) {
            L.m(TAG, DEBUG, res.toString());
        }
    }
}
