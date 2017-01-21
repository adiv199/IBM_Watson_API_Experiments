package text_processing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Entities;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Entity;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keyword;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Keywords;
import com.ibm.watson.developer_cloud.alchemy.v1.model.SAORelation;
import com.ibm.watson.developer_cloud.alchemy.v1.model.SAORelations;
import com.ibm.watson.developer_cloud.alchemy.v1.model.Taxonomies;

public class AlchemyProcesses {
	
	public void get_alchemy_results(File transcript_file, String alch_out,String alch_ent, String alch_rel, String sumr)
	{
		AlchemyLanguage service = new AlchemyLanguage();
		/*Key for avishw4@uic.edu
		 * {
  "url": "https://gateway-a.watsonplatform.net/calls",
  "note": "It may take up to 5 minutes for this key to become active",
  "apikey": "44d6b5aaa71bc0537e2ac69fc8fbe5bad363ff0a"
}
		 */
		service.setApiKey("44d6b5aaa71bc0537e2ac69fc8fbe5bad363ff0a");
		
		File f1 = new File(alch_out);
		File f2 = new File(alch_ent);
		File f3 = new File(alch_rel);
		File f4 = new File(sumr);
try
{
		if(!f1.exists())
			f1.createNewFile();
		if(!f2.exists())
			f2.createNewFile();
		if(!f3.exists())
			f3.createNewFile();
		if(!f4.exists())
			f4.createNewFile();
		
		FileWriter fw = new FileWriter(f1);
		FileWriter fw1 = new FileWriter(f2);
		FileWriter fw2 = new FileWriter(f3);
		FileWriter fw3 = new FileWriter(f4,true);
		FileReader fr = new FileReader(transcript_file);
		StringBuffer sb = new StringBuffer("");
		BufferedReader buf = new BufferedReader(fr);
		String line="";
		while((line=buf.readLine())!=null)
		{
			sb.append(line);
		}
		String fileRead = sb.toString();
		System.out.println("This is the file:::\n "+ fileRead);
		
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(AlchemyLanguage.TEXT, fileRead);
		DocumentSentiment sentiment = service.getSentiment(params).execute();
		Taxonomies tx = service.getTaxonomy(params).execute();
		Entities en = service.getEntities(params).execute(); 
		Keywords ky = service.getKeywords(params).execute();
		SAORelations rl = service.getRelations(params).execute();
		List<Entity> ent = en.getEntities();
		fw3.append("\n<br/>The following were mentioned :</br>");
		for (Entity i : ent)
		{
			fw3.append(i.getType()+":<b>"+i.getText()+"</b><br/>");
			System.out.println(i.toString());
			fw1.write(i.toString()+"\n");
		}
		fw.append("KEYWORDS: ");
		
		fw3.append("<br/> The following keywords were mentioned:\n");
		List<Keyword> kyw = ky.getKeywords();
		for (Keyword i : kyw)
		{
			fw3.append(i.getText()+",");
			fw.append(i.toString()+"\n");
		}
		fw3.append("<br/>");
		fw.append("RELATIONS:");
		List<SAORelation> rlt = rl.getRelations();
		for (SAORelation i : rlt)
		{
			//fw3.append("\n")
			fw2.append(i.toString()+"\n");
		}
		
		
		fw.close();
		fw1.close();
		fw2.close();
		fw3.close();
}
catch(IOException e)
{
	e.printStackTrace();
}
	}

}
