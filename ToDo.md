# Items that should be improved or made in the nearest feature
==========================================================================================
## Reports
* Override interface methods in reports pages not parent method
* Get rid of response objects in report data:
    * Leads report;
    * Leads preview;
    * Leads transactions;
* Scroll to proper column
* Check graphics (pagination, ranges) after filtering without cells
## Fail listener
* Immediately stop test method if error happens (is it possible?)
## Element helper
* Get rid of implicitly wait
## Screenshot
* Consolidate screen both horizontal/vertical
* Try on FF and look through it code
## Technical debts
* JSONWrapper, JSONArrayWrapper extend JSONObject, JSONArray
* The only 1 publisher under publisher user
* DetailsPage -> EditPage dependency with all
* Override each of these classes above
* filters() with hasGraphics() move to enums with 2 get filters:
** graphics verification;
** filter accordance
* Outbound under publisher user verification according to transaction type:
** Empty email
** Request/Response data
===========================================================================================
# Epics
## Logs parser + report             -           http://jira.revimedia.com/browse/RVMD-13474
## Automation project refactoring -             http://jira.revimedia.com/browse/RVMD-13472
## Extend project with test case based module - http://jira.revimedia.com/browse/RVMD-13473
## Extend project with test case based module - http://jira.revimedia.com/browse/RVMD-13473
## Performance testing -                        http://jira.revimedia.com/browse/RVMD-13864