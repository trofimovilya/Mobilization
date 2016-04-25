### Mobilization
#### Test App for Yandex Mobile Development School 
List Screen                      |  Details Screen                      | In Action
:-------------------------------:|:------------------------------------:|:------------------------------:
![screen_lsit](/screen_list.png) | ![screen_detail](/screen_detail.png) | ![screencast](/screencast.gif)

#### Description
Simple two-screen app that fetches data from the Internet, stores it and displays it as list of artists. 
The app allows to view information about each artist and go to listening to them in Yandex.Music app
(if it is installed of course).

The app uses standard UI-features such as:
- CoordinatorLayout with CollapsingToolbarLayout
- Transitions and Shared Element Transitions
- ObjectAnimators
- SwipeRefreshLayout
- and others...

[Download APK](https://dl.dropboxusercontent.com/u/46772061/Mobilization.apk)

#### Used libraries 
- Volley
- Picasso
- Butter Knife
- EventBus by GreenRobot
- Sugar ORM

### Requirements
- API 14 or higher 
- There is a chance that to build the app you have to disable Instant Run in Android Studio 2.0 (because of Sugar ORM) 
