package Commands;

import Functions.*;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;

public class Play extends Command {
    public Play(){
        this.name = "play";
        this.aliases = new String[] {"p" , "pl"};
        this.help = "Plays a song!";
        this.guildOnly = true;
        this.hidden = false;
    }

    @Override
    protected void execute(CommandEvent e){
        // Objects
        final TextChannel channel = e.getTextChannel();
        final Member self = e.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final MusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
        final AudioPlayer audioPlayer = musicManager.audioPlayer;
        final Member member = e.getMember();
        Join join = new Join();
        EmbedBuilder eb;

        // Not me. not you....
        if(!(member.getPermissions().contains(Permission.ADMINISTRATOR) || member.getId().equals(UniversalVariables.DJZK) || UniversalVariables.Unlocked)){
            eb = EmbedMaker.embedBuilderDescription(MessageSender.noPermission);
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        // Bot in voice channel
        if(!selfVoiceState.inVoiceChannel()){
            Join.called = true;
            join.execute(e);
        }

        final GuildVoiceState memberVoiceState = member.getVoiceState();
        // User in voice channel
        if(!memberVoiceState.inVoiceChannel()){
            eb = EmbedMaker.embedBuilderDescription("Are you sure? Go Join a VC first....");
            channel.sendMessageEmbeds(eb.build()).queue();
            eb.clear();
            return;
        }

        // Not with the bot
        if(!Join.called) {
            if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
                eb = EmbedMaker.embedBuilderDescription("What? Come on and join with me first!");
                channel.sendMessageEmbeds(eb.build()).queue();
                eb.clear();
                return;
            }
        }
        Join.called = false;

        // Finally

        // Play without params
        if(e.getArgs().isEmpty() && audioPlayer.getPlayingTrack() == null){
            eb = EmbedMaker.embedBuilderDescription("Yo, play some song.");
            channel.sendMessageEmbeds(eb.build()).queue();
            eb.clear();
            return;
        }

        // Arguments for play pause
        if(e.getArgs().isEmpty()){

            // Play Pause Logic shit fuck
            if(e.getArgs().isEmpty() && audioPlayer.isPaused()){
                audioPlayer.setPaused(false);
                eb = EmbedMaker.embedBuilderDescription("Alright, I'll resume..");
                channel.sendMessageEmbeds(eb.build()).queue();
                eb.clear();
                return;
            }

            if(e.getArgs().isEmpty() && !audioPlayer.isPaused()){
                audioPlayer.setPaused(true);
                eb = EmbedMaker.embedBuilderDescription("Aight... aight, I'll pause..");
                channel.sendMessageEmbeds(eb.build()).queue();
                eb.clear();
                return;
            }
        }

        // Queuing a song
        if(!e.getArgs().isEmpty()){
            // Comparing link and not
            String link = String.join(" ",e.getArgs());
            if(!isUrl(link)){
                System.out.println("Searching... " + link);
                link = "ytsearch:" + link;
                PlayerManager.getInstance().loadAndPlay(channel,link, "search");
            }
            // Playlist
            else {
                System.out.println("Link detected! -> " + link);
                PlayerManager.getInstance().loadAndPlay(channel, link, "playlist");
            }
        }

    }

    private boolean isUrl(String url){
        try{
            new URI(url);
            return true;
        }
        catch(URISyntaxException err){
            return false;
        }
    }

}
