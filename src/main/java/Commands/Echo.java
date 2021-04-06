package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Echo extends Command {
    public Echo() {
        this.name = "echo";
        this.help = "enables/disables auto nowplaying and such";
        this.guildOnly = true;
        this.hidden = true;
    }
    @Override
    protected void execute(CommandEvent e) {
        // Objects
        final TextChannel channel = e.getTextChannel();
        final Member member = e.getMember();
        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

        EmbedBuilder eb;

        // Not me, not you
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK))){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Finally
        final boolean newEcho = !musicManager.scheduler.echo;
        musicManager.scheduler.echo = newEcho;
        if(newEcho){
            eb = EmbedMaker.embedBuilderDescription("I will now make noise about anything i say...");
        }
        else{
            eb = EmbedMaker.embedBuilderDescription("Right, I'll stay quiet.");
        }
        channel.sendMessage(eb.build()).queue();
    }
}
