package com.angopapo.datooapp.app;


public class Config {

    // Parse Server
    public static final String SERVER_URL = "https://serverUrl/";
    public static final String LIVE_QUERY_URL = "ws://livequeryUrl";
    static final String SERVER_APP_ID = "HYCScpeir3rgUpdjdfdfdfTpjYPQVqSvGDHS";
    static final String SERVER_CLIENT_KEY = "DF4YRDEDZbOIpDSFfdfdfJDssDFfdfdUJ56F";

    // FlutterWave
    public static final String FLUTTERWAYE_PUBLISH_KEY = "FLWPUBK-b3aa7d507e19a3d68ecfadbace19b569-X";
    public static final String FLUTTERWAYE_ENCRYPTION_KEY = "92a7ebb56ffcd02c727dc313";

    // PayPal
    public static final String PAYPAL_CLIENT_ID = "AeDV4Lr7Byih9L7tccceD18iI8ng6fXbtUdzzql6ibEdzP3xc-KYSAQnxsbYen2pI7U2ES5iKrq3YEAy";

    // PaymentWall
    public static final String PAYMENT_WALL_PROJECT_KEY ="3027b23d9d40763c6d950dfb6d9e828e";
    public static final String PAYMENT_WALL_SECRET_KEY ="a9f522a8f6012a240352f35b133ca648";

    // Push notifications
    public static final String CHANNEL = "global";

    // Agora API
    public static final String AGORA_APP_ID = "0d4931449ffdfd8094fff5bc5eab03";

    // Instagram API
    public static final String INSTAGRAM_APP_ID = "21117443498873";
    public static final String INSTAGRAM_APP_SECRET = "8c56be05129ffdds6d9a8a9a4b31900";
    public static final String INSTAGRAM_REDIRECT_URI = "https://www.angopapo.com/";

    // Google Admob
    static final String HOME_BANNER_ADS = "ca-app-pub-8614234010137830/5572570626";
    static final String REWARDED_ADS= "ca-app-pub-8614234010137830/1441753926";
    static final String NEARBY_NATIVE_ADS = "ca-app-pub-8614234010137830/4913013508";
    static final String ENCOUNTERS_NATIVE_ADS = "ca-app-pub-8614234010137830/4274912773";

    // Google Play In-app Purchases IDs
    public static final String CREDIT_550 = "datoo.550.credits";
    public static final String CREDIT_100 = "datoo.100.credits";
    public static final String CREDIT_1250 = "datoo.1250.credits";
    public static final String CREDIT_2750 = "datoo.2750.credits";

    public static final String PAY_LIFETIME = "datoo.pay.lifetime";

    //Google Play In-app Subscription IDs
    public static final String SUBS_3_MONTHS = "datoo.3.months";
    public static final String SUBS_1_WEEK = "datoo.1.week";
    public static final String SUBS_1_MONTH = "datoo.1.month";
    public static final String SUBS_6_MONTHS = "datoo.6.months";

    // Web links for help, privacy policy and terms of use.
    public static final String HELP_CENTER = "https://datoo.angopapo.com/help.html";
    public static final String PRIVACY_POLICY = "https://datoo.angopapo.com/privacy.html";
    public static final String TERMS_OF_USE = "https://datoo.angopapo.com/terms.html";
    public static final String TERMS_OF_USE_IN_APP = "https://datoo.angopapo.com/terms.html";

    // Withdraw tokens to PayPal
    public static final int MinTokenWithdraw = 200; // Minimum amount of tokens allowed to exchange
    public static final int WithdrawExchangeRate = 100; // 100 tokens equal to 1 USD

    // Enable or disable logins system
    public static final boolean EMAIL_LOGIN = true;
    public static final boolean PHONE_LOGIN = true;
    public static final boolean GOOGLE_LOGIN = true;
    public static final boolean FACEBOOK_LOGIN = true;

    // Enable or Disable Payment Systems
    public static final boolean PAYPAL_ENABLED = true;
    public static final boolean FLUTTER_WAVE_ENABLED = true;
    public static final boolean GOOGLE_PAY_ENABLED = true;
    public static final boolean PAYMENTWALL_ENABLED = false;

    // Extra features to add in payment methods
    public static final boolean PAYPAL_CREDIT_CARD_ENABLED = false;

    // Exchange your tokens
    public static final int MinTokenExchange = 100; // Minimum amount of tokens allowed to exchange
    public static final int TokenExchangeRate = 2; // 2 tokens equal to 1 Credit

    // Enable or Disable paid messages.
    public static final boolean isPaidMessagesActivated = false;

    // Credits needed to activate features
    public static int TYPE_RISE_UP = 50;
    public static int TYPE_GET_MORE_VISITS = 100;
    public static int TYPE_ADD_EXTRA_SHOWS = 100;
    public static int TYPE_SHOW_IM_ONLINE = 100;
    public static int TYPE_3X_POPULAR = 200;

    // Amount of days to activate features
    public static int DAYS_TO_ACTIVATE_FEATURES = 7;

    // Enable or Disable Ads and Premium.
    public static final boolean isAdsActivated = false;
    public static final boolean isPremiumEnabled = true;
    public static final boolean isNearByNativeAdsActivated = false;
    public static final boolean isEncountersNativeAdsActivated = false;

    // Encounters crush
    public static final boolean isCrushCreditNeeded = true;
    public static final int CrushCreditNeeded = 50;
    public static final boolean isCrushAdsEnabled = true;
    public static final int CrushAdsLimitPerDay = 5;

    // Application setup
    public static final String bio = "Hey! i'm using datoo!";
    public static final int WelcomeCredit = 10;
    public static final int MinimumAgeToRegister = 18;
    public static final int MaximumAgeToRegister = 80;
    public static final int MaxUsersNearToShow = 100;
    public static final int DistanceForRealBadge = 5;
    public static final int DistanceForRealKm = 40;
    public static final int MinDistanceBetweenUsers = 20;
    public static final int MaxDistanceBetweenUsers = 1000;
    public static final double DistanceBetweenUsersLive = 1000;
    public static final boolean ShowBlockedUsersOnQuery = true;
    public static final boolean isVideoCallEnabled = true;
    public static final boolean isVoiceCallEnabled = true;
    public static final int ShowNearbyNativeAdsAfter = 15;
    public static final int ShowEncountersNativeAdsAfter = 2;
    public static final int freeMessagesInTotal = 10;
    public static final int freeMessagesPerDay = 5;

    // Enable or Disable Fake messages.
    public static final boolean isFakeMessagesActivated = false;
    public static final String defaultFakeMessage = "Hello, how are you ?";
}