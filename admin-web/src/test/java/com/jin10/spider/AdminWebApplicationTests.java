package com.jin10.spider;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import org.apache.ibatis.annotations.Param;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@SpringBootTest
class AdminWebApplicationTests {

	@Test
	void contextLoads() {

	}

	public static void main(String[] args) {
//		String s = "117.69.201.253,49.89.85.14,59.57.38.232,114.239.252.141,121.226.215.4,49.70.89.56,117.95.162.212,175.43.59.26,183.164.239.31,117.57.91.147,182.34.32.59,183.166.86.149,183.164.238.252,123.163.96.170,1.198.72.222,1.197.204.177,59.57.148.85,1.197.204.49,114.239.198.215,1.197.203.249,27.152.91.77,222.190.222.246,114.239.151.101,117.57.90.193,49.70.85.9,182.34.35.8,183.166.70.255,115.210.67.169,117.28.96.93,114.239.110.190,27.43.186.186,114.239.172.23,114.239.250.14,117.57.90.248,183.164.239.71,183.166.111.39,27.43.189.219,117.30.112.202,27.152.91.245,222.89.32.132,183.164.239.84,183.166.124.129,182.34.33.183,171.12.113.150,121.205.84.80,182.34.37.4,123.163.97.154,110.243.11.39,59.57.149.218,117.57.91.182,59.57.148.50,59.57.148.4,58.253.156.58,121.205.14.10,117.30.113.130,120.83.109.165,114.239.150.235,182.35.87.114,123.52.97.225,60.13.42.176,117.57.91.205,114.239.42.216,113.124.94.72,114.239.172.20,117.69.200.70,117.57.90.245,117.69.200.223,27.152.90.96,183.164.238.68,144.123.69.216,114.239.249.51,27.152.90.155,125.78.176.185,117.28.97.222,49.89.222.253,117.57.90.202,117.57.90.253,117.69.200.29,121.232.111.96,171.35.170.138,117.57.90.73,117.57.90.189,113.194.31.62,182.35.87.89,183.164.238.137,120.83.111.18,117.95.199.139,117.57.91.240,117.57.90.150,117.69.201.137,121.226.188.41,117.30.113.176,58.253.152.244,183.164.238.179,183.164.239.34,183.166.7.2,117.95.201.104,121.226.188.158,117.57.91.54,114.239.255.105";
//		String[] split = s.split(",");
//		List<String> strings = Arrays.asList(split);
//		for (String ip : strings){
//			getBaidu(ip);
//		}
		threadPool();

	}

	private static int getBaidu(String ip){
		try{

			InetSocketAddress addr = new InetSocketAddress(ip, 9999);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
			int status = HttpRequest.get("m.baidu.com")
					.setProxy(proxy)
					//.header(Header.USER_AGENT, "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Mobile Safari/537.36")
					.timeout(10000).execute().getStatus();

			System.out.println("=====" + status + "====" + ip);
			return status;

		}catch (Exception ex){

		}
		return  0;
	}


	private static void threadPool(){
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		List<Future<Integer>> resultList = new ArrayList<Future<Integer>>();
		Random random = new Random();
		for (int i = 0; i < 10; i ++) {
			int rand = random.nextInt(10);
			FactorialCalculator factorialCalculator = new FactorialCalculator(rand);
			Future<Integer> res = executor.submit(factorialCalculator);//异步提交, non blocking.
			resultList.add(res);
		}

		do {
//            System.out.printf("number of completed tasks: %d\n", executor.getCompletedTaskCount());
			for (int i = 0; i < resultList.size(); i++) {
				Future<Integer> result = resultList.get(i);
				System.out.printf("Task %d : %s \n", i, result.isDone());
			}
			try {
				TimeUnit.MILLISECONDS.sleep(50);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (executor.getCompletedTaskCount() < resultList.size());


		System.out.println("Results as folloers:");
		for (int i = 0; i < resultList.size(); i++) {
			Future<Integer> result = resultList.get(i);
			Integer number = null;

			try {
				number = result.get();// blocking method
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			System.out.printf("task: %d, result %d:\n", i, number);
		}
		executor.shutdown();
	}

	static class FactorialCalculator implements Callable<Integer> {


		private Integer number;

		public FactorialCalculator(Integer number) {
			this.number = number;
		}
		public Integer call() throws Exception {
			int result = 1;

			if (number == 0 || number == 1) {
				result = 1;
			}else {
				for (int i = 2; i < number; i++) {
					result *= i;
					TimeUnit.MICROSECONDS.sleep(200);
					if (i == 5) {
						throw new IllegalArgumentException("excepion happend");//计算5以上的阶乘都会抛出异常. 根据需要注释该if语句
					}
				}
			}
			System.out.printf("%s: %d\n", Thread.currentThread().getName(), result);
			return result;
		}
	}

}
