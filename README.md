# Hoots

"Hoots" is a small social media network. The name "Hoots" is based on the sound an owl makes.

This project was created as a team task for the courses "SWE3" and "Vernetzte Systeme" of "Hochschule Bremerhaven" in early 2022.

## Quick Guide

### Dependencies

This project should be run in the intended location: a docker container in the HS Bremerhaven infrastructure.

If for whatever reason you want to modify this project and run it yourself as is, make sure that your docker container (or server) has the following software installed:

 - any common linux OS with bash
 - JDK 11+
 - Tomcat 9+
 - MariaDB 10.2.1+ (CHECK Constraints need to be enforced)
 - gnuplot
 - Redis
 - if you want to run load tests: Grafana k6

### Development Process

Each team member developed in his/her own docker container hosted in the HS Bremerhaven infrastructure.
This prevented issues between different development tasks.

The development process was defined as follows:

- create an empty project directory in your docker container
- clone the repository on your local machine
- make a copy of `.env.sample` and rename it to `.env`. Enter your individual information and the remote project dir
- only once you need to set up the remote database by running `bin/setupDbInDocker`
- run `bin/deploy` (this will sync your local files with the remote project dir and redeploy the tomcat container)

## Documentation

### Scripts (local)

These scripts are for the local development.

Location: `bin/`

#### `deploy`

- load environment settings from .env
- rsync files to the specified target
- run the `buildAndUpdate` Task

#### `deployAndRestartTomcat`

- like `bin/deploy`
- also restart Tomcat

#### `deployFrontend`

- only deploy frontend changes to an already running server

#### `setupDbInDocker`

- first time setup of the remote database
- this script can be re-run to reset the database and rebuild it from scratch. This will delete all data.

#### `uploadFiles`

- only upload changed files to the remote project directory

### Scripts (Docker)

These scripts are used for the deployment process on the remote server.

Location: `bin/remote/`

#### `buildAndUpdate`

- copies `app/*` to `build`
- compile java files in `src` to `build/WEB-INF/classes`
- deploys via curl over the "manager-app" to the tomcat (credentials in .netrc)

#### `clean`

- delete `build`
- delete `war`

#### `undeploy`

- undeploy application

#### `list`

- list active webapps

### Project structure

`src` contains all java files

`app` contains all required files for Tomcat (WEB-INF, web.xml, etc.), frontend files (js, css, html) and all required java libraries.

`complibs` contains all libraries that are only required at compilation time.

`docs` documentation and monitoring data.

`tests` various load tests written for K6.
