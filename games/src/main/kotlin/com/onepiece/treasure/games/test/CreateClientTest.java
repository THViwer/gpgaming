//package com.onepiece.treasure.games.test;
//
//import java.security.SecureRandom;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.digest.DigestUtils;
//
//public class CreateClientTest {
//
//	private static String propertyId = "7864997";
//	private static String desKey = "KsAsFUHSyl9bH3qUTxxHg1mZGRgwQpQ4";
//	private static String md5Key = "mnhLwO3Zh0M34d8Pr2CESM2b/jZtvAIvEvu0Af7aXrg=";
//	private static String apiUrl = "https://api3.apidemo.net:8443";
//
//	public static void main(String[] args) throws Exception {
//
//		Long ts = System.currentTimeMillis();
//		String queryString = "agent=00000a&client=t" + ts + "&password=test001&vipHandicaps=12&orHandicaps=1&orHallRebate=0.5&random="+new SecureRandom().nextLong();
//
//		String data = TripleDES.encrypt(queryString, desKey, null);
//
//		String sign = Base64.encodeBase64String(DigestUtils.md5((data + md5Key)));
//
//
//		System.out.println(data);
//		System.out.println(sign);
//
//	}
//
//}