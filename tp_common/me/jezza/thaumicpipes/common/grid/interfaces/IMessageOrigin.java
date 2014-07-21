package me.jezza.thaumicpipes.common.grid.interfaces;

import me.jezza.thaumicpipes.common.grid.MessageLocating;

public interface IMessageOrigin {

    public void onPacketCompletion(MessageLocating message);

}
