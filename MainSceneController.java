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

public class MainSceneController {

	final String SCENE_TITLE = "Music Player";
	final int MIN_HEIGHT = 400, MIN_WIDTH = 600;
	private Stage stage;

	@FXML private ChoiceBox viewsChoiceBox;
	@FXML private Text songNameText;
	@FXML private Text artistNameText;
	@FXML private Text featuresNamesText;
	@FXML private Text albumNameText;
	@FXML private Text timeElapsedtext;
	@FXML private Slider songProgressBar;
	@FXML private Text songLengthText;
	@FXML private Button currentSongPauseButton;
	@FXML private AnchorPane libraryPane;
	@FXML private AnchorPane queuePane;
	@FXML private TextField searchBox;

	public MainSceneController () {
		System.out.println("+ Main Scene Controller class started");
		// add first album to queue
		Main.musicController.addToQueueEnd(Album.getAllFromDatabase().get(0));
	}

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
		viewsChoiceBox.getSelectionModel().selectFirst();

		//TODO VVVVVVVVV
		//updateSongText(Main.musicController.getCurrentSong());//TODO make this update automatically
		//updateTimeElapsed();//TODO same

		fillLibraryPane();
	}

	public void updateSongText(Song song) {
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
		timeElapsedtext.setText(Main.musicController.getTimeElapsed());
	}

	public void updateSongQueueContents() {
		//songQueue.addAll(Main.musicController.getQueue().sublist(1, queue.size()));
	}

	public void fillLibraryPane() {
		switch (viewsChoiceBox.getSelectionModel().getSelectedItem().toString()) {
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

	@FXML void onBackButtonPressed() {
		fillLibraryPane();
	}

	@FXML void currentSongPauseClicked() {
		Main.musicController.togglePaused();

		//Main.musicController.isPaused() ? currentSongPauseButton.setText(">") : currentSongPauseButton.setText("||");
		//currentSongPauseButton.setText(">"); TODO
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
		fillLibraryPane();

		System.out.println(">this");
	}


	// BEHIND THE SCENES

	void showAlbumsView() {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);

		for (Album album : Album.getAllFromDatabase()) {
			VBox vBox = new VBox();
			vBox.setPadding(new Insets(4));
			vBox.setSpacing(4);

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
			tilePane.getChildren().addAll(vBox);
		}
	}

	void showSongsView() {
		//just overloaded to show all songs
		showSongsView("All Songs", Song.getAllFromDatabase());
	}

	void showSearchResultsView(String query) {
		//basically just turns a query into an arraylist for showsongsview
		showSongsView("Search Results for query '" + query + "'",
			Song.getAllFromDatabase().stream().filter(
			s -> s.toString().toLowerCase().contains(query.toLowerCase()))
			.collect(Collectors.toList())
		);
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
			HBox hBox = new HBox();
			Text songNameText = new Text(song.toString() + " - ");
			Text artistNameText = new Text(song.getArtist().toString());
			Text featuresNamesText = new Text(song.getFeatures().size() != 0 ? " (ft. " + String.join(", ", song.getFeatures().stream().map((artist) -> artist.toString()).collect(Collectors.toList())) + ") - " : " - ");
			Text albumNameText = new Text(song.getAlbum().toString() + " - ");
			Text songLengthText = new Text(song.getLength());
			hBox.getChildren().addAll(songNameText, artistNameText, featuresNamesText, albumNameText, songLengthText);
			vBox.getChildren().addAll(hBox);
		}
	}

	void showArtistsView() {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);

		for (Artist artist : Artist.getAllFromDatabase()) {
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
			tilePane.getChildren().addAll(vBox);
		}
	}

	void showGenresView() {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);

		for (Genre genre : Genre.getAllFromDatabase()) {
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
			tilePane.getChildren().addAll(vBox);
		}
	}

	void showPlaylistsView() {
		libraryPane.getChildren().clear();
		TilePane tilePane = new TilePane();
		libraryPane.getChildren().addAll(tilePane);

		for (Playlist playlist : Playlist.getAllFromDatabase()) {
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
			tilePane.getChildren().addAll(vBox);
		}
	}
}
