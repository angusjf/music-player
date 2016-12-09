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

	private MediaPlayer mediaPlayer;

	public MusicController () {
		queue = new ArrayList<Song>();
	}

	/*
	 * SETTERS
	 */

	//QUEUE
	
	public void addToQueueEnd(Song song) {
		queue.add(song);
		Main.mainSceneController.updateSongQueueContents();
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
		Main.mainSceneController.updateSongQueueContents();
	}

	public void skipCurrentSongAndPlay(Song song) {
		addToQueueNext(song);
		if (queue.size() > 1)
			nextSong();
		setPaused(false);
	}

	public void nextSong() {
		if (queue.size() > 0) {
			queue.remove(0);
			Main.mainSceneController.updateSongQueueContents();
		}

		if (queue.size() > 0) { //checks there is a next song to play TODO
			if (shuffle) {
				setSong(queue.get((int)(Math.random() * queue.size())));
			} else {
				setSong(queue.get(0));
			}
		} else {
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

	public void seekTo(float pos) {
		//expect pos as a decimal 0 > 1 TODO
		//mediaPlayer.seek(pos * mediaPlayer);
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
		else
			setSong(queue.get(0));
	}

	private void pause() {
		if (mediaPlayer != null)
 			mediaPlayer.pause();
	}

	/*
	 * NASTY VISUALISER STUFF
	*/

	double t = 0;//TODO
	public double getFrequency (double n) {
		//frequencies[i] = (double)mediaPlayer.getAudioEqualizer().getBands().get(i).getCenterFrequency(); TODO

		t -= 0.0001; //TODO

		return (Math.sin(n + t) + 1 ) / 2;
	}

}
