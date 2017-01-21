package text_processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;

public class NLPProcesses {

	static ArrayList<String> justify(String s, int limit) {
		ArrayList<String> chunks = new ArrayList<String>();
	    //StringBuilder justifiedText = new StringBuilder();
	    StringBuilder justifiedLine = new StringBuilder();
	    String[] words = s.split(" ");
	    for (int i = 0; i < words.length; i++) {
	        justifiedLine.append(words[i]).append(" ");
	        if (i+1 == words.length || justifiedLine.length() + words[i+1].length() > limit) {
	            justifiedLine.deleteCharAt(justifiedLine.length() - 1);
	            chunks.add(justifiedLine.toString());
	            //justifiedText.append(justifiedLine.toString()).append(System.lineSeparator());
	            justifiedLine = new StringBuilder();
	        }
	    }
	    return chunks;
	}
	
	public File get_nlp_classification(String audioTrans)
	{
		File classifiedFile = new File("G:/Fall2016/Cognitive/nlpClassifications/nlpclasses1.json");
		try
		{
		if(!classifiedFile.exists())
		{
			classifiedFile.createNewFile();
		}
		NaturalLanguageClassifier service = new NaturalLanguageClassifier();
		service.setUsernameAndPassword("05605902-1fcb-4c70-9f9c-f6fd162a2f8c", "rnuICo5vNyne");
		 service.setEndPoint("https://gateway.watsonplatform.net/natural-language-classifier/api");
		//Classifier classifier = service.createClassifier("meetingClassfier","en", trainFile).execute();
		//System.out.println(classifier);
		

/*** Classfier *******/
		FileWriter fwnlp = new FileWriter(classifiedFile);
		ArrayList<String> getChunks = justify(audioTrans,999);
		
		Iterator<String> i =getChunks.iterator();
		
		while(i.hasNext())
		{
			Classification classification = service.classify("8d6cddx124-nlc-3645",i.next()).execute();
			System.out.println(classification);
			fwnlp.append(classification.toString()+"\n");
		}
		
		fwnlp.close();
		
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return classifiedFile;
	}
	
	public void get_nlp_finalconfidence(File nlpclasses,String final_file)
	{
		System.out.println("This is the final file name:...... "+final_file);
		try
		{
		FileReader frnlp = new FileReader(nlpclasses);
		File fi_class = new File(final_file);
		if(!fi_class.exists())
		{
			fi_class.createNewFile();
		}
		 
		//Gson gs = new Gson();
		JsonStreamParser jp = new JsonStreamParser(frnlp);
		FileWriter fwnlp = new FileWriter(fi_class,true);
		//BufferedWriter nlpbw = new BufferedWriter(fwnlp);
		HashMap<String,Double> retrievedClasses = new HashMap<String,Double>();
		HashMap<String,Double> alsoClasses = new HashMap<String,Double>();
		while(jp.hasNext())
		{
			JsonObject obj = (JsonObject)jp.next();
			JsonArray jclass_ar = obj.get("classes").getAsJsonArray();
			Iterator<JsonElement> i = jclass_ar.iterator();
			while(i.hasNext())
			{
				JsonObject c_ar = i.next().getAsJsonObject();
				double confidence = c_ar.get("confidence").getAsDouble();
				String c_name = c_ar.get("class_name").getAsString();
				
				
				if(confidence>=0.1)
				{
					retrievedClasses.put(c_name, confidence);
				}
				else
					alsoClasses.put(c_name, confidence);
			}
			
		}
		
		fwnlp.append("<br/><b>The meeting attendees were: </b>");
		fwnlp.append("<br/>Aditi V: ");
		fwnlp.append("<br/>Ronak M: ");
		fwnlp.append("<br/>Saikrishna C: ");
		fwnlp.append("<br/>Nitin C: ");
		fwnlp.append("<br/><b>The following topics were spoken about in the meeting: \n</b><br/>");
		for(String key : retrievedClasses.keySet())
		{
			Double percent_conf = (double) Math.round(retrievedClasses.get(key)*100);
			fwnlp.append("<b>"+key+"</b> with a confidence of : "+percent_conf+"%\n<br/>");
		}
		
		fwnlp.append("<br/><b>The following topics were also brought up in the meeting but"+
		"<br/>I cannot say for sure with a lot of confidence</b>");
		for(String key : alsoClasses.keySet())
		{
			Double percent_conf = (double) Math.round(alsoClasses.get(key)*100);
			fwnlp.append("<br/><b>"+key+"</b> with a confidence of : "+percent_conf+"%\n<br/>");
		}
		
		fwnlp.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
