package Functions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

public class MessageSender {
    private static JDA jda;

    // Messages
    public static String suppressed = "This functionality was suppressed";
    public static String noPermission = "Nope... Not gonna happen :)";
    public MessageSender(JDA api) {this.jda = api;}

    public static void sendMessage(String GuildID, String TextID, EmbedBuilder eb){
        try{
            jda.getGuildById(GuildID).getTextChannelById(TextID).sendMessage(eb.build()).queue();
        } catch (Exception e){
            System.out.println("Something happened");
            e.printStackTrace();
        }
    }
}
