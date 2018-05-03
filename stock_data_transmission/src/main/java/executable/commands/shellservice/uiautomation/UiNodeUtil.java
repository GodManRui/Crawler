package executable.commands.shellservice.uiautomation;

import java.lang.reflect.Method;

import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

public class UiNodeUtil {
	public static final String NOID = "NO-ID";
	
	public static boolean isVisibleToUser(AccessibilityNodeInfo node) {
		try {
			Method m = AccessibilityNodeInfo.class.getDeclaredMethod("isVisibleToUser");
			m.setAccessible(true);
			return (Boolean) m.invoke(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static String getNodeId(AccessibilityNodeInfo node) {
		String resourceId = null;
		if(Build.VERSION.SDK_INT >= 18) {
			try {
				Method m = AccessibilityNodeInfo.class.getDeclaredMethod("getViewIdResourceName");
				m.setAccessible(true);
				resourceId = (String) m.invoke(node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(resourceId != null) {
			return resourceId;
		}
		
		return NOID;
	}
}
