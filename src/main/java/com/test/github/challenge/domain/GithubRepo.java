package com.test.github.challenge.domain;

import java.util.List;

/**
 * Model class to represent a repository, stargazer relationship for a github
 * repository
 *
 * @author Dustin Roan (dustin.a.roan@gmail.com)
 *
 */
public class GithubRepo {
	private final String name;
	private final List<GithubUser> stargazers;

	private GithubRepo(final String userId, final List<GithubUser> followers) {
		this.name = userId;
		this.stargazers = followers;
	}

	public String user() {
		return this.name;
	}

	public List<GithubUser> followers() {
		return this.stargazers;
	}

	public static class Builder {
		private String name;
		private List<GithubUser> stargazers;

		public Builder name(final String userId) {
			this.name = userId;
			return this;
		}

		public Builder stargazers(final List<GithubUser> followers) {
			this.stargazers = followers;
			return this;
		}

		public Builder reset() {
			this.stargazers = null;
			this.name = null;

			return this;
		}

		public GithubRepo build() {
			return new GithubRepo(this.name, this.stargazers);
		}
	}
}
