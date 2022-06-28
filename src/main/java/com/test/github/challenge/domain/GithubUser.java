package com.test.github.challenge.domain;

import org.json.JSONPropertyIgnore;

import java.util.List;

/**
 * Model class to represent a user, follower, and repo relationship for a github
 * user
 *
 * @author Dustin Roan (dustin.a.roan@gmail.com)
 *
 */
public class GithubUser {
	private final String userId;
	private final List<GithubUser> followers;
	private final List<GithubRepo> repos;

	private GithubUser(final String userId, final List<GithubUser> followers, final List<GithubRepo> repos) {
		this.userId = userId;
		this.followers = followers;
		this.repos = repos;
	}

	public String user() {
		return this.userId;
	}

	public List<GithubUser> followers() {
		return this.followers;
	}

	public List<GithubRepo> repos() {
		return this.repos;
	}

	public static class Builder {
		private String userId;
		private List<GithubUser> followers;
		private List<GithubRepo> repos;

		public Builder user(final String userId) {
			this.userId = userId;
			return this;
		}

		public Builder followers(final List<GithubUser> followers) {
			this.followers = followers;
			return this;
		}

		public Builder repos(final List<GithubRepo> repos) {
			this.repos = repos;
			return this;
		}

		public Builder reset() {
			this.followers = null;
			this.userId = null;
			this.repos = null;

			return this;
		}

		public GithubUser build() {
			return new GithubUser(this.userId, this.followers, this.repos);
		}
	}
}
