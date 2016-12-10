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
import javafx.stage.WindowEvent;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import java.util.Stack;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class MainSceneController {

	final String SCENE_TITLE = "Music Player";
	final int MIN_HEIGHT = 400, MIN_WIDTH = 600;
	private Stage stage;

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

		// secure close button
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				Main.terminate();
				we.consume();
			}
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
		viewsChoiceBox.getSelectionModel().selectedItemProperty().addListener(
			new ChangeListener<String>() {
				@Override public void changed(ObservableValue ov, String t, String t1) {                
					fillLibraryPane();
				}    
			}
		);
		viewsChoiceBox.getSelectionModel().selectFirst();

		//TODO VVVVVVVVV
		updateSongText();//TODO make this update automatically

		//timeElapsedtext.textProperty().bind(Main.musicController.getTimeElapsed()); 
		//songProgressBar.  Main.musicController.setSeek(M

		updateVisualiseButtonEnabled(true);
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
		List<Song> queue = Main.musicController.getQueue().subList(1, Main.musicController.getQueue().size());
		VBox vBox = new VBox();
		queuePane.getChildren().setAll(vBox);
		for (Song song : queue) {
			vBox.getChildren().addAll(generateSongBox(song));
		}
		updateClearQueueButtonEnabled(queue.size() > 0);
		updateSkipButtonEnabled(queue.size() > 0);
	}

	public void fillLibraryPane() {
		String viewString = viewsChoiceBox.getSelectionModel().getSelectedItem().toString();
		switch (viewString) {
			case "Albums":
				showAlbumsView();
				break;
			case "Songs":
				showSongsView();
				break;
			case "Artists":
				showArtistsView();
				break;
			case "Genres":
				showGenresView();
				break;
			case "Playlists":
				showPlaylistsView();
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

	/*
	 * ON UI ACTION METHODS
	 */

	@FXML void onBackButtonPressed() {
		updateBackButtonEnabled(false);
		switch (viewsChoiceBox.getSelectionModel().getSelectedItem().toString()) {
			case "Albums":
				showAlbumsView();
				break;
			case "Songs":
				break;
			case "Artists":
				showArtistsView();
				break;
			case "Genres":
				showGenresView();
				break;
			case "Playlists":
				showPlaylistsView();
				break;
			default:
				System.out.println("- went 'back' past the last view??");
				showAlbumsView();
				break;
		}
	}

	@FXML void cyclePlayModeClicked() {
		Main.musicController.cyclePlayMode();
	}

	@FXML void currentSongPauseClicked() {
		Main.musicController.togglePaused();
	}

	@FXML void currentSongSkipClicked() {
		Main.musicController.nextSong();
	}

	@FXML void openVisualiserClicked() {
		Main.openVisualiser();
	}

	@FXML void onSearch() {
		showSearchResultsView(searchBox.getText());
	}

	@FXML void newFileClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose a Music File");
		List<File> list = fileChooser.showOpenMultipleDialog(stage);
		if (list != null) {
			for (File file : list) {
				Song song = new Song(file);
			}
		}

		System.out.println(">new file(s) added");
		fillLibraryPane();
	}

	@FXML void clearQueuePressed() {
		Main.musicController.clearQueue();
	}

	/*
	 * BEHIND THE SCENES
	 */

	void showSearchResultsView(String query) {
		//basically just turns a query into an arraylist for showXview
		switch (viewsChoiceBox.getSelectionModel().getSelectedItem().toString()) {
			case "Albums":
				showAlbumsView(Album.getAllFromDatabase().stream().filter(s -> s.toString().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList()));
				break;
			case "Songs":
				showSongsView("Search Results for query '" + query + "'", Song.getAllFromDatabase().stream().filter(s -> s.toString().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList()));
				break;
			case "Artists":
				showArtistsView(Artist.getAllFromDatabase().stream().filter(s -> s.toString().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList()));
				break;
			case "Genres":
				showGenresView(Genre.getAllFromDatabase().stream().filter(s -> s.toString().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList()));
				break;
			case "Playlists":
				showPlaylistsView(Playlist.getAllFromDatabase().stream().filter(s -> s.toString().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList()));
				break;
			default:
				System.out.println("- !!!! SOMETHING WENT VERY WRONG IN THE SEARCH");
				break;
		}
	}

	void showAlbumsView() {
		showAlbumsView(Album.getAllFromDatabase());
	}

	void showAlbumsView(List<Album> albums) {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);

		for (Album album : albums) {
			tilePane.getChildren().addAll(generateAlbumBox(album));
		}
	}

	void showSongsView() {
		showSongsView("All Songs", Song.getAllFromDatabase());
	}

	void showSongsView(String header, List<Song> songs) { // the 'real' method
		libraryPane.getChildren().clear();
		VBox vBox = new VBox();
		libraryPane.getChildren().addAll(vBox);
		vBox.getChildren().addAll(new Text(header + ":"));
		if (songs.size() == 0) {
			vBox.getChildren().addAll(new Text("No songs found!"));
		}
		for (Song song : songs) {
			vBox.getChildren().addAll(generateSongBox(song));
		}
	}

	void showArtistsView() {
		showArtistsView(Artist.getAllFromDatabase());
	}

	void showArtistsView(List<Artist> artists) {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);

		for (Artist artist : artists) {
			tilePane.getChildren().addAll(generateArtistBox(artist));
		}
	}

	void showGenresView() {
		showGenresView(Genre.getAllFromDatabase());
	}

	void showGenresView(List<Genre> genres) {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);

		for (Genre genre : genres) {
			tilePane.getChildren().addAll(generateGenreBox(genre));
		}
	}

	void showPlaylistsView() {
		showPlaylistsView(Playlist.getAllFromDatabase());
	}

	void showPlaylistsView(List<Playlist> playlists) {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);

		for (Playlist playlist : playlists) {
			tilePane.getChildren().addAll(generatePlaylistBox(playlist));
		}
	}

	/*
	 * BOX GENERATORS
	 */

	HBox generateSongBox(Song song) {
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

	VBox generateAlbumBox(Album album) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));
		vBox.setSpacing(4);

		System.out.println(album.getPicture());
		ImageView image = new ImageView(album.getPicture());
		image.setFitHeight(142);
		image.setFitWidth(142);
		Text name = new Text(album.toString());
		Text artist = new Text(album.getArtist().toString());
		//artist.setFill(Color.GREY); TODO

		//album.setOnAction((ActionEvent ae) -> showSongsView(album.getSongs()));
		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				showSongsView(album.toString(), album.getSongs());
				event.consume();
			}
		});

		vBox.getChildren().addAll(image, name, artist);
		return vBox;
	}

	VBox generateArtistBox(Artist artist) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));
		vBox.setSpacing(4);

		ImageView image = new ImageView(artist.getPicture());
		image.setFitHeight(142);
		image.setFitWidth(142);
		Text name = new Text(artist.toString());

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				//TODO could this be better?
				List<Song> allSongs = new ArrayList<>();
				for (Album a : artist.getAlbums()) {
					allSongs.addAll(a.getSongs());
				}
				showSongsView(artist.toString(), allSongs);
				event.consume();
			}
		});

		vBox.getChildren().addAll(image, name);
		return vBox;
	}

	VBox generateGenreBox(Genre genre) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));
		vBox.setSpacing(4);

		TilePane imageBox = new TilePane();
		imageBox.setPrefTileHeight(64);
		imageBox.setPrefTileWidth(64);

		for (int i = 0; i < Math.min(4, genre.getSongs().size()); i++) {
			ImageView im = new ImageView(genre.getSongs().get(i).getAlbum().getPicture());
			im.setFitHeight(64);
			im.setFitWidth(64);
			imageBox.getChildren().addAll(im);
		}

		Text name = new Text(genre.toString());
		Text soungCount = new Text(Integer.toString(genre.getSongs().size()) + " songs");
		//artist.setFill(Color.GREY); TODO

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				showSongsView(genre.toString(), genre.getSongs());
				event.consume();
			}
		});

		vBox.getChildren().addAll(imageBox, name, soungCount);
		return vBox;
	}

	VBox generatePlaylistBox(Playlist playlist) {
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(4));
		vBox.setSpacing(4);

		TilePane imageBox = new TilePane();
		imageBox.setPrefTileHeight(64);
		imageBox.setPrefTileWidth(64);

		for (int i = 0; i < Math.min(4, playlist.getSongs().size()); i++) {
			ImageView im = new ImageView(playlist.getSongs().get(i).getAlbum().getPicture());
			im.setFitHeight(64);
			im.setFitWidth(64);
			imageBox.getChildren().addAll(im);
		}

		Text name = new Text(playlist.toString());
		Text soungCount = new Text(Integer.toString(playlist.getSongs().size()) + " songs");
		//artist.setFill(Color.GREY); TODO

		vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				showSongsView(playlist.toString(), playlist.getSongs());
				event.consume();
			}
		});
		vBox.getChildren().addAll(imageBox, name, soungCount);
		return vBox;
	}

}
