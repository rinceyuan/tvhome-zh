package com.gathin.tvhome;

final class NetworkStatus {
    static final int OFFLINE = 0;
    static final int OTHER = 1;
    static final int WIFI = 2;
    static final int ETHERNET = 3;

    int connection = OFFLINE;
    private boolean vpn;

    void addNetwork(boolean isVpn, boolean ethernet, boolean wifi, boolean internet) {
        if (isVpn) {
            vpn = true;
            return;
        }
        if (!internet) return;
        connection = Math.max(connection, ethernet ? ETHERNET : wifi ? WIFI : OTHER);
    }

    boolean hasVpn() {
        return vpn && connection != OFFLINE;
    }
}
