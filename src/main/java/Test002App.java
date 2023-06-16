package ;
   
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * main class for project Test002
 *
 * @author Frédéric
 * @since 1.0.0
 */
public class Test002App{

    private ResourceBundle messages = ResourceBundle.getBundle("i18n/messages");
    private Properties config = new Properties();
    private boolean exit = false;

    public Test002App(){
        System.out.println(String.format("Initialization application %s (%s)",
                messages.getString("app.name"),
                messages.getString("app.version")));    }

    public void run(String[] args){
        init(args);
        loop();
        dispose();
    }

    private void init(String[] args){
        List<String> lArgs = Arrays.asList(args);
        try {
            config.load(this.getClass().getResourceAsStream("/config.properties"));

            exit = Boolean.parseBoolean(config.getProperty("app.exit"));
        } catch (IOException e) {
            System.out.println(String.format("unable to read configuration file: %s", e.getMessage()));
        }

        lArgs.forEach(s -> {
            System.out.println(String.format("- arg: %s", s));
        });  
    }
    private void loop(){
        while(!exit){
            // will loop until exit=true or CTRL+C
        }
    }
    private void dispose(){
        System.out.println("End of application Test002");
    }

    public static void main(String[] argc){
        Test002App app = new Test002App();
        app.run(argc);
    }
}
