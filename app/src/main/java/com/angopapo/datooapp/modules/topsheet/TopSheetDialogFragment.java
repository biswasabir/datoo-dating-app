package com.angopapo.datooapp.modules.topsheet;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Created by andrea on 23/08/16.
 */
public class TopSheetDialogFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TopSheetDialog(getContext(), getTheme());
    }
}
