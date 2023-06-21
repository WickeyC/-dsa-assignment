***************
| Mac Remarks 
***************

To compile and execute:
javac -cp ":./lib/*" */*.java && java -cp ":./lib/*" main.Main

*******************
| Windows Remarks 
*******************

To extract all the source code path:
dir /s /B *.java > sources.txt

To compile and execute:
javac -cp ".;lib/*" @sources.txt && java -cp ".;lib/*" main.Main