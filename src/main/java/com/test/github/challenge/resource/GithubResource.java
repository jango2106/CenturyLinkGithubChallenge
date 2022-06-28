package com.test.github.challenge.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.test.github.challenge.data.GithubController;
import com.test.github.challenge.domain.GithubUser;

/**
 * Resource class for retrieving information from the Github API
 *
 * @author Dustin Roan (Dustin.a.roan@gmail.com)
 */
@Path("/github")
@Produces(MediaType.APPLICATION_JSON)
public class GithubResource {
	private final GithubController controller = new GithubController();

	/**
	 * Retrieves a list with all of the followers associated to a given Github
	 * username. It will then retrieve all of the followers of those followers, and
	 * so on until it gets at most 3 levels deep.
	 *
	 * @param username - a passed in Github username
	 * @return a {@link Response} containing the usernames of the requested user and
	 *         all of the followers and sub-followers
	 */
	@GET
	@Path("/users/{username}/followers")
	public Response getUserFollowersAndSubfollowers(@PathParam("username") final String username, @QueryParam("depth") final Integer depth) {
		final GithubUser user = controller.getAllFollowersRecursive(username, depth == null ? 3 : depth);
		return Response.ok(new Gson().toJson(user)).build();
	}

	/**
	 * Retrieves a list with all of the repositories associated to a given Github
	 * username. It will then retrieve all of the stargazers of those repositories,
	 * and so on until it gets at most 3 levels deep.
	 *
	 * @param username - a passed in Github username
	 * @return a {@link Response} containing all of the nested repositories and
	 *         stargazers related to the given username
	 */
	@GET
	@Path("/users/{username}/repos/stargazers")
	public Response getReposAndStargazers(@PathParam("username") final String username, @QueryParam("depth") final Integer depth) {
		final GithubUser user = controller.getReposAndStargazersRecursive(username, depth == null ? 3 : depth);
		return Response.ok(new Gson().toJson(user)).build();
	}
}
