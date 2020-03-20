.DEFAULT_GOAL := run

run: compile
	java -cp out Game2048.GUI.Gui2048

compile: clean
	mkdir -p out
	javac -source 1.8 -target 1.8 -d out src/Game2048/AI/*.java src/Game2048/Game/*.java src/Game2048/GUI/*.java

release: compile
	cp Manifest.txt ./out
	cd out; jar cfm Game2048.jar Manifest.txt Game2048/*
	mv out/Game2048.jar .

clean:
	rm -rf out
