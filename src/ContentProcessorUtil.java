package webCrawler.src;

import java.io.IOException;
import java.util.Random;

public class ContentProcessorUtil {

   public static void main(String args[]) throws IOException {
      int fileIndexInt = randInt(1, 120);
      System.out.println(fileIndexInt);
      String fileName = "/Users/hanzili/Desktop/repository/" 
      + Integer.toString(fileIndexInt) + ".html";
      ContentProcessor test = new ContentProcessor(fileName);
   }
   
   // returns a random number between min and max, inclusive
   private static int randInt(int min, int max) {
      Random rand = new Random();
      int randomNum = rand.nextInt((max - min) + 1) + min;
      return randomNum;
   }
}
