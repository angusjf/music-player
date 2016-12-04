all: comp run clean
comp:
	javac *.java
run:
	# -Xdock:icon=resources/logo.png (adds icon on macOS)
	java -cp .:+libs/* Main
clean:
	rm *.class
