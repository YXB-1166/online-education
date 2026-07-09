@echo off
REM =============================================================
REM 在线教育系统 MySQL 定时备份 - 安装 & 运行
REM =============================================================
REM
REM 手动运行: 双击此文件 或 在命令行执行
REM 设定定时任务（管理员）:
REM   schtasks /CREATE /SC DAILY /TN "EduMysqlBackup" /TR "powershell.exe -ExecutionPolicy Bypass -File \"D:\ChineseSoftWork\realwork\online-education\sql\backup\backup_mysql.ps1\"" /ST 03:00 /RL HIGHEST
REM
REM 删除定时任务:
REM   schtasks /DELETE /TN "EduMysqlBackup" /F
REM =============================================================

powershell.exe -ExecutionPolicy Bypass -File "%~dp0backup_mysql.ps1"
pause
