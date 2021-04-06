package Functions;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class MusicManager {
    public final AudioPlayer audioPlayer;
    public final TrackScheduler scheduler;
    private final PlayerSendHandler sendHandler;
    public MusicManager(AudioPlayerManager manager){
        this.audioPlayer = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.audioPlayer);
        this.audioPlayer.addListener(this.scheduler);
        this.sendHandler = new PlayerSendHandler(this.audioPlayer);
    }

    public PlayerSendHandler getSendHandler(){
        return sendHandler;
    }
}
