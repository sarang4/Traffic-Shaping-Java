# Traffic-Shaping-Java

This is traffic shaping program provides rate limiting feature using rate_limit.json file present in /src/main/resources/static folder.

This is project uses Jedis to connect to Redis, Spring Framework and Maven builder.

Currently when rate limit hits, platform returns 429 response code with message as "Rate limit exceeded, wait for sometime."
