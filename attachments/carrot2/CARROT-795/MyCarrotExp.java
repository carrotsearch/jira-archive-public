package org.carrot2.examples.clustering;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;


public class MyCarrotExp {

public static void main(String[] args) throws IOException {
		
		File location = new File("Text\\");
		final ArrayList<Document> documents = new ArrayList<Document>();
				
		if (location.isDirectory() && location != null) 
			for (File f : location.listFiles()) 
			{
				if (f.isFile()) 
				{
					String fileContents = FileUtils.readFileToString(f, "Cp1252");
					documents.add(new Document("", fileContents,  LanguageCode.ARABIC));

				}
			}			

		
		final Controller controller =  ControllerFactory.createCachingPooling(IDocumentSource.class);
		

		final ProcessingResult byTopicClusters = controller.process(documents, null,LingoClusteringAlgorithm.class);
	    final List<Cluster> clustersByTopic = byTopicClusters.getClusters();
	      
	    System.out.println("Cluster by TOPIC");
	    ConsoleFormatter.displayClusters(clustersByTopic,0);
	    
	}


}
