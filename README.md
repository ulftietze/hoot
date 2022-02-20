# Hoots

"Hoots" is a small social media network. This project is created within
"SWE3" and "Vernetzte Systeme" in the third semester. The name "Hoots"
is based on the owl and the corresponding "hooting".

## Quick Guide

### Development Process

Each team member will develop on its own docker machine. This prevents issues between different development tasks. The
discussed development process is declared as follows:

- Clone repository on your machine
- create and define path in your local docker
- copy `.env.sample` to `.env' and set user and remote path
- Make local changes
- run `bin/deploy` (this will sync files with remote and update tomcat)

## Documentation

### Scripts (local)

These scripts are for the local development.

Location: `/bin/`

#### `deploy`

- load environment settings from .env
- rsync files to specified target
- run the `buildAndUpdate` Task

### Scripts (Docker)

These scripts are for the handling on the remote server.

Location: `/bin/remote/`

#### `buildAndUpdate`

- copies `app/*` to `build`
- compile java-files in `src` to `build/WEB-INF/classes`
- deploys via curl over the "manager-app" to the tomcat (credentials in .netrc)

#### `clean`

- delete `build`
- delete `war`

#### `undeploy`

- undeploy application

#### `list`

- list active webapps

In src liegt das package hoot mit Java-Dateien.

In app befindet sich die Deployment-Struktur mit html-Dateien WEB-INF, web.xml und den notwendigen Bibliotheken für die
Laufzeit.

In complibs befinden sich die jars, die zum Compilieren notwendig sind:
servlets. 

