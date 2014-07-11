package me.jezza.thaumicpipes.common.multipart;

import me.jezza.thaumicpipes.common.multipart.pipe.thaumic.ThaumicPipePart;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.MultipartGenerator;
import codechicken.multipart.TMultiPart;

public class MultiPartFactory implements IPartFactory {

    public static final String thaumicPipe = "tp_thaumicPipe";

    public void init() {
        MultiPartRegistry.registerParts(this, new String[] { thaumicPipe });

        MultipartGenerator.registerPassThroughInterface("me.jezza.thaumicpipes.common.interfaces.IThaumicPipe");
    }

    @Override
    public TMultiPart createPart(String name, boolean client) {
        if (thaumicPipe.equals(name))
            return new ThaumicPipePart();
        return null;
    }
}
