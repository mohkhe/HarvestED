# HarvestED
# How to Run our Browser-Based Harvester 
## Websites Configurations
In this section, it is described how to make a confoguration file for a website to be harvested by HarvestED. Also, the information necessary to have from a website to be able to harvest it. 
##Inforamtion
To crawl a website by our harvester, the following details should be provided. This information is compulsory to be provided. Otherwise, it can make to failure or unwilling results in the crawling process. 
* The template of the website. In this template, the address of the 
* The xPath for the search results items
* The xPath for the detailed pages URLs
* The xPath for Next Page URL
To get the additional information on the detailed pages and products (e.g. jobs in job vacancies website) we need the following information:
* The xPath of Title in search result item
* The xPath of Thumbnail in search result item
Additional Information
* The URL of the website
* Comments and description

## Configuration File
We define the configurations of websites by using the openfiledescription. Such these files could be created for each of the websites and saved in the "websources/DT01/" folder.

<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/" xmlns:snipdex="http://www.snipdex.org/snipdex-opensearch/1.0/">
   <ShortName>4Shared</ShortName>
   <Description>Free File Sharing</Description>
   <Url type="text/html" template="http://search.4shared.com/q/1/{searchTerms}" snipdex:item_path="//td/table//tr/td[.//h1/a]"/>
</OpenSearchDescription>

## Database of Websites Configurations
* 1. postgreSql Server:  		teehuis.ewi.utwente.nl 
* 1. Username:			"contact us!"
* 1. Password:			"contact us!"
* 1. Database: 			"contact us!"
* 1. port:				5432
* 1. URL in java: 			jdbc:postgresql://teehuis.ewi.utwente.nl/

# Run Harvester
This is the tutorial to have the program in eclipse. In the following steps, it is described how to run the harvester. 

Step 1

