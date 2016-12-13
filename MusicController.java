import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import javafx.scene.media.EqualizerBand;
import javafx.util.Duration;
import javafx.scene.media.AudioSpectrumListener;

enum PlayMode {
	REPEAT, CYCLE, SINGLE
}

class AngusListener implements AudioSpectrumListener {

	public float[] magnitudes;

	public AngusListener() {
		magnitudes = new float[128];
	}

	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		this.magnitudes = magnitudes;
	}
}

class MusicController {

	private ArrayList<Song> queue;

	private boolean paused = true;
	private PlayMode currentMode;
	private boolean shuffle = false;

	private MediaPlayer mediaPlayer;
	private AngusListener asl;

	public MusicController () {
		// TODO setCurrentMode(PlayMode.REPEAT);
		queue = new ArrayList<Song>();
		asl = new AngusListener();
	}

	/*
	 * SETTERS
	 */

	//QUEUE

	public void addToQueueEnd(Song song) {
		queue.add(song);
		Main.mainSceneController.updateSongQueueContents();
		Main.mainSceneController.updatePlayButtonEnabled(true);
	}

	public void addToQueueEnd(Album album) {
		for (Song song : album.getSongs()) {
			addToQueueEnd(song); // ^
		}
	}

	public void addToQueueNext(Song song) {
		if (queue.size() == 0) {
			queue.add(0, song);
		} else {
			queue.add(1, song);
		}
		Main.mainSceneController.updatePlayButtonEnabled(queue.size() > 0);
		Main.mainSceneController.updateSongQueueContents();
	}

	public void skipCurrentSongAndPlay(Song song) {
		addToQueueNext(song);
		if (queue.size() > 1)
			nextSong();
		setPaused(false);
	}

	public void onSongEnd() {
		System.out.println("!!! SONG END");
		if (currentMode == PlayMode.REPEAT) {
			seekTo(0);
		} else if (currentMode == PlayMode.CYCLE) {
			//TODO
			nextSong();
		} else {
			nextSong();
		}
	}

	public void nextSong() {
		if (queue.size() > 0) {
			queue.remove(0);
			Main.mainSceneController.updatePlayButtonEnabled(false);
			Main.mainSceneController.updateSongQueueContents();
		}

		if (queue.size() > 0) { //checks there is a next song to play TODO
			if (shuffle) {
				setSong(queue.get((int)(Math.random() * queue.size())));
			} else {
				setSong(queue.get(0));
			}
		} else {
			if (currentMode == PlayMode.CYCLE)
				setSong();
			else
				setSong();
		}
	}

	public void clearQueue() {
		if (queue.size() > 0) {
			Song temp = queue.get(0);
			queue.clear();
			queue.add(temp);
		}
		Main.mainSceneController.updateSongQueueContents();
	}

	//PAUSE / PLAY

	public void togglePaused() {
		setPaused(!isPaused());
	}

	public void setPaused(boolean p) {
		paused = p;
		if (paused) pause(); else play();
		Main.mainSceneController.updateCurrentSongPausedButton();
	}

	//PLAY MODE / SHUFFLE

	public void toggleShuffle() {
		shuffle = !shuffle;
	}

	public void cyclePlayMode() {
		switch (currentMode) {
			case REPEAT:
				setCurrentMode(PlayMode.CYCLE);
				break;
			case CYCLE:
				setCurrentMode(PlayMode.SINGLE);
				break;
			case SINGLE:
				setCurrentMode(PlayMode.REPEAT);
				break;
			default:
				break;
		}
	}

	private void setCurrentMode(PlayMode mode) {
		currentMode = mode;
		switch (mode) {
			case REPEAT:
				Main.mainSceneController.updatePlayModeButtonText("Repeat");
				break;
			case CYCLE:
				Main.mainSceneController.updatePlayModeButtonText("Cycle");
				break;
			case SINGLE:
				Main.mainSceneController.updatePlayModeButtonText("Single");
				break;
			default:
				break;
		}
	}

	public void seekTo(double pos) {
		//expect pos as a decimal 0 > 1 TODO
		mediaPlayer.seek(new Duration(pos));
	}

	/*
	 * GETTERS
	 */

	public boolean isPaused() { return paused; }

	public Song getCurrentSong() { return queue.size() > 0 ? queue.get(0) : null; }

	public String getTimeElapsed() { return mediaPlayer != null ? mediaPlayer.currentTimeProperty().toString() : ""; }

	public ArrayList<Song> getQueue() { return queue; }

	/*
	 * INTERNAL METHODS (only mention of mediaPlayer should be here)
	 */

	private void setSong(Song song) {
		Media media = new Media(new File(song.getFile()).toURI().toString());
		if (mediaPlayer != null) mediaPlayer.stop();
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setOnEndOfMedia(() -> onSongEnd());
		mediaPlayer.setAudioSpectrumListener(asl);
		setPaused(false);
	}

	private void setSong() {
		setPaused(true);
	//	mediaPlayer.stop();
		mediaPlayer = null;
	}

	private void play() {
		if (mediaPlayer != null)
 			mediaPlayer.play();
		else if (queue.size() > 0)
			setSong(queue.get(0));
	}

	private void pause() {
		if (mediaPlayer != null)
 			mediaPlayer.pause();
	}

	/*
	 * NASTY VISUALISER STUFF
	 */

	//double t = 0;//TODO
	public float getMagnitudeOfFrequency (int n) {
		if (isPaused())
			return 0;
		float mag = asl.magnitudes[n];
		mag *= -1;
		mag = 60 - mag;
		mag /= 60;
		return mag;
	}

}
