package com.angopapo.datooapp.modules.merlin;

class ConnectCallbackManager extends MerlinCallbackManager<Connectable> {

    ConnectCallbackManager(Register<Connectable> register) {
        super(register);
    }

    void onConnect() {
        Logger.d("onConnect");
        for (Connectable connectable : registerables()) {
            connectable.onConnect();
        }
    }

}
