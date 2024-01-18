# AQuA2 Fiji plugin

This is the site for the Fiji plugin of AQuA2.
For details, please visit [the main repository](https://github.com/yu-lab-vt/AQuA2).

# How to install:

1. Download the newest release .jar file from [releases](https://github.com/yu-lab-vt/AQuA2-Fiji/releases).
2. Download three .jar files in "lib" folder
3. Move "AQuA2.jar", "MorphoLibJ_-1.6.2.jar", "jdistlib-0.4.5.jar" files into "Fiji.app\plugins\"
4. (If there is a reminder "Need to update Jna lib" during use, replace "Fiji.app\jars\jna-x.x.x.jar" by "jna-jpms-5.13.0.jar")

# How to use:

In Fiji menu, select "Plugins -> AQuA2". Then everything would be similar to MATLAB GUI of AQuA2.
For GUI instructions, please visit [the main repository](https://github.com/yu-lab-vt/AQuA2).

# TODO Note:

* Since the watershed packages in MATLAB and Fiji have some differences, the results may differ a little bit. Will unify these algorithms later.
* Current jar can only work on 64-bit Windows system. Will adjust the code to make it compatible with Mac later.

# Update:
* 2024-01-18: Update files for Linux and Mac systems.