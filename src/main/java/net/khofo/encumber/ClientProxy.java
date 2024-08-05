package net.khofo.encumber;

import net.khofo.encumber.client.events.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    public void preInit() {
        super.preInit();
        // Client pre-initialization code
    }

    public void init() {
        super.init();
        // Client initialization code
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.KeyInputHandler.class);
    }

    public void postInit() {
        super.postInit();
        // Client post-initialization code
    }
}

