package fr.coppernic.samples.core.ui.hardwareTools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import fr.coppernic.samples.core.R;

public class OptionDialogTerminal extends DialogFragment {

	public final static String TAG = "OptionDialogTerminal";

	public final static int RTS = 0;
	public final static int CTS = 1;
	public final static int CLEAR = 2;
	public final static int REFRESH = 3;
	public final static int CLEAR_HISTORY = 4;
	private final static int COUNT = 5;

	private boolean[] checkedItems;
	private boolean[] selectedItems = new boolean[COUNT];

	public interface OptListener {

		void setOpt(boolean[] checkedOpt);
	}

	public static OptionDialogTerminal newInstance(boolean rts, boolean cts,
			boolean clear, boolean refresh, boolean clear_history) {

		OptionDialogTerminal instance = new OptionDialogTerminal();

		boolean[] b = new boolean[COUNT];

		b[RTS] = rts;
		b[CTS] = cts;
		b[CLEAR] = clear;
		b[REFRESH] = refresh;
		b[CLEAR_HISTORY] = clear_history;

		Bundle args = new Bundle();
		args.putBooleanArray("checkedItems", b);
		instance.setArguments(args);

		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		checkedItems = getArguments().getBooleanArray("checkedItems");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.dial_opt)
				.setMultiChoiceItems(R.array.array_opt_term,
						checkedItems,
						new DialogInterface.OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {

								selectedItems[which] = isChecked;
							}
						})
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {

//						CpcHardwareTest act = (CpcHardwareTest) getActivity();
//						OptListener l = act.getTerminalFragment();
//						l.setOpt(selectedItems);
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {

							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
