package webCrawler.src;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ContentProcessorTest
{

   @Test
   public void test() throws IOException
   {
      String fileName = "/Users/hanzili/Desktop/aboutscu.html";
      ContentProcessor test = new ContentProcessor(fileName);
   }

}
