import Commands.*;
import Functions.MessageSender;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import javax.security.auth.login.LoginException;
import java.util.Scanner;

public class MainActivity implements EventListener {

    public static void main(String[] args) throws LoginException, InterruptedException {

        Scanner in = new Scanner(System.in);
        String token;
        if(args.length == 0){
            System.out.println("Enter bot token:");
            token = in.nextLine();
        }
        else{
            token = args[0];
        }
        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(new MainActivity())
                .build();


        // Optionally block until JDA is Ready
        jda.awaitReady();

        // Sets the Online Status + Game Playing
        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.listening("your orders."));

        // Commands
        CommandClientBuilder command = new CommandClientBuilder();

        command.setOwnerId("623189902510522373");
        command.setPrefix(";");
        command.setHelpWord("help");

        // Commands go here vvvvvvvvvvvvvvvvvvvvv
        command.addCommands(
                new Clear(),
                new Echo(),
                new Join(),
                new Leave(),
                new Loop(),
                new LoopQueue(),
                new NowPlaying(),
                new Play(),
                new Queue(),
                new Remove(),
                new Shuffle(),
                new Skip(),
                new Stop(),
                // new Lyrics(),
                new Lock()
        );


        // Building the commands
        CommandClient commands = command.build();
        jda.addEventListener(commands);

        // Other Events
        new MessageSender(jda); // Passing the JDA to another
    }


    @Override
    public void onEvent(GenericEvent event){
        if (event instanceof MainActivity){
            System.out.println("I am ready!");
        }
    }
}
