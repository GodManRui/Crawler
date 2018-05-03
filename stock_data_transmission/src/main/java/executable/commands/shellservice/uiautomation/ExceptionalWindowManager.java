package executable.commands.shellservice.uiautomation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * 一个负责判断并处理异常窗口弹出的类
 * 
 * @author chenyp
 *
 */
public class ExceptionalWindowManager implements Runnable {
	public static final String TAG = "DialogManager";
	
	private UiAutomationHelper monitor;
	private AccessibilityEvent lastEvent;
	
	private Thread mHandleThread;
	private boolean mQuit = false;
	
	private LinkedBlockingQueue<AccessibilityEvent> mExceptionalWindowQueue = new LinkedBlockingQueue<AccessibilityEvent>();
	
	private ArrayList<String> packages = new ArrayList<String>();
	
	private String[] availableViewText = new String[] {
			"允许",
			"确定",
			"取消"
	};
	
	public ExceptionalWindowManager(UiAutomationHelper monitor) {
		this.monitor = monitor;
	}
	
	public void startCheck() {
		mQuit = false;
		mHandleThread = new Thread(this);
		mHandleThread.setName("ExceptionalWindowHandler");
		mHandleThread.start();
	}
	
	public void stopCheck() {
		mQuit = true;
		if(mHandleThread != null) {
			mHandleThread.interrupt();
		}
	}
	
	public void addPackage(String packageName) {
		packages.add(packageName);
	}
	
	//异常窗口判断函数
	private boolean isUnexceptedWindow(AccessibilityEvent window) {
		//有的手机运行uiautomator events时是不会打印当前窗口的，所以PhoneWindowStateMonitor的当前窗口
		//只能是null，这种情况视为异常
		if(window == null) {
			return true;
		}
		
		if(packages.isEmpty()) {
			return false;
		}
		
		for(String pkg : packages) {
			//如果有checker声明忽略此窗口则此窗口不会做为异常窗口进行处理
			if(window.getPackageName() != null && window.getPackageName().equals(pkg)) {
				return false;
			}
		}
		
		return true;
	}
	
	//判断当前窗口是否处于异常状态
	private boolean isCurrentWindowUnexpected() {
		return isUnexceptedWindow(lastEvent);
	}
	
	private void tapScreen(Point point) {
		System.out.println("##tap screen " + point.x + "," + point.y);
		try {
			Runtime.getRuntime().exec(new String[]{"input", "tap", point.x + "", point.y + ""});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isTextValid(AccessibilityNodeInfo node) {
		if(!node.isClickable()) {
			return false;
		}
		
		CharSequence text = node.getText();
		if(text == null || text.toString().isEmpty()) {
			return false;
		}
		
		for(String validStr : availableViewText) {
			if(text.toString().startsWith(validStr)) {
				System.out.println("##find text-->[" + text);
				return true;
			}
		}
		
		return false;
	}
	
	//计算一个点的坐标，点击这个点可能会让异常窗口消失，只是可能
	//因为没有固定的判断方法，所以这里只能根据经验查找特定控件
	private Point caculateTapPointCoordinate() {
		Point point = null;
		
		for(AccessibilityNodeInfo node : getNodeList()) {
			if(UiNodeUtil.isVisibleToUser(node) && isTextValid(node) && !node.getClassName().toString().contains("CheckBox")) {
				Rect bound = new Rect();
				node.getBoundsInScreen(bound);
				
				point = new Point();
				point.x = bound.centerX();
				point.y = bound.centerY();
				System.out.println("##find bounds - " + bound.toShortString() + ", point [" + point.x + "," + point.y + "]");
				break;
			}
		}
		return point;
	}
	
	private ArrayList<AccessibilityNodeInfo> getNodeList() {
		ArrayList<AccessibilityNodeInfo> nodeList = new ArrayList<AccessibilityNodeInfo>();
		AccessibilityNodeInfo rootNode = monitor.getRootInActiveWindow();
		genNodeList(rootNode, nodeList);
		
		return nodeList;
	}
	
	private void genNodeList(AccessibilityNodeInfo root, ArrayList<AccessibilityNodeInfo> nodeList) {
		if(root == null) {
			return;
		}
		
		nodeList.add(root);
		
		for(int i = 0; i < root.getChildCount(); i++) {
			genNodeList(root.getChild(i), nodeList);
		}
	}
	
	/**
	 * 异常窗口的处理函数
	 * 
	 * 之所以要把back操作放在控件查找后面，是因为有的对话框虽然能够被back键消除，
	 * 但对话框会执行我们不想要得操作，比如授权框，点击back后可能就相当于拒绝授权
	 * 操作了。
	 * 
	 * @param event
	 */
	private void handleUnexceptedWindow(AccessibilityEvent window) {
		System.out.println("unexpected window #++++++++++++++#: [" + window.getPackageName() + "/" + window.getClassName() + "]");
		
		//如果满足以下两个条件则先不做处理
		//1、当前窗口已经处于正常状态
		//2、有新的异常窗口弹出
		if(!isCurrentWindowUnexpected() || window != lastEvent) {
			return;
		}
		
		//通过操作窗口元素进行处理
		System.out.println("##start find View to tap");
		
		while(window == lastEvent) {
			Point point = caculateTapPointCoordinate();
			if(point == null) {
				System.out.println("##point is null");
				return;
			}
			
			//查找控件就代表窗口监听程序要重新启动，这里保存重启后收到的最新窗口信息，
			//与点击后的最新窗口进行比较，判断有没有窗口变化
			if(window == lastEvent) {
				tapScreen(point);
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void windowStateChanged(AccessibilityEvent event) {
		lastEvent = AccessibilityEvent.obtain(event);
		
		if(isUnexceptedWindow(lastEvent)) {
			//添加异常窗口到异常窗口队列中
			try {
				synchronized(mExceptionalWindowQueue) {
					mExceptionalWindowQueue.put(lastEvent);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			//如果是非异常窗口则丢弃之前所有的异常窗口信息，因为
			//当前窗口已经处于正常状态
			synchronized(mExceptionalWindowQueue) {
				while(mExceptionalWindowQueue.size() > 0) {
					mExceptionalWindowQueue.poll().recycle();
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			while(!mQuit) {
				AccessibilityEvent window = mExceptionalWindowQueue.take();
				//如果发现异常窗口，则取最新的那个进行处理
				synchronized(mExceptionalWindowQueue) {
					while(mExceptionalWindowQueue.size() > 0) {
						window.recycle();
						window = mExceptionalWindowQueue.poll();
					}
				}
				
				if(window != null) {
					handleUnexceptedWindow(window);
				}
			}
		} catch (InterruptedException e) {
			System.out.println("ExceptionalWindowHandler is quit!");
		}
	}
}
