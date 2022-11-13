package gg.m2ke4u.lutils.threading.traversing;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CollectionConcurrentUtils{
    /**
     * Forked and edited form bilibili,Video link:https://www.bilibili.com/video/BV1e44y1677h?spm_id_from=333.999.0.0 This method can traverse streams faster than parallel stream.And you can use yourself pool
     * @param stream The stream will traverse
     * @param action your action
     * @param pool your thread pool
     * @param <E> the type of stream and action
     */
    public static <E> void traverseConcurrent(Stream<E> stream, Consumer<E> action, Executor pool){
        CompletableFuture[] tasks = stream.map(value-> CompletableFuture.runAsync(()-> action.accept(value),pool)).toArray(length->new CompletableFuture[length]);
        CompletableFuture.allOf(tasks).join();
    }

    /**
     * Forked and edited form bilibili,Video link:https://www.bilibili.com/video/BV1e44y1677h?spm_id_from=333.999.0.0 This method can traverse streams faster than parallel stream.
     * @param stream The stream will traverse
     * @param action your action
     * @param <E> the type of stream and action
     */
    public static <E> void traverseConcurrent(Stream<E> stream, Consumer<E> action){
        CompletableFuture[] tasks = stream.map(value-> CompletableFuture.runAsync(()-> action.accept(value))).toArray(length->new CompletableFuture[length]);
        CompletableFuture.allOf(tasks).join();
    }

    /**
     * Collection traversing support
     * @param collection The collection will traverse
     * @param action your action
     * @param pool your thread pool
     * @param <E> the type of stream and action
     */
    public static <E> void traverseConcurrent(Collection<E> collection, Consumer<E> action, Executor pool){
        traverseConcurrent(collection.stream(),action,pool);
    }

    /**
     * Collection traversing support
     * @param collection The collection will traverse
     * @param action your action
     * @param <E> the type of stream and action
     */
    public static <E> void traverseConcurrent(Collection<E> collection, Consumer<E> action){
        traverseConcurrent(collection.stream(),action);
    }

    public static <E> void runTraverseInOtherPool(Stream<E> stream, Consumer<E> action, ForkJoinPool pool){
        pool.submit(()->stream.parallel().forEach(action)).join();
    }

    public static <E> Object[] runSupplyConcurrent(Supplier<E>[] suppliers,Executor executor){
        CompletableFuture<E>[] futures = Arrays.stream(suppliers).map(value-> CompletableFuture.supplyAsync(value,executor)).toArray(length->new CompletableFuture[length]);
        Object[] objects = new Object[suppliers.length];
        int i = 0;
        for (CompletableFuture<E> future : futures){
            try{
                objects[i] = future.join();
            }catch (Exception e){
                e.printStackTrace();
            }
            ++i;
        }
        return objects;
    }
}
