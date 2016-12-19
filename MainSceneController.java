import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import java.util.Stack;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class MainSceneController {

	final String SCENE_TITLE = "Music Player";
	final int MIN_HEIGHT = 400, MIN_WIDTH = 600;
	private Stage stage;
	private final Color greyText = Color.rgb(130, 130, 130);

	@FXML private AnchorPane libraryPane, queuePane;
	@FXML private Button backButton, playModeButton, currentSongPauseButton, currentSongSkipButton, visualiseButton, clearQueueButton;
	@FXML private Text albumNameText, artistNameText, featuresNamesText, songLengthText, songNameText, timeElapsedtext;
	@FXML private TextField searchBox;
	@FXML private ChoiceBox viewsChoiceBox;
	@FXML private Slider songProgressBar;

	public MainSceneController () { }

	public void setupStage(Stage stage, Scene scene) {
		this.stage = stage;
		stage.setScene(scene);
		stage.setTitle(SCENE_TITLE);
		stage.setMinHeight(MIN_WIDTH);
		stage.setMinWidth(MIN_HEIGHT);
		stage.getIcons().add(new Image("file:resources/logo.png"));
		stage.show();

		// setup close button
		stage.setOnCloseRequest(we -> {
			Main.terminate();
			we.consume();
		});
	}

	@FXML void initialize() {
		System.out.println("+ Main Scene initialized");
		try {
			assert viewsChoiceBox != null		: "- viewsChoiceBox is null";
			assert songNameText != null			: "- songNameText is null";
			assert artistNameText != null		: "- artistNameText is null";
			assert featuresNamesText != null	: "- featuresNamesText is null";
			assert albumNameText != null		: "- albumNameText is null";
			assert timeElapsedtext != null		: "- timeElapsedtext is null";
			assert songProgressBar != null		: "- songProgressBar is null";
			assert songLengthText != null		: "- songLengthText is null";
			assert currentSongPauseButton != null	: "- currentSongPauseButton is null";
			assert libraryPane != null		: "- libraryPane is null";
			assert queuePane != null		: "- QueuePane is null";
			assert searchBox != null		: "- searchBox is null";
		} catch (AssertionError ae) {
			System.out.println("FXML assertion failure: " + ae.getMessage());
			Main.terminate();
		}

		viewsChoiceBox.getItems().addAll("Albums", "Songs", "Artists", "Genres", "Playlists");
		viewsChoiceBox.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<String>() {
			@Override public void changed(ObservableValue ov, String t, String t1) {
				updateBackButtonEnabled(false);
				updateLibraryPane();
			}
		});
		viewsChoiceBox.getSelectionModel().selectFirst();

		//TODO VVVVVVVVV
		updateSongText();//TODO make this update automatically

		//timeElapsedtext.textProperty().bind(Main.musicController.getTimeElapsed());
		//songProgressBar.  Main.musicController.setSeek(M

		updateVisualiseButtonEnabled(true); //REMOVE TODO
	}

	/*
	 * PUBLIC UI UPDATE METHODS
	 */

	public void updateSongText() {
		Song song = Main.musicController.getCurrentSong();
		if (song == null) {
			songNameText.setText("");
			artistNameText.setText("");
			featuresNamesText.setText("");
			albumNameText.setText("");
		} else {
			songNameText.setText(song.toString() + " - ");
			artistNameText.setText(song.getArtist().toString() + " - ");
			featuresNamesText.setText(song.getFeatures().size() != 0 ? String.join(", ", song.getFeatures().stream().map((artist) -> artist.toString()).collect(Collectors.toList()) + " - ") : "");
			albumNameText.setText(song.getAlbum().toString());
			songLengthText.setText(song.getLength());
		}
	}

	public void updateTimeElapsed() {
		if (Main.musicController.getCurrentSong() != null)
			timeElapsedtext.setText(Main.musicController.getTimeElapsed());
		else
			timeElapsedtext.setText("");
	}

	public void updateSongQueueContents() {
		List<Song> queue = Main.musicController.getQueue();
		if (Main.musicController.getQueue().size() > 0) {
			queue = queue.subList(1, Main.musicController.getQueue().size());
			VBox vBox = new VBox();
			queuePane.getChildren().setAll(vBox);
			for (Song song : queue) {
				vBox.getChildren().addAll(generateSongBox(song));
			}
		}
		updateClearQueueButtonEnabled(queue.size() > 1);
		updateSkipButtonEnabled(queue.size() > 1);
	}

	public void updateLibraryPane() {
		String viewString = viewsChoiceBox.getSelectionModel().getSelectedItem().toString();
		updateBackButtonEnabled(false);
		switch (viewString) {
			case "Albums":		showHasSongsView(Album.getAllFromDatabase()); break;
			case "Songs":		showSongsView(Song.getAllFromDatabase()); break;
			case "Artists":		showHasAlbumsView(Artist.getAllFromDatabase()); break;
			case "Genres":		showHasAlbumsView(Genre.getAllFromDatabase()); break;
			case "Playlists":	showHasSongsView(Playlist.getAllFromDatabase()); break;
			default:			System.out.println("- unknown viewsChoiceBox item"); break;
		}
	}

	public void updateCurrentSongPausedButton() {
		currentSongPauseButton.setText(Main.musicController.isPaused() ? ">" : "||");
	}

	public void updateBackButtonEnabled(boolean enabled) {
		backButton.setDisable(!enabled);
	}

	public void updatePlayButtonEnabled(boolean enabled) {
		currentSongPauseButton.setDisable(!enabled);
	}

	public void updateSkipButtonEnabled(boolean enabled) {
		currentSongSkipButton.setDisable(!enabled);
	}

	public void updateVisualiseButtonEnabled(boolean enabled) {
		visualiseButton.setDisable(!enabled);
	}

	public void updateClearQueueButtonEnabled(boolean enabled) {
		clearQueueButton.setDisable(!enabled);
	}

	public void updatePlayModeButtonText(String text) {
		playModeButton.setText(text);
	}

	/*
	 * ON UI ACTION METHODS
	 */

	@FXML void onBackButtonPressed() {
		updateBackButtonEnabled(false);
		updateLibraryPane();
	}

	@FXML void onCyclePlayModeClicked() {
		Main.musicController.cyclePlayMode();
	}

	@FXML void onCurrentSongPauseClicked() {
		Main.musicController.togglePaused();
	}

	@FXML void onCurrentSongSkipClicked() {
		Main.musicController.nextSong();
	}

	@FXML void onOpenVisualiserClicked() {
		Main.openVisualiser();
	}

	@FXML void onSearch() {
		showSearchResultsView(searchBox.getText());
	}

	@FXML void onNewFileClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose a Music File");
		List<File> list = fileChooser.showOpenMultipleDialog(stage);
		if (list != null) {
			for (File file : list) {
				Song song = new Song(file);
			}
		}
		updateLibraryPane();
	}

	@FXML void onClearQueuePressed() {
		Main.musicController.clearQueue();
	}

	/*
	 * BEHIND THE SCENES
	 */

	private void showSearchResultsView(String query) {
		if (query.equals("")) return;
		updateBackButtonEnabled(true);
		libraryPane.getChildren().clear();
		VBox searchResults = new VBox();
		libraryPane.getChildren().add(searchResults);

		//SONGS
		{
			VBox songsBox = new VBox();
			ArrayList<Song> songResults = new ArrayList<>();
			for (Song s : Song.getAllFromDatabase()) {
				if (s.toString().toLowerCase().contains(query.toLowerCase())) {
					songResults.add(s);
					songsBox.getChildren().add(generateSongBox(s));
				}
			}
			Text songsText = new Text(songResults.size() > 0 ? "Search results for '" + query + "' in songs:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(songsText, songsBox);
		}

		//ALBUMS
		{
			VBox albumsBox = new VBox();
			ArrayList<Album> albumResults = new ArrayList<>();
			for (Album a : Album.getAllFromDatabase()) {
				if (a.toString().toLowerCase().contains(query.toLowerCase())) {
					albumResults.add(a);
					albumsBox.getChildren().add(generateHasSongsBox(a));
				}
			}
			Text albumsText = new Text(albumResults.size() > 0 ? "Search results for '" + query + "' in albums:" : "No alubms found for '" + query + "'.");
			searchResults.getChildren().addAll(albumsText, albumsBox);
		}

		//ARTISTS
		{
			VBox artistsBox = new VBox();
			ArrayList<Artist> artistResults = new ArrayList<>();
			for (Artist a : Artist.getAllFromDatabase()) {
				if (a.toString().toLowerCase().contains(query.toLowerCase())) {
					artistResults.add(a);
					artistsBox.getChildren().add(generateHasAlbumsBox(a));
				}
			}
			Text artistsText = new Text(artistResults.size() > 0 ? "Search results for '" + query + "' in artists:" : "No artists found for '" + query + "'.");
			searchResults.getChildren().addAll(artistsText, artistsBox);
		}

		//GENRES
		{
			VBox genresBox = new VBox();
			ArrayList<Genre> genreResults = new ArrayList<>();
			for (Genre g : Genre.getAllFromDatabase()) {
				if (g.toString().toLowerCase().contains(query.toLowerCase())) {
					genreResults.add(g);
					genresBox.getChildren().add(generateHasAlbumsBox(g));
				}
			}
			Text genresText = new Text(genreResults.size() > 0 ? "Search results for '" + query + "' in genres:" : "No genres found for '" + query + "'.");
			searchResults.getChildren().addAll(genresText, genresBox);
		}

		//PLAYLITS
		{
			VBox playlistsBox = new VBox();
			ArrayList<Playlist> playlistResults = new ArrayList<>();
			for (Playlist p : Playlist.getAllFromDatabase()) {
				if (p.toString().toLowerCase().contains(query.toLowerCase())) {
					playlistResults.add(p);
					playlistsBox.getChildren().add(generateHasSongsBox(p));
				}
			}
			Text playlistText = new Text(playlistResults.size() > 0 ? "Search results for '" + query + "' in playlists:" : "No playlists found for '" + query + "'.");
			searchResults.getChildren().addAll(playlistText, playlistsBox);
		}
	}

	private void showSongsView(List<Song> songList) {
		libraryPane.getChildren().clear();
		VBox vBox = new VBox();
		libraryPane.getChildren().addAll(vBox);
		for (Song song : songList) vBox.getChildren().addAll(generateSongBox(song));
	}

	private void showHasSongsView(List<? extends HasSongs> hasSongsList) {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);
		for (HasSongs hs : hasSongsList) tilePane.getChildren().addAll(generateHasSongsBox(hs));
	}

	private void showHasAlbumsView(List<? extends HasAlbums> hasAlbumsList) {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);
		for (HasAlbums ha : hasAlbumsList) tilePane.getChildren().addAll(generateHasAlbumsBox(ha));
	}

	/*
	 * BOX GENERATORS
	 */

	private HBox generateSongBox(Song song) {
		HBox hBox = new HBox();
		Button playButton = new Button(">");
		playButton.setOnAction(event -> Main.musicController.skipCurrentSongAndPlay(song));
		Button addButton = new Button("+");
		addButton.setOnAction(event -> Main.musicController.addToQueueEnd(song));
		Text songNameText = new Text(song.toString() + " - ");
		Text artistNameText = new Text(song.getArtist().toString());
		Text featuresNamesText = new Text(song.getFeatures().size() != 0 ? " (ft. " + String.join(", ", song.getFeatures().stream().map((artist) -> artist.toString()).collect(Collectors.toList())) + ") - " : " - ");
		Text albumNameText = new Text(song.getAlbum().toString() + " - ");
		Text songLengthText = new Text(song.getLength());
		hBox.getChildren().addAll(playButton, addButton, songNameText, artistNameText, featuresNamesText, albumNameText, songLengthText);
		return hBox;
	}

	//create a box for an artist OR genre
	private VBox generateHasAlbumsBox(HasAlbums hasAlbums) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));
		vBox.setSpacing(4);

		ImageView image = new ImageView(/*hasAlbums.getPicture()*/);
		image.setFitHeight(142);
		image.setFitWidth(142);
		Text name = new Text(hasAlbums.toString());
		Text albumCount = new Text(hasAlbums.getAlbums().size() + " Albums");
		albumCount.setFill(greyText);

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			showHasSongsView(/*hasAlbums.toString(),*/ hasAlbums.getAlbums());
			updateBackButtonEnabled(true);
			event.consume();
		});

		vBox.getChildren().addAll(image, name);
		return vBox;
	}

	//create a box for a playlist OR album
	private VBox generateHasSongsBox(HasSongs hasSongs) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));
		vBox.setSpacing(4);

		GridPane imageBox = new GridPane();

		List<Song> songs = hasSongs.getSongs();
		if (songs.size() > 4) {
			for (int i = 0; i < 4; i++) {
				ImageView im = new ImageView(songs.get(i).getAlbum().getPicture());
				im.setFitHeight(64);
				im.setFitWidth(64);
				imageBox.add(im, i < 2 ? 0 : 1, i % 2);
			}
		} else {
			ImageView im = new ImageView(songs.size() > 0 ? songs.get(0).getAlbum().getPicture() : "resources/images/error.png");
			im.setFitHeight(128);
			im.setFitWidth(128);
			imageBox.add(im, 0, 0);
		}

		Text name = new Text(hasSongs.toString());
		Text songCount = new Text(Integer.toString(hasSongs.getSongs().size()) + " Songs");
		songCount.setFill(greyText);

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			showSongsView(/*hasSongs.toString(),*/ hasSongs.getSongs());
			updateBackButtonEnabled(true);
			event.consume();
		});

		vBox.getChildren().addAll(imageBox, name, songCount);
		return vBox;
	}
}
