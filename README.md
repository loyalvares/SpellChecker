# Spell Checker

This application is a simple spell checker built using vanilla Java. There are no external libraries used for this project.
This Spell Checker application will perform three tasks:
1. Spot the misspelled words in a file by checking each word in the file against a provided dictionary.
2. Provide the user with a list of alternative words to replace any misspelled word.
3. Write a new file with the corrected words as selected by the user.

Please note that this application assumes that the input files have no punctuation. Hence punctuation ig ingored completely.
This means no periods, question marks, quotations, apostrophes: just think of this as a stream of words. However, line breaks are allowed.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to run the application and how to set them

```
You will need a dictionary. For this application, the dictionary is called "engDictionary.txt" and is included in the project folder.
```

## Running the application

For getting the word suggestions, the following values have been set.
1. candidate word length is +/- 3 characters.
2. common percent is set to 0.5 (50%).
3. Number of top word suggestions to be returned is set to 10.

To run the application, compile and execute the main.java.spellchecker.SpellChecker class.

## Built With

* [Java 8]
* FilenameUtils.removeExtension(String filename) method borrowed from Apache commons-io library.


## Improvements That Can Be Done

* Move all configurations to a property file.
* Add support to replace words with Synonyms.
* Add support for multiple languages.