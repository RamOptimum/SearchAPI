package com.searchapi.rest.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


import com.searchapi.rest.constants.EnvConstants;
import com.searchapi.rest.utils.ApiResponse;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class SearchAPIs {

	Properties userProp = new Properties();
	Properties custProp = new Properties();

	@BeforeTest
	public void getData() throws IOException {

		FileInputStream userDetails = new FileInputStream(
				System.getProperty("user.dir") + "/properties/UserDetails.properties");
		FileInputStream customerDetails = new FileInputStream(
				System.getProperty("user.dir") + "/properties/customerDetails.properties");
		userProp.load(userDetails);
		custProp.load(customerDetails);

	}

	@Test(priority = 0)
	public void generateToken() {
		String uri = EnvConstants.USER_AUTH_URL + "/authenticate";
		String jwtToken = "";
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json; charset=UTF-8");
		request.header("Accept", "application/json");
		JSONObject json = new JSONObject();
		json.put("username", userProp.getProperty("username"));
		json.put("password", userProp.getProperty("password"));
		Response raw = request.body(json.toString()).relaxedHTTPSValidation().when().log().all().post(uri)
				.then().assertThat().contentType(ContentType.JSON).log().all().extract().response();
		ApiResponse response = new ApiResponse(raw);
		if (response.getStatusCode() == 200)
			jwtToken = response.getResponseAsJson().getString("token");
		userProp.setProperty("token", jwtToken);

	}

	@Test(priority = 1)
	public void authenticateWithInvalidCredentials() {
		String uri = EnvConstants.USER_AUTH_URL + "/authenticate";
		RequestSpecification request = RestAssured.given();
		request.header("Content-type", "application/json");
		request.header("Accept", "application/json");
		JSONObject json = new JSONObject();
		json.put("username", userProp.getProperty("username"));
		json.put("password", userProp.getProperty("password").substring(1, 3));
		request.body(json.toString()).relaxedHTTPSValidation().when().log().all().post(uri).then().assertThat()
				.statusCode(401).and().contentType(ContentType.JSON).log().all().extract().response();;

	}

	@Test(priority = 2)
	public void getUserRecords() {
		String uri = EnvConstants.USER_AUTH_URL + "/api/v1/users";
		JSONArray jsonArray = null;
		RequestSpecification request = RestAssured.given();
		request.header("Content-type", "application/json");
		request.header("Accept", "application/json");
		request.header("Authorization", "Bearer " + userProp.getProperty("token"));
		Response raw = request.relaxedHTTPSValidation().when().log().all().get(uri)
				.then().assertThat().contentType(ContentType.JSON).log().all().extract().response();;
		ApiResponse response = new ApiResponse(raw);
		if (response.getStatusCode() == 200)
			jsonArray = response.getResponseAsJSONArray();
		SoftAssert soft = new SoftAssert();
		int recordsCount = jsonArray.length();
		soft.assertEquals(recordsCount, 3);

	}

	@Test(priority = 3)
	public void getUserRecordWithPhoneNumber() {
		String uri = EnvConstants.USER_AUTH_URL + "/api/v1/users/" + custProp.getProperty("Phone_No_Aliko");
		JSONObject json = null;
		RequestSpecification request = RestAssured.given();
		request.header("Content-type", "application/json");
		request.header("Accept", "application/json");
		request.header("Authorization", "Bearer " + userProp.getProperty("token"));
		Response raw = request.relaxedHTTPSValidation().when().log().all().get(uri)
				.then().assertThat().contentType(ContentType.JSON).log().all().extract().response();;
		ApiResponse response = new ApiResponse(raw);
		if (response.getStatusCode() == 200)
			json = response.getResponseAsJson();
		String firstName = json.has("first_name") ? json.getString("first_name") : "";
		Assert.assertEquals(firstName, "Aliko");
		Assert.assertEquals(json.getString("phone"), custProp.getProperty("Phone_No_Aliko"));

	}

}
