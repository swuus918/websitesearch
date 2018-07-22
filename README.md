# websitesearch
Simple Website Searcher. This would go to https://s3.amazonaws.com/fieldlens-public/urls.txt and look at all the rows after the header and pick up the second column as the URL to do web search using the search term provided as input. You can also supply how many concurrent HTTP request as the second input.

## Prerequisite
Latest Java Runtime Environment installed

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
