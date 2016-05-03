package webCrawler.src;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DriverMultiThreadDemo {
   public static void main(String[] args) throws IOException {

      String fileDir = "/Users/hanzili/Desktop/";
      
      String inputFileName = fileDir + "specification.csv";
      ReadSeedsDemo seeds = new ReadSeedsDemo(inputFileName, fileDir);

      ArrayList<WebCrawlerMultiThread> myCrawlers = new ArrayList<>();
      ArrayList<String> visited = new ArrayList<String>();      
      System.out.println("Start Web Crawler...\n");

      int threadNum = seeds.getSeeds().size();
      Thread[] threads = new Thread[threadNum];
      for (int i = 0; i < threadNum; ++i) {
         WebCrawlerMultiThread myCrawler = new WebCrawlerMultiThread(visited, seeds.getSeeds().get(i), 
               seeds.getMaxCrawls().get(i), seeds.getDomains().get(i), 
               seeds.getOutReports().get(i), seeds.getOutRepositorys().get(i));
         myCrawlers.add(myCrawler);
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

      System.out.println("\nWeb Crawler Finished:");
      System.out.println("HTML files are saved in the repository folder;");  
      System.out.println("report.html file is generated, "
            + "which include the page URL, title and additional statistics: ");
      System.out.println("\nStart Content Processing, "
            + "final all main content will be write into a txt file... \n");


      for (int i = 0; i < threadNum; i++) {
         
         // one folder for one seed
         String outTxtFolder = fileDir + "TxtForSeed" + i;
         File dir = new File(outTxtFolder);
         dir.mkdir();
         

         HashMap<String, Integer> myhpCount = myCrawlers.get(i).getHpCount();
         HashMap<String, Integer> myhpResponse = myCrawlers.get(i).getHpResponse();
         Set<String> myhpCountKeys = myhpCount.keySet();

         for (String s: myhpCountKeys) {
          
            int n = myhpCount.get(s);
            if (myhpResponse.get(s) == 200) {
               System.out.println("URL: "+ s + "; FileIndex: " + n + ".html;");
               String fileNameHTML = fileDir + "repository" + i + "/" 
                     + Integer.toString(n) + ".html";
               ContentProcessor oneContent = new ContentProcessor(fileNameHTML);
               
               String fileNameTxt = outTxtFolder + "/" + Integer.toString(n) + ".txt";      
               FileWriter out = new FileWriter(fileNameTxt);
               BufferedWriter bw = new BufferedWriter(out);  
               bw.write(oneContent.getFinalText());
               bw.close();
            }
         }
         
      }
   }
   

}
