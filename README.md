# README

## Context

This is the readme file for Application (1.0.0) project
The project can be built or executed through the provided `srcipts/build.sh` script.

## Build

To build the project, just execute the following command line :

```bash
$> build.sh a
```

## Run

To execute the build project, just run it with :

```bash
$> build.sh r
```

or you can execute the command line :

```bash
$> java -jar target/Application-1.0.0.jar
```

## Debug

you may use the jdb command line :

```bash
jdb -sourcepath src/main/java/,src/main/resources \
    target/classes/com/snapgames/demo/Application
```

## Reference

> **NOTE** _To get more information this script usga ejust execite:_
>
> ```bash
> $> scripts/build.sh
> Build of program 'Application-1.0.0' ...
> -----------
> build2 command line usage :
> ---------------------------
> $> build2 [options]
> where:
> - a|A|all     : perform all following operations
> - c|C|compile : compile all sources project
> - d|D|doc     : generate javadoc for project
> - t|T|test    : execute JUnit tests
> - j|J|jar     : build JAR with all resources
> - w|W|wrap    : Build and wrap jar as a shell script
> - s|S|sign    : Build and wrap signed jar as a shell  script
> - r|R|run     : execute (and build if needed) the  created JAR
>
> (c)2022 MIT License Frederic Delorme (@McGivrer) fredericDOTdelormeATgmailDOTcom
> --
> -----------
> ... done.
> ```

Enjoy !

Frédéric.
