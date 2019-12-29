<!-- Copyright (C) 2019-Present Pivotal Software, Inc. All rights reserved.

This program and the accompanying materials are made available under the terms of the under the Apache License, Version
2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License. -->
 
# Hello, World! Code Example

This guide presents a Hello, World! example app.
Running the app demonstrates that caching improves read performance.

Working through this introductory example provides Spring Boot developers
with the development experience of running an app in two ways:

- The first way runs the app locally with a Pivotal GemFire cluster as
the cache. GemFire is the caching software that powers Pivotal Cloud Cache.
- The second way pushes the app to a PAS environment to run with a
Cloud Cache service instance as the cache.

As the app runs,
the browser-based user interface allows the user to look up the value
associated with the key "hello."
The app prints the key, its value,
and the amount of time it took to do the lookup.

A lookup always first checks whether the desired key is in the cache.
The first lookup encounters a cache miss,
because the "hello" key is not in the cache.
On a cache miss, the app is responsible for acquiring the key's value.
In a real-world example, the app might send a query to a database
to acquire the value—an operation that takes a relatively
lengthy amount of time.
In this Hello World! example,
the value is computed to be the current time,
and the app injects an artificial time delay to simulate a database query. 

Before returning the value resulting from the lookup,
a cache miss has the side effect of placing the acquired value
for the key into the cache.
Subsequent lookups of that key will result in cache hits,
and the value will be returned quickly.

## App Dependencies

All supported versions of Cloud Cache use the same Spring dependencies
for this Hello, World! app.
The app uses Spring Boot, version 2.1.7.RELEASE. 

The app uses the following dependencies:

- Spring Boot Web Starter: 

    ```
    org.springframework.boot:spring-boot-starter-web
    ```
- Spring GemFire Starter 1.1.x:

    ```
    org.springframework.geode:spring-gemfire-starter:1.1.0.RELEASE
    ```
    &#x1F534; For this dependency, you will need access to the Pivotal Commercial Maven Repository, which requires a one-       time registration step to obtain an account. The URL for both registration and subsequent logins after registration is 
    https://commercial-repo.pivotal.io/login/auth. Click on the Create Account link to register. You will receive a 
    confirmation email; follow the directions in this email to activate your account.
    
    In the gradle.properties file, configure the following variables with the username and password you used to login 
    to the commercial repo mentioned above:
    
    ```
    gemfireReleaseRepoUser=<USERNAME>
    gemfireReleaseRepoPassword=<PASSWORD>
    ```
    
## Step 1: Acquire the Hello, World App Source Code

Clone the Hello, World! app from the repository at
`https://github.com/pivotal/cloud-cache-examples`.

```
$ git clone git@github.com:pivotal/cloud-cache-examples.git
```

## Step 2: Import the App into Your IDE and Build

Import the Hello, World! app into your IDE as a gradle project.

With a current working directory of `cloud-cache-examples/hello-world`,
build the app:

```
$ ./gradlew build
```

## Step 3: Start a Cluster on Your Local Machine

The local development environment uses a GemFire cluster to stand in
for the Cloud Cache service instance. 

Follow the directions in <Environment Setup Link here> to acquire
GemFire prior to doing this step. 

A custom Gradle task invokes a gfsh script to instantiate
a minimum-sized local cluster and create a region called Hello.
A region is a logical grouping of cached key-value pairs,
analogous to a relational database table. 

To instantiate a GemFire cluster,
with a current working directory of `cloud-cache-examples/hello-world`,
invoke the build from the command line:

```
$ ./gradlew startCluster
```

This task may take several minutes to complete.

## Step 4: Run the App Locally

The app exposes a single endpoint: /hello,
for easy viewing in a browser.
Hitting the endpoint generates a cache lookup for the "hello" key.
The first time that the "hello" key is looked up,
it will take a longer amount of time because it is not yet cached.
Subsequent lookups will be faster, as the value has been cached.

To see the timing of cache lookups,
run the application with the local GemFire cluster.
With a current working directory of `cloud-cache-examples/hello-world`,
invoke the Gradle bootRun task from the command line:

```
$ ./gradlew bootRun
```

Hit the /hello endpoint in a browser.
The URL will be the noted URL appended with /hello;
it will likely be http://localhost:8080/hello.
You should see that the key-value pair with key "hello"
and its value of the current time is displayed along with
the quantity of time that it took to acquire the key-value pair.

Hit the /hello endpoint a second time to observe the performance improvement on doing a lookup of a cached key-value pair.

When finished with running the app locally,
stop and tear down the GemFire cluster.
With a current working directory of `cloud-cache-examples/hello-world`,
invoke the custom Gradle task `stopAndCleanUpCluster` from the command line:

```
$ ./gradlew stopAndCleanUpCluster
```

## Step 5: Create a Cloud Cache Service Instance on PAS

To run the app with a Cloud Cache service instance,
create a service instance in one of two ways.
Either use the Cloud Foundry command-line interface (cf CLI),
or create the service instance from Apps Manager.

### Create the service instance using the cf CLI: 

- Run `cf login`, and create or target your organization's space.
- To create a Cloud Cache service instance within the space,
run a command of the form:

    ```
    cf create-service p-cloudcache <PLAN-NAME> <SERVICE-INSTANCE-NAME>
    ```

    Replace `<PLAN-NAME>` with one of the pre-configured plans
    for your Cloud Cache tile.
    The command `cf marketplace` lists available plans.

    Replace `<SERVICE-INSTANCE-NAME>` with your own custom name
    for your service instance.
    The service instance name can include alpha-numeric characters, hyphens,
    and underscores.  This is the name you will use in your manifest.yml. 

It can take several minutes for the service instance creation to complete.
The command 

```
$ cf services 
```

outputs the current status of the service instance creation.

### Create the service instance using Apps Manager:

- Within your org, create a space or navigate to the space
that will hold your Cloud Cache service instance.
- Click on the Services tab.
- Click on the ADD A SERVICE button.
- Click on Pivotal Cloud Cache.
- Choose one of the plans listed from the available radio buttons. Click SELECT PLAN.
- Fill in the instance name with a custom name for your service instance.  The service instance name can include alpha-numeric characters, hyphens, and underscores. This is the name you will use in your manifest.yml.
Leave other fields as they are.  
- Click CREATE.

It can take several minutes for the service instance creation to complete.
You will see the status create succeeded when the service is ready to use. 

## Step 6: Run The App On PAS Using Cloud Cache

This step repeats running the app,
but with the Cloud Cache service instance that is now running. 

Edit the `manifest.yml` file and replace the string
`<your-pivotal-cloud-cache-service>` with the name of the
Cloud Cache service instance you created in Step 5.

Run `cf login`, and target your organization's space.

With a current working directory of `cloud-cache-examples/hello-world`,
push the app to the PAS environment: 

```
$ cf push
```

The `cf push` operation will also bind the app to the Cloud Cache
service instance and run the app.

Output from the `cf push` command will give the route to the app.
Append /hello to this route to form the URL for the app's endpoint.
Enter the URL in a browser.
As when you ran the app locally,
you should see that the key-value pair with key "hello" and its value
of the current time is displayed along with the quantity of time
that it took to acquire the key-value pair.

Hit the endpoint a second time to observe the performance improvement
on doing a lookup of a cached key-value pair.

There are two ways to dispose of the app and the Cloud Cache service instance.
Either use the cf CLI, or use Apps Manager.

### Delete the app and the service instance using the cf CLI: 

- Delete the app first, such that the Cloud Cache service instance does not
have an app bound to it:

    ```
    $ cf delete helloworld -r -f
    ```

- Run a command of the form

    ```
    cf delete-service <SERVICE-INSTANCE-NAME>
    ```

    Replace `<SERVICE-INSTANCE-NAME>` with the name of your Cloud Cache
    service instance.
    Confirm that you want the service removed by answering "yes" when prompted.

### Delete the app and the service instance using Apps Manager:

- Navigate to the org and space that holds your Cloud Cache service instance.
Click on the App tab.
- Click on the name of the app.
- Click on the Settings tab.
- Click on DELETE APP (at the bottom of the page), and confirm that you want to delete the app.
- Click on the Service tab.
- Click on the name of your service instance.
- Click on the Settings tab.
- Click on the DELETE SERVICE INSTANCE button, and confirm the deletion.
