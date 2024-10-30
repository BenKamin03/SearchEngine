cd frontend
web: npm i --force && npm run build
cd ../
server: java -cp target/SearchEngine-1.0-SNAPSHOT.jar edu.usfca.cs272.Driver -html https://usf-cs272-spring2024.github.io/project-web/docs/api/allclasses-index.html -crawl 50 -threads 3 -server 3000