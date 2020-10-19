$resultsFile = $args[0]
$resultsFile >> $logFile
$token = $args[1]
$cxURL = $args[2]
[xml]$report = Get-Content -Path $resultsFile
$body = @{
    token = $token
    scanComments = $report.CxXMLResults.scanComments
}
$cxURL += "/" + $report.CxXMLResults.ScanId
Invoke-RestMethod -Method 'Post' -Uri $cxURL -Body $body