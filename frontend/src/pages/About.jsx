import React from "react";
import Navbar from "../components/navbar/Navbar";

const About = ({ toggleTheme, selectedTheme }) => {
    return (
        <div
            className={`w-screen ${
                selectedTheme === "light" ? "text-black" : "text-white"
            }`}
            style={{height: "calc(100vh - 128px)"}}
        >
            <Navbar selectedTheme={selectedTheme} toggleTheme={toggleTheme} />
            <div className="px-8 py-4 flex justify-center items-center w-full h-full flex-col">
                <h1 className="text-2xl font-bold mb-4 w-full">
                    About My Search Engine
                </h1>
                <p className="mb-4 w-full">
                    My search engine is a full-stack application
                    designed to provide precise and efficient search results. It
                    performs the following key functions:
                </p>
                <h2 className="text-xl font-semibold mb-2 w-full">Web Crawling</h2>
                <p className="mb-4 w-full">
                    My system crawls the web to gather and index
                    information from 50 of pages from the Javadoc website. 
                </p>
                <h2 className="text-xl font-semibold mb-2 w-full">
                    Inverted Index with Stemming
                </h2>
                <p className="mb-4 w-full">
                    We create an inverted index of stemmed words. Stemming helps
                    in reducing words to their root form, enabling my search
                    engine to efficiently handle variations of words and improve
                    search accuracy. The inverted index maps each stemmed word
                    to the documents where it appears.
                </p>
                <h2 className="text-xl font-semibold mb-2 w-full">Search Results</h2>
                <p>
                    When you perform a search, my engine calculates the
                    relevance of each document based on the number of matching
                    stems relative to the total number of stems in the document.
                    This method ensures that the results are ranked according to
                    their relevance and context.
                </p>
            </div>
        </div>
    );
};

export default About;
