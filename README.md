# toppages
a Jahia Community module to get the top pages from awstats.pl script.

# Usage
1. Install the module and enable it on System Site and required site
2. Go to Jahia Administration > Configuration > Top Pages Configuration
3. Add AWStats configuration: You can add the site url a regEx for the aws inclusion Filter and a regEx for the exclusion filter.
4. After adding the configuration. Go to any page and add a top pages node. You may configure the following properties:
    - Number of Results to display
    - Number of Months to aggregate (this will aggregate results for the past N month)
    - Override the Global configuration if needed
5. Publish the node
# Main Features
- Results are saved to and retreived from JCR. Hence, it will not stop if there is any connection problems with the awstats.pl script.
- RegEX Inclusion and exclusion filter to fine tune the returned results
- The ability to aggregate results for the past N months
- The ability to override Global configuration if required.
- A Cron job to automatically update the nodes based on a Quartz job, the default schedule is set to 12:AM on Sunday every week.

