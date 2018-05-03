package executable.commands.shellservice.uiautomation;


public class TroubledWindowKiller {

	public static void main(String[] args) {
		System.out.println("++++++" + args[0]);
		UiAutomationHelper.self().addPackage(args[0]);
		System.out.println("killer:" + android.os.Process.myPid());
		
		//这里需要添加代码阻止进程的终止
		Object o = new Object();
		synchronized(o) {
			try {
				o.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
