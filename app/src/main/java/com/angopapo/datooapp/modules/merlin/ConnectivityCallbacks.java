package com.angopapo.datooapp.modules.merlin;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ConnectivityCallbacks extends ConnectivityManager.NetworkCallback {

    private final MerlinService.ConnectivityChangesNotifier connectivityChangesNotifier;
    private final ConnectivityChangeEventExtractor connectivityChangeEventExtractor;

    ConnectivityCallbacks(MerlinService.ConnectivityChangesNotifier connectivityChangesNotifier,
                          ConnectivityChangeEventExtractor connectivityChangeEventExtractor) {
        this.connectivityChangesNotifier = connectivityChangesNotifier;
        this.connectivityChangeEventExtractor = connectivityChangeEventExtractor;
    }

    @Override
    public void onAvailable(Network network) {
        notifyMerlinService(network);
    }

    @Override
    public void onLosing(Network network, int maxMsToLive) {
        notifyMerlinService(network);
    }

    @Override
    public void onLost(Network network) {
        notifyMerlinService(network);
    }

    private void notifyMerlinService(Network network) {
        if (!connectivityChangesNotifier.canNotify()) {
            Logger.d("Cannot notify " + MerlinService.ConnectivityChangesNotifier.class.getSimpleName());
            return;
        }
        ConnectivityChangeEvent connectivityChangeEvent = connectivityChangeEventExtractor.extractFrom(network);
        connectivityChangesNotifier.notify(connectivityChangeEvent);
    }

}
