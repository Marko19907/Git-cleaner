# Git cleaner
A small program written in Java that can run the git clean command on all git projects found in the given folder.
The goal is to reduce the size of the .git folder(s) and save some space.

[![Java CI with Maven](https://github.com/Marko19907/Git-cleaner/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/Marko19907/Git-cleaner/actions/workflows/maven.yml)


## Usage example
The program will run the
`` git gc --aggressive --prune ``
command on all git folders that it finds in a given directory.

For example, consider the following folder structure: 

```
root
  ├───home
  │     ├───homeProj1
  │     ├───homeProj2
  │     └───homeProj3
  └───work
        ├───proj1
        ├───proj2
        └───proj3
```

When given the folder **"root"** as the input, the program will descend down the tree to find all folders that
contain a git project and run the `` git gc --aggressive --prune `` command in each folder. 


## Requirements
* A Windows based OS, Linux and macOS are not supported.


## Getting Started

### GitHub Releases [Recommended]
Released builds can be downloaded from this repository's "Releases" page <br/>
This is the preferred method.

### Other installation methods

#### Via GitHub
Either clone the repository with git or download it as a zip, then run it with you favourite IDE. <br>
This method requires a local JDK 17 or above installation. 


## Disclaimers
* The icon was made by ["Freepik"](https://www.flaticon.com/authors/freepik) from [flaticon.com](https://www.flaticon.com/)