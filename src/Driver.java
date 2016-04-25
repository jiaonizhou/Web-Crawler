package webCrawler.src;

import java.io.IOException;

public class Driver
{
   public static void main(String[] args) throws IOException {
      WebCrawlerMulti myWebMulti = new WebCrawlerMulti();

      int threadNum = 4; // can be changed
      Thread[] threads = new Thread[threadNum];
      for (int i = 0; i < threadNum; ++i) {
         threads[i] = new Thread(myWebMulti);
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
      System.out.println("html files are saved in the repository folder;");  
      System.out.println("report.html file is generated, "
            + "which include the page URL, title and additional statistics:");  
   }
}

