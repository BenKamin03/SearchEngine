package edu.usfca.cs272.utils;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.net.URI;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {

     private final WorkQueue workQueue;

     private final InvertedIndex invertedIndex;

     private final Stemmer stemmer;

     public WebCrawler(InvertedIndex invertedIndex, WorkQueue workQueue) {
          this.invertedIndex = invertedIndex;
          this.workQueue = workQueue;
          this.stemmer = new SnowballStemmer(ENGLISH);
     }

     public WebCrawler(InvertedIndex invertedIndex, WorkQueue workQueue, Stemmer stemmer) {
          this.invertedIndex = invertedIndex;
          this.workQueue = workQueue;
          this.stemmer = stemmer;
     }

     public void crawl(URI seed, int max) {
          List<URI> links = getLinks(seed);
          
          for (var link : links) {
               System.out.println(link);
          }

          var iterator = links.iterator();
          while (iterator.hasNext() && --max > 0) {
               addURI(iterator.next());
               System.out.println(max);
          }
          
          workQueue.finish();
     }

     public List<URI> getLinks(URI uri) {
          return HtmlCleaner.getURIsFromFile(getHTML(uri), uri);
     }

     public String getHTML(URI uri) {
          String html = HtmlFetcher.fetch(uri, 3);
          InvertedIndex localIndex = new InvertedIndex();
          if (html != null) {
               String[] parsedLine = FileStemmer.parse(HtmlCleaner.stripHtml(html));
               int i = 1;
               System.out.println("URI: " + getFileName(uri));
               for (String word : parsedLine) {
                    localIndex.addIndex(stemmer.stem(word).toString(), getFileName(uri), i++);
               }

               invertedIndex.addIndex(localIndex);
          }

          return html;
     }

     public void addURI(URI uri) {
          // workQueue.execute(new WebCrawlerTask(uri));
          new WebCrawlerTask(uri).run();
     }

     public String getFileName(URI uri) {
          return uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
     }

     public class WebCrawlerTask implements Runnable {

          private final URI uri;

          public WebCrawlerTask(URI uri) {
               this.uri = uri;
          }

          @Override
          public void run() {
               getHTML(uri);
          }

     }
}
