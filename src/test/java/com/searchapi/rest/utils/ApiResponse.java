package com.searchapi.rest.utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.restassured.response.Response;



public class ApiResponse {

	

	private Response response;

	public ApiResponse(Response response) {
		this.response = response;
	}

	public int getStatusCode() {
		return response.getStatusCode();
	}

	public Response getResponse() {
		return response;
	}

	public String getResponseAsString() {
		return response.asString();
	}
	
	public JSONObject getResponseAsJson() throws JSONException {
		JSONObject jsonObject = new JSONObject(response.asString());
		return jsonObject;
	}
	public JSONArray getResponseAsJSONArray() throws JSONException {
		JSONArray JSONArray = new JSONArray(response.asString());
		return JSONArray;
	}

}