# AssetLocationLogger
Android app to log GPS data to your own Google Forestore back-end.
You install the Logger app (this app) to the device which is attached to the asset to be tracked.
There's a companion app which is the Controller and Viewer for the logger app and the back-end.

The main purpose is that I don't store or even touch any of your data. You just have to spin out your own Firebase project with your Firestore back-end, and you'll control your own data. This eco-system supports strict data control (only specified users can record or view), multiple assets to track, and ad-hoc geo-fencing (think about alarming or disarming your car).

The intended use:

0. Create Firebase project with Firestore back-end enabled.
1. Populate google-services.json into the source code.
2. Build and install this Logger app on the phone which will be placed in the asset to be protected.
3. Start the app, turn off battery optimization for it and also make it start by default.
4. Since the phone may not be in an accessible location at the protectable asset, and it won't be in an interactive environment, the app is using Anonymous login to log the GPS data.

For managing the asset that's the companion app's role.

Notes:
* The original intent was to give a choice for the user after app start to pick a Firebase project with the Firestore by supplying Project ID, App ID and API Key. However very sadly it turns out that it's impossible to a.) not tie the app to a default Firebase project: a user can only select a secondary project to connect to later. b.) Google Services has to be intermingled with the app (even if [Google Play Services is not required](https://github.com/FirebaseExtended/auth-without-play-services). Someone might avoid Play Services, but that would require reverting to REST API which [may stop functioning](https://github.com/FirebaseExtended/auth-without-play-services/issues/2) and also would not provide automatic off-line data caching and upload for network coverage outages, plus you still need Google Services, so that's a dead-end). Since the default Firebase project for the app would be mine I wouldn't want to release any app to the App Store which would handle such extremely sensitive data, hence you'll need to build this on your own and side load it by yourself. A little more DIY work than form the app store, although you'd have to setup your Firestore anyway.
* In my case I'll use this as a loss prevention measure. Since the protected asset can be stolen along with the logger phone, you do not want to log in with your own credentials to the logger phone, that would be a ticking time bomb if the theif is smart and can break into the phone! Personally I'm setting up Google accounts as virtual children and will manage them with Google Family Link. Since I do not want to release this app to the App store (see previous point), I'll have to [allow app installations from unknown sources](https://community.useboomerang.com/hc/en-us/articles/360025964892-Unknown-Sources-is-blocked-by-Administrator-Google-Family-Link-installed-) and to allow developer options on the family link device so I can side load it. This is a trade-off needed to be taken. These rights can be granted from the Family Link "parent" app.
* In any event the logger would stop working it'd need to be restarted on it's own. Since the phone will not be in an interactive situation, that has to happen without user interaction, so I must use anonymous login for the logger. This is another trade off I had to make.
