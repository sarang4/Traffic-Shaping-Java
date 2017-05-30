# Traffic-Shaping-Java

This is traffic shaping program provides rate limiting feature using rate_limit.json file present in /src/main/resources/static folder.

This is project uses Jedis to connect to Redis, Spring Framework and Maven builder.

Currently when rate limit hits, platform returns 429 response code with message as "Rate limit exceeded, wait for sometime."

# Setup
1. Git Clone this repository
2. Download redis server from https://redis.io and make sure it is running on localhost and on port 6379. If you do any customization in redis while running please do respective changes in RedisManager class for connection.
3. Import project in IDE, I was using IntelliJ IDEA CE.
4. Build the project and Run RatelimitApplication.java present in src/main/java/com/ratelimit/ratelimit/ .
5. This will start the application.
6. Currently there are 3 apis present in the project, /pay, /status, /intercept, which can be used to test the ratelimit thing.
7. Current configuration of rate limit is present in rate_limit.json at /src/main/resources/static. If you change anything in this, please restart the application so that it will get loaded again in redis. After restart I have added code to delete older keys from redis, we can comment this if not required.

