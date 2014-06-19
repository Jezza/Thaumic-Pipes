package me.jezza.thaumicpipes.common.core;

public enum ConnectionType {
    CONTAINER(0.5F), JAR(0.1F), PIPE(0.0F), CONSTRUCT(0.0F), UNKNOWN(-1.0F);

    private float size = 0.0F;

    private ConnectionType(float size) {
        this.size = size;
    }

    public float getSize() {
        return size;
    }

    public boolean isValid() {
        return this != UNKNOWN;
    }

    public boolean isContainerType() {
        return this == CONTAINER;
    }

    public boolean isJarType() {
        return this == JAR;
    }

    public boolean isPipeType() {
        return this == PIPE;
    }

    public boolean isBigNode() {
        return isJarType() || isContainerType();
    }
}