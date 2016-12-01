
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.collections.FXCollections;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.geometry.Insets;
import java.awt.Color;
import java.util.stream.Collectors;

public class MainSceneController {

    @FXML private ChoiceBox viewsChoiceBox;
    @FXML private Text songNameText;
    @FXML private Text artistNameText;
    @FXML private Text featuresNamesText;
    @FXML private Text albumNameText;
    @FXML private Text timeElapsedtext;
    @FXML private Slider songProgressBar;
    @FXML private Text songLengthText;
    @FXML private Button currentSongPauseButton;
    @FXML private TilePane libraryPane;
    @FXML private TextField searchBox;

    public MainSceneController () {
        System.out.println("+ Main Scene Controller class started");
        Main.musicController.addToQueueEnd(Main.libraryController.getAlbums().get(0)); //TEMP TODO

    }

    @FXML
    void initialize() {

        try
        {
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
            assert searchBox != null		: "- searchBox is null";
        }
        catch (AssertionError ae)
        {
            System.out.println("FXML assertion failure: " + ae.getMessage());
            Main.terminate();
        }
        viewsChoiceBox.setValue("what");//TODO
        viewsChoiceBox.setItems(
            FXCollections.observableArrayList("Albums", "Songs", "Artists", "Genres", "Playlists")
        );

        updateSongText(Main.musicController.getCurrentSong());//TODO make this update automatically
        updateTimeElapsed();//TODO same

        fillLibraryPane();
    }

    public void updateSongText(Song song) {
        if (song == null) {
            //hide and text
            songNameText.setText("");
            artistNameText.setText("");
            featuresNamesText.setText("");
            albumNameText.setText("");
        }
        songNameText.setText(song.toString() + " - ");
        artistNameText.setText(song.getArtist().toString() + " - ");
        featuresNamesText.setText(song.getFeatures().size() != 0 ? String.join(", ", song.getFeatures().stream().map((artist) -> artist.toString()).collect(Collectors.toList()) + " - ") : "");
        albumNameText.setText(song.getAlbum().toString());
        songLengthText.setText(song.getLength());
    }

    public void updateTimeElapsed() {
        timeElapsedtext.setText(Main.musicController.getTimeElapsed());
    }

    public void updateSongQueueContents() {//TODO
        /* queue = Main.musicController.getQueue();
         * for (int i = 1; i < queue.size(); i++) {
         * 	songQueue.add(queue.get(i));
         * }
         */
    }

    public void fillLibraryPane() {
        switch (viewsChoiceBox.getValue().toString()) {
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

    @FXML
    void currentSongPauseClicked() {
        System.out.println("+ pause clicked");

        fillLibraryPane();

        Main.musicController.togglePaused();

        //Main.musicController.isPaused() ? currentSongPauseButton.setText(">") : currentSongPauseButton.setText("||");
        //		currentSongPauseButton.setText(">"); TODO
    }

    @FXML
    void viewsChoiceBoxClicked() { //TODO
        System.out.println("+ viewsChoiceBoxClicked");
    }

    @FXML
    void songPlayClicked() {
        //Song song = new Song();//(Song)listView.getSelectionModel().getSelectedItem();
        //if (song != null)
        //	Main.musicController.addToQueue(song);
    }

    @FXML
    void albumPlayClicked() { /*
        Song song = (Song)listView.getSelectionModel().getSelectedItem();
        if (song != null)
        Main.musicController.addToQueue(song);
         */}

    @FXML
    void openVisualiserClicked() {
        Main.openVisualiser();
    }

    @FXML
    void onSearch() {
        showSearchResultsView(searchBox.getText());
    }

    // BEHIND THE SCENES

    void showAlbumsView() {
        libraryPane.getChildren().clear();
        for (Album album : Main.libraryController.getAlbums()) {
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(4));
            vBox.setSpacing(4);

            ImageView image = new ImageView(album.getPicture());
            image.setFitHeight(142);
            image.setFitWidth(142);
            Text name = new Text(album.toString());
            Text artist = new Text(album.getArtist().toString());
            //artist.setFill(Color.GREY); TODO

            vBox.getChildren().addAll(image, name, artist);
            libraryPane.getChildren().addAll(vBox);
        }
    }

    void showSongsView() {
        libraryPane.getChildren().clear();
        for (Song song : Main.libraryController.getSongs()) {
            VBox vBox = new VBox();
            vBox.getChildren().addAll(new Text(song.toString()));
            libraryPane.getChildren().addAll(vBox);
        }
    }

    void showSearchResultsView(String query) {
        libraryPane.getChildren().clear();
        for (Song song : Main.libraryController.getSongs().stream().filter(s -> s.toString().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList())) {
            VBox vBox = new VBox();
            vBox.getChildren().addAll(new Text(song.toString()));
            libraryPane.getChildren().addAll(vBox);
        }
    }

    void showArtistsView() {
        libraryPane.getChildren().clear();
        for (Artist artist : Main.libraryController.getArtists()) {
            VBox vBox = new VBox();
            vBox.getChildren().addAll(new Text(artist.toString()));
            libraryPane.getChildren().addAll(vBox);
        }
    }

    void showGenresView() {
        libraryPane.getChildren().clear();
        for (Genre genre : Main.libraryController.getGenres()) {
            Text text = new Text(genre.toString());
            libraryPane.getChildren().addAll(text);
        }
    }

    void showPlaylistsView() {
        libraryPane.getChildren().clear();
        for (Playlist playlist : Main.libraryController.getPlaylists()) {
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

            vBox.getChildren().addAll(imageBox, name, soungCount);
            libraryPane.getChildren().addAll(vBox);
        }
    }
}
