package Functions;

import net.dv8tion.jda.api.EmbedBuilder;

public class EmbedMaker {
    public static EmbedBuilder embedBuilderDescription(String message){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(1351);
        eb.setDescription(message);
        return eb;
    }

    public static EmbedBuilder embedBuilderAuthor(String Author, String Description){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(1351);
        eb.setTitle(Author);
        eb.setDescription(Description);
        return eb;
    }
}
