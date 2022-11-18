package gg.m2ke4u.skylight.concurrent;

import java.util.Spliterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

public class ForkJoinTraverse<E> extends RecursiveAction {
    private final Spliterator<E> spliterator;
    private final Consumer<E> action;
    private final long threshold;

    public ForkJoinTraverse(Iterable<E> iterable, int threads, Consumer<E> action){
        this.spliterator = iterable.spliterator();
        this.action = action;
        this.threshold = Math.min(20,(int)iterable.spliterator().getExactSizeIfKnown() / threads);
    }

    private ForkJoinTraverse(Spliterator<E> spliterator, Consumer<E> action, long t) {
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
            new ForkJoinTraverse<>(this.spliterator.trySplit(), this.action, this.threshold).fork();
            new ForkJoinTraverse<>(this.spliterator, this.action, this.threshold).fork();
        }
    }

    public static <E> ForkJoinTraverse<E> wrapNewTask(Iterable<E> input, Consumer<E> action, ForkJoinPool pool){
        return (ForkJoinTraverse<E>) pool.submit(new ForkJoinTraverse<E>(input,pool.getParallelism(),action));
    }
}
