# AssetLocationLogger
Android app to log GPS data to your own Google Forestore back-end.
You install the Logger app (this app) to the device which is attached to the asset to be tracked.
There's a companion app which is the Controller and Viewer for the logger app and the back-end.

The main purpose is that I don't store or even touch any of your data. You just have to spin out your own Firebase project with your Firestore back-end, and you'll control your own data. This eco-system supports strict data control (only specified users can record or view), multiple assets to track, and ad-hoc geo-fencing (think about alarming or disarming your car).

The intended use:
0. Create Firebase project with Firestore back-end enabled.
1. Install this Logger app on the phone which will be placed in the asset (car) need to be protected.
2. Start the app and login with the asset logger phone's user into the Logger app.
3. Add that logger user privileges in your Firestore back-end so it'll be allowed to log GPS data.
4. Configure the logger app with your Firebase project's ID, API Key, and also specify the unique name of the asset (this is so you can protect multiple assets (cars).
5. From then on the Logger will log the location every hour, or by the frquency it's instructed to. The app checks the frequency every minute, so if the Manager app instructs for more active reporting.
6. Place the phone in the protected asset.

For managing the asset that's the companion app's role.
