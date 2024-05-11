package edu.usfca.cs272.utils;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.net.URI;
import java.util.HashSet;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {

     private final WorkQueue workQueue;
     private final MultiThreadedInvertedIndex invertedIndex;
     private final HashSet<URI> visitedPages;
     private final MultiReaderLock pageLock;

     public WebCrawler(MultiThreadedInvertedIndex invertedIndex, WorkQueue workQueue) {
          this.invertedIndex = invertedIndex;
          this.workQueue = workQueue;
          this.visitedPages = new HashSet<>();
          this.pageLock = new MultiReaderLock();
     }

     public void crawl(URI seed, int max) throws IllegalArgumentException {
          if (seed == null) {
               throw new IllegalArgumentException("Web Crawler - crawl(): seed is null");
          } else if (max <= 0) {
               throw new IllegalArgumentException("Web Crawler - crawl(): max is 0 or negative");
          }

          visitedPages.add(seed);
          workQueue.execute(new WebCrawlerTask(seed, max));
          workQueue.finish();
     }

     public void addHTMLToIndex(String html, String uri) {
          InvertedIndex index = new InvertedIndex();
          Stemmer stemmer = new SnowballStemmer(ENGLISH);

          String[] parsedLine = FileStemmer.parse(HtmlCleaner.stripHtml(html));
          int i = 1;

          for (String word : parsedLine) {
               index.addIndex(stemmer.stem(word).toString(), uri, i++);
          }

          invertedIndex.addIndex(index);
     }

     public void createCrawlTasks(String html, URI uri, int max) {
          List<URI> hrefs = HtmlCleaner.getURIsFromFile(html, uri);
          var hrefIterator = hrefs.iterator();

          pageLock.writeLock().lock();
          try {
               while (hrefIterator.hasNext() && visitedPages.size() < max) {
                    URI currHref = hrefIterator.next();
                    if (!visitedPages.contains(currHref)) {
                         visitedPages.add(currHref);
                         workQueue.execute(new WebCrawlerTask(currHref, max));
                    }
               }
          } finally {
               pageLock.writeLock().unlock();
          }
     }

     public class WebCrawlerTask implements Runnable {

          private final URI uri;
          private final int max;

          public WebCrawlerTask(URI uri, int max) {
               this.uri = uri;
               this.max = max;
          }

          @Override
          public void run() {
               String html = HtmlFetcher.fetch(uri, 3);

               if (html != null) {
                    createCrawlTasks(html, uri, max);
                    addHTMLToIndex(html, uri.toString());
               }
          }
     }
}
