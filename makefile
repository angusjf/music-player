all: comp run clean
comp:
	javac *.java
run:
	java -cp .:+libs/* Main
clean:
	rm *.class
