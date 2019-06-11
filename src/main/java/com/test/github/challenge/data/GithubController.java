package com.test.github.challenge.data;

import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.json.JSONArray;
import org.json.JSONObject;

import com.test.github.challenge.domain.GithubRepo;
import com.test.github.challenge.domain.GithubUser;

/**
 * Controller used to access data from the Github API.
 *
 * @author Dustin Roan (dustin.a.roan@gmail.com)
 *
 */
public class GithubController {
	private static final int MAX_FOLLOWERS_PER_USER = 5;
	private static final int MAX_REPO_AND_STARGAZERS = 3;
	private static final String GITHUB_BASE_URL = "https://api.github.com/";
	private static final String GITHUB_FOLLOWER_URL = "users/%s/followers";
	private static final String PER_PAGE_SIZE_PARAM = "per_page";
	private static final String GITHUB_REPO_URL = "users/%s/repos";
	private static final String GITHUB_STARGAZER_URL = "repos/%s/%s/stargazers";
	private final JerseyWebTarget client = JerseyClientBuilder.createClient().target(URI.create(GITHUB_BASE_URL));

	/**
	 * Gets a {@link GithubUser} for the given username with all of the followers
	 * associated to it. It will then retrieve all of the followers of those
	 * followers, and so on until it gets 3 levels deep.
	 *
	 * @param userId - a Github username
	 * @return a {@link GithubUser} associated to the requested user
	 */
	public GithubUser getAllFollowers(final String userId) {
		final GithubUser.Builder builder = new GithubUser.Builder();

		final ArrayList<GithubUser> firstTierFollowerList = new ArrayList<>();
		// retrieves the first tier of followers for the user
		for (final String firstTierFollower : getFollowers(userId)) {
			final ArrayList<GithubUser> secondTierFollowerList = new ArrayList<>();
			// retrieves the second tier of followers from the first tier followers
			for (final String secondTierFollower : getFollowers(firstTierFollower)) {
				final ArrayList<GithubUser> thirdTierFollowerList = new ArrayList<>();
				// retrieves the third tier of followers from the second tier of followers
				for (final String thirdTierFollower : getFollowers(secondTierFollower)) {
					thirdTierFollowerList.add(builder.reset().user(thirdTierFollower).build());
				}
				secondTierFollowerList
						.add(builder.reset().user(secondTierFollower).followers(thirdTierFollowerList).build());
			}
			firstTierFollowerList
					.add(builder.reset().user(firstTierFollower).followers(secondTierFollowerList).build());
		}
		return builder.reset().user(userId).followers(firstTierFollowerList).build();
	}

	/**
	 * Gets a {@link GithubUser} for the given username with the associated
	 * repositories and stargazers of those repositories. Then it will retrieve the
	 * repositories and stargazers of those stargazers, and so on until three levels
	 * deep.
	 *
	 * @param userId - a github username
	 * @return a {@link GithubUser} associated to the requested user
	 */
	public GithubUser getReposAndStargazers(final String userId) {
		final GithubUser.Builder userBuilder = new GithubUser.Builder();
		final GithubRepo.Builder repoBuilder = new GithubRepo.Builder();

		final ArrayList<GithubRepo> firstTierRepoList = new ArrayList<>();
		// retrieves first tier of repositories
		for (final String firstTierRepo : getRepos(userId)) {
			final ArrayList<GithubUser> firstTierStargazerList = new ArrayList<>();
			// retrieves first tier of stargazers related to first tier of repositories
			for (final String firstTierStargazer : getStargazers(userId, firstTierRepo)) {
				final ArrayList<GithubRepo> secondTierRepoList = new ArrayList<>();
				// retrieves second tier of repositories related to first tier of stargazers
				for (final String secondTierRepo : getRepos(firstTierStargazer)) {
					// retrieves second tier of stargazers related to first second of repositories
					final ArrayList<GithubUser> secondTierStargazerList = new ArrayList<>();
					for (final String secondTierStargazer : getStargazers(firstTierStargazer, secondTierRepo)) {
						final ArrayList<GithubRepo> thirdTierRepoList = new ArrayList<>();
						// retrieves third tier of repositories related to second tier of stargazers
						for (final String thirdTierRepo : getRepos(secondTierStargazer)) {
							final ArrayList<GithubUser> thirdTierStargazerList = new ArrayList<>();
							// retrieves third tier of stargazers related to third tier of repositories
							for (final String thirdTierStargazer : getStargazers(secondTierStargazer, thirdTierRepo)) {
								thirdTierStargazerList.add(userBuilder.reset().user(thirdTierStargazer).build());
							}
							thirdTierRepoList.add(
									repoBuilder.reset().name(thirdTierRepo).stargazers(thirdTierStargazerList).build());
						}
						secondTierStargazerList
								.add(userBuilder.reset().user(secondTierStargazer).repos(thirdTierRepoList).build());
					}
					secondTierRepoList
							.add(repoBuilder.reset().name(secondTierRepo).stargazers(secondTierStargazerList).build());
				}
				firstTierStargazerList
						.add(userBuilder.reset().user(firstTierStargazer).repos(secondTierRepoList).build());
			}
			firstTierRepoList.add(repoBuilder.reset().name(firstTierRepo).stargazers(firstTierStargazerList).build());
		}
		return userBuilder.reset().user(userId).repos(firstTierRepoList).build();
	}

	/**
	 * Retrieves a number of followers for the given userId
	 *
	 * @param userId - a Github username
	 * @return a list of followers for the given user. If the REST call isn't
	 *         successful, list will be returned as blank.
	 */
	private ArrayList<String> getFollowers(final String userId) {
		final ArrayList<String> followers = new ArrayList<>();
		final Response userFollowerResponse = client.path(String.format(GITHUB_FOLLOWER_URL, userId))
				.queryParam(PER_PAGE_SIZE_PARAM, MAX_FOLLOWERS_PER_USER).request(MediaType.APPLICATION_JSON).get();
		if (userFollowerResponse.getStatus() == Status.OK.getStatusCode()) {
			final JSONArray jsonFollowers = new JSONArray(userFollowerResponse.readEntity(String.class));
			jsonFollowers.forEach((follower) -> {
				if (followers.size() < MAX_FOLLOWERS_PER_USER) {
					followers.add((String) ((JSONObject) follower).get("login"));
				}
			});
		}
		return followers;
	}

	/**
	 * Retrieves a number of repositories for the given userId
	 *
	 * @param userId - a Github username
	 * @return a list of repositories for the given user. If the REST call isn't
	 *         successful, list will be returned as blank.
	 */
	private ArrayList<String> getRepos(final String userId) {
		final ArrayList<String> repos = new ArrayList<>();
		final Response userRepoResponse = client.path(String.format(GITHUB_REPO_URL, userId))
				.queryParam(PER_PAGE_SIZE_PARAM, MAX_REPO_AND_STARGAZERS).request(MediaType.APPLICATION_JSON).get();
		if (userRepoResponse.getStatus() == Status.OK.getStatusCode()) {
			final JSONArray jsonRepos = new JSONArray(userRepoResponse.readEntity(String.class));
			jsonRepos.forEach((repo) -> {
				if (repos.size() < MAX_REPO_AND_STARGAZERS) {
					repos.add((String) ((JSONObject) repo).get("name"));
				}
			});
		}
		return repos;
	}

	/**
	 * Retrieves a number of stargazers for the given userId/repo combination
	 *
	 * @param userId - a Github username
	 * @param repo   - a Github repo name associated to the username
	 * @return a list of stargazers for the given user. If the REST call isn't
	 *         successful, list will be returned as blank.
	 */
	private ArrayList<String> getStargazers(final String userId, final String repo) {
		final ArrayList<String> stargazers = new ArrayList<>();
		final Response repoStargazerResponse = client.path(String.format(GITHUB_STARGAZER_URL, userId, repo))
				.queryParam(PER_PAGE_SIZE_PARAM, MAX_REPO_AND_STARGAZERS).request(MediaType.APPLICATION_JSON).get();
		if (repoStargazerResponse.getStatus() == Status.OK.getStatusCode()) {
			final JSONArray jsonStargazers = new JSONArray(repoStargazerResponse.readEntity(String.class));
			jsonStargazers.forEach((stargazer) -> {
				if (stargazers.size() < MAX_REPO_AND_STARGAZERS) {
					stargazers.add((String) ((JSONObject) stargazer).get("login"));
				}
			});
		}
		return stargazers;
	}

}
