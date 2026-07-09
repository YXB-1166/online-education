# Database backup script for edu system
$containerName = "mysql-edu"
$mysqlUser     = "root"
$mysqlPass     = "123456"
$database      = "edu"
$backupRoot    = Join-Path $PSScriptRoot "."
$dateStr       = Get-Date -Format "yyyyMMdd_HHmmss"
$backupFile    = Join-Path $backupRoot "edu_backup_$dateStr.sql"
$maxBackups    = 30

$now = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
Write-Host "[$now] Starting backup of $database ..."

$dumpCmd = "mysqldump -u$mysqlUser -p$mysqlPass --default-character-set=utf8mb4 --single-transaction --routines --triggers --databases $database"
$result = docker exec $containerName sh -c $dumpCmd 2>&1
$result | Out-File -FilePath $backupFile -Encoding UTF8

if (Test-Path $backupFile) {
    $len = (Get-Item $backupFile).Length
    $sizeKB = [math]::Round($len / 1024, 2)
    Write-Host "  OK  Backup saved: $backupFile ($sizeKB KB)"
} else {
    Write-Host "  FAIL Backup failed"
    exit 1
}

$allBackups = Get-ChildItem -Path $backupRoot -Filter "edu_backup_*.sql" | Sort-Object LastWriteTime -Descending
if ($allBackups.Count -gt $maxBackups) {
    $toDelete = $allBackups[$maxBackups..($allBackups.Count-1)]
    foreach ($f in $toDelete) {
        Remove-Item -LiteralPath $f.FullName -Force
        Write-Host "  DEL Removed old backup: $($f.Name)"
    }
}

$now = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
Write-Host "[$now] Backup completed"
