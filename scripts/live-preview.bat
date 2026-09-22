@echo off
REM Atalho pra rodar o live-preview.ps1 com duplo clique ou "scripts\live-preview.bat".
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0live-preview.ps1" %*
