package me.jezza.thaumicpipes.common.core;

import java.util.ArrayList;
import java.util.Iterator;

import me.jezza.thaumicpipes.common.core.AspectContainerList.AspectContainerState;
import me.jezza.thaumicpipes.common.interfaces.IThaumicPipe;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

import com.google.common.collect.Lists;

public class AspectContainerList implements Iterable<AspectContainerState> {

    private ArrayList<AspectContainerState> containerList;
    private Aspect filter = null;

    public AspectContainerList() {
        containerList = Lists.newArrayList();
    }

    public void add(IThaumicPipe pipe) {
        if (filter == null) {
            TPLogger.severe("Filter not set");
            return;
        }
        containerList.add(new AspectContainerState(pipe));
    }

    public void add(IAspectContainer container) {
        if (filter == null) {
            TPLogger.severe("Filter not set");
            return;
        }
        containerList.add(new AspectContainerState(container));
    }

    public void addAll(AspectContainerList otherList) {
        for (AspectContainerState state : otherList.containerList)
            containerList.add(state);
    }

    public void clear(Aspect filter) {
        containerList.clear();
        setFilter(filter);
    }

    public AspectContainerList setFilter(Aspect filter) {
        this.filter = filter;
        return this;
    }

    public Aspect getFilter() {
        return filter;
    }

    public int size() {
        return containerList.size();
    }

    public boolean isEmpty() {
        return containerList.isEmpty();
    }

    public class AspectContainerState {

        private IThaumicPipe pipe;
        private IAspectContainer container;
        private int flag = 0;

        private AspectContainerState(IThaumicPipe pipe) {
            this.pipe = pipe;
        }

        private AspectContainerState(IAspectContainer container) {
            this.container = container;
            flag = 1;
        }

        public int getAspectSize() {
            AspectList aspectList = null;
            if (flag == 0)
                aspectList = pipe.getAspectList();
            if (flag == 1)
                aspectList = container.getAspects();

            if (aspectList == null)
                return 0;

            return aspectList.getAmount(filter);
        }

        public void removeAmount(int amountToRemove) {
            if (flag == 0)
                pipe.removeAspect(filter, amountToRemove);
            if (flag == 1)
                container.takeFromContainer(filter, amountToRemove);
        }
    }

    public static class AspectIterator implements Iterator<AspectContainerState> {

        private ArrayList<AspectContainerState> containerList;
        int currentIndex = 0;

        public AspectIterator(ArrayList<AspectContainerState> containerList) {
            this.containerList = containerList;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < containerList.size();
        }

        @Override
        public AspectContainerState next() {
            return containerList.get(currentIndex++);
        }

        @Override
        public void remove() {
            containerList.remove(currentIndex);
        }
    }

    @Override
    public Iterator<AspectContainerState> iterator() {
        return new AspectIterator(containerList);
    }
}
