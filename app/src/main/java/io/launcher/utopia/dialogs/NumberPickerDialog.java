package io.launcher.utopia.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import io.launcher.utopia.R;

public abstract class NumberPickerDialog extends AlertDialog {
    int defaultValue = 4;

    public NumberPickerDialog(@NonNull Context context, int defaultValue) {
        super(context);
        getWindow().setGravity(Gravity.BOTTOM);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.defaultValue = defaultValue;
        getWindow().setLayout(dm.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_number_picker);
        final NumberPicker npColumns = findViewById(R.id.npColumns);
        npColumns.setMinValue(3);
        npColumns.setMaxValue(5);

        Button btOK = findViewById(R.id.btOK);
        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOKPressed(npColumns.getValue());
                dismiss();
            }
        });

        npColumns.setValue(this.defaultValue);

    }

    public abstract void onOKPressed(int i);
}
