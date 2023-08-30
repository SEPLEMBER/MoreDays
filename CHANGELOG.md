# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

<!--
################################################################################
### PLEASE LINK THE ISSUES IF THERE IS ONE, OTHERWISE LINK THE PULL-REQUESTS ###
################################################################################
e.g. - - Add new unit type `Assignment`. [#600](https://gitlab.com/wuapps/moredays/-/issues/600)
-->
## [Unreleased]
### Added
### Fixed
### Refactored

## [1.0.18] - 2023-08-30 - 🏁-Release
### Added
- add option to show motivation at start or not
- add export to markdown format
- add more tables to excel export
- backup/restore db-file

### Fixed
- show confetti once
### Refactored
- updated to new libs

## [1.0.17] - 2023-05-16 - 🏁-Release
### Fixed
- journal: pick images up to 2mb only, removed restriction

### Refactored
- updated to new libs


## [1.0.16] - 2023-03-23 - 🏁-Release
### Added
- option to enter journal entries more than once a day, you may choose between day/hour/minute

### Refactored
- updated to new libs

## [1.0.15] - 2023-02-01 - 🏁-Release
### Added
- setting to choose if % or n/m (=n of m points) should be shown
- add explanation to circles in analyse
- add crop to image selection in journal

### Fixed
- scroll did not work in bar chart (analyse)
- circles in analyse showed 0 in row, although there were more, but simply the minimum was not reached
- improve visibility of emoji in journal
- image library does not show all images -> changed library
- goals with 0 points are not allowed

### Refactored
- updated to new libs

## [1.0.12] - 2022-09-23 - 🏁-Release
### Added
- make welcome screen skippable
- add settings to switch off animations
- export data (no journal images) to excel --> needs min sdk version 26 for the whole app!
- support to install/move to sd card

### Refactored
- updated to new libs

## [1.0.11] - 2022-07-18 - 🏁-Release

### Fixed
- delete goal and answering the dialog with no deleted the goal anyhow, fixed.
- bottom navigation showed wrong selection after going deeper into the app, see issue #3, fixed.

### Refactored
- updated to new libs

## [1.0.10] - 2022-05-22 - 🏁-Release

### Fixed

- FDROID lint error #1

## [1.0.9] - 2022-04-14 - 🏁-Release

### Fixed

- 100% line after editing goal points or activate/inactivate a goal 
- update libs and fix code inspection issues

### Added

- support negative points in activities

## [1.0.8] - 2022-01-13 - 🏁-Release

### Added

- Prepare release for FDROID


## [1.0.6] - 2021-12-15 - 🏁-Release

### Fixed

- fix/refactor/style: lottie, dark mode fixes, hundred percent fixes
