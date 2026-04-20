package ca.jrvs.apps.practice;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaStreamImp implements LambdaStreamExc {


    @Override
    public Stream<String> createStrStream(String... strings) {

        return Optional.ofNullable(strings).map(Arrays::stream).orElseGet(Stream::empty);
    }

    @Override
    public Stream<String> toUpperCase(String... strings) {
        return createStrStream(strings)
                .filter(Objects::nonNull)
                .map(String::toUpperCase);
    }

    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern) {
        // Defensive check: if stream is null, return empty
        if (stringStream == null) return Stream.empty();
        // If no pattern, just return the stream
        if (pattern == null) return stringStream;

        return stringStream.filter(s -> s == null || !s.contains(pattern));
    }

    @Override
    public IntStream createIntStream(int[] arr) {
        return Optional.ofNullable(arr).map(Arrays::stream).orElseGet(IntStream::empty);
    }

    @Override
    public <E> List<E> toList(Stream<E> stream) {
        // collect(Collectors.toList())
        return Optional.ofNullable(stream)
                .map(s -> s.collect(Collectors.toList()))
                .orElseGet(java.util.Collections::emptyList);
    }

    @Override
    public List<Integer> toList(IntStream intStream) {
        // boxed() converts primitives to objects so they can fit in a List
        return Optional.ofNullable(intStream)
                .map(s -> s.boxed().collect(Collectors.toList()))
                .orElseGet(java.util.Collections::emptyList);
    }

    @Override
    public IntStream createIntStream(int start, int end) {
        // rangeClosed is inclusive of the end value
        return IntStream.rangeClosed(start, end);
    }

    @Override
    public DoubleStream squareRootIntStream(IntStream intStream) {
        return Optional.ofNullable(intStream)
                .map(s -> s.mapToDouble(Math::sqrt))
                .orElseGet(DoubleStream::empty);
    }

    @Override
    public IntStream getOdd(IntStream intStream) {
        return Optional.ofNullable(intStream)
                .map(s -> s.filter(x -> x % 2 != 0))
                .orElseGet(IntStream::empty);
    }

    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix) {
        // Using Optional.ofNullable to provide defaults
        String p = Optional.ofNullable(prefix).orElse("");
        String s = Optional.ofNullable(suffix).orElse("");
        return msg -> System.out.println(p + msg + s);
    }

    @Override
    public void printMessages(String[] messages, Consumer<String> printer) {
        if (printer != null) {
            createStrStream(messages).forEach(printer);
        }
    }

    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer) {
        if (printer != null) {
            getOdd(intStream).mapToObj(String::valueOf).forEach(printer);
        }
    }

    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {
        // Filter out null lists before flattening
        return Optional.ofNullable(ints)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .flatMap(List::stream);
    }

    public static void main(String[] args) {
        LambdaStreamExc lse = new LambdaStreamImp();

        Consumer<String> printer = lse.getLambdaPrinter("start>", "<end");
        printer.accept("Message will show");
    }

}