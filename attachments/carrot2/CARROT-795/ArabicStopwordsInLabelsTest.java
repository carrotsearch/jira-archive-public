package org.carrot2.clustering.lingo;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.carrot2.core.*;
import org.carrot2.text.linguistic.DefaultLexicalDataFactory;
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.util.MutableCharArray;

import com.ibm.icu.text.Transliterator;

/**
 * 
 */
public class ArabicStopwordsInLabelsTest
{
    final static Transliterator instance = Transliterator.getInstance("Any-Latin");

    public static void main(String [] args) throws IOException
    {
        File location = new File("/home/dweiss/tmp/Text");
        final ArrayList<Document> documents = new ArrayList<Document>();

        if (location.isDirectory() && location != null) for (File f : location
            .listFiles())
        {
            if (f.isFile())
            {
                String fileContents = FileUtils.readFileToString(f, "Cp1256");
                documents.add(new Document("", fileContents, LanguageCode.ARABIC));
            }
        }

        final Controller controller = ControllerFactory
            .createCachingPooling(IDocumentSource.class);

        final ProcessingResult byTopicClusters = controller.process(documents, null,
            LingoClusteringAlgorithm.class);
        final List<Cluster> clustersByTopic = byTopicClusters.getClusters();

        DefaultLexicalDataFactory df = new DefaultLexicalDataFactory();
        ILexicalData lexicalData = df.getLexicalData(LanguageCode.ARABIC);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        System.out.println("Stopwords: ");
        for (String s : IOUtils.readLines(cl.getResourceAsStream("stopwords.ar"), "UTF-8")) {
            if (s.startsWith("#") || s.isEmpty()) continue;
            System.out.println(instance.transliterate(s));
        }

        System.out.println("Stoplabels: ");
        for (String s : IOUtils.readLines(cl.getResourceAsStream("stoplabels.ar"), "UTF-8")) {
            if (s.startsWith("#") || s.isEmpty()) continue;
            System.out.println(instance.transliterate(s));
        }
        
        System.out.println("Cluster by TOPIC: ");
        for (Cluster c : clustersByTopic)
        {
            if (lexicalData.isCommonWord(new MutableCharArray(c.getLabel()))) {
                System.out.print("#!! > ");
            }
            System.out.println(
                instance.transliterate(c.getLabel()));
        }
    }
}
