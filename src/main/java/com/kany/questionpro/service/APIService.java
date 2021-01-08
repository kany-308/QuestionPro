package com.kany.questionpro.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.kany.questionpro.model.Comment;
import com.kany.questionpro.model.Story;
import com.kany.questionpro.repository.StoryRepository;
import com.kany.questionpro.util.Constant;

@Service
public class APIService {

	@Value("${size.limit: 10}")
	private int sizeLimit;

	private Map<Integer, Story> storyCache = new ConcurrentHashMap<>();
	private Map<Integer, Comment> commentCache = new ConcurrentHashMap<>();
	private Set<Integer> inValidComments = new HashSet<>();
	private Map<String, Date> userCache = new ConcurrentHashMap<>();

	@Autowired
	HNAPIService hnApiService;

	@Autowired
	StoryRepository storyRepository;

	public List<Story> getBestStories() {
		List<Story> stories = new ArrayList<>();
		List<Integer> storyIds = hnApiService.getBestStoryIds();
		int size = 0, idx = 0;
		while (idx < storyIds.size() && size < sizeLimit) {
			int storyId = storyIds.get(idx);
			// Check story in cache first
			if (storyCache.containsKey(storyId)) {
				stories.add(storyCache.get(storyId));
				size++;
			} else {
				// Since story is not available in cache get story from API
				JsonObject storyJson = hnApiService.getStoryById(storyId);
				Story story = getStory(storyJson);
				if (story != null) {
					// Add API data in cache
					storyCache.put(storyId, story);
					// Add API data in in-memory DB, will be used to get
					// past-stories
					storyRepository.insertStory(story);
					stories.add(story);
					size++;
				}
			}
			idx++;
		}
		return stories;
	}

	private Story getStory(JsonObject json) {
		if (json == null)
			return null;
		Story story = new Story();
		story.setId(json.get(Constant.ID).getAsInt());
		story.setScore(json.get(Constant.SCORE).getAsInt());
		story.setTitle(json.get(Constant.TITLE).getAsString());
		story.setUrl(json.get(Constant.URL).getAsString());
		story.setUserName(json.get(Constant.BY).getAsString());
		story.setSubmissionTime(new Date(json.get(Constant.TIME).getAsLong()));
		return story;
	}

	public List<Comment> getComments(int storyId) {
		List<Integer> commentIds = hnApiService.getCommentIds(storyId);
		// This could be improved by using Heap
		Set<Comment> comments = new TreeSet<>((cm1, cm2) -> cm2.getChildComment() - cm1.getChildComment());

		for (Integer commentId : commentIds) {
			if (inValidComments.contains(commentId)) {
				continue;
			}
			// Check comment in cache first
			if (commentCache.containsKey(commentId)) {
				comments.add(commentCache.get(commentId));
			} else {
				// Since comment is not available in cache get comment from API
				JsonObject commentJson = hnApiService.getCommentById(commentId);
				Comment comment = getComment(commentJson);
				if (comment != null) {
					// Add API data in cache
					commentCache.put(commentId, comment);
					comments.add(comment);
				} else {
					inValidComments.add(commentId);
				}
			}
		}
		return comments.stream().collect(Collectors.toList()).subList(0, Math.min(sizeLimit, comments.size()));
	}

	private Comment getComment(JsonObject json) {
		if (json == null || (json.get(Constant.DELETED) != null && json.get(Constant.DELETED).getAsBoolean()))
			return null;
		Comment comment = new Comment();
		comment.setId(json.get(Constant.ID).getAsInt());
		comment.setText(json.get(Constant.TEXT).getAsString());
		comment.setUserName(json.get(Constant.BY).getAsString());
		int kidsSize = 0;
		if (json.get(Constant.KIDS) != null) {
			kidsSize = json.get(Constant.KIDS).getAsJsonArray().size();
		}
		comment.setChildComment(kidsSize);
		String userName = json.get(Constant.BY).getAsString();
		Date userProfileDate = getUserProfileDate(userName);
		int userProfileAge = Math.abs(Period
				.between(userProfileDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now())
				.getYears());
		comment.setUserProfileAge(userProfileAge);
		return comment;
	}

	private Date getUserProfileDate(String userName) {
		if (userCache.containsKey(userName)) {
			return userCache.get(userName);
		}
		Date profileDate = hnApiService.getUserProfileDateByUserName(userName);
		userCache.put(userName, profileDate);
		return profileDate;
	}
}