package com.kany.questionpro.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kany.questionpro.util.Constant;

@Service
public class HNAPIService {

	@Value("${best.stories.url}")
	private String bestStoriesUrl;

	@Value("${item.url}")
	private String itemUrl;

	@Value("${user.url}")
	private String userUrl;

	@Value("${replace.string}")
	private String replaceString;

	private final CloseableHttpClient httpClient = HttpClients.createDefault();
	private static final Logger LOG = LoggerFactory.getLogger(HNAPIService.class);
	private final Gson gson = new Gson();

	// Get Story by Id
	public JsonObject getStoryById(int id) {
		JsonObject storyJson = null;
		String url = itemUrl.replace(replaceString, String.valueOf(id));
		try {
			String jsonResponse = "";
			HttpGet getRequest = new HttpGet(url);
			// We can check for status and then return response accordingly
			try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
				jsonResponse = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
			}
			if (!StringUtils.isEmpty(jsonResponse)) {
				JsonObject jsonObj = gson.fromJson(jsonResponse, JsonObject.class);
				// Check type of item
				if (!jsonObj.get(Constant.TYPE).getAsString().equals(Constant.STORY)) {
					return null;
				}
				storyJson = jsonObj;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return storyJson;
	}

	// This will top best story ids
	public List<Integer> getBestStoryIds() {
		List<Integer> storyIds = new ArrayList<>();
		try {
			String jsonResponse = "";
			HttpGet getRequest = new HttpGet(bestStoriesUrl);
			// We can check for status and then return response accordingly
			try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
				jsonResponse = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
			}
			if (!StringUtils.isEmpty(jsonResponse)) {
				JsonArray jsonArr = gson.fromJson(jsonResponse, JsonArray.class);
				for (int i = 0; i < jsonArr.size(); i++) {
					storyIds.add(jsonArr.get(i).getAsInt());
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return storyIds;
	}

	// Get all kids of given story
	public List<Integer> getCommentIds(int id) {
		List<Integer> commentIds = new ArrayList<>();
		String url = itemUrl.replace(replaceString, String.valueOf(id));
		try {
			String jsonResponse = "";
			HttpGet getRequest = new HttpGet(url);
			// We can check for status and then return response accordingly
			try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
				jsonResponse = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
			}
			if (!StringUtils.isEmpty(jsonResponse)) {
				JsonObject jsonObj = gson.fromJson(jsonResponse, JsonObject.class);
				// Check type of item
				JsonArray jsonArr = jsonObj.get(Constant.KIDS).getAsJsonArray();
				for (int i = 0; i < jsonArr.size(); i++) {
					commentIds.add(jsonArr.get(i).getAsInt());
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return commentIds;
	}

	// Get Comment by Id
	public JsonObject getCommentById(int id) {
		JsonObject commentJson = null;
		String url = itemUrl.replace(replaceString, String.valueOf(id));
		try {
			String jsonResponse = "";
			HttpGet getRequest = new HttpGet(url);
			// We can check for status and then return response accordingly
			try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
				jsonResponse = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
			}
			if (!StringUtils.isEmpty(jsonResponse)) {
				JsonObject jsonObj = gson.fromJson(jsonResponse, JsonObject.class);
				// Check type of item
				if (!jsonObj.get(Constant.TYPE).getAsString().equals(Constant.COMMENT)) {
					return null;
				}
				commentJson = jsonObj;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return commentJson;
	}

	// Get creation date of user profile
	public Date getUserProfileDateByUserName(String userName) {
		Date profileDate = null;
		String url = userUrl.replace(replaceString, userName);
		try {
			String jsonResponse = "";
			HttpGet getRequest = new HttpGet(url);
			// We can check for status and then return response accordingly
			try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
				jsonResponse = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
			}
			if (!StringUtils.isEmpty(jsonResponse)) {
				JsonObject jsonObj = gson.fromJson(jsonResponse, JsonObject.class);
				profileDate = new Date(jsonObj.get(Constant.CREATED).getAsLong());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return profileDate;
	}
}
