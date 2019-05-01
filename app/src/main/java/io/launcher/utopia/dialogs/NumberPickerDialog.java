package io.launcher.utopia.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

import io.launcher.utopia.R;

public abstract class NumberPickerDialog extends AlertDialog {
    private final int defaultValue;

    protected NumberPickerDialog(@NonNull Context context, int defaultValue) {
        super(context);
        Objects.requireNonNull(getWindow()).setGravity(Gravity.BOTTOM);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.defaultValue = defaultValue;
        getWindow().setLayout(dm.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_number_picker);
        final NumberPicker npColumns = findViewById(R.id.npColumns);
        Objects.requireNonNull(npColumns).setMinValue(3);
        npColumns.setMaxValue(5);

        Button btOK = findViewById(R.id.btOK);
        Objects.requireNonNull(btOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOKPressed(npColumns.getValue());
                dismiss();
            }
        });

        npColumns.setValue(this.defaultValue);

    }

    protected abstract void onOKPressed(int i);
}
