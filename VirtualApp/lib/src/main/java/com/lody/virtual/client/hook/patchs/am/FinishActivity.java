package com.lody.virtual.client.hook.patchs.am;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.IBinder;
import android.util.TypedValue;

import com.lody.virtual.client.hook.base.Hook;
import com.lody.virtual.client.local.ActivityClientRecord;
import com.lody.virtual.client.local.VActivityManager;

import java.lang.reflect.Method;

/**
 * @author Lody
 */

public class FinishActivity extends Hook {
	@Override
	public String getName() {
		return "finishActivity";
	}

	@Override
	public Object afterHook(Object who, Method method, Object[] args, Object result) throws Throwable {
		IBinder token = (IBinder) args[0];
		ActivityClientRecord r = VActivityManager.get().getActivityRecord(token);
		if (r != null && r.activity != null && r.info.getThemeResource() != 0) {
			boolean taskRemoved = VActivityManager.get().onActivityDestroy(token);
			if (!taskRemoved) {
				try {
					TypedValue out = new TypedValue();
					Resources.Theme theme = r.activity.getResources().newTheme();
					theme.applyStyle(r.info.getThemeResource(), true);
					if (theme.resolveAttribute(android.R.attr.windowAnimationStyle, out, true)) {

						TypedArray array = theme.obtainStyledAttributes(out.data,
								new int[]{
										android.R.attr.activityCloseEnterAnimation,
										android.R.attr.activityCloseExitAnimation
								});

						r.activity.overridePendingTransition(array.getResourceId(0, 0), array.getResourceId(1, 0));
						array.recycle();
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return super.afterHook(who, method, args, result);
	}

	@Override
	public boolean isEnable() {
		return isAppProcess();
	}
}
