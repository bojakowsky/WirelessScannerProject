# WirelessScannerProject
Project created for one of the studies laboratories. 
Collaboratos: Martin Kmieciak, Krzysztof Bojakowski

Our goal was to create mobile Android app, that collects wireless data (Bluetooth, Routers, Hotspots, etc.).
Data collected is sent to Web Application, where we can display, sort, filter data we want to see.
We also built map, where all the data is displayed based on time, date, GPS localization and other parameters.
Below some screenshots of work we have done.


## Website http://wireless-collector.azurewebsites.net
After registration, account must be accepted to actually use lists or the maps.
To be accepted - send mail to krzysztof0bojakowski@gmail.com, and tell me why you need access.
Because of azure costs we can't add certifactes and SSL to our site, thus site won't be fully secured until moving from student azure subscription. And that's why site is mainly for our personal use - for now.

However, you can actually build your own site. All you need to test it, is to use trail azure - free 30 days subscription.
Of course you can also run this site on your local machine. Just fork it and run it. Database server we used for local tests is SQL Server express 2012. Also whole solution was build on Visual Studio 2015.
![alt tag](https://github.com/bojakowsky/WirelessScannerProject/blob/master/1.png)
![alt tag](https://github.com/bojakowsky/WirelessScannerProject/blob/master/2.png)
![alt tag](https://github.com/bojakowsky/WirelessScannerProject/blob/master/3.png)
![alt tag](https://github.com/bojakowsky/WirelessScannerProject/blob/master/4.png)
## Mobile App
Minimum Android API to run wireless scanner is 21. 
There is no authorization - as you run it on your phone you can start collecting data. By default data is sent to our azure web app. You will easily locate the address in android code. Type your server address and you're ready to go. Data will be sent every 12 seconds - after GPS will find you. Of course - feel free to change the configuration.
![alt tag](https://github.com/bojakowsky/WirelessScannerProject/blob/master/1a.png)
