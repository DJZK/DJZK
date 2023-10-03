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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Download extends Command {

    public Download() {
        this.name = "download";
        this.aliases = new String[]{"d", "dl"};
        this.help = "Downloads a Song!";
        this.guildOnly = true;
        this.hidden = false;
    }

    @Override
    protected void execute(CommandEvent e) {
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
        String uri, title;
        // Queuing a song
        if(!e.getArgs().isEmpty()){
            // Comparing link and not
            String link = String.join(" ",e.getArgs());
            if(!isUrl(link)){
                System.out.println("Searching... " + link);
                link = "ytsearch:" + link;
                uri = PlayerManager.getInstance().getTrackLink(channel,link)[0];
                title = PlayerManager.getInstance().getTrackLink(channel,link)[1];
            }
            // Playlist
            else {
                System.out.println("Link detected! -> " + link);
                uri = PlayerManager.getInstance().getTrackLink(channel,link)[0];
                title = PlayerManager.getInstance().getTrackLink(channel,link)[1];
            }

            try {



                ProcessExecute PE = new ProcessExecute();
                Thread.sleep(3000);
                PE.executeProcessAsync("bin\\yt-dlp.exe", "-x", "--audio-format", "mp3", "--audio-quality", "0", "-o", "\\Download\\%(title)s.%(ext)s", uri);
                // executeProcessAsync("bin\\yt-dlp.exe", "-x", "--audio-format", "mp3", "--audio-quality", "0", "-o", "\\Download\\%(title)s.%(ext)s", uri);

                eb = EmbedMaker.embedBuilderAuthor("Download Complete", title);
                e.getTextChannel().sendMessageEmbeds(eb.build()).queue();
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
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

