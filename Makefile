.DEFAULT_GOAL := run

run: compile
	java -cp out Game2048.GUI.Gui2048

compile: clean
	mkdir -p out
	javac -d out src/Game2048/AI/*.java src/Game2048/Game/*.java src/Game2048/GUI/*.java

clean:
	rm -rf out
