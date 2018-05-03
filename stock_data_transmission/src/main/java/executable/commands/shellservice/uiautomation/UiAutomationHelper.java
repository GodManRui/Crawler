package executable.commands.shellservice.uiautomation;



import android.annotation.SuppressLint;
import android.app.UiAutomation;
import android.app.UiAutomation.OnAccessibilityEventListener;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.android.uiautomator.core.UiAutomationShellWrapper;

public class UiAutomationHelper {

    private static UiAutomationHelper mSelf;
    private static Object obj = new Object();
    //   private UiTestAutomationBridge bridge;
    private UiAutomationShellWrapper automationWrapper;
    private ExceptionalWindowManager windowHandler;

    private UiAutomationHelper() {
        windowHandler = new ExceptionalWindowManager(this);
        windowHandler.startCheck();
    }

    public static UiAutomationHelper self() {
        synchronized (obj) {
            if (mSelf == null) {
                mSelf = new UiAutomationHelper();
                Log.i("Test", "start init...");
                mSelf.init();
                Log.i("Test", "end init ...");
            }
        }

        return mSelf;
    }

    @SuppressLint("NewApi")
    private void init() {
        System.out.println("SDK [" + Build.VERSION.SDK_INT + "]");
        if (Build.VERSION.SDK_INT >= 18) {
            automationWrapper = new UiAutomationShellWrapper();
            automationWrapper.connect();
            automationWrapper.getUiAutomation().setOnAccessibilityEventListener(
                new OnAccessibilityEventListener() {
                    @Override
                    public void onAccessibilityEvent(AccessibilityEvent event) {
                        if (event != null) {
                            handleEvent(event);
                        }
                    }
                });
            System.out.println("UiAutomationShellWrapper is connected!!");
        }
        //else if (Build.VERSION.SDK_INT >= 16) {
//            bridge = new UiTestAutomationBridge() {
//                @Override
//                public void onAccessibilityEvent(AccessibilityEvent event) {
//                    if (event != null) {
//                        handleEvent(event);
//                    }
//                }
//            };
//            bridge.connect();
//            System.out.println("UiTestAutomationBridge is connected!!");
//        }
        else {
            System.out.println("Unsurpported Opration!!!");
            return;
        }
    }

    private void handleEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }

        System.out.println(event.toString());

        windowHandler.windowStateChanged(event);
    }

    public void addPackage(String packageName) {
        windowHandler.addPackage(packageName);
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        if (Build.VERSION.SDK_INT >= 18) {
            UiAutomation uiAutomation = automationWrapper.getUiAutomation();
            AccessibilityNodeInfo info = uiAutomation.getRootInActiveWindow();
            if (info != null) {
                return info;
            }
        } /*else if (Build.VERSION.SDK_INT >= 16) {
            return bridge.getRootAccessibilityNodeInfoInActiveWindow();
        }*/

        return null;
    }
}
