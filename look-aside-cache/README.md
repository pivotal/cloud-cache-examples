# Look-aside Cache Pattern Example

This repo contains provides an example application demonstrating the use of
Pivotal Cloud Cache as a [look-aside cache](https://content.pivotal.io/blog/an-introduction-to-look-aside-vs-inline-caching-patterns).

The application uses [Spring Boot for Pivotal Gemfire](https://docs.spring.io/autorepo/docs/spring-boot-data-geode-build/current/reference/html5/) to cache data from the Bikewise.org public REST API. Look-aside caching is enabled with just a few annotations. When serving cached data, the application response time is dramatically improved.
