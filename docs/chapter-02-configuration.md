## Configuration

Everything about configuration.

The `Configuration` component intends to provide a set of configuration keys to set up default values at start
for the `Application`.

Basically, it will:

1. load a properties file and then extract default values for attributes at first,
2. If no file exists, some default by construction values is set,
3. and then parse the command line arguments to overload the default loaded values.

### the Configuration class

Let's dive into some java code:

```java
public class Configuration {

    public Configuration(Application app, String pathCfgFile, List<String> lArgs) {
        this.pathToConfigFile = pathCfgFile;
        parseArgs(lArgs);
        try {
            InputStream inConfigStream = Application.class.getClassLoader().getResourceAsStream(pathToConfigFile);
            if (inConfigStream == null) {
                inConfigStream = Application.class.getResourceAsStream(pathToConfigFile);
            }
            if (inConfigStream != null) {
                props.load(inConfigStream);
                parseConfig(props);
            } else {
                System.exit(-1);
            }
        } catch (IOException e) {
            System.err.printf(">> <?> unable to read configuration file: %s%n", e.getMessage());
        }

        parseArgs(lArgs);
    }

    private void parseConfig(Properties config) {
    }

    private void parseArgs(List<String> lArgs) {
    }
    // some helpers and getters ...
}
```

Main steps:

1. parse arguments the first time to gather another configuration file path if provided to overwrite the default one,
2. load the defined configuration file,
3. parse configuration keys/values and set attributes to the desired values,
4. parse arguments a second time to overwrite required attributes.

### Parsing configuration keys/values

The parsing mechanism will rely on some helpers to extract typed values from the property file and set the
corresponding attribute to the right typed value:

```java
public class Configuration {
    //...

    private static boolean getParsedBoolean(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));
        return Boolean.parseBoolean(config.getProperty(key, defaultValue));
    }

    private static int getParsedInt(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));
        return Integer.parseInt(config.getProperty(key, defaultValue));
    }

    private static double getParsedDouble(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));
        return Double.parseDouble(config.getProperty(key, defaultValue));
    }


    private Rectangle2D getParsedRectangle2D(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));

        String[] paArgs = config.getProperty(key, defaultValue).split("x");
        return new Rectangle2D.Double(
            0, 0,
            Integer.parseInt(paArgs[0]),
            Integer.parseInt(paArgs[1]));
    }

    private Dimension getParsedDimension(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));

        String[] winSizeArgs = config.getProperty(key, defaultValue).split("x");
        return new Dimension(
            Integer.parseInt(winSizeArgs[0]),
            Integer.parseInt(winSizeArgs[1]));
    }
    //...
}
```

Every typed has its own helper which sharing the same global
signature `getParsed[Type](Propeties props, String key, String defaultValue)`
where:

-   `props` is the source of data,
-   `key` is the key to be retrieved,
-   `defaultValue` is the default value in text format.

here are the existing and available helpers:

| Data Type   | Static method         |
| ----------- | --------------------- |
| boolean     | getParsedBoolean(...) |
| int         | getParsedInt(...)     |
| double      | getParsedDouble(...)  |
| Rectangle2D | getRectangle2D(...)   |
| Dimension   | getDimension(...)     |

### Using it

```java
import com.snapgames.core.Application;

public class MyApp extends Application {
    Configuration configuration;

    @Override
    public void initialize(String lArgs[]) {
        configuration = new Configuration(this, "config.properties", lArgs);
    }
}
```

The configuration parsing will gather from the following file:

```properties
app.window.name=AppTest
app.debug=true
app.debug.level=4
app.debug.filter=testObj1
app.test.mode=true
app.exit=true
app.window.size=800x480
app.render.resolution=400x240
app.render.fps=60
app.physic.constrained=true
app.physic.ups=120
app.physic.speed.max=40.0
app.physic.acceleration.max=4.0
app.physic.world=world(amazing,0.10,(1024x1024))
```

these extracted values:

| Name                        | Default value                   | Type      | Description                                 |
| :-------------------------- | :------------------------------ | :-------- | :------------------------------------------ |
| app.window.name             | "AppTest"                       | String    | the default application name                |
| app.debug                   | true                            | boolean   | the debug flag mode                         |
| app.debug.level             | 4                               | int       | the debug level                             |
| app.debug.filter            | "testObj1"                      | String    | the debug object name's filter              |
| app.test.mode               | true                            | boolean   | the test mode for unit test execution only  |
| app.exit                    | true                            | boolean   | the exit flag                               |
| app.window.size             | 800x480                         | Dimension | the window size                             |
| app.render.resolution       | 400x240                         | Dimension | the rendering buffer resolution             |
| app.render.fps              | 60                              | int       | the frame per second rate                   |
| app.physic.constrained      | true                            | boolean   | the physic engine play area constrain flag  |
| app.physic.ups              | 120                             | int       | the update per second rate                  |
| app.physic.speed.max        | 40.0                            | double    | the physic engine entity's max speed        |
| app.physic.acceleration.max | 4.0                             | double    | the physic engine entity's max acceleration |
| app.physic.world            | world(amazing,0.10,(1024x1024)) | World     | the physic engine world definition          |

The command line arguments parsing is a specifc implmentation based on a switch :

```java
public class Configuration {
    //...
    private void parseArgs(List<String> lArgs) {
        lArgs.forEach(s -> {
            System.out.printf("- process arg: '%s'%n", s);
            String[] arg = s.split("=");
            switch (arg[0]) {
                case "x", "exit" -> {
                    requestExit = Boolean.parseBoolean(arg[1]);
                }
                case "testMode" -> {
                    testMode = Boolean.parseBoolean(arg[1]);
                }
                // define debug level for this application run.
                case "d", "debugLevel" -> {
                    debugLevel = Integer.parseInt(arg[1]);
                    this.debug = true;
                }
                case "f", "fps" -> {
                    fps = Integer.parseInt(arg[1]);
                }
                case "u", "ups" -> {
                    ups = Integer.parseInt(arg[1]);
                }
                case "c", "configPath" -> {
                    this.pathToConfigFile = arg[1];
                }
                case "ds", "defaultScene" -> {
                    this.defaultScene = arg[1];
                }
                default -> {
                    System.err.printf(">> <?> unknown argument: %s in %s%n", arg[0], s);
                }
            }
        });
    }
}
```

Parsing all the arguments is trying to extract argument and value, separated by an '=' symbol, and switch on possible
values, here are the defined switch cases:

| Argument value       | Description                                                       |
| -------------------- | ----------------------------------------------------------------- |
| "x", "exit"          | Request exit mode                                                 |
| "testMode"           | run Application class in a test mode for unit test execution only |
| "d", "debugLevel"    | Activate the debug mode and set the debug level                   |
| "f", "fps"           | Define the frame-per-Second rendering rate                        |
| "u", "ups"           | Define the update-per-second processing rate                      |
| "c", "configPath"    | Set a new configuration file to start Application                 |
| "ds", "defaultScene" | set a default scene to start the Application                      |
