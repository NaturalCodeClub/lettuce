package org.skylight.executor.forkjointraversing;

import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;
import java.util.Spliterator;


public class ConcurrentlyTraverse<E> extends RecursiveAction {
    private final Spliterator<E> spliterator;
    private final Consumer<E> action;
    private final long threshold;

    public ConcurrentlyTraverse(Iterable<E> iterable,int threads,Consumer<E> action){
        this.spliterator = iterable.spliterator();
        this.action = action;
        this.threshold = (int)iterable.spliterator().getExactSizeIfKnown() / threads;
    }

    private ConcurrentlyTraverse(Spliterator<E> spliterator,Consumer<E> action,long t) {
        this.spliterator = spliterator;
        this.action = action;
        this.threshold = t;
    }

    @Override
    protected void compute() {
        if (this.spliterator.getExactSizeIfKnown() <= this.threshold) {
            this.spliterator.forEachRemaining(o->{
                try {
                    this.action.accept(o);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        } else {
            new ConcurrentlyTraverse<>(this.spliterator.trySplit(), this.action, this.threshold).fork();
            new ConcurrentlyTraverse<>(this.spliterator, this.action, this.threshold).fork();
        }
    }
}

