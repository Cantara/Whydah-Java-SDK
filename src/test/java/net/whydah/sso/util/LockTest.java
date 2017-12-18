package net.whydah.sso.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LockTest {

	Lock lock1 = new Lock();
	
	
	@Test
	public void testMultipleThreadAccessWithLockEnabled() throws InterruptedException{
		makeThreads(true);
	}
	
	@Test
	public void testMultipleThreadAccessWithLockDisabled() throws InterruptedException {
		makeThreads(false);
	}
	
	void makeThreads(boolean lockEnabled) throws InterruptedException{
		List<Thread> list = new ArrayList<>();
		for(int i = 0; i<100; i++){
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						if(lockEnabled){
							lock1.lock();
						}
						doSomething(Thread.currentThread().getName(), lockEnabled);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(lockEnabled){
						 lock1.unlock();
						}
					}
				}
			}, "thread " + i);
			list.add(t);
			t.start();
			
		}
		
		for(Thread t : list){
			t.join();
		}
		

	}
	
	int count = 0;
	boolean tested = false;

	protected void doSomething(String i, boolean useLock) {
		try {
			System.out.println("started some work by " + i);
			count ++;
			Thread.sleep(100);
			if(!tested){
				tested = true;
				Assert.assertTrue(useLock? count == 1 : (count > 1)); //see the difference?
				//when not using the lock, many thread can access doSometing() simultaneously and thus the count must be greater than 1
				//but when using the lock, only one thread can access the method and count one by one
			}
			System.out.println("finished some work by " + i);
			count = 0;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
