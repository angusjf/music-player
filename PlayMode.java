 

import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import javafx.scene.media.EqualizerBand;

enum PlayMode {
	ONCE, CYCLE, SINGLE
}

class MusicController {

	private ArrayList<Song> queue;
	private boolean paused = true;
	private PlayMode currentMode = PlayMode.ONCE;
	private boolean shuffle = false;
	private String timeElapsed = "0:00";//TODO
	private MediaPlayer mediaPlayer;

	public MusicController () {
		queue = new ArrayList<Song>();
	}

	// SETTERS

	public void addToQueueEnd(Song song) {
		queue.add(song);
	}

	public void addToQueueEnd(Album album) {
		for (Song song : album.getSongs()) {
			queue.add(song);
		}
	}

	public void addToQueueNext(Song song) {
	
	}

	public void skipCurrentSongAndPlay(Song song) {
		addToQueueNext(song);
		nextSong();
	}

	public void nextSong() {
		queue.remove(0);
		if (queue.size() > 0) { //checks there is a next song to play TODO
			if (shuffle) {
				//play(queue.get(Random.nextInt(queue.size() + 1)));
			} else {
				//play(queue.get(0));
			}
		}
	}

	public void togglePaused() {
		if (mediaPlayer == null) { //TODO
			Media media = new Media(new File(queue.get(0).getFile()).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
		}

		paused = !paused;
		if (paused) mediaPlayer.pause();
		else mediaPlayer.play();
	}

	public void toggleShuffle() {
		shuffle = !shuffle;
	}

	public void cyclePlayMode() {
		switch (currentMode) {
			case ONCE:
				currentMode = PlayMode.CYCLE;
				break;
			case CYCLE:
				currentMode = PlayMode.SINGLE;
				break;
			case SINGLE:
				currentMode = PlayMode.ONCE;
				break;
			default:
				break;
		}
	}

	// GETTERS
	
	public boolean isPaused() {
		return paused;
	}

	public Song getCurrentSong() {
		return queue.get(0);
	}

	public String getTimeElapsed() {
		return timeElapsed;
	}

	public ArrayList<Song> getQueue() {
		return queue;
	}

	double t = 0;//TODO
	public double getFrequency (double n) {
		//frequencies[i] = (double)mediaPlayer.getAudioEqualizer().getBands().get(i).getCenterFrequency(); TODO

		t -= 0.0001; //TODO

		return (Math.sin(n + t) + 1 ) / 2;
	}

	//INTERNAL METHODS

	
}
