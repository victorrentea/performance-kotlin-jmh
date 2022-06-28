package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.lang.Math.sqrt;
import static java.util.stream.Collectors.toList;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class StreamsCPUOnlyTest {


   @Param({"1000"/*, "1000000"*/})
   public int n_items;

   @Param({"light"/*, "heavy"*/})
   public String cpu_intensity;

   private List<Integer> numbers;

   @Setup
   public void createNumbersList() {
      numbers = IntStream.range(0, n_items).boxed().collect(toList());
   }

   @Benchmark
   public int forClassic() {
      int sum = 0;
      for (int n : numbers) {
         if (n % 2 == 0) {
            sum += cpuOnlyTask(n);
         }
      }
      return sum;
   }
//   @Benchmark
//   public int forI() {
//      int sum = 0;
//      for (int i = 0, numbersSize = numbers.size(); i < numbersSize; i++) {
//         int n = numbers.get(i);
//         if (n % 2 == 0) {
//            sum += cpuOnlyTask(n);
//         }
//      }
//      return sum;
//   }
   @Benchmark
   public int forKotlin() {
      return KotlinLoops.INSTANCE.range(numbers, this::cpuOnlyTask);
   }

   @Benchmark
   public long stream() {
      return numbers.stream()
          .filter(n -> n % 2 == 0)
          .map(this::cpuOnlyTask)
          .count();
   }



   public int cpuOnlyTask(int n) {
//      System.out.println(Thread.currentThread().getName());
      switch (cpu_intensity) {
         case "light":
            return (int) sqrt(n);
         case "heavy":
            double sum = 0;
            for (int i = n * 1000; i < (n + 1) * 1000; i++) {
               sum += sqrt(i);
            }
            return (int) sum;
         default:
            throw new IllegalStateException("Unexpected value: " + cpu_intensity);
      }
   }


}
