package speech_to_text;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.JsonElement;

public class TranscriptElems {
	
	int result_index;
	JsonElement results;
	JsonElement trans;
	
	public int getResult_index() {
		return result_index;
	}

	public void setResult_index(int result_index) {
		this.result_index = result_index;
	}

	public JsonElement getResults() {
		return results;
	}
	
	public void setResults(JsonElement jsonElement) {
		this.results = jsonElement;
	}

	public JsonElement getTrans() {
		return trans;
	}

	public void setTrans(JsonElement jsonElement) {
		this.trans = jsonElement;
	}
	
	public ArrayList<String> listFilesForFolder(File folder) {
		ArrayList<String> filelist = new ArrayList<String>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	        	
	            System.out.println(fileEntry.getName());
	            filelist.add(fileEntry.getName());
	        }
	    }
	    return filelist;
	}

}
