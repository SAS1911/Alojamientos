$docxPath = "PatronesGRASP_GrupoM.docx"
$zipPath = "PatronesGRASP_GrupoM.zip"
$tempDir = "temp_docx"

if (Test-Path $tempDir) { Remove-Item -Recurse -Force $tempDir }
if (Test-Path $zipPath) { Remove-Item -Force $zipPath }

# Copy to .zip
Copy-Item $docxPath $zipPath

Expand-Archive -Path $zipPath -DestinationPath $tempDir
$xmlPath = Join-Path $tempDir "word/document.xml"
if (Test-Path $xmlPath) {
    [xml]$xml = Get-Content -Path $xmlPath -Encoding UTF8
    $namespaces = @{w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"}
    $paragraphs = Select-Xml -Xml $xml -XPath "//w:p" -Namespace $namespaces
    $textList = @()
    foreach ($p in $paragraphs) {
        $texts = Select-Xml -Node $p.Node -XPath ".//w:t" -Namespace $namespaces | ForEach-Object { $_.Node.InnerText }
        if ($texts) {
            $textList += ($texts -join "")
        }
    }
    $textList -join "`r`n`r`n" | Out-File -FilePath "docx_content.txt" -Encoding utf8
    Write-Output "Successfully extracted DOCX text to docx_content.txt"
} else {
    Write-Error "Could not find word/document.xml"
}

if (Test-Path $tempDir) { Remove-Item -Recurse -Force $tempDir }
if (Test-Path $zipPath) { Remove-Item -Force $zipPath }
