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

public class Download {
    public class Play extends Command {
        public Play() {
            this.name = "play";
            this.aliases = new String[]{"p", "pl"};
            this.help = "Plays a song!";
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
            String uri;
            // Queuing a song
            if(!e.getArgs().isEmpty()){
                // Comparing link and not
                String link = String.join(" ",e.getArgs());
                if(!isUrl(link)){
                    System.out.println("Searching... " + link);
                    link = "ytsearch:" + link;
                    uri = PlayerManager.getInstance().getTrackLink(channel,link);
                }
                // Playlist
                else {
                    System.out.println("Link detected! -> " + link);
                    uri =PlayerManager.getInstance().getTrackLink(channel,link);
                }

                try {
                    executeProcessAsync("-x --audio-format mp3 --audio-quality 0 -o \\Download\\%(title)s.%(ext)s " + uri);
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

        private String executeProcessAsync(String arguments) throws IOException, InterruptedException {
            ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp.exe", arguments);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Process exited with error code " + exitCode);
            }

            return output.toString();
        }

    }
}
