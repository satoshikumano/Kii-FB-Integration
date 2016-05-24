# Facebook and Kii SDK integration.

This sample application shows how to integrate Facebook and Kii Cloud user sign up/in.

## Preparation

- Edit [Config.class](app/src/main/java/com/pgtest/example/kii/fb_integration/Constants.java). Replace the KII\_APP\_ID, KII\_APP\_KEY and KII\_SITE with your application's one. You can confirm the value on [Kii Developer Console](https://developer.kii.com).

- Edit [strings.xml](app/src/main/res/values/strings.xml).
Replace the facebook\_app\_id value with your Facebook application's one.
You can confirm the value on [Facebook Developer](https://developers.facebook.com)

### Configure Kii Application
Please refer to this [guide](http://docs.kii.com/en/guides/android/managing-users/social-network-integration/facebook/#configuring-facebook-integration).

### Configure your Facebook Application
Please refer to this [guide](https://developers.facebook.com/docs/android/getting-started/).

## Run app.
- After the app started, Click Facebook Login Button and login to Facebook.

After succeeded, Application start process signup/ signin to Kii Cloud.

You'll see message on top of the screen when succeeded/ failed.

