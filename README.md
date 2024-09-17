# My Search Engine

## About

My search engine is a powerful full-stack application designed to provide accurate and relevant search results. Here’s how it works:

### Web Crawling

My system uses a web crawler to gather information from the Javadoc website.

### Inverted Index with Stemming

I create an inverted index of stemmed words. By reducing words to their root forms, stemming helps handle variations of words and enhances search accuracy. The inverted index maps each stemmed word to the documents where it appears.

### Search Results

When you perform a search, my engine calculates the relevance of each document based on the number of matching stems relative to the total number of stems in the document. This method ensures that results are ranked according to their relevance and context.

## Instructions for Running

1. **Create a `.env` file in the `/frontend` directory with the following content:**

    ```env
    REACT_APP_PORT=3000
    REACT_APP_API_URL=http://localhost
    ```

2. **Run the following command to start the system:**

    ```bash
    -html https://usf-cs272-spring2024.github.io/project-web/docs/api/allclasses-index.html -crawl 50 -threads 3 -server 3000
    ```

    - `-html` specifies the URL for the HTML documentation.
    - `-crawl` sets the number of pages to crawl (50 in this example).
    - `-threads` specifies the number of threads to use (3 in this example).
    - `-server` sets the port for the server (3000 in this example).

## Contact

For any questions or feedback, please reach out to [benjamin.kamin.81@gmail.com].
