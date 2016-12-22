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
				libraryPane.getChildren().setAll(generateListOfHasSongs(Album.getAllFromDatabase()));
				break;
			case "Songs":
				libraryPane.getChildren().setAll(generateListOfSongs(Song.getAllFromDatabase()));
				break;
			case "Artists":
				libraryPane.getChildren().setAll(generateListOfHasAlbums(Artist.getAllFromDatabase()));
				break;
			case "Genres":
				libraryPane.getChildren().setAll(generateListOfHasAlbums(Genre.getAllFromDatabase()));
				break;
			case "Playlists":
				libraryPane.getChildren().setAll(generateListOfHasSongs(Playlist.getAllFromDatabase()));
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
		/* this is a broken because an arraylist<?> cannot polymorph
		//eh
		List<?>[] modelLists = new List<?>[5];
		modelLists[0] = Song.getAllFromDatabase();
		modelLists[1] = Album.getAllFromDatabase();
		modelLists[2] = Artist.getAllFromDatabase();
		modelLists[3] = Genre.getAllFromDatabase();
		modelLists[4] = Playlist.getAllFromDatabase();

		String[] modelNames = new String[modelLists.length];
		modelNames[0] = "songs";
		modelNames[1] = "";
		modelNames[2] = "";
		modelNames[3] = "";
		modelNames[4] = "";

		for (int i = 0; i < modelLists.length; i++) {
			VBox box = new VBox();
			boolean foundAResult = false;
			for (int j = 0; j < modelLists[i].size(); j++) {
				if (modelLists[i].get(j).toString().toLowerCase().contains(query.toLowerCase())) {
					box.getChildren().add(generateBox(modelLists[i].get(j)));
					foundAResult = true;
				}
			}
			Text text = new Text(foundAResult ? "Search results for '" + modelNames[i] + "' in songs:" : "No songs found for '" + modelNames[i] + "'.");
			searchResults.getChildren().addAll(text, box);//same
		}
		*/
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
	 * just calls generateBox for a list of Albums OR playlists
	 * and puts them into a Tile-Pane
	 */
	private TilePane generateListOfHasSongs(List<? extends HasSongs> hasSongsList) {
		TilePane tilePane = new TilePane();
		for (HasSongs hs : hasSongsList) tilePane.getChildren().addAll(generateBox(hs));
		return tilePane;
	}

	/**
	 * Create a tile view of Genres OR Artists
	 * just calls generateBox for a list of Genres OR Artists
	 * and puts them into a Tile-Pane
	 * TODO merge above??????????
	 */
	private TilePane generateListOfHasAlbums(List<? extends HasAlbums> hasAlbumsList) {
		TilePane tilePane = new TilePane();
		for (HasAlbums ha : hasAlbumsList) tilePane.getChildren().addAll(generateBox(ha));
		return tilePane;
	}

	/**
	 * Create a box for a song
	 */
	private HBox generateBox(Song song) {
		HBox hBox = new HBox();
		Button playButton = new Button(">");
		playButton.setOnAction(event -> Main.musicController.skipCurrentSongAndPlay(song));
		Button addButton = new Button("+");
		addButton.setOnAction(event -> Main.musicController.addToQueueEnd(song));
		Text songNumberText = new Text(song.getTrackNumber() + ". ");
		Text songNameText = new Text(song.toString() + " - ");
		Text artistNameText = new Text(song.getArtist().toString());
		Text featuresNamesText = new Text(song.getFeatures().size() != 0 ? " (ft. " + String.join(", ", song.getFeatures().stream().map((artist) -> artist.toString()).collect(Collectors.toList())) + ") - " : " - ");
		Text albumNameText = new Text(song.getAlbum().toString() + " - ");
		Text songLengthText = new Text(song.getLength());
		hBox.getChildren().addAll(playButton, addButton, songNumberText, songNameText, artistNameText, featuresNamesText, albumNameText, songLengthText);
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
	 * Create a box for an artist OR genre
	 */
	private VBox generateBox(HasAlbums hasAlbums) {
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
			Text text = new Text(hasAlbums.toString() + ":");
			libraryPane.getChildren().setAll(text, generateListOfHasSongs(hasAlbums.getAlbums()));

			updateBackButtonEnabled(true);
			event.consume();
		});

		vBox.getChildren().addAll(image, name);
		return vBox;
	}

	/**
	 * Create a box for a playlist OR album.
	 * TODO merge above????????
	 * OR rename all generateBox methods to be more specific AND remove meaningless polymorphism
	 */
	private VBox generateBox(HasSongs hasSongs) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));

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
			ImageView pic = new ImageView(songs.get(0).getAlbum().getPicture());
			pic.setFitHeight(192);
			pic.setFitWidth(192);
			Text type = new Text("Album");
			Text title = new Text(hasSongs.toString());
			Text desc = new Text(
				"By " + songs.get(0).getAlbum().getArtist() + " - "
			 	+ songs.get(0).getAlbum().getYear() + " - "
				+ songs.get(0).getAlbum().getNumberOfSongs() + " songs, " + songs.get(0).getAlbum().getLength()
			);
			Button playAll = new Button("Play all from " + hasSongs.toString());
			playAll.setOnAction(a -> Main.musicController.skipCurrentSongAndPlay(hasSongs));
			Button addAll = new Button("Add all from " + hasSongs.toString() + " to queue");

			VBox inPane = new VBox();
			HBox topDescription = new HBox();
			VBox sideDescription = new VBox();
			sideDescription.getChildren().setAll(type, title, desc, playAll, addAll);
			topDescription.getChildren().setAll(pic, sideDescription);
			inPane.getChildren().setAll(topDescription, generateListOfSongs(hasSongs.getSongs()));
			libraryPane.getChildren().setAll(inPane);

			updateBackButtonEnabled(true);
			event.consume();
		});

		vBox.getChildren().addAll(imageBox, name, songCount);
		return vBox;
	}
}
