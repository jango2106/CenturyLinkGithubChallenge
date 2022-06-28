# CenturyLinkGithubChallenge
A repo for CenturyLinks coding challenge 06/2019 to get information from the Github API

# Requirements:
Java JRE of 1.8+

Maven (latest)

## Dependencies:
Jersey - This runs on an embedded Jersey Grizzly server and uses a Jersey Client to make the GET request to 

Google Gson - Used for object to json serialization

# API Endpoints:
## *Followers and Sub-followers by username*
   * Type: GET
   * url: /github/users/{username}/followers
     * PathParam: username - any github username 
     * QueryParam: depth - how many steps down you want to go. Default 3
   * Produces: Json
#### Example:
Response of depth 1
```json
{
  "userId": "string",
  "followers": [
    {
      "userId": "string",
      "followers": []
    }
  ]
}
```

## *Nested repositories and stargazers by username*
   * Type: GET
   * url: /github/repos/{username}/repos/stargazers
     * PathParam: username - any github username 
     * QueryParam: depth - how many steps down you want to go. Default 3
   * Produces: Json
#### Example:
Response of depth 1
```json
{
  "userId": "string",
  "repos": [
    {
      "name": "string",
      "stargazers": [
        {
          "userId": "string",
          "repos": []
        }
      ]
    }
  ]
}
```
## How to run:
1. Checkout the repository
1. From the main project level run `mvn clean package` 
1. From the main project level run a `java -jar target github.challenge-0.0.1-SNAPSHOT-jar-with-dependencies.jar` which will bring up the service

## How to test:
After following the "How to Run" steps the service should start up as an embedded grizzly web service with the base url of "localhost:8080/service". You should then be able to do a simple GET request through the browser or an application like Postman/Insomnia against one of the API endpoints. 

For the followers endpoint I used the username "atmos" to test against to make sure there is enough nested data to get the maximum length results. I also used the username "ahoopes16" to test getting a partial depth return.

For the repos and stargazers endpoint I used the username "jango2106" to test against which has both fully nested and partially nested data.

## Notes:
This services uses an unauthenticated request to the GitHub API which means it is limited to 60 requests per hour per IP address. This may result in some false results seen below. If this happens, just wait an hour and the requests should come back correctly.
```json
{
    "userId": "string",
    "followers": []
}
```
```json
{
    "userId": "string",
    "repos": []
}
```