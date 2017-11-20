# FinalProject

Christopher Moorad, Gabe Corso, and Jack Taylor
CS 65 17F Final Project README


Included:

-An overview summary of our application and it's functionality
-An explanation of the user experience and backend for the game in whole
-Detailed explanations of each minigame within the game
-Ways to build out our application in the future



Overview:

Our application, Dart Games, is a game intended for Dartmouth students to play around Dartmouth's campus. We chose this game because it extends the functionality of the labs we worked on for class, while also putting a different spin on the type of games that can be created using location services and the google maps api. The game consists of three, location-specific minigames: Logger, Frogger, and a word game. Each minigame is specific to a particular zone on campus, and can only be played when the user is within that zone. The zone's match the environment the user is in: a water-themed game for when the user is near the river, a forest-themed game for when the user is in College Park, and a metropolitan-themed game for when the user is in the library (in the center of campus). Upon completing all three of these minigames, the user will have completed the game in whole. In order to complete the game, the user needs to go all over campus, so we are hoping that this game will encourage Dartmouth students to go outside and explore different parts of campus (in the future directions section we will discuss  opportunities for using the minigames during orientation-type campus introductions).



Game On Whole:

Carried over from previous labs in the course, the game on the whole requires signing into an account created through the CS 65 server set up by Prof. Bratus. Users have the opportunity to create a new account instead of signing into a previous one - also a functionality carried over from previous labs. Lastly carried over from previous labs is the history fragment, which displays each minigame and whether or not the user has completed it yet. Upon entering the game (pressing Play), the user sees a beautiful banner image previewing the minigames they will soon be playing. They are also prompted to hit the play button to begin playing the game. Upon starting the game, the user learns that they must get themselves to one of the game zones on their map in order to play one of the minigames. In order to create the game zone we needed to use the google polygon functionality and check for locations within them, which required implementing the google "Polygon" and "PolyUtil" classes. Once in a game zone (polygon), the user may play the corresponding minigame. The user must play the minigame until they win if they want to complete the minigame. Users may give up temporarily and try a different minigame if they wish, but in order to win the game they must complete all three. Upon winning the game, the user will be taken to a success screen and have the opportunity to leave the game or continue playing (purely for fun). If the user chooses to leave the game and come back, they will be re-informed that they won the game. To reset this, users merely need to re-enter the game from the success screen and hit the "Reset All Minigames" button, which will set all minigames to incomplete. The theory behind the way the games and victories are handled is mainly laid out via intents and activities. Three boolean variables represent the relative success of each minigame, and are passed through each inter-activity intent along with the user's username and password (for posterity). The variables are used for the entire game: keeping track of successes for the history fragment, determining the successes/failures of each minigame, and determining whole game completion.



MiniGame Overviews:

-Forest Game: The forest game features a logger that has to cut trees before they fall through the bottom of the screen. Here, we introduce sound effects (new to the course). The game serves as a reminder not to cut down young trees, as this ruins the forest. The game is complete after a given time limit, or failed if the logger cuts down young trees or misses an old tree. The forest mini-game is based on its own activity (ForestGameActivity) which uses a custom view class (ForestGameView) to display graphics. Each “character” in the game is represented by its own class. The user’s character in this game is a lumberjack, who must navigate through the forest and cut down tall, old trees while avoiding small, young trees. Each character class contains a method getDetectCollision that determines if the user’s character has collided with any of the tree characters.

-Library Game: The library game features several text boxes and requires that the user create palindromes in every direction: left to right, top to bottom, and diagonal. The game gives a success when the user successfully creates the palindromes, and the user may not succeed / must manually return to the game if a success has not been reached.

-River Game: The river game is coming soon...for now it serves as a reminder to get outside and be active.



Future Directions:

-Saving to network: given that network requests for Sergey's network were limited, we didn't get into saving game progress to a network, but it would be a great addition to functionality.

-Preferences fragment: we never ended up implementing the preferences fragment, but left it for posterity to be built out if we ever decide to continue working on the game

-Ranking fragment: it would be really neat if Dartmouth students could compare their score (in terms of time taken to complete the game) against other by looking at an additional "ranking" fragment

-More minigames: the best way to build out the game is by adding more zones and minigames! You can never have too many minigames!

-Uses: Use of this kind of game could be tailored to a variety of different types of campus exploration - from new student orientations to prospective student tours
