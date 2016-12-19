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
			assert songNameText != null		: "- songNameText is null";
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
		/*
		switch (viewsChoiceBox.getSelectionModel().getSelectedItem().toString()) {
			case "Albums": showAlbumsView(); break;
			case "Songs": showSongsView(); break;
			case "Artists": showArtistsView(); break;
			case "Genres": showGenresView(); break;
			case "Playlists": showPlaylistsView(); break;
			default: System.out.println("- went 'back' past the last view??"); showAlbumsView(); break;
		}*/
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
				}
			}
			addSongsList(songResults, songsBox.getChildren());
			searchResults.getChildren().add(songsBox);
		}

		//ALBUMS
		{
			VBox albumsBox = new VBox();
			ArrayList<Album> albumResults = new ArrayList<>();
			for (Album a : Album.getAllFromDatabase()) {
				if (a.toString().toLowerCase().contains(query.toLowerCase())) {
					albumResults.add(a);
				}
			}
			addHasSongsList(albumResults, albumsBox.getChildren());
			searchResults.getChildren().add(albumsBox);
		}

		//ARTISTS
		{
			VBox artistsBox = new VBox();
			ArrayList<Artist> artistResults = new ArrayList<>();
			for (Artist a : Artist.getAllFromDatabase()) {
				if (a.toString().toLowerCase().contains(query.toLowerCase())) {
					artistResults.add(a);
				}
			}
			addHasAlbumsList(artistResults, artistsBox.getChildren());
			searchResults.getChildren().add(artistsBox);
		}

		//GENRES
		{
			VBox genresBox = new VBox();
			ArrayList<Genre> genreResults = new ArrayList<>();
			for (Genre g : Genre.getAllFromDatabase()) {
				if (g.toString().toLowerCase().contains(query.toLowerCase())) {
					genreResults.add(g);
				}
			}
			addHasAlbumsList(genreResults, genresBox.getChildren());
			searchResults.getChildren().add(genresBox);
		}

		//PLAYLITS
		{
			VBox playlistsBox = new VBox();
			ArrayList<Playlist> playlistResults = new ArrayList<>();
			for (Playlist p : Playlist.getAllFromDatabase()) {
				if (p.toString().toLowerCase().contains(query.toLowerCase())) {
					playlistResults.add(p);
				}
			}
			addHasSongsList(playlistResults, playlistsBox.getChildren());
			searchResults.getChildren().add(playlistsBox);
		}
	}

	private void showSongsView(List<Song> songs) {
		libraryPane.getChildren().clear();
		if (viewsChoiceBox.getSelectionModel().getSelectedItem().toString() != "Songs") updateBackButtonEnabled(true);
		VBox vBox = new VBox();
		libraryPane.getChildren().addAll(vBox);
		//if (songs.size() == 0) container.addAll(new Text("No songs found!"));
		//vBox.getChildren().addAll(new Text(header + ":"));
		addSongsList(songs, vBox.getChildren());
	}

	private void showHasSongsView(List<? extends HasSongs> hasSongs) {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		//if (albums.size() == 0) container.addAll(new Text("No songs found!"));
		libraryPane.getChildren().addAll(tilePane);
		addHasSongsList(hasSongs, tilePane.getChildren());
	}

	private void showHasAlbumsView(List<? extends HasAlbums> hasAlbums) {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		//if (hasAlbums.size() == 0) container.addAll(new Text("No songs found!"));
		libraryPane.getChildren().addAll(tilePane);
		addHasAlbumsList(hasAlbums, tilePane.getChildren());
	}

	private void addSongsList(List<Song> songList, ObservableList<Node> container) {
		for (Song song : songList) container.addAll(generateSongBox(song));
	}

	private void addHasSongsList(List<? extends HasSongs> hasSongsList, ObservableList<Node> container) {
		for (HasSongs hs : hasSongsList) container.addAll(generateHasSongsBox(hs));
	}

	private void addHasAlbumsList(List<? extends HasAlbums> hasAlbumsList, ObservableList<Node> container) {
		for (HasAlbums ha : hasAlbumsList) container.addAll(generateHasAlbumsBox(ha));
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
			event.consume();
		});

		vBox.getChildren().addAll(imageBox, name, songCount);
		return vBox;
	}
}
