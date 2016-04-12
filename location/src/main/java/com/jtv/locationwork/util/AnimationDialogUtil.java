package com.jtv.locationwork.util;

import com.gitonway.niftydialogeffects.widget.niftydialogeffects.Effectstype;
import com.gitonway.niftydialogeffects.widget.niftydialogeffects.effects.BaseEffects;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.Window;

public class AnimationDialogUtil {

	public static void startAnimation(final Effectstype type, final AlertDialog create) {
		if (type != null && create != null) {
			create.setOnShowListener(new OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {

					try {
						View decorView = create.getWindow().getDecorView();
						type.getAnimator().start(decorView);
					} catch (Exception e) {
					}
				}
			});
		}

	}

}
