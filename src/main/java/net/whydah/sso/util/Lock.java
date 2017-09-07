package net.whydah.sso.util;
public class Lock{

	boolean isLocked = false;
	Thread  lockedBy = null;
	int     lockedCount = 0;

	public synchronized void lock()
			throws InterruptedException{
		Thread callingThread = Thread.currentThread();
		while(isLocked && lockedBy != callingThread){
			wait();
		}
		isLocked = true;
		lockedCount++;
		lockedBy = callingThread;
	}

	public boolean isLocked(){
		return isLocked;
	}

	public synchronized void unlock(){
		if(Thread.currentThread() == this.lockedBy){
			lockedCount--;

			if(lockedCount == 0){
				isLocked = false;
				notify();
			}
		}
	}

}