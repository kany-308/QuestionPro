package com.kany.questionpro.model;

public class Comment {
	private int id;
	private String text;
	private String userName;
	private int userProfileAge;
	private int childComment;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserProfileAge() {
		return userProfileAge;
	}

	public void setUserProfileAge(int userProfileAge) {
		this.userProfileAge = userProfileAge;
	}

	public int getChildComment() {
		return childComment;
	}

	public void setChildComment(int childComment) {
		this.childComment = childComment;
	}
}
