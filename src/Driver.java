package webCrawler.src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Driver {
   public static void main(String[] args) throws IOException {

      System.out.println("Start Web Crawler...\n");

      WebCrawlerSingle myWebMulti = new WebCrawlerSingle();

      System.out.println("Web Crawler Finished:");
      System.out.println("HTML files are saved in the repository folder;");  
      System.out.println("report.html file is generated, "
            + "which include the page URL, title and additional statistics:");


      HashMap<String, Integer> myhpCount = myWebMulti.hpCount;
      Set<String> myhpCountKeys = myhpCount.keySet();
      System.out.println("\nStart Content Processing... \n");
      
      // the final all main content is write into a txt file
      FileWriter out = new FileWriter("/Users/hanzili/Desktop/allMainContentGov10.txt");
      BufferedWriter bw = new BufferedWriter(out);

      int count = 1;
      for (String s: myhpCountKeys) {
         if (count > 10) {
            break;
         }
         count++;
         int n = myhpCount.get(s);
         if (myWebMulti.hpResponse.get(s) == 200) {
            System.out.println("URL: "+ s + "; FileIndex: " + n + ".html;");
            String fileName = "/Users/hanzili/Desktop/repository/" 
                  + Integer.toString(n) + ".html";
            ContentProcessor oneContent = new ContentProcessor(fileName);
            bw.write(oneContent.getFinalText());
         }
      }
      
      bw.close();

   }
}

