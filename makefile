all: comp run
comp:
	javac -d . src/*.java
run:
	java -cp .:lib/* Main
clean:
	rm *.class
