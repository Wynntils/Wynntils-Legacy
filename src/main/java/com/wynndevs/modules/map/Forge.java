package com.wynndevs.modules.map;

import journeymap.client.api.IClientAPI;

class ForgeEventListener {
    IClientAPI jmAPI;

    /**
     * Constructor.
     *
     * @param jmAPI API implementation
     */
    ForgeEventListener(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
    }
}