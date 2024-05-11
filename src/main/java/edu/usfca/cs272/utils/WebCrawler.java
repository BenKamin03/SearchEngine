package edu.usfca.cs272.utils;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class responsible for downloading webpages and adding them to the invertedindex
 *
 * @author Ben Kamin
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2024
 */
public class WebCrawler {

     /**
      * the workqueue
      */
     private final WorkQueue workQueue;

     /**
      * the thread safe inverted index
      */
     private final MultiThreadedInvertedIndex invertedIndex;

     /**
      * the places the crawler has visited
      */
     private final HashSet<URI> visitedPages;

     /**
      * the lock for the pages
      */
     private final MultiReaderLock pageLock;

     /**
      * creates the webcrawler class
      * 
      * @param invertedIndex the thread safe inverted index
      * @param workQueue the workqueue
      */
     public WebCrawler(MultiThreadedInvertedIndex invertedIndex, WorkQueue workQueue) {
          this.invertedIndex = invertedIndex;
          this.workQueue = workQueue;
          this.visitedPages = new HashSet<>();
          this.pageLock = new MultiReaderLock();
     }

     
     /** performs the webcrawl given the seed and the max number of places to visit 
      * 
      * @param seed the seed
      * @param max the max
      * @throws IllegalArgumentException an illegal arugment exception for if the seed is null or the max isnt a positive integer
      */
     public void crawl(URI seed, int max) throws IllegalArgumentException {
          if (seed == null) {
               throw new IllegalArgumentException("Web Crawler - crawl(): seed is null");
          } else if (max <= 0) {
               throw new IllegalArgumentException("Web Crawler - crawl(): max is 0 or negative");
          }
          
          try {
               seed = HtmlCleaner.cleanURI(seed);
          } catch (URISyntaxException e) {
               System.out.println("Seed URI could not be cleaned");
          }

          visitedPages.add(seed);
          workQueue.execute(new WebCrawlerTask(seed, max));
          workQueue.finish();
     }

     /**
      * The task for each URI
      */
     public class WebCrawlerTask implements Runnable {

          /**
           * the uri this task is responsible for
           */
          private final URI uri;

          /**
           * the max amount of places to visit
           */
          private final int max;

          /**
           * creates the webcrawler task
           * @param uri the uri to parse
           * @param max the max number of uris to visit
           */
          public WebCrawlerTask(URI uri, int max) {
               this.uri = uri;
               this.max = max;
          }

          @Override
          public void run() {
               String html = HtmlFetcher.fetch(uri, 3);

               if (html != null) {
                    String htmlNoBlocks = HtmlCleaner.stripBlockElements(html);
                    createCrawlTasks(htmlNoBlocks, uri, max);
                    
                    String strippedHtml = HtmlCleaner.stripEntities(HtmlCleaner.stripTags(htmlNoBlocks));
                    addHTMLToIndex(strippedHtml, uri.toString());
               }
          }

          /**
           * Creates the recursive tasks for accessing subsequent uris
           * 
           * @param htmlNoBlocks the html without block elements
           * @param uri the uri
           * @param max the max number of files to parse
           */
          private void createCrawlTasks(String htmlNoBlocks, URI uri, int max) {
               List<URI> hrefs = HtmlCleaner.getURIsFromFile(htmlNoBlocks, uri);
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

          /**
           * adds the HTML file to the index
           * 
           * @param strippedHtml the html stripped of all tags and block elements
           * @param uri the uri as a string (used for keys)
           */
          private void addHTMLToIndex(String strippedHtml, String uri) {
               InvertedIndex index = new InvertedIndex();
               Stemmer stemmer = new SnowballStemmer(ENGLISH);
     
               String[] parsedLine = FileStemmer.parse(strippedHtml);
               int i = 1;
     
               for (String word : parsedLine) {
                    index.addIndex(stemmer.stem(word).toString(), uri, i++);
               }
     
               invertedIndex.addIndex(index);
          }
     }
}
