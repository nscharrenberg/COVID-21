<br/>
<p align="center">
  <a href="https://github.com/nscharrenberg/COVID-21">
    <img src="https://www.childcareaware.org/wp-content/uploads/2020/03/EmergencyPrep-Pandemic.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">COVID-21 The Game</h3>

  <p align="center">
    Prevent another pandemic! Fight against COVID-21 and eradicate all its mutations!
    <br/>
    <br/>
    <a href="https://github.com/nscharrenberg/COVID-21/wiki"><strong>Explore the docs »</strong></a>
    <br/>
    <br/>
    <a href="https://github.com/nscharrenberg/COVID-21/issues/new?assignees=&labels=bug&template=bug_report.md&title=%5BBUG%5D">Report Bug</a>
    .
    <a href="https://github.com/nscharrenberg/COVID-21/issues/new?assignees=&labels=enhancement&template=feature_request.md&title=%5BFEATURE%5D">Request Feature</a>
  </p>
</p>

![Contributors](https://img.shields.io/github/contributors/nscharrenberg/COVID-21?color=dark-green) ![Issues](https://img.shields.io/github/issues/nscharrenberg/COVID-21) 

## Table Of Contents

* [About the Project](#about-the-project)
* [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Authors](#authors)
* [Acknowledgements](#acknowledgements)

## About The Project

![Screen Shot](https://raw.githubusercontent.com/nscharrenberg/COVID-21/master/preview.png)

This project is developed in assignment of the [department of Data Science & Knowledge Engineering](https://www.maastrichtuniversity.nl/education/bachelor/data-science-and-artificial-intelligence) from the University of Maastricht.

COVID-21 is based of the board game [Pandemic](https://www.zmangames.com/en/games/pandemic/) and contains the same rule set and similar visualization as [Pandemic the board game](https://www.zmangames.com/en/games/pandemic/).

The eventual goal is to create an AI for the game that together with the other players tries to defeat COVID-21 and it's mutations.

## Built With

The game is developed using [JMonkey Engine 3](https://wiki.jmonkeyengine.org)

## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

In order to make a build you need the following prerequisites:

* Java SDK 16+
* Maven

### Installation

1. clone or download: 
``` 
git clone git@github.com:nscharrenberg/COVID-21.git
```

2. run `maven install` to install all required libraries

3. run `maven compile` to compile the game

4. run `maven exec:java` to run the game.

## Usage

Take a look at the "Show Rules" button when you started the game. (after you configured the game and press the "Start Game" button.
or refer the COVID-21 docs, on the top of this read me.
Additionally you could also take a look at the [Pandemic rules](https://images.zmangames.com/filer_public/53/ed/53edbee8-adfb-4715-899f-dd381e1420d7/zm7101_rules_web.pdf).

Once the game starts you have a menu on the bottom left which you can use to gather information about the players or to select the actions you would want to use.
After selecting an action or event to play you select the city you want to execute it on.
Dialog boxes may popup asking you for some additional information (such as choosing the disease to cure), or general error messages for invalid moves.


## Roadmap

See the [open issues](https://github.com/nscharrenberg/COVID-21/issues) for a list of proposed features (and known issues).

## Contributing

We only allow contributions from the assigned project group.

### Creating A Pull Request

1. Clone the project
2. Create your Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request from your branch to the master branch.
6. Wait for approval from the lead developer.

## License

The game code is to not be used in other projects, unless explicit permission has been granted by the developers.

## Authors

* **Noah** - *Software Engineer & Bsc DSAI Student* - [Noah](https://nscharrenberg.nl) - *Lead Developer*
* **Lorenzo** - *Bsc DSAI Student* - [Lorenzo](https://github.com/Lozzio99) - *Developer*
* **Eric** - *Bsc DSAI Student* - [Eric](https://github.com/Wei17083) - *Developer*
* **Kai** - *Bsc DSAI Student* - [Kai](https://github.com/KaiK-Um) - *Developer*
* **Illian** - *Bsc DSAI Student* - [Illian](https://github.com/SuleymanIII) - *Developer*
* **Drago** - *Bsc DSAI Student* - [Drago](https://github.com/DragoStoyanovDKE) - *Developer*

## Acknowledgements

* [Google Guice](https://github.com/google/guice)
* [Katharina Schneider](#)
* [Chiara Sironi](#)
* [Bill Gates](https://nl.wikipedia.org/wiki/Bill_Gates)
* [Steve Jobs](https://nl.wikipedia.org/wiki/Steve_Jobs)
* [Linus Torvalds](https://nl.wikipedia.org/wiki/Linus_Torvalds)
* [Google Guava](https://github.com/google/guava)
* [Lemur](https://github.com/jMonkeyEngine-Contributions/Lemur)
* [Google Gson](https://github.com/google/gson)

## Known Issues

* ~~Event Cards Missing~~
* ~~There is a 5th player in the config menu visible? (It only adds a new player when you click the ADD button, and when you click on "Add" for player 5 you get an error, thus you only have 4 players) -> Prossible fix: Add check in "Add" function in configuration screen to ensure a 5th player will not be entered (rendered).~~
* ~~Infection cubes not getting removed visually after treating. (unique identifiers required to fix) -> Possible fix: Instead of using the index of the array for it's naming when rendering, so utilize an identifier (ID) which should not change while it's placed in a city (or not at all throughout the game, your choice). So when rendering it'll take that name instead of the index of an array.~~
* Actions that can not be used are still used in the Action menu, they should not be available at all. remove them.
* ~~One of the pawns is fully white, this should get the corresponding color to it's role. (I think it's the only role with a custom color)~~
* Missing GUI elements (due to forced refactor)
* RHEA incomplete implementation
* MCTS implementation missing

## Do Not Fix (or fix at very last)
* ~~Dispatcher role missing~~
* Fullscreen crash 
* Overlapping menu items 
* Items off-screen with smaller resolutions. (and apparently multiple monitors)
* Lines that go from e.g Tokyo to San Fransisco go across the map, instead of wrapping over edges.
* Settings screen has duplicate resolution options in the dropdown.
