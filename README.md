2048 Game with AI
=========

Description
-----
This is a simplified version of the 2048 game. My version will not spawn a 4 tile
but will always spawn a 2 tile for every move. This is to simplify game mechanics
as well as reduce complexity required for the AI. The AI itself is built with
Expectiminimax algorithm with various performance improvements. 

Install & Requirements
-----
You can either run this project inside Intellij IDEA or in terminal on Linux/Mac.
Software requirements are as follow:  
1. JDK 11 and Make (For Linux/Mac)  
2. Intellij IDEA (If you are using IDEA to compile/run the game)  

How to run the program
-----
**Using Intellij IDEA**: I have included necessary files, just pull the repo 
and open with IDEA.

**Using terminal in Linux or Mac**: run the following command to compile and run the game  
```
make
```

Instructions
-----
| **Button** | **Functions** |
| ------ | ------ |
| UP, DOWN, LEFT, RIGHT | Play the game, shifting tiles to one side and merge them together to gain points. |
| ENTER | Enable/Disable the AI. User will lose control of the game when AI is enabled. Disable AI to gain control back. | 
| C | Change AI's level of intelligence (depth of search in Expectiminimax) | 