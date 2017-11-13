## FileMover

This is a small, lightweight utility app that simply copies arbitrary files from one location to another at the click of a button. It originally came into being to help my organisation's non-technical testers to easily deploy .war files that they acquired from developers to their local containers.

### Usage

Simply double-click the FileMover.jar file to run it. While the app is running, you can reload the configuration by pressing F5 - this will reread the configuration.json file found in `%USER_HOME%/FileMover/` and refresh the UI accordingly without the need to restart the app.

#### For Developers

You can view the console output for debugging purposes if you launch the app via CLI using `java -jar "File Mover.jar"`, or run the app as a Java Application within your IDE.

### Compatibility

FileMover aims to be platform-independent, so it _should_ work on all platforms. It has been tested and verified on the following:
 - Windows 7
 - Windows 10

#### About the Author

I'm a Software Delivery Lead and Java Developer at MHR, based in Nottingham, England. I've been a professional developer since early 2012.
