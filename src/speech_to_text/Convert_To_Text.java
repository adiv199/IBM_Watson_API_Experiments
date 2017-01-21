package speech_to_text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

public class Convert_To_Text {
	
	 private static CountDownLatch lock = new CountDownLatch(1);

	  public File convert_to_text(File audioFile) throws InterruptedException, Exception {
	    SpeechToText service = new SpeechToText();
	    Gson gs = new Gson();
	    service.setUsernameAndPassword("d115c8ad-6958-41bc-a1fc-50a1ce723ef5", "JSOWggCNzpvv");

	    FileInputStream audio = new FileInputStream(audioFile);
	    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	    
	    Random r = new Random(600);
	    int rand = r.nextInt(500);
	    
	    long suf = timestamp.getTime()%rand;
	    
	    File fe = new File("G:/Fall2016/Cognitive/jsonTranscripts/transcript_"+suf+".json");
	    
	    if(!fe.exists())
	    {
	    	fe.createNewFile();
	    }
	    else
	    {
	    	suf = timestamp.getTime()%rand+100;
	    	fe.createNewFile();
	    }
	    FileWriter fw = new FileWriter(fe);
	    RecognizeOptions options = new RecognizeOptions.Builder()
	        .continuous(true)
	        .interimResults(true) // do you want continuous interim results?
	        .contentType(HttpMediaType.AUDIO_WAV)
	        .build();

	    service.recognizeUsingWebSocket(audio, options, new BaseRecognizeCallback() {
	      @Override
	      public void onTranscription(SpeechResults speechResults) {
	    	  BufferedWriter bw = new BufferedWriter(fw);
			  try
			  {
				  fw.write(speechResults.toString());
			  }
			  catch (IOException e)
			  {
				  System.out.println(e.getStackTrace());
			  }
	    	  
	        System.out.println(speechResults);
	      }
	      
	      @Override
	      public void onDisconnected() {
	        lock.countDown();
	      }
	    });

	    lock.await(1, TimeUnit.MINUTES);
	    fw.close();
	    
	    return fe;
	  }
	  
	  public void makeTranscript(File folder)
	  {
		  try {
			TranscriptElems t = new TranscriptElems();
			List<String[]> tList = new ArrayList<String[]>();
			ArrayList<String> filesSeen = new ArrayList<String>();
			//final File folder = new File("G:/Fall2016/Cognitive/jsonTranscripts");
			filesSeen = t.listFilesForFolder(folder);
			
			for(String fil : filesSeen)
			{	
				FileReader fr;
				fr = new FileReader("G:/Fall2016/Cognitive/jsonTranscripts/"+fil);
				File fe = new File("G:/Fall2016/Cognitive/textTranscripts/"+fil.substring(0,fil.indexOf('.'))+"_out.txt");
				if(!fe.exists())
				{
					fe.createNewFile();
				}
				FileWriter fw = new FileWriter(fe);
				JsonStreamParser jp = new JsonStreamParser(fr);
				StringBuffer sb = new StringBuffer("");
			
			
				while(jp.hasNext())
				{
					JsonObject obj = (JsonObject)jp.next();
					JsonObject obj1 = obj.get("results").getAsJsonArray().get(0).getAsJsonObject();
					Boolean resultFinal = obj1.get("final").getAsBoolean();
					JsonObject obj2 = obj1.get("alternatives").getAsJsonArray().get(0).getAsJsonObject();
					if(resultFinal)
					sb.append(obj2.get("transcript").getAsString());
				}
			fw.write(sb.toString());
			
			fr.close();
			fw.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	  }
}
