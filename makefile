all: comp run
comp:
	javac *.java
run:
	java -cp .:+libs/* Main
clean:
	rm *.class
