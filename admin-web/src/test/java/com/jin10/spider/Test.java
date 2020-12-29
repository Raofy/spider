package com.jin10.spider;

import javax.xml.bind.DatatypeConverter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


public class Test {

	public static void main(String[] args) throws Exception {
//		/api/queues/vhost/name
//		/api/queues
//		String httpurl = "http://47.111.24.100:15672/api/queues/%2F/sitech.devops.deploy.queue/bindings";
		String httpurl = "http://47.111.24.100:15672/api/api/queues";
		/**
		 * 1.初始化client
		 */
		String CODE_ENCODING = "UTF-8";
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter("charset", CODE_ENCODING);
		SchemeRegistry schreg = new SchemeRegistry();    
		schreg.register(new Scheme("http",80,PlainSocketFactory.getSocketFactory()));   
		schreg.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		PoolingClientConnectionManager pccm = new PoolingClientConnectionManager(schreg);  
		pccm.setDefaultMaxPerRoute(20); //每个主机的最大并行链接数   
		pccm.setMaxTotal(100); //客户端总并行链接最大数 
		DefaultHttpClient client =  new DefaultHttpClient(pccm,httpParams); 
		
		/**
		 * 2.连接
		 */
		HttpGet get = new HttpGet(httpurl);
		get.addHeader("Content-Type", "application/json;charset=" + CODE_ENCODING);
		get.setHeader("Accept", "application/json");
		get.setHeader("Content-Type", "application/json;charset=" + CODE_ENCODING);
		
		String authorization = DatatypeConverter.printBase64Binary("jin10:j6ixcv2airw".getBytes("UTF-8"));
		System.out.println(authorization);
		get.setHeader("Authorization", "Basic "+authorization);
		
		HttpResponse res = client.execute(get);
		int statusCode = res.getStatusLine().getStatusCode();
		// 判断返回编码是否为200
		if (statusCode != 200 && statusCode != 201) {
			throw new Exception("Get " + httpurl + "Error!Response Code " + statusCode);
		}
		HttpEntity entity = res.getEntity();
		String response = EntityUtils.toString(entity, CODE_ENCODING);
		System.out.println(response);
	}

}
