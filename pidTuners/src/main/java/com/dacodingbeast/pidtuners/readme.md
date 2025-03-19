# PSO4PID

This is a simple pivot PID controller tuner for FTC robots.

## Installation

### Recommended Setup

Clone this repository and follow the tuning guide [here](tuning.md)

If you want to implement this into you own repository

1. Edit TeamCode build.gradle and add repositories{maven { url 'https://jitpack.io' } maven {
   url 'https://maven.brott.dev/' }}
2. In the project build.gradle (name of repository - build.gradle) remove the repositories {
   mavenCentral() } at the bottom of the file.
3. In the build.common.gradle change compileSdkVersion 29 to compileSdkVersion 34
