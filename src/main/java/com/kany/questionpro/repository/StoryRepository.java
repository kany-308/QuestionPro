package com.kany.questionpro.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kany.questionpro.model.Story;

@Component
public class StoryRepository {
	// This map will be used store the past stories
	private Map<Integer, Story> storyMap = new LinkedHashMap<>();

	// We can limit the size to prevent OutOfMeoryException
	// If size is greater than given limit remove older data
	public Story insertStory(Story story) {
		return storyMap.put(story.getId(), story);
	}

	// Get all data from DB
	public List<Story> getAll() {
		return storyMap.values().stream().collect(Collectors.toList());
	}

	// Get Stoey by Id
	public Story getStory(int id) {
		return storyMap.get(id);
	}
}
