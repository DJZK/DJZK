package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Loop extends Command {
    public Loop() {
        this.name = "loop";
        this.aliases = new String[]{"l"};
        this.help = "Repeats the current track";
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
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK))){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Bot in VC
        if (!selfVoiceState.inVoiceChannel()) {
            eb = EmbedMaker.embedBuilderDescription("You gotta call me, come on..");
            channel.sendMessage(eb.build()).queue();
            return;
        }
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        // User in voice channel
        if(!memberVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("Are you sure? Go Join a VC first....");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Not with the bot
        if(!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())){
            eb = EmbedMaker.embedBuilderDescription("What? Come on and join with me first!");
            channel.sendMessage(eb.build()).queue();
            return;
        }

        // Finally
        final boolean newLooping = !musicManager.scheduler.looping;
        musicManager.scheduler.looping = newLooping;
        if(newLooping){
            eb = EmbedMaker.embedBuilderDescription("I will be looping that track till it's tired");
        }
        else{
            eb = EmbedMaker.embedBuilderDescription("Tired of its sound? Would not be looping the track now.");
        }

        channel.sendMessage(eb.build()).queue();
    }
}
