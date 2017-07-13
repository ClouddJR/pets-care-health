package com.clouddroid.pettypetscarehealth.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import com.clouddroid.pettypetscarehealth.R;

/**
 * Created by Arkadiusz on 13.07.2017.
 */

public class DialogAnimalPicker extends Dialog {

  public DialogAnimalPicker(@NonNull Context context, @StyleRes int themeResId) {
    super(context, themeResId);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_animal_picker);
  }
}
