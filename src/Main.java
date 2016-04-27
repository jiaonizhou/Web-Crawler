package webCrawler.src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Main {
   public static void main(String[] args) throws IOException {
      
      String inputFileName = "/Users/hanzili/Desktop/specification.csv";
      ReadSeeds seeds = new ReadSeeds(inputFileName);
      
      ArrayList<String> visited = new ArrayList<String>();      
      System.out.println("Start Web Crawler...\n");
      
      int threadNum = seeds.getSeeds().size();
      Thread[] threads = new Thread[threadNum];
      for (int i = 0; i < threadNum; ++i) {
         WebCrawler2 myCrawler = new WebCrawler2(visited, seeds.getSeeds().get(i), 
               seeds.getMaxCrawls().get(i), seeds.getDomains().get(i), 
               seeds.getOutReports().get(i), seeds.getOutRepositorys().get(i));
         threads[i] = new Thread(myCrawler);
         threads[i].start();       
      }
      
      for (int i = 0; i < threadNum; ++i) {
         try {
            threads[i].join();
         } catch (InterruptedException ex) {
            System.out.println("Exception : " + ex);
         }
      }

      System.out.println("Web Crawler Finished:");
      System.out.println("HTML files are saved in the repository folder;");  
      System.out.println("report.html file is generated, "
            + "which include the page URL, title and additional statistics:");

   }

}
