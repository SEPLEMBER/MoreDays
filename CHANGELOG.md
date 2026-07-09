# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

<!--
################################################################################
### PLEASE LINK THE ISSUES IF THERE IS ONE, OTHERWISE LINK THE PULL-REQUESTS ###
################################################################################
e.g. - - Add new unit type `Assignment`. [#600](https://gitlab.com/wuapps/moredays/-/issues/600)
-->

## [1.0.26] - 2026-07-08 - 🏁-Release 

### Added
- Setting for duration of showing the affirmation, issue [#22](https://gitlab.com/wuapps/moredays/-/work_items/22)
- Setting for timing, when to show the affirmation, issue [#23](https://gitlab.com/wuapps/moredays/-/work_items/23)
- Allow goals with 0 points, issue [#25](https://gitlab.com/wuapps/moredays/-/work_items/25)
- Dark background on affirmation overlay for better readability
- Tips & tricks: explain 0-point goals (not part of daily 100%) and 0-point activities (tracking only) as additional use cases
- Add some animations
- Add settings for supported languages

### Fixed
- Keyboard hiding input fields when editing or creating a scale/activity or goal
- Affirmation overlay hidden behind the progress circle
- Images not shown in journal list view (were visible in detail view only)
- layout problems
- app crash after switching light/dark mode

### Refactored
- Upgraded target SDK to Android 17 (API 37)
- Charts: legend for goal history chart now readable with many goals
- Charts: goal summary cards show goal title below the circle with a thinner ring
- update libs

## [1.0.25] - 2026-03-07 - 🏁-Release 

### Fixed
- Clicking a goal with activities worth 0 points no longer fails to update the view correctly

### Refactored
- updated libs

## [1.0.24] - 2024-10-29 - 🏁-Release 

### Added
- better journaling [#19](https://gitlab.com/wuapps/moredays/-/issues/19)
  - configurable journaling questions, guided journal 
  - suggestions for journaling notes 

### Fixed
- do not show inactive activities [#17](https://gitlab.com/wuapps/moredays/-/issues/17)
- update 100% line in charts correctly [#18](https://gitlab.com/wuapps/moredays/-/issues/18)

### Refactored
- updated libs

## [1.0.23] - 2024-03-14 - 🏁-Release

### Fixed
- delete diary entry [#16](https://gitlab.com/wuapps/moredays/-/issues/16)
- confetti was not shown for the second goal made

### Refactored
- updated libs
- made hide/show settings more intuitive

## [1.0.22] - 2024-02-21 - 🏁-Release

### Added
- push notifications [#8](https://gitlab.com/wuapps/moredays/-/issues/8)

### Fixed
- change color of cancel button
- after selecting a different date and navigating to a different screen, the label for the date was mixed up
- it was not possible to change to light mode
- counter intuitive labeling in settings
- fix image problem on android 13+ [#4](https://gitlab.com/wuapps/moredays/-/issues/4)

### Refactored
- optimized code
- updated libs
- splash screen -- ready for android 13+

## [1.0.21] - 2024-01-31 - 🏁-Release

### Added

- add Italian, feat [#10](https://gitlab.com/wuapps/moredays/-/issues/10), thanks to [RAMAR RAR](https://gitlab.com/RAMAR-RAR) for the translation
- auto capitalize sentences in journal, feat [#14](https://gitlab.com/wuapps/moredays/-/issues/14)
- add delete confirmation for journal entries, feat [#13](https://gitlab.com/wuapps/moredays/-/issues/13)

### Fixed

### Refactored

- update libs

## [1.0.20] - 2024-01-14 - 🏁-Release

### Added

- add x to cancel an activity of a day directly in the home screen (issue # 5)
- swipe through days (issue # 6)

### Fixed

### Refactored

- updated to new libs

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
