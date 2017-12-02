# kagu-todos

A simple todo application built with the Kagu web framework.

### Components

This project needs 3 main components running to function: a MongoDB instance, an instance of the backend, and a webserver that serves the output of the frontend build.

### Running the project

For now, the project is set up to work in local mode. It expects a MongoDB instance to run on `mongodb://localhost:27017`, the backend to run on `http://localhost:8080`, and the frontend to be served by IntelliJ IDEA's built-in webserver on `http://localhost:63342/kagu-todos/web/`.

If MongoDB is added to the `$PATH`, the JDK is added to the path, Google Chrome is installed and IntelliJ is running with the project open, invoking the `build_and_run.bat` script found in the project root will launch all of these components and open the application in Chrome.
