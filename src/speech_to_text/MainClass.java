package speech_to_text;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;

import text_processing.AlchemyProcesses;
import text_processing.EmailSummary;
import text_processing.NLPProcesses;

public class MainClass {

	public static void main(String[] args) {
		
		Convert_To_Text ct = new Convert_To_Text();
		TranscriptElems te = new TranscriptElems();
		NLPProcesses np = new NLPProcesses();
		AlchemyProcesses ap = new AlchemyProcesses();
		File audioFile = new File("G:/Fall2016/Cognitive/conversations/meeting_12082016.wav");
		File folder_audio = new File("G:/Fall2016/Cognitive/conversations");
		File folder = new File("G:/Fall2016/Cognitive/jsonTranscripts");
		File folder_transcripts = new File("G:/Fall2016/Cognitive/textTranscripts1");
		EmailSummary es = new EmailSummary();
		
		
		/******************* EMAIL Details ************************/
		String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "illuminatiarns@gmail.com";
        String password = "arns@2016";
 
        // message info
        String mailTo = "avishw4@uic.edu";
        String subject = "Your meeting summary for: "+Calendar.getInstance().getTime().toString();
 
        // attachments
        String[] attachFiles = new String[1];
        /*****************************************************/
		try {
		//	File json_output = ct.convert_to_text(audioFile);
			//ct.makeTranscript(folder);
			
		//Get Transcript files:
		ArrayList<String> getTranscriptFiles = te.listFilesForFolder(folder_transcripts);
		for(String trans_fil : getTranscriptFiles)
		{
			String confidence_file = "G:/Fall2016/Cognitive/nlpClassifications/conf_"+trans_fil;
			String confidence_alch = "G:/Fall2016/Cognitive/nlpClassifications/con_"+trans_fil;
			String alch_rel_file = "G:/Fall2016/Cognitive/nlpClassifications/alcr_"+trans_fil;
			String alch_ent_file = "G:/Fall2016/Cognitive/nlpClassifications/alce_"+trans_fil;
			String alch_comp_file = "G:/Fall2016/Cognitive/nlpClassifications/alcc_"+trans_fil;
			
			FileWriter fwFinal = new FileWriter(confidence_file);
		    String content = new String(Files.readAllBytes(Paths.get("G:/Fall2016/Cognitive/textTranscripts/"+trans_fil)));
		    System.out.println(content);
		    File tf = np.get_nlp_classification(content);
		    np.get_nlp_finalconfidence(tf, confidence_file);
		    ap.get_alchemy_results(new File(folder_transcripts+"/"+trans_fil), 
		    		alch_comp_file, alch_ent_file, alch_rel_file,confidence_alch);
		    
		    String mergedText = es.mergeText(new File(confidence_file), new File(confidence_alch));
		    attachFiles[0] = folder_transcripts+"/"+trans_fil;
	        String message = mergedText;
	        es.sendEmailWithAttachments(host, port, mailFrom, password,mailTo, 
	        		subject, message, attachFiles);
		   /* if(afile.renameTo(new File("C:\\folderB\\" + afile.getName()))){
	    		System.out.println("File is moved successful!");
	    	   }else{
	    		System.out.println("File is failed to move!");
	    	   }*/
		    
		    fwFinal.close();
		    
		}
		
		// SMTP info
        
        
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
