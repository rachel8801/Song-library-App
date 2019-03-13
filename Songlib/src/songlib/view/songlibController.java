/*CS 213 Fall 2018 - Assignment 1, Song Library
		Student1 Name Alex Silva  netid: ars366
		Student2 Name: Hongping Lin netid: hl793
		Team#45
		Grader Name: Govind*/

package songlib.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import songlib.application.Song;
import songlib.application.SongMethod;

public class songlibController{

	@FXML
	private TextField songName;
	@FXML
	private TextField artist;
	@FXML
	private TextField album;
	@FXML
	private TextField year;

	@FXML
	public Text songNameDisplay;
	@FXML
	public Text artistDisplay;
	@FXML
	public Text albumDisplay;
	@FXML
	public Text yearDisplay;

	@FXML
	Button addSong;
	@FXML
	Button editSong;
	@FXML
	Button deleteSong;
	@FXML
	Button saveEdit;
	@FXML
	Button cancelOperation;

	@FXML
	ListView<Song> listView;

	private ObservableList<Song> obsList = FXCollections.observableArrayList();
	public static ArrayList<Song> songList = new ArrayList<>();

	public void start(Stage mainStage) {

		// get data from SongLibrary.txt
		readfromFIle();

		// keep buttons deactivated if list is empty
		if (songList.isEmpty())
			toggleButtons(1, 1);
		else
			toggleButtons(0, 1);

		// write data from file to arrayList
		obsList.addAll(songList);
		listView.getItems().addAll(obsList);

		// pre-select first song in listView if any
		if (!obsList.isEmpty())
			listView.getSelectionModel().select(0);

		// display first song details
		if (!songList.isEmpty())
			setDisplay(songList.get(0));

		// gets every mouse selection and display the song details
		listView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Song>() {
					@Override
					public void changed(ObservableValue<? extends Song> observable, Song oldValue, Song newValue) {
						//Song song = listView.getSelectionModel().getSelectedItem();
						//int index = listView.getSelectionModel().getSelectedIndex();
						//System.out.println(obsList.get(index).getSongName() + " > " + index);
						// sets the text displays to the values of the song selected
						setDisplay(newValue);
					}
				});

		// CXL button to clear all input fields
		cancelOperation.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// wipe input fields
				clearTextField();

				// enable all buttons except save button
				if (obsList.isEmpty())
					toggleButtons(1, 1);
				else
					toggleButtons(0, 1);
			}
		});


		// ADD button listener to add fields to ObservableList
		addSong.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				//check if input fields are empty
				if (!songName.getText().isEmpty() && !artist.getText().isEmpty()) {

					// Create new song instance to add to ArrayList
					Song newSong = new Song(songName.getText(), artist.getText(),
							album.getText(), year.getText());

					// retrieve sorted index to insert new song, returns -1 if song is duplicate
					int index = new SongMethod().insertSortedIndex(songList, newSong);

					// Pop Up error message for duplicate song
					if (index == -1) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.initOwner(mainStage);  
						alert.setTitle("Error");
						alert.setHeaderText("Song is already in the List");
						String content = "Song: " + newSong.getSongName() + " " +
								"Artist: " + newSong.getArtist();

						alert.setContentText(content);
						alert.showAndWait();

					} else {
						// add songs to ArrayList and then to ObservableList
						songList.add(index, newSong);
						updateObslList(index, obsList);
						setDisplay(newSong);
						// wipe input fields
						clearTextField();
					}
					// Keep all buttons active except save button
					toggleButtons(0, 1);
				}
			}
		});

		// Edit song fields and switch order in case of song or artist change
		editSong.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public final void handle(ActionEvent event) {

				//showItemInputDialog(mainStage);
				toggleButtons(2, 0);

				// get mouse selected song and index
				Song song = listView.getSelectionModel().getSelectedItem();
				int index = listView.getSelectionModel().getSelectedIndex();

				// Fill textFields with item selected to edit
				songName.setText(song.getSongName());
				artist.setText(song.getArtist());
				album.setText(song.getAlbum());
				year.setText(song.getYear());

				// fetch songList for the index holding the selected song
				int songListIndex = songList.indexOf(song);

				// saves edited song to the songList and updates the observableList
				saveEdit.setOnAction(e -> saveAction(index, songListIndex, song, mainStage));

			}
		});

		// Delete song and wipe it from list
		// Still gives error when list is empty
		deleteSong.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				if (!songList.isEmpty()) {

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("WARNING");
					alert.setHeaderText("You are about to delete the selected song");
					alert.setContentText("Are you sure?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						Song song = listView.getSelectionModel().getSelectedItem();
						int index = listView.getSelectionModel().getSelectedIndex();

						// get index of the song in the ArrayList
						int songListIndex = songList.indexOf(song);

						// update ArrayList, observableList and ListView
						songList.remove(songListIndex);
						obsList.remove(song);
						listView.getItems().remove(index);

						// get arrayList index when not empty
						int lastItem = songList.size() - 1;

						if (lastItem >=	 0 ){
							listView.getSelectionModel().select(lastItem);
							setDisplay(songList.get(lastItem));
						}
						else
							setDisplay(null);
					}
				}

				// wipe input fields
				clearTextField();
			}
		});
	}

	private void toggleButtons(int flag, int saveFlag) {

		// Disable all buttons
		if (flag == 1) {
			editSong.setDisable(true);
			deleteSong.setDisable(true);

			// Enable all buttons
		} else if (flag == 0) {
			addSong.setDisable(false);
			editSong.setDisable(false);
			deleteSong.setDisable(false);

		} else if (flag == 2) {
			addSong.setDisable(true);
			deleteSong.setDisable(true);
		}
		// disable/enable save button
		if (saveFlag == 1) {
			saveEdit.setDisable(true);
			//cancelOperation.setDisable(true);
		} else if (saveFlag == 0) {
			saveEdit.setDisable(false);
			//cancelOperation.setDisable(false);
		}
	}

	// Populate observableList with new Song
	private void updateObslList(int index, ObservableList<Song> obsList) {
		obsList.clear();
		obsList.addAll(songList);
		listView.getItems().setAll(obsList);

		// pre-select added song
		listView.getSelectionModel().select(index);
	}

	private void saveAction(int index, int songListIndex, Song song, Stage mainStage) {

		// update song fields
		song.setSongFields(songName.getText(), artist.getText(),
				album.getText(), year.getText());

		songList.remove(songListIndex);

		// fetch sorted index to insert song in the ArrayList
		int newIndex = new SongMethod().insertSortedIndex(songList, song);

		// Pop Up error message for duplicate song
		if (newIndex == -1) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(mainStage);
			alert.setTitle("Error");
			alert.setHeaderText("Song is already in the List");
			String content = "Song: " + song.getSongName() + " " +
					"Artist: " + song.getArtist();

			alert.setContentText(content);
			alert.showAndWait();

		} else {

			// update ArrayList, observableList and Listview
			songList.add(newIndex, song);
			updateObslList(newIndex, obsList);

			// Clear all input fields
			clearTextField();

			// Enable all buttons except save button
			toggleButtons(0, 1);
		}
	}

	// clear all TextFields
	private void clearTextField() {
		songName.clear();
		artist.clear();
		album.clear();
		year.clear();
	}

	private void setDisplay(Song song) {
		
		// empty fields if observable list is empty
		if (song == null) {
			songNameDisplay.setText(null);
			artistDisplay.setText(null);
			albumDisplay.setText(null);
			yearDisplay.setText(null);
			// Keep all buttons active except save button. If list is empty only enable Add Button
			toggleButtons(1,1);
		}
		else {
			// set display fields to mouse selected Song
			songNameDisplay.setText(song.getSongName());
			artistDisplay.setText(song.getArtist());
			albumDisplay.setText(song.getAlbum());
			yearDisplay.setText(song.getYear());
		}
	}

	// Read songs from file and input them into the ArrayList
	public void readfromFIle(){

		File file = new File("SongLibrary.txt");
		String items = null;
		String[] songItems;

		// check if file exists
		if(file.exists() && file.isFile() ) {
			try {
				Scanner sc = new Scanner(file);

				// iterate line by line and split each line into 4 strings to store as the items of each song
				while (sc.hasNextLine()) {
					items = sc.nextLine();
					songItems = items.split("\t");
					
					// check if any fields of the List are empty (no album or field were provided)
					if (songItems.length == 4)
						songList.add(new Song (songItems[0], songItems[1], songItems[2], songItems[3]));
					
					else if (songItems.length == 3)
						songList.add(new Song (songItems[0], songItems[1], songItems[2], null));
					else if (songItems.length == 2)
						songList.add(new Song (songItems[0], songItems[1], null, null));
				}
				sc.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("WARNING");
				alert.setContentText("File does not exist");
			}
		}
	}

	// write ArrayList to file
	public void writetoFile() {
		File file = new File("SongLibrary.txt");
		try {
			PrintWriter write = new PrintWriter(file);
			file.createNewFile();

			for(int i =0; i<songList.size(); i++) {
				write.println(songList.get(i).songtoWrite());
			}
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
