package me.jezza.thaumicpipes;

import me.jezza.oc.api.network.NetworkInstance;
import me.jezza.oc.api.network.NetworkResponse.NodeAdded;
import me.jezza.oc.api.network.NetworkResponse.NodeRemoved;
import me.jezza.oc.api.network.NetworkResponse.NodeUpdated;
import me.jezza.oc.api.network.exceptions.NetworkException;
import me.jezza.oc.api.network.interfaces.INetworkNode;
import me.jezza.oc.common.core.CoreProperties;

public class CommonProxy {

    private static NetworkInstance networkInstance;

    private static boolean init = false;

    public static void createNetworkInstance() {
        if (init)
            return;
        init = true;
        networkInstance = new NetworkInstance();
    }

    public void initServerSide() {
    }

    public void initClientSide() {
    }

    public NodeAdded addNetworkNode(INetworkNode node) {
        NodeAdded response;
        try {
            response = networkInstance.addNetworkNode(node);
        } catch (NetworkException e) {
            CoreProperties.logger.fatal("Failed to add node.", e);
            response = NodeAdded.NETWORK_FAILED_TO_ADD;
        }
        return response;
    }

    public NodeRemoved removeNetworkNode(INetworkNode node) {
        NodeRemoved response;
        try {
            response = networkInstance.removeNetworkNode(node);
        } catch (NetworkException e) {
            CoreProperties.logger.fatal("Failed to remove node.", e);
            response = NodeRemoved.NETWORK_FAILED_TO_REMOVE;
        }
        return response;
    }

    public NodeUpdated updateNetworkNode(INetworkNode node) {
        NodeUpdated response;
        try {
            response = networkInstance.updateNetworkNode(node);
        } catch (NetworkException e) {
            CoreProperties.logger.fatal("Failed to update n.", e);
            response = NodeUpdated.NETWORK_FAILED_TO_UPDATE;
        }
        return response;
    }
}
