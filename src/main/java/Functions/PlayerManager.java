package Functions;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, MusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public MusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final MusicManager musicManager = new MusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
            return musicManager;
        });
    }

    public void getTrackLink(TextChannel channel, String trackName, String type){
        final MusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackName, new AudioLoadResultHandler() {

            EmbedBuilder eb;
            @Override
            public void trackLoaded(AudioTrack audioTrack){
                eb = EmbedMaker.embedBuilderAuthor("Downloading from Link: ",
                        audioTrack.getInfo().title + "\n"
                                + audioTrack.getInfo().uri);
                channel.sendMessageEmbeds(eb.build()).queue();


                try {
                    ProcessExecute PE = new ProcessExecute();
                    PE.executeProcessAsync("bin\\yt-dlp.exe", "-x", "--audio-format", "mp3", "--audio-quality", "0", "-o", "\\Download\\%(title)s.%(ext)s", audioTrack.getInfo().uri);
                    eb = EmbedMaker.embedBuilderAuthor("Download Complete", audioTrack.getInfo().title);
                    channel.sendMessageEmbeds(eb.build()).queue();
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();


                if(type.equals("search")){
                    final AudioTrack oneTrack = tracks.get(0);

                    eb = EmbedMaker.embedBuilderAuthor("Downloading: ",
                            oneTrack.getInfo().title + "\n"
                                    + oneTrack.getInfo().uri);
                    channel.sendMessageEmbeds(eb.build()).queue();


                    try {
                        ProcessExecute PE = new ProcessExecute();
                        PE.executeProcessAsync("bin\\yt-dlp.exe", "-x", "--audio-format", "mp3", "--audio-quality", "0", "-o", "\\Download\\%(title)s.%(ext)s", oneTrack.getInfo().uri);
                        // executeProcessAsync("bin\\yt-dlp.exe", "-x", "--audio-format", "mp3", "--audio-quality", "0", "-o", "\\Download\\%(title)s.%(ext)s", uri);

                        eb = EmbedMaker.embedBuilderAuthor("Download Complete", oneTrack.getInfo().title);
                        channel.sendMessageEmbeds(eb.build()).queue();
                    } catch (IOException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    return;
                }
                else if(type.equals("playlist")){
                    if (musicManager.scheduler.echo) {
                        eb = EmbedMaker.embedBuilderDescription("Downloading: '"
                                + tracks.size()
                                + "' tracks from playlist '"
                                + audioPlaylist.getName()
                                + "'");
                        channel.sendMessageEmbeds(eb.build()).queue();
                        eb.clear();
                    }

                    // Loop for adding the tracks to queue
                    for (final AudioTrack track : tracks) {
                        try {
                            eb = EmbedMaker.embedBuilderAuthor("Downloading: ",
                                    track.getInfo().title + "\n"
                                            + track.getInfo().uri);
                            channel.sendMessageEmbeds(eb.build()).queue();

                            ProcessExecute PE = new ProcessExecute();
                            PE.executeProcessAsync("bin\\yt-dlp.exe", "-x", "--audio-format", "mp3", "--audio-quality", "0", "-o", "\\Download\\%(title)s.%(ext)s", track.getInfo().uri);
                            // executeProcessAsync("bin\\yt-dlp.exe", "-x", "--audio-format", "mp3", "--audio-quality", "0", "-o", "\\Download\\%(title)s.%(ext)s", uri);

                            eb = EmbedMaker.embedBuilderAuthor("Download Complete", track.getInfo().title);
                            channel.sendMessageEmbeds(eb.build()).queue();
                        } catch (IOException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                }


            }
            @Override
            public void noMatches() {
                EmbedBuilder eb = EmbedMaker.embedBuilderDescription("No match found! Try searching again?");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder eb = EmbedMaker.embedBuilderDescription("An error occurred! Sorry I'm not perfect yet, try again perhaps?");
                e.printStackTrace();
                channel.sendMessageEmbeds(eb.build()).queue();
            }
        });

    }



    public void loadAndPlay(TextChannel channel, String trackURL, String type) {
        final MusicManager musicManager = this.getMusicManager(channel.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                EmbedBuilder eb;
                musicManager.scheduler.queue(audioTrack);

                // URL Track based
                if (musicManager.scheduler.echo) {
                    eb = EmbedMaker.embedBuilderAuthor("Adding to queue: ",
                            audioTrack.getInfo().title + "\n"
                                    + audioTrack.getInfo().uri);
                    channel.sendMessageEmbeds(eb.build()).queue();
                }
                return;
            }

            // Playlist Wide
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                EmbedBuilder eb;
                final List<AudioTrack> tracks = audioPlaylist.getTracks();

                // Searching
                if (type.equals("search")) {
                    // Will load the first result
                    final AudioTrack oneTrack = tracks.get(0);

                    // Adding to queue
                    musicManager.scheduler.queue(oneTrack);

                    if (musicManager.scheduler.echo) {
                        eb = EmbedMaker.embedBuilderAuthor("Adding to queue: ",
                                oneTrack.getInfo().title + "\n"
                                        + oneTrack.getInfo().uri);

                        channel.sendMessageEmbeds(eb.build()).queue();
                    }
                    System.out.println("Adding... " + oneTrack.getInfo().title);

                    //  musicManager.scheduler.LastPlayingTrack = oneTrack.getInfo().title;
                    return;
                } else if (type.equals("playlist")) {
                    // Shows the size of the playlist
                    if (musicManager.scheduler.echo) {
                        eb = EmbedMaker.embedBuilderDescription("Adding to queue: '"
                                + tracks.size()
                                + "' tracks from playlist '"
                                + audioPlaylist.getName()
                                + "'");
                        channel.sendMessageEmbeds(eb.build()).queue();
                        eb.clear();
                    }

                    // Loop for adding the tracks to queue
                    for (final AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);

                    }


                    if (musicManager.scheduler.echo) {
                        final AudioPlayer player = musicManager.audioPlayer;
                        final AudioTrack tracker = player.getPlayingTrack();
                        final AudioTrackInfo info = tracker.getInfo();

                        eb = EmbedMaker.embedBuilderAuthor("Now Playing ", info.title + "\n" + info.uri);
                        channel.sendMessageEmbeds(eb.build()).queue();
                        eb.clear();


                    }


                }
                return;
            }

            @Override
            public void noMatches() {
                EmbedBuilder eb = EmbedMaker.embedBuilderDescription("No match found! Try searching again?");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder eb = EmbedMaker.embedBuilderDescription("An error occurred! Sorry I'm not perfect yet, try again perhaps?");
                e.printStackTrace();
                channel.sendMessageEmbeds(eb.build()).queue();
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }
}
