package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Shuffle extends Command {
    public Shuffle(){
        this.name = "shuffle";
        this.aliases = new String[] {"sh", "shuf", "shuff"};
        this.help = "Shuffles the current playing queue";
        this.guildOnly = true;
        this.hidden = false;

    }

    @Override
    protected void execute(CommandEvent e) {
        // Objects
        EmbedBuilder eb;
        final TextChannel channel = e.getTextChannel();
        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;
        final List<AudioTrack> trackList = new ArrayList<>(queue);
        final Member self = e.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = e.getMember();

        // Not me, not you
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK) || UniversalVariables.Unlocked)){
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
        int length = trackList.size();
        int min = 1;
        int range = length - min + 1;
        int rand;

        // Ensures the full shuffling
        for(int j = 0; j <= 3; j++) {
            // Shuffling happens here
            for (int i = 0; i < length; i++) {
                rand = (int) (Math.random() * range) + min;
                try {
                    AudioTrack aud = trackList.get(rand - 1);
                    queue.remove(aud);
                    queue.add(aud);
                }
                // Error
                catch (Exception err) {
                    err.printStackTrace();
                    eb = EmbedMaker.embedBuilderDescription("Error occurred");
                    channel.sendMessage(eb.build()).queue();
                    eb.clear();
                    return;
                }
            }
        }
        // Shuffled
        eb = EmbedMaker.embedBuilderDescription("LET'S GET READY TO RUMBLE!!!!");
        channel.sendMessage(eb.build()).queue();
    }
}
