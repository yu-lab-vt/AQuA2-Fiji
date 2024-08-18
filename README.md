# AQuA2 Fiji plugin

This is the site for the Fiji plugin of AQuA2.
For details, please visit [the main repository](https://github.com/yu-lab-vt/AQuA2).

# How to install:

Neccessary:

1. Download the newest release .jar file from [releases](https://github.com/yu-lab-vt/AQuA2-Fiji/releases).
2. Download three .jar files in "lib" folder
3. Move "AQuA2.jar", "MorphoLibJ_-1.6.2.jar", "jdistlib-0.4.5.jar" files into "Fiji.app\plugins\"

Optional:

4. If there is a reminder "Need to update Jna lib" during use, replace "Fiji.app\jars\jna-x.x.x.jar" by "jna-jpms-5.13.0.jar"

5. Some algorithms are coded in C++ and compiled into dynamic libraries. On Windows systems, Microsoft Visual C++ redistributable is required, which can be obtained from [here](https://learn.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist). For Linux system, g++11 or higher version is required.

# How to use:

In Fiji menu, select "Plugins -> AQuA2". Then everything would be similar to MATLAB GUI of AQuA2.
For GUI instructions, please visit [the main repository](https://github.com/yu-lab-vt/AQuA2).

# Update:
* 2024-05-15: Fix the issue that cannot link .dll files

* 2024-02-22: Synchronize the watershed function in both MATLAB and Fiji versions. Now thte results of two versions are the same.

* 2024-01-18: Update files for Linux and Mac systems.