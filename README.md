# websitesearch
Simple Website Searcher


## how to run it
```
java -jar ./websearch.jar -searchTerm welcome -consumerCount 20
```

## parameters
Required:
* -searchTerm: the regex that would be passed in to match the html doc of the website
* -consumerCount: the number of threads which would do HTTP request concurrently

Optional:
* -outputFilename: by default it is Results.txt
