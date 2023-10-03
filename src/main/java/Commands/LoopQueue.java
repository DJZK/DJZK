package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class LoopQueue extends Command {
    public LoopQueue(){
        this.name = "loopqueue";
        this.aliases = new String[]{"lq", "loopq"};
        this.help = "Repeats the whole queue";
        this.guildOnly = true;
        this.hidden = false;
    }

    @Override
    protected void execute(CommandEvent e){
        // Objects
        final TextChannel channel = e.getTextChannel();
        final Member self = e.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = e.getMember();
        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

        EmbedBuilder eb;

        // Not me, not you
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK) || UniversalVariables.Unlocked)){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Bot in VC
        if (!selfVoiceState.inVoiceChannel()) {
            eb = EmbedMaker.embedBuilderDescription("You gotta call me, come on..");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        // User in voice channel
        if(!memberVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("Are you sure? Go Join a VC first....");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Not with the bot
        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            eb = EmbedMaker.embedBuilderDescription("What? Come on and join with me first!");
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Finally
        final boolean newLoopQueue = !musicManager.scheduler.loopQueue;
        musicManager.scheduler.loopQueue = newLoopQueue;
        if(newLoopQueue){
            eb = EmbedMaker.embedBuilderDescription("I will be looping these tracks till they're tired");
        }
        else{
            eb = EmbedMaker.embedBuilderDescription("Tired of 'em? Then would not be looping them...");
        }

        channel.sendMessageEmbeds(eb.build()).queue();
    }
}
