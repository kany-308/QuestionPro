package com.kany.questionpro.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kany.questionpro.model.Comment;
import com.kany.questionpro.model.Story;
import com.kany.questionpro.repository.StoryRepository;

@Service
public class ClientService {

	@Value("${cache.time.minute: 15}")
	private int cacheTimeMinute;

	private static final Logger LOG = LoggerFactory.getLogger(APIService.class);

	private List<Story> storyCache = new ArrayList<>();
	private Date cacheUpdateTime = null;

	@Autowired
	APIService apiService;

	@Autowired
	StoryRepository storyRepository;

	public List<Story> getBestStories() {
		try {
			if (cacheUpdateTime == null) {
				setStoryAndUpdateCache();
			} else {
				int elapsedMinute = LocalTime.now().getMinute()
						- cacheUpdateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().getMinute();
				if (elapsedMinute < cacheTimeMinute) {
					return storyCache;
				} else {
					setStoryAndUpdateCache();
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return storyCache;
	}

	private void setStoryAndUpdateCache() {
		cacheUpdateTime = new Date();
		List<Story> stories = apiService.getBestStories();
		storyCache = new ArrayList<>(stories);
	}

	public List<Story> getPastStories() {
		List<Story> stories = null;
		try {
			stories = storyRepository.getAll();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return stories;
	}

	public List<Comment> getCommentsByStoryId(int storyId) {
		List<Comment> comments = null;
		try {
			comments = apiService.getComments(storyId);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return comments;
	}
}
