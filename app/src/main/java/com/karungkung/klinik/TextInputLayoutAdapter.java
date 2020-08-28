package com.karungkung.klinik;

import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter;
import com.mobsandgeeks.saripaar.exception.ConversionException;

public class TextInputLayoutAdapter implements ViewDataAdapter<TextInputLayout, String> {
    @Override
    public String getData(TextInputLayout view) throws ConversionException {
        return view.getEditText().getText().toString();
    }
}
