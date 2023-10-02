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
            channel.sendMessage(eb.build()).queue();
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
                executeProcessAsync("yt-dlp.exe", "-x", "--audio-format", "mp3", "--audio-quality", "0", "-o", "\\Download\\%(title)s.%(ext)s", uri);
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            eb = EmbedMaker.embedBuilderAuthor("Download Complete", title);
            e.getTextChannel().sendMessage(eb.build()).queue();
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

    public void executeProcessAsync(String command, String... arguments) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.command().addAll(List.of(arguments));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("Process exited with error code " + exitCode);
        }

    }

}

