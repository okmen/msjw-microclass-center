package cn.microclass.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Provider {

	private static ClassPathXmlApplicationContext context;

	public static void main(String[] args) {
		context = new ClassPathXmlApplicationContext(new String[] { "provider.xml" });
		context.start();
		System.out.println("---账号中心服务，启动---");
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
