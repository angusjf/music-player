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
import javafx.scene.paint.Color;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Font;
import java.util.HashSet;
import javafx.stage.Popup;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;

public class MainSceneController {

	final String SCENE_TITLE = "Music Player";
	final int MIN_HEIGHT = 400, MIN_WIDTH = 600;
	private Stage stage;
	private final Color greyText = Color.rgb(130, 130, 130);

	@FXML private VBox libraryPane;
	@FXML private AnchorPane queuePane;
	@FXML private Button backButton, playModeButton, currentSongPauseButton, currentSongSkipButton, visualiseButton, clearQueueButton;
	@FXML private Text albumNameText, artistNameText, featuresNamesText, songLengthText, songNameText, timeElapsedtext;
	@FXML private TextField searchBox;
	@FXML private ChoiceBox viewsChoiceBox;
	@FXML private Slider songProgressBar;

	public MainSceneController () { }

	/**
	 * This method is called once by Main, before initialize.
	 * It serves two functions:
	 *  - sets up the stage and stage events
	 *  - acts as a setter for stage
	 */
	public void setupStage(Stage stage, Scene scene) {
		this.stage = stage;
		stage.setScene(scene);
		stage.setTitle(SCENE_TITLE);
		stage.setMinHeight(MIN_WIDTH);
		stage.setMinWidth(MIN_HEIGHT);
		stage.getIcons().add(new Image("file:resources/logo.png"));
		scene.getStylesheets().add("data/stylesheet.css");
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
			assert viewsChoiceBox != null : "- viewsChoiceBox is null";
			assert songNameText != null : "- songNameText is null";
			assert artistNameText != null : "- artistNameText is null";
			assert featuresNamesText != null : "- featuresNamesText is null";
			assert albumNameText != null : "- albumNameText is null";
			assert timeElapsedtext != null : "- timeElapsedtext is null";
			assert songProgressBar != null : "- songProgressBar is null";
			assert songLengthText != null : "- songLengthText is null";
			assert currentSongPauseButton != null : "- currentSongPauseButton is null";
			assert libraryPane != null : "- libraryPane is null";
			assert queuePane != null : "- QueuePane is null";
			assert searchBox != null : "- searchBox is null";
		} catch (AssertionError ae) {
			System.out.println("FXML assertion failure: " + ae.getMessage());
			Main.terminate();
		}

		// set up viewsChoiceBox
		viewsChoiceBox.getItems().setAll("Albums", "Songs", "Artists", "Genres", "Playlists");
		viewsChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue ov, String t, String t1) {
				updateBackButtonEnabled(false);
				updateLibraryPane();
			}
		});
		viewsChoiceBox.getSelectionModel().selectFirst();

		updateSongText();
	}

	public void showPopup(String message) {
		Popup popup = new Popup();
		popup.getContent().addAll(new Button(message));
		popup.show(new Stage(), 0, 0);
	}

	/*========================================
	||                                      ||
	||                                      ||
	||     EVERYTHING PAST THIS BOX IS      ||
	||      CODE FOR *_UPDATING UI_*        ||
	||                                      ||
	||                                      ||
	========================================*/

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
				vBox.getChildren().addAll(generateBox(song)); //TODO make a more specific method
			}
		}
	}

	public void updateLibraryPane() {
		String viewString = viewsChoiceBox.getSelectionModel().getSelectedItem().toString();
		updateBackButtonEnabled(false);
		searchBox.clear();

		switch (viewString) {
			case "Albums":
				libraryPane.getChildren().setAll(generateListOfAlbums(Album.getAllFromDatabase()));
				break;
			case "Songs":
				libraryPane.getChildren().setAll(generateListOfSongs(Song.getAllFromDatabase()));
				break;
			case "Artists":
				libraryPane.getChildren().setAll(generateListOfArtists(Artist.getAllFromDatabase()));
				break;
			case "Genres":
				libraryPane.getChildren().setAll(generateListOfGenres(Genre.getAllFromDatabase()));
				break;
			case "Playlists":
				libraryPane.getChildren().setAll(generateListOfPlaylists(Playlist.getAllFromDatabase()));
				break;
			default:
				System.out.println("- unknown viewsChoiceBox item");
				break;
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

	/*========================================
	||                                      ||
	||                                      ||
	||    EVERYTHING PAST THIS BOX IS       ||
	||     CODE FOR *_ON UI ACTIONS_*       ||
	||                                      ||
	||                                      ||
	========================================*/

	@FXML void onBackButtonPressed() {
		showPopup("oh no");
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
		String query = searchBox.getText();
		if (query.equals("")) return;
		updateBackButtonEnabled(true);
		VBox searchResults = new VBox();
		libraryPane.getChildren().setAll(searchResults);

		{
			//Song
			VBox box = new VBox();
			boolean foundAResult = false;

			for (Song s : Song.getAllFromDatabase()) {
				if (s.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateBox(s));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in songs:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);

		}

		{
			//Albums
			VBox box = new VBox();
			boolean foundAResult = false;

			for (Album a : Album.getAllFromDatabase()) {
				if (a.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateBox(a));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in albums:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);

		}

		{
			//Artist
			VBox box = new VBox();
			boolean foundAResult = false;

			for (Artist a : Artist.getAllFromDatabase()) {
				if (a.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateBox(a));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in artists:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);

		}

		{
			//Genre
			VBox box = new VBox();
			boolean foundAResult = false;

			for (Genre g : Genre.getAllFromDatabase()) {
				if (g.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateBox(g));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in genres:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);

		}

		{
			//Playlist
			VBox box = new VBox();
			boolean foundAResult = false;

			for (Playlist p : Playlist.getAllFromDatabase()) {
				if (p.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateBox(p));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in playlists:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);
		}
	}

	@FXML void onNewFileClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose a Music File");
		List<File> list = fileChooser.showOpenMultipleDialog(stage);

		if (list != null) {
			for (File file : list) new SongFileImporter(file);
		}
	}

	@FXML void onClearQueuePressed() {
		Main.musicController.clearQueue();
	}

	/*========================================
	||                                      ||
	||                                      ||
	||    EVERYTHING PAST THIS BOX IS       ||
	||      CODE FOR *_GENERATORS_*         ||
	||                                      ||
	||                                      ||
	========================================*/

	/**
	 * Create a list view of songs
	 * just calls generateBox for a list of songs
	 * and puts them into a V-Box
	 */
	private VBox generateListOfSongs(List<Song> songList) {
		VBox vBox = new VBox();
		for (Song song : songList) vBox.getChildren().addAll(generateBox(song));
		return vBox;
	}

	/**
	 * Create a tile view of Albums OR playlists
	 * just calls generateBox for a list of Albums
	 * and puts them into a Tile-Pane
	 */
	private TilePane generateListOfAlbums(List<Album> albumsList) {
		TilePane tilePane = new TilePane(10, 10);
		tilePane.setMinWidth(640);
		tilePane.setStyle("-fx-background-color: #FFFFFF;");
		for (Album a : albumsList) tilePane.getChildren().addAll(generateBox(a));
		return tilePane;
	}

	/**
	 * Create a tile view of Albums OR playlists
	 * just calls generateBox for a list of Playlists
	 * and puts them into a Tile-Pane
	 */
	private TilePane generateListOfPlaylists(List<Playlist> playlistsList) {
		TilePane tilePane = new TilePane(10, 10);
		for (Playlist p : playlistsList) tilePane.getChildren().addAll(generateBox(p));
		return tilePane;
	}

	/**
	 * Create a tile view of Genres OR Artists
	 * just calls generateBox for a list of Genres
	 * and puts them into a Tile-Pane
	 */
	private TilePane generateListOfGenres(List<Genre> genresList) {
		TilePane tilePane = new TilePane();
		for (Genre g : genresList) tilePane.getChildren().addAll(generateBox(g));
		return tilePane;
	}

	/**
	 * Create a tile view of Genres OR Artists
	 * just calls generateBox for a list of Artists
	 * and puts them into a Tile-Pane
	 */
	private TilePane generateListOfArtists(List<Artist> artistsList) {
		TilePane tilePane = new TilePane();
		for (Artist a : artistsList) tilePane.getChildren().addAll(generateBox(a));
		return tilePane;
	}

	/**
	 * Create a box for a song.
	 */
	private HBox generateBox(Song song) {
		final HBox hBox = new HBox(2);
		final Button playButton = new Button(">");
		playButton.setOnAction(event -> Main.musicController.skipCurrentSongAndPlay(song));
		final Button addButton = new Button("+");
		addButton.setOnAction(event -> Main.musicController.addToQueueEnd(song));
		hBox.getChildren().addAll(
			playButton,
			addButton,
			new Text(song.getTrackNumber() + ". "),
			new Text(song.toString() + " - "),
			new Text(song.getArtist().toString()),
			new Text(song.getFeatures().size() != 0 ? " (ft. " + String.join(", ", song.getFeatures().stream().map((artist) -> artist.toString()).collect(Collectors.toList())) + ") - " : " - "),
			new Text(song.getAlbum().toString() + " - "),
			new Text(song.getLength())
		);
		// MOUSE_ENTERED
		hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

			final ContextMenu contextMenu = new ContextMenu();
			contextMenu.setOnShowing(e -> {
				System.out.println("showing");
			});

			MenuItem remove = new MenuItem("Remove");
			remove.setOnAction(e -> {
				System.out.println("removing " + song);
			});

			contextMenu.getItems().setAll(remove);

			event.consume();
		});
		return hBox;
	}

	/**
	 * Create a box for an artist.
	 */
	private VBox generateBox(Artist hasAlbums) {
		VBox vBox = new VBox();

		Text name = new Text(hasAlbums.toString());
		Text albumCount = new Text(hasAlbums.getAlbums().size() + " Albums");
		albumCount.setFill(greyText);

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			Text text = new Text(hasAlbums.toString() + ":");
			libraryPane.getChildren().setAll(text, generateListOfAlbums(hasAlbums.getAlbums()));

			updateBackButtonEnabled(true);
			event.consume();
		});
		
		ArrayList<Song> songs = new ArrayList<Song>();
		for (Album a : hasAlbums.getAlbums()) {
			songs.addAll(a.getSongs());
		}
		vBox.getChildren().addAll(generateImage(songs), name);
		return vBox;
	}

	/**
	 * Create a box for an genre
	 */
	private VBox generateBox(Genre hasAlbums) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));
		vBox.setSpacing(4);

		Text name = new Text(hasAlbums.toString());
		Text albumCount = new Text(hasAlbums.getAlbums().size() + " Albums");
		albumCount.setFill(greyText);

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			Text text = new Text(hasAlbums.toString() + ":");
			libraryPane.getChildren().setAll(text, generateListOfAlbums(hasAlbums.getAlbums()));

			updateBackButtonEnabled(true);
			event.consume();
		});
		
		ArrayList<Song> songs = new ArrayList<Song>();
		for (Album a : hasAlbums.getAlbums()) {
			songs.addAll(a.getSongs());
		}
		vBox.getChildren().addAll(generateImage(songs), name);
		return vBox;
	}

	/**
	 * Create a box for a playlist.
	 */
	private VBox generateBox(Playlist hasSongs) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));

		Text name = new Text(hasSongs.toString());
		Text songCount = new Text(Integer.toString(hasSongs.getSongs().size()) + " Songs");
		songCount.setFill(greyText);

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {/*
			VBox inPane = new VBox(); //ALL
			HBox topDescription = new HBox(); //TOP
			VBox sideDescription = new VBox(); //TOP RIGHT

			Text type = generateHeader1("Playlist");
			Text title = generateHeader2(hasSongs.toString());
			Text desc = new Text(
				"By " + songs.get(0).getAlbum().getArtist() + " - "
				+ songs.get(0).getAlbum().getYear() + " - "
				+ songs.get(0).getAlbum().getNumberOfSongs() + " songs, " + songs.get(0).getAlbum().getLength()
			);
			Button playAll = new Button("Play all from " + hasSongs.toString());
			playAll.setOnAction(a -> Main.musicController.skipCurrentSongAndPlay(hasSongs));
			Button addAll = new Button("Add all from " + hasSongs.toString() + " to queue");
			sideDescription.getChildren().setAll(type, title, desc, playAll, addAll);

			topDescription.getChildren().setAll(generateImage(hasSongs.getSongs()), sideDescription);
			inPane.getChildren().setAll(topDescription, generateListOfSongs(hasSongs.getSongs()));
			libraryPane.getChildren().setAll(inPane);

			updateBackButtonEnabled(true);*/
			event.consume();
		});

		vBox.getChildren().addAll(generateImage(hasSongs.getSongs()), name, songCount);
		return vBox;
	}

	/**
	 * Create a box for an album.
	 */
	private VBox generateBox(Album hasSongs) {
		VBox vBox = new VBox(8);
		//vBox.setPadding(new Insets(4));

		Text name = new Text(hasSongs.toString());
		Text songCount = new Text(Integer.toString(hasSongs.getSongs().size()) + " Songs");
		songCount.setFill(greyText);

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			VBox inPane = new VBox(); //ALL
			HBox topDescription = new HBox(); //TOP
			VBox sideDescription = new VBox(); //TOP RIGHT

			Album album = (Album)hasSongs;
			Text type = generateHeader1("Album");
			Text title = generateHeader2(album.toString());
			Text desc = new Text( "By " + album.getArtist() + " - " + album.getYear() + " - "
				+ album.getNumberOfSongs() + " songs, " + album.getLength());
			Button playAll = new Button("Play all from " + hasSongs.toString());
			playAll.setOnAction(a -> Main.musicController.skipCurrentSongAndPlay(hasSongs));
			Button addAll = new Button("Add all from " + hasSongs.toString() + " to queue");
			sideDescription.getChildren().setAll(type, title, desc, playAll, addAll);

			topDescription.getChildren().setAll(generateImage(hasSongs.getSongs()), sideDescription);
			inPane.getChildren().setAll(topDescription, generateListOfSongs(hasSongs.getSongs()));
			libraryPane.getChildren().setAll(inPane);

			updateBackButtonEnabled(true);
			event.consume();
		});

		vBox.getChildren().addAll(generateImage(hasSongs.getSongs()), name, songCount);
		return vBox;
	}

	private Text generateHeader1(String s) {
		Text text = new Text(s);
		text.setFont(new Font(20));
		return text;
	}

	private Text generateHeader2(String s) {
		Text text = new Text(s);
		text.setFont(new Font(16));
		return text;
	}

	private GridPane generateImage(List<Song> songs) {
		GridPane imageGrid = new GridPane();

		HashSet<String> images = new HashSet<String>();
		for (Song s : songs) {
			images.add(s.getAlbum().getPicture());
		}

		Color albumColor = Color.BLACK;
		//super hacky
		for (String s : images) {
			Image i = new Image(s);
			albumColor = i.getPixelReader().getColor((int)i.getWidth()/4, (int)i.getHeight()/4);
			break;
		}
		DropShadow dropShadow = new DropShadow(BlurType.THREE_PASS_BOX, albumColor, 20, 0.0, 0, 0);
		imageGrid.setEffect(dropShadow);

		if (images.size() > 4) {
			for (int i = 0; i < 4; i++) {
				ImageView im = new ImageView(images.iterator().next());
				im.setFitHeight(64);
				im.setFitWidth(64);
				im.setSmooth(true);
				imageGrid.add(im, i < 2 ? 0 : 1, i % 2);
			}
		} else {
			ImageView im = new ImageView(images.size() > 0 ? images.iterator().next() : "resources/images/error.png");
			im.setFitHeight(128);
			im.setFitWidth(128);
			im.setSmooth(true);
			imageGrid.add(im, 0, 0);
		}
		return imageGrid;
	}
}
