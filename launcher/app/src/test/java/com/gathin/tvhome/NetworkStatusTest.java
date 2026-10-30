package com.gathin.tvhome;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetworkStatusTest {
    @Test
    public void vpnDoesNotReplaceItsUnderlyingEthernet() {
        NetworkStatus status = new NetworkStatus();
        status.addNetwork(true, true, true, true);
        status.addNetwork(false, true, false, true);

        assertEquals(NetworkStatus.ETHERNET, status.connection);
        assertTrue(status.hasVpn());
    }

    @Test
    public void splitTunnelVpnIsDetectedAlongsideWifi() {
        NetworkStatus status = new NetworkStatus();
        status.addNetwork(false, false, true, true);
        status.addNetwork(true, false, false, true);

        assertEquals(NetworkStatus.WIFI, status.connection);
        assertTrue(status.hasVpn());
    }

    @Test
    public void ethernetWinsRegardlessOfEnumerationOrder() {
        for (boolean ethernetFirst : new boolean[] {false, true}) {
            NetworkStatus status = new NetworkStatus();
            status.addNetwork(false, ethernetFirst, !ethernetFirst, true);
            status.addNetwork(false, !ethernetFirst, ethernetFirst, true);

            assertEquals(NetworkStatus.ETHERNET, status.connection);
            assertFalse(status.hasVpn());
        }
    }

    @Test
    public void localOnlyNetworksDoNotMakeAStaleVpnLookConnected() {
        NetworkStatus status = new NetworkStatus();
        status.addNetwork(true, false, false, true);
        status.addNetwork(false, false, true, false);

        assertEquals(NetworkStatus.OFFLINE, status.connection);
        assertFalse(status.hasVpn());
    }

    @Test
    public void rebuildingTheSnapshotDropsDisconnectedVpn() {
        NetworkStatus connected = new NetworkStatus();
        connected.addNetwork(false, true, false, true);
        connected.addNetwork(true, false, false, true);
        assertTrue(connected.hasVpn());

        NetworkStatus disconnected = new NetworkStatus();
        disconnected.addNetwork(false, true, false, true);

        assertEquals(NetworkStatus.ETHERNET, disconnected.connection);
        assertFalse(disconnected.hasVpn());
    }
}
