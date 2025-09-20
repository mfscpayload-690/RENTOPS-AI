@echo off
echo Cleaning...
if exist clean_bin rmdir /s /q clean_bin
mkdir clean_bin

echo Compiling...
javac -cp ".;lib/*" -d clean_bin dao/*.java models/*.java services/*.java ui/*.java utils/*.java

echo Copying icons...
xcopy /E /I icons clean_bin\icons

echo Running application...
java -cp "clean_bin;lib/*" ui.Main

echo Done.