<!-- Copyright (C) 2019-Present Pivotal Software, Inc. All rights reserved.

This program and the accompanying materials are made available under the terms of the under the Apache License, Version
2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License. -->

# Look-aside Cache Pattern Example

This repo contains provides an example application demonstrating the use of
Pivotal Cloud Cache as a [look-aside cache](https://content.pivotal.io/blog/an-introduction-to-look-aside-vs-inline-caching-patterns).

The application uses [Spring Boot for Pivotal Gemfire](https://docs.spring.io/autorepo/docs/spring-boot-data-geode-build/current/reference/html5/) to cache data from the Bikewise.org public REST API. Look-aside caching is enabled with just a few annotations. When serving cached data, the application response time is dramatically improved.
