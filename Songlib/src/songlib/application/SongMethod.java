/*CS 213 Fall 2018 - Assignment 1, Song Library
		Student1 Name Alex Silva  netid: ars366
		Student2 Name: Hongping Lin netid: hl793
		Grader Name: Govind*/

package songlib.application;

import java.util.ArrayList;

public class SongMethod extends Song{

  
    public int insertSortedIndex(ArrayList<Song> songList, Song song){
        
        int i = 0;
    	for(i = 0; i < songList.size(); i++){
			if(songList.get(i).compareTo(song)==0){
				return -1; // song found same
			}
			else if(songList.get(i).compareTo(song) >0){
				return i; //take position in list
			}
			else{
				// (songList.get(i).compareTo(s)<0)
				continue; //keep iterating for a spot
			}
		}
	
		return i;
	}

}
