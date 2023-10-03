package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Stop extends Command {
    public Stop() {
        this.name = "stop";
        this.aliases = new String[]{"st"};
        this.help = "Stops the player and clears the queue";
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
        musicManager.scheduler.player.stopTrack();
        musicManager.scheduler.queue.clear();
        musicManager.scheduler.looping = false;
        musicManager.scheduler.loopQueue = false;
        eb = EmbedMaker.embedBuilderDescription("Right, Imma shut up");
        channel.sendMessageEmbeds(eb.build()).queue();
    }
}
