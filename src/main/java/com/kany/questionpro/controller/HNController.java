package com.kany.questionpro.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kany.questionpro.model.Comment;
import com.kany.questionpro.model.Story;
import com.kany.questionpro.service.APIService;
import com.kany.questionpro.service.ClientService;
import com.kany.questionpro.util.Constant;
import com.kany.questionpro.util.Response;

@RestController
@RequestMapping("/api")
public class HNController {

	private static final Logger LOG = LoggerFactory.getLogger(APIService.class);

	@Autowired
	ClientService clientService;

	@GetMapping("/getBestStories")
	public Response<List<Story>> getBestStories() {
		final Response<List<Story>> res = new Response<>();
		res.setStatus(Constant.STATUS_SUCCESS);
		try {
			List<Story> data = clientService.getBestStories();
			res.setData(data);
		} catch (Exception e) {
			res.setStatus(Constant.STATUS_FAIL);
			res.setReason(e.getMessage());
			LOG.error(e.getMessage(), e);
		}
		return res;
	}

	@GetMapping("/getPastStories")
	public Response<List<Story>> getPastStories() {
		final Response<List<Story>> res = new Response<>();
		res.setStatus(Constant.STATUS_SUCCESS);
		try {
			List<Story> data = clientService.getPastStories();
			res.setData(data);
		} catch (Exception e) {
			res.setStatus(Constant.STATUS_FAIL);
			res.setReason(e.getMessage());
			LOG.error(e.getMessage(), e);
		}
		return res;
	}

	@GetMapping("/getCommentsByStoryId")
	public Response<List<Comment>> getCommentsByStoryId(@RequestParam("storyId") int storyId) {
		final Response<List<Comment>> res = new Response<>();
		res.setStatus(Constant.STATUS_SUCCESS);
		try {
			List<Comment> data = clientService.getCommentsByStoryId(storyId);
			res.setData(data);
		} catch (Exception e) {
			res.setStatus(Constant.STATUS_FAIL);
			res.setReason(e.getMessage());
			LOG.error(e.getMessage(), e);
		}
		return res;
	}

}
