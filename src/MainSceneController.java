import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class MainSceneController {

	final String SCENE_TITLE = "Music Player";
	final int MIN_HEIGHT = 400, MIN_WIDTH = 600;
	private Stage stage;
	private Scene scene;
	private final Color greyText = Color.rgb(130, 130, 130);

	@FXML private VBox libraryPane, queuePane;
	@FXML private ScrollPane scrollPane;
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
		this.scene = scene;
		stage.setScene(scene);
		stage.setTitle(SCENE_TITLE);
		stage.setMinHeight(MIN_WIDTH);
		stage.setMinWidth(MIN_HEIGHT);
		stage.getIcons().add(new Image("file:data/logo.png"));
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
		updateQueueArea();
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

	public void updateLibraryPane() {
		String viewString = viewsChoiceBox.getSelectionModel().getSelectedItem().toString();
		updateBackButtonEnabled(false);
		searchBox.clear();

		libraryPane.getChildren().setAll(generateHeader1("All " + viewString));

		switch (viewString) {
			case "Albums":
				libraryPane.getChildren().addAll(generateListOfAlbums(Album.getAllFromDatabase()));
				break;
			case "Songs":
				libraryPane.getChildren().addAll(generateListOfSongs(Song.getAllFromDatabase()));
				break;
			case "Artists":
				libraryPane.getChildren().addAll(generateListOfArtists(Artist.getAllFromDatabase()));
				break;
			case "Genres":
				libraryPane.getChildren().addAll(generateListOfGenres(Genre.getAllFromDatabase()));
				break;
			case "Playlists":
				Button newPlaylist = new Button("Create a new playlist");
				newPlaylist.setOnAction(f -> {
					TextInputDialog dialog = new TextInputDialog("");
					dialog.setTitle("New playlist");
					dialog.setHeaderText("Create a new playlist");
					dialog.setContentText("Enter the name for the new playlist");

					Optional<String> result;

					result = dialog.showAndWait();

					if (result.isPresent() && result.get().length() > 0) {
						Playlist.createNewPlaylist(result.get());
					}

					/*
					dialog.showAndWait().ifPresent(input -> {
						if (input.length() > 0) {
							Playlist.createNewPlaylist(input);
						}
					});
					*/
				});

				libraryPane.getChildren().addAll(newPlaylist, generateListOfPlaylists(Playlist.getAllFromDatabase()));
				break;
			default:
				System.out.println("- unknown viewsChoiceBox item");
				break;
		}
	}

	public void updateQueueArea() {
		//updateSongText
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

		timeElapsedtext.setText("0:00");

		//updateSongQueueContents
		List<Song> queue = Main.musicController.getQueue();
		if (Main.musicController.getQueue().size() > 0) {
			queue = queue.subList(1, Main.musicController.getQueue().size());
			VBox vBox = generateVBox1();
			queuePane.getChildren().setAll(vBox);
			for (Song songFromQueue : queue) {
				vBox.getChildren().addAll(generateSongBox(songFromQueue)); //TODO make a more specific method
			}
		}

		//updateCurrentSongPausedButton
		currentSongPauseButton.setText(Main.musicController.isPaused() ? ">" : "||");

		//updatePlayButtonEnabled
		currentSongPauseButton.setDisable(Main.musicController.getQueue().size() < 1);

		//updateSkipButtonEnabled
		currentSongSkipButton.setDisable(Main.musicController.getQueue().size() <= 1);

		//updateVisualiseButtonEnabled
		visualiseButton.setDisable(Main.musicController.getQueue().size() < 1);

		//updateClearQueueButtonEnabled
		clearQueueButton.setDisable(Main.musicController.getQueue().size() <= 1);
	}

	public void updateTimeElapsed() {
		if (Main.musicController.getCurrentSong() != null)
			timeElapsedtext.setText(Main.musicController.getTimeElapsed());
		else
			timeElapsedtext.setText("");
	}

	public void updatePlayModeButtonText(String text) {
		playModeButton.setText(text);
	}

	public void updateBackButtonEnabled(boolean enabled) {
		backButton.setDisable(!enabled);
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
		VBox searchResults = generateVBox3();
		libraryPane.getChildren().setAll(searchResults);

		{
			//Song
			VBox box = generateVBox1();
			boolean foundAResult = false;

			for (Song s : Song.getAllFromDatabase()) {
				if (s.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateSongBox(s));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in songs:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);
		} {
			//Albums
			VBox box = generateVBox2();
			boolean foundAResult = false;

			for (Album a : Album.getAllFromDatabase()) {
				if (a.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateAlbumBox(a));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in albums:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);
		} {
			//Artist
			VBox box = generateVBox2();
			boolean foundAResult = false;

			for (Artist a : Artist.getAllFromDatabase()) {
				if (a.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateArtistBox(a));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in artists:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);
		} {
			//Genre
			VBox box = generateVBox2();
			boolean foundAResult = false;

			for (Genre g : Genre.getAllFromDatabase()) {
				if (g.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateGenreBox(g));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + query + "' in genres:" : "No songs found for '" + query + "'.");
			searchResults.getChildren().addAll(text, box);
		} {
			//Playlist
			VBox box = generateVBox2();
			boolean foundAResult = false;

			for (Playlist p : Playlist.getAllFromDatabase()) {
				if (p.toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generatePlaylistBox(p));
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
		VBox vBox = generateVBox1();
		for (Song song : songList) vBox.getChildren().addAll(generateSongBox(song));
		return vBox;
	}

	/**
	 * Create a tile view of Albums OR playlists
	 * just calls generateBox for a list of Albums
	 * and puts them into a Tile-Pane
	 */
	private Pane generateListOfAlbums(List<Album> albumsList) {
		Pane tilePane = generatePane();
		for (Album a : albumsList) tilePane.getChildren().addAll(generateAlbumBox(a));
		return tilePane;
	}

	/**
	 * Create a tile view of Albums OR playlists
	 * just calls generateBox for a list of Playlists
	 * and puts them into a Tile-Pane
	 */
	private Pane generateListOfPlaylists(List<Playlist> playlistsList) {
		Pane pane = generatePane();
		for (Playlist p : playlistsList) pane.getChildren().addAll(generatePlaylistBox(p));
		return pane;
	}

	/**
	 * Create a tile view of Genres OR Artists
	 * just calls generateBox for a list of Genres
	 * and puts them into a Tile-Pane
	 */
	private Pane generateListOfGenres(List<Genre> genresList) {
		Pane pane = generatePane();
		for (Genre g : genresList) pane.getChildren().addAll(generateGenreBox(g));
		return pane;
	}

	/**
	 * Create a tile view of Genres OR Artists
	 * just calls generateBox for a list of Artists
	 * and puts them into a Tile-Pane
	 */
	private Pane generateListOfArtists(List<Artist> artistsList) {
		Pane pane = generatePane();
		for (Artist a : artistsList) pane.getChildren().addAll(generateArtistBox(a));
		return pane;
	}

	/**
	 * Create a box for a song.
	 */
	private HBox generateSongBox(Song song) {
		Button playButton = new Button(">");
		playButton.setOnAction(event -> Main.musicController.skipCurrentSongAndPlay(song));
		Button addButton = new Button("+");
		addButton.setOnAction(event -> Main.musicController.addToQueueEnd(song));

		HBox hBox = new HBox(2,
			playButton,
			addButton,
			new Text(song.getTrackNumber() + ". "),
			new Text(song.toString() + " - "),
			new Text(song.getArtist().toString()),
			new Text(song.getFeatures().size() != 0 ? " (ft. " + String.join(", ", song.getFeatures().stream().map((artist) -> artist.toString()).collect(Collectors.toList())) + ") - " : " - "),
			new Text(song.getAlbum().toString() + " - "),
			new Text(song.getLength())
		);

		MenuItem playNow = new MenuItem("Play now");
		playNow.setOnAction(e -> Main.musicController.skipCurrentSongAndPlay(song));
		MenuItem playNext = new MenuItem("Play next");
		playNext.setOnAction(e -> Main.musicController.addToQueueNext(song));
		MenuItem addToQueueEnd = new MenuItem("Add to queue end");
		addToQueueEnd.setOnAction(e -> Main.musicController.addToQueueEnd(song));
		MenuItem addToQueueNext = new MenuItem("Add to queue next");
		addToQueueNext.setOnAction(e -> Main.musicController.addToQueueNext(song));

		MenuItem addToPlaylist = new MenuItem("Add '" + song.toString() + "' to a playlist...");
		addToPlaylist.setOnAction(e -> {
			List<Playlist> all = Playlist.getAllFromDatabase();
			ChoiceDialog<Playlist> choosePlaylistDialog = new ChoiceDialog<Playlist>(all.get(0), all);
			choosePlaylistDialog.setTitle("Select Playlist");
			choosePlaylistDialog.setHeaderText("Choose a Playlist to add '" + song.toString() + "' to:");

			Optional<Playlist> playlistChosen = choosePlaylistDialog.showAndWait();
			if (playlistChosen.isPresent()) {
				playlistChosen.get().addSong(song);
			}
		});

		MenuItem goToArtist = new MenuItem("Go to artist '" + song.getArtist() + "'");
		goToArtist.setOnAction(e -> {
			libraryPane.getChildren().setAll(generateArtistView(song.getArtist()));
		});

		MenuItem goToAlbum = new MenuItem("Go to album '" + song.getAlbum() + "'");
		goToAlbum.setOnAction(e -> {
			libraryPane.getChildren().setAll(generateAlbumView(song.getAlbum()));
		});

		MenuItem remove = new MenuItem("Delete song '" + song.toString() + "'");
		remove.setOnAction(e -> {
			song.removeFromDatabase();
		});

		ContextMenu contextMenu = new ContextMenu(playNow, playNext, addToQueueNext, addToQueueEnd, addToPlaylist, goToArtist, goToAlbum, remove);

		hBox.setOnMouseClicked(event -> {
			contextMenu.show(hBox, event.getScreenX(), event.getScreenY());
		});

		return hBox;
	}

	/**
	 * Create a box for an artist.
	 */
	private VBox generateArtistBox(Artist artist) {
		//flatten arraylist of arraylists
		ArrayList<Song> songs = new ArrayList<Song>();
		for (Album a : artist.getAlbums()) {
			songs.addAll(a.getSongs());
		}
		
		VBox vBox = generateVBox2();
		vBox.getChildren().addAll(
			generateImage(songs),
			generateTitle(artist.toString()),
			generateDescription(artist.getAlbums().size() + " Albums")
		);

		vBox.setOnMouseClicked(event -> {
			libraryPane.getChildren().setAll(generateArtistView(artist));
			updateBackButtonEnabled(true);
			event.consume();
		});
		
		return vBox;
	}

	private VBox generateArtistView(Artist artist) {
		VBox vBox = generateVBox2();
		vBox.getChildren().addAll(
			generateHeader1("Artist"),
			generateHeader2(artist.toString() + ":"),
			generateListOfAlbums(artist.getAlbums())
		);
		return vBox;
	}

	/**
	 * Create a box for an genre
	 */
	private VBox generateGenreBox(Genre genre) {
		//flatten arraylist of arraylists
		ArrayList<Song> songs = new ArrayList<Song>();
		for (Album a : genre.getAlbums()) {
			songs.addAll(a.getSongs());
		}

		VBox vBox = generateVBox2();
		vBox.getChildren().addAll(
			generateImage(songs),
			generateTitle(genre.toString()),
			generateDescription(genre.getAlbums().size() + " Albums")
		);

		vBox.setOnMouseClicked(event -> {
			libraryPane.getChildren().setAll(generateGenreView(genre));
			updateBackButtonEnabled(true);
			event.consume();
		});
		
		return vBox;
	}

	private VBox generateGenreView(Genre genre) {
		VBox vBox = generateVBox2();
		vBox.getChildren().addAll(
			generateHeader2(genre.toString() + ":"),
			generateListOfAlbums(genre.getAlbums())
		);
		return vBox;
	}

	/**
	 * Create a box for a playlist.
	 */
	private VBox generatePlaylistBox(Playlist playlist) {
		VBox vBox = generateVBox2();
		vBox.getChildren().addAll(
			generateImage(playlist.getSongs()),
			generateTitle(playlist.toString()),
			generateDescription(Integer.toString(playlist.getSongs().size()) + " Songs")
		);

		vBox.setOnMouseClicked(event -> {
			libraryPane.getChildren().setAll(generatePlaylistView(playlist));
			updateBackButtonEnabled(true);
			event.consume();
		});

		vBox.setOnMouseEntered(event -> scene.setCursor(Cursor.HAND));
		vBox.setOnMouseExited(event -> scene.setCursor(Cursor.DEFAULT));

		return vBox;
	}

	/**
	 * Create a view for a playlist.
	 */
	private VBox generatePlaylistView(Playlist playlist) {
		Button playAll = new Button("Play all from " + playlist.toString());
		playAll.setOnAction(a -> Main.musicController.skipCurrentSongAndPlay(playlist));
		Button addAll = new Button("Add all from " + playlist.toString() + " to queue");
		addAll.setOnAction(a -> Main.musicController.addToQueueEnd(playlist));
		Button delete = new Button("Delete playlist");
		delete.setOnAction(a -> playlist.removeFromDatabase());

		VBox vBox = generateVBox2();

		VBox info = generateVBox2();
		info.getChildren().addAll(
			generateHeader1("Playlist"),
			generateHeader2(playlist.toString()),
			generateHeader3(playlist.getNumberOfSongs() + " songs, " + playlist.getLength()),
			playAll,
			addAll,
			delete
		);

		vBox.getChildren().addAll(
			new HBox(6, generateImage(playlist.getSongs()), info),
			generateListOfSongs(playlist.getSongs()) //list of songs
		);

		return vBox;
	}

	/**
	 * Create a box for an album.
	 */
	private VBox generateAlbumBox(Album album) {
		VBox vBox = generateVBox2();
		vBox.getChildren().addAll(
			generateImage(album.getSongs()),
			generateTitle(album.toString()),
			generateDescription(album.getArtist().toString())
		);

		vBox.setOnMouseClicked(event -> {
			libraryPane.getChildren().setAll(generateAlbumView(album));
			updateBackButtonEnabled(true);
			event.consume();
		});

		vBox.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> scene.setCursor(Cursor.HAND));
		vBox.addEventHandler(MouseEvent.MOUSE_EXITED, event -> scene.setCursor(Cursor.DEFAULT));

		return vBox;
	}

	/**
	 * Create a view for an album.
	 */
	private VBox generateAlbumView(Album album) {
		Button playAll = new Button("Play all from " + album.toString());
		playAll.setOnAction(a -> Main.musicController.skipCurrentSongAndPlay(album));
		Button addAll = new Button("Add all from " + album.toString() + " to queue");
		addAll.setOnAction(a -> Main.musicController.addToQueueEnd(album));

		VBox vBox = generateVBox2();
		VBox info = generateVBox2();
		info.getChildren().addAll(
			generateHeader1("Album"),
			generateHeader2(album.toString()),
			generateHeader3("By " + album.getArtist() + " - " + album.getYear() + " - " + album.getNumberOfSongs() + " songs, " + album.getLength()),
			playAll,
			addAll
		);

		vBox.getChildren().addAll(
			new HBox(6, generateImage(album.getSongs()), info),
			generateListOfSongs(album.getSongs()) //list of songs
		);

		return vBox;
	}

	/**
	 * Text style generators.
	 */
	private Text generateHeader1(String s) {
		Text text = new Text(s.toUpperCase());
		text.setFont(new Font(11));
		return text;
	}

	private Text generateHeader2(String s) {
		Text text = new Text(s);
		text.setFont(new Font(20));
		return text;
	}

	private Text generateHeader3(String s) {
		Text text = new Text(s);
		text.setFont(new Font(9));
		return text;
	}

	private Text generateTitle(String s) {
		Text text = new Text(s);
		text.setFont(new Font(12));
		return text;
	}

	private Text generateDescription(String s) {
		Text text = new Text(s);
		text.setFont(new Font(12));
		text.setFill(greyText);
		return text;
	}

	private VBox generateVBox1() { return new VBox(3); }
	private VBox generateVBox2() { return new VBox(6); }
	private VBox generateVBox3() { return new VBox(8); }

	private Pane generatePane() {

		//TODO fix this

		TilePane tilePane = new TilePane(24, 24);

		tilePane.setMinWidth(0);
		tilePane.setPrefWidth(scrollPane.getWidth());
		//tilePane.setMaxWidth(2380);
		return tilePane;
	}

	private GridPane generateImage(List<Song> songs) {
		GridPane imageGrid = new GridPane();

		HashSet<String> imagesSet = new HashSet<String>();
		for (Song s : songs) {
			imagesSet.add(s.getAlbum().getPicture());
		}
		String[] images = imagesSet.toArray(new String[imagesSet.size()]);

		Color albumColor = Color.BLACK;
		if (images.length > 0) {
			Image i = new Image(images[0]);
			albumColor = i.getPixelReader().getColor((int)i.getWidth()/4, (int)i.getHeight()/4);
		}
		DropShadow dropShadow = new DropShadow(BlurType.THREE_PASS_BOX, albumColor, 20, 0.0, 0, 0);
		imageGrid.setEffect(dropShadow);

		if (images.length >= 4) {
			for (int i = 0; i < 4; i++) {
				ImageView im = new ImageView(images[i]);
				im.setFitHeight(64);
				im.setFitWidth(64);
				im.setSmooth(true);
				imageGrid.add(im, i < 2 ? 0 : 1, i % 2);
			}
		} else if (images.length > 1) {
			for (int i = 0; i < 4; i++) {
				ImageView im = new ImageView(images[i % 2 == 0 ? 0 : 1]);
				im.setFitHeight(64);
				im.setFitWidth(64);
				im.setSmooth(true);
				int y = i < 2 ? 0 : 1;
				int x = y > 0 ? (i % 2 == 0 ? 0 : 1) : (i % 2 == 0 ? 1 : 0);

				imageGrid.add(im, x, y);
			}
		} else {
			ImageView im = new ImageView(images.length > 0 ? images[0] : "data/error.png");
			im.setFitHeight(128);
			im.setFitWidth(128);
			im.setSmooth(true);
			imageGrid.add(im, 0, 0);
		}
		return imageGrid;
	}
}
