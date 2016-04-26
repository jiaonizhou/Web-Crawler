package webCrawler.src;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class ContentProcessorTest
{

   @Test
   public void test() throws IOException
   {
      String fileName = "/Users/hanzili/Desktop/recreation.html";
      ContentProcessor oneContent = new ContentProcessor(fileName);
      FileWriter out = new FileWriter("/Users/hanzili/Desktop/oneContent.txt");
      BufferedWriter bw = new BufferedWriter(out);
      System.out.println(oneContent.getFinalText());
      bw.write(oneContent.getFinalText());
   }

}
