package me.jezza.thaumicpipes.common.transport;

import thaumcraft.api.aspects.Aspect;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ListThing {

    public ListThing() {

        LinkedHashMap<AspectEntry, Collection<Object>> list;

    }

    public boolean add(AspectEntry aspectEntry, Object... objects) {
        return false;
    }

    static class AspectEntry implements Map.Entry<Aspect, Integer> {
        private Aspect aspect;
        private int amount = 0;

        public AspectEntry(Aspect aspect, int amount) {
            this.aspect = aspect;
            this.amount = amount;
        }

        @Override
        public Aspect getKey() {
            return aspect;
        }

        @Override
        public Integer getValue() {
            return amount;
        }

        @Override
        public Integer setValue(Integer value) {
            Integer i = amount;
            amount = value;
            return i;
        }

        public final boolean equals(Object var1) {
            if (!(var1 instanceof java.util.Map.Entry)) {
                return false;
            } else {
                java.util.Map.Entry var2 = (java.util.Map.Entry) var1;
                Object var3 = this.getKey();
                Object var4 = var2.getKey();
                if (var3 == var4 || var3 != null && var3.equals(var4)) {
                    Object var5 = this.getValue();
                    Object var6 = var2.getValue();
                    if (var5 == var6 || var5 != null && var5.equals(var6)) {
                        return true;
                    }
                }

                return false;
            }
        }

        public final int hashCode() {
            return Objects.hashCode(this.getKey()) ^ Objects.hashCode(this.getValue());
        }

        public final String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }
}
