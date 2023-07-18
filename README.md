# README

## Context

This is the readme file for JavaTestApp (1.0.0) project.

The java project can be built or executed through the provided `scripts/build.sh` script, see reference later in this
README for more info on this build script.

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
$> java -jar target/JavaTestApp-1.0.0.jar
```

![A screenshot from the current version](doc/images/screenshot-003-renderer-plugin-archi.png "A screenshot from the current version")

## Debug

you may use the jdb command line :

```bash
jdb -sourcepath src/main/java/,src/main/resources \
    target/classes/com/snapgames/demo/Application
```

## Reference

### Build script

> **NOTE** _To get more information this script usga ejust execite:_
>
> ```bash
> $> scripts/build.sh
> Build of program 'JavaTestApp-1.0.0' ...
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

### Keys from the demo

| Key                             | description                                              |
|:--------------------------------|:---------------------------------------------------------|
| `UP`,`DOWN`,<br/>`LEFT`,`RIGHT` | Move player vertically <br/>or horizontally              |
| `ALT+UP`, `ALT+DOWN`            | Player rotation left or right                            |                                                     |
| `CTRL+D`,<br/> `D`              | Activate debug display, <br/>change debug level (0 to 5) |
| `PAGE-UP`, `PAGE-DOWN`          | Camera roll                                              |
| `ALT+BACKSPACE`                 | Reset camera roll to initial position                    |
| `CTRL+Z`                        | Reset Scene                                              |
| `ESCAPE`                        | Exit demonstration                                       |
| `P`,`PAUSE`                     | set/unset Game update in pause mode                      |
| `G`                             | Reverse World gravity                                    |
| `F3`                            | take screen shot (not implemented yet)                   |

Enjoy !

Frédéric.
