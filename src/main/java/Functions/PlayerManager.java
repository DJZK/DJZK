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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, MusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private static String trackURL = "";
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

    public String getTrackLink(TextChannel channel, String trackName){
        final MusicManager musicManager = this.getMusicManager(channel.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackName, new AudioLoadResultHandler() {

            EmbedBuilder eb;
            @Override
            public void trackLoaded(AudioTrack audioTrack){
                trackURL = audioTrack.getInfo().uri;
                eb = EmbedMaker.embedBuilderAuthor("Downloading: ",
                        audioTrack.getInfo().title + "\n"
                                + audioTrack.getInfo().uri);
                channel.sendMessage(eb.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                final AudioTrack oneTrack = tracks.get(0);
                trackURL = oneTrack.getInfo().uri;
                eb = EmbedMaker.embedBuilderAuthor("Adding to queue: ",
                        oneTrack.getInfo().title + "\n"
                                + oneTrack.getInfo().uri);
                channel.sendMessage(eb.build()).queue();
            }
            @Override
            public void noMatches() {
                EmbedBuilder eb = EmbedMaker.embedBuilderDescription("No match found! Try searching again?");
                channel.sendMessage(eb.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder eb = EmbedMaker.embedBuilderDescription("An error occurred! Sorry I'm not perfect yet, try again perhaps?");
                e.printStackTrace();
                channel.sendMessage(eb.build()).queue();
            }
        });
        return trackURL;
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
                    channel.sendMessage(eb.build()).queue();
                }
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

                        channel.sendMessage(eb.build()).queue();
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
                        channel.sendMessage(eb.build()).queue();
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
                        channel.sendMessage(eb.build()).queue();
                        eb.clear();


                    }


                }
                return;
            }

            @Override
            public void noMatches() {
                EmbedBuilder eb = EmbedMaker.embedBuilderDescription("No match found! Try searching again?");
                channel.sendMessage(eb.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                EmbedBuilder eb = EmbedMaker.embedBuilderDescription("An error occurred! Sorry I'm not perfect yet, try again perhaps?");
                e.printStackTrace();
                channel.sendMessage(eb.build()).queue();
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
