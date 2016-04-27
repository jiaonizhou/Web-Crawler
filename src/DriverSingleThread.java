
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class DriverSingleThread {
   public static void main(String[] args) throws IOException {

      System.out.println("Start Web Crawler...\n");

      WebCrawlerSingleThread myWebCrawler = new WebCrawlerSingleThread();

      System.out.println("Web Crawler Finished:");
      System.out.println("HTML files are saved in the repository folder;");  
      System.out.println("report.html file is generated, "
            + "which include the page URL, title and additional statistics:");

      HashMap<String, Integer> myhpCount = myWebCrawler.hpCount;
      Set<String> myhpCountKeys = myhpCount.keySet();
      System.out.println("\nStart Content Processing... \n");
      
      // the final all main content is write into a txt file
      FileWriter out = new FileWriter("allMainContent.txt");
      BufferedWriter bw = new BufferedWriter(out);

      for (String s: myhpCountKeys) {
         int n = myhpCount.get(s);
         if (myWebCrawler.hpResponse.get(s) == 200) {
            System.out.println("URL: "+ s + "; FileIndex: " + n + ".html;");
            String fileName = "repository/" 
                  + Integer.toString(n) + ".html";
            ContentProcessor oneContent = new ContentProcessor(fileName);
            bw.write(oneContent.getFinalText());
         }
      }
      
      bw.close();

   }
}

