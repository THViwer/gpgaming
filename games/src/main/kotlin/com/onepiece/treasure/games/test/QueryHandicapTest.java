package com.onepiece.treasure.games.test;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class QueryHandicapTest {

	private static String propertyId = "7864997";
	private static String desKey = "D6Pi5hL1jVbMB42uAdZGfXzwmmN1BraW";
	private static String md5Key = "gKqKE4rktpP8rbpmY0sAnJPkwAgAuBf1jzotthMXwKk=";
	private static String apiUrl = "https://api3.apidemo.net:8443";

	public static void main(String[] args) throws Exception {

//			String queryString = "agent=00000a&random="+new SecureRandom().nextLong();

		String queryString = "agent=auzfpa&random=c3265528-301d-439a-a885-7a5436569d40";

		String data = TripleDES.encrypt(queryString, desKey, null);
		String sign = Base64.encodeBase64String(DigestUtils.md5((data + md5Key)));


		System.out.println(data);
		System.out.println(sign);
	}
}

