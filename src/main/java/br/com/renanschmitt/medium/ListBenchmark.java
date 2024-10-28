/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package br.com.renanschmitt.medium;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Measurement(time = 50, iterations = 3, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
@Warmup(time = 100, timeUnit = TimeUnit.MILLISECONDS, iterations = 1)
public class ListBenchmark {

  private static final String ARRAY_LIST = "ArrayList";
  private static final String LINKED_LIST = "LinkedList";
  private static final String VECTOR = "Vector";

  @Param({
    "10", "25", "50", "75", "100", "250", "500", "750", "1000", "2500", "5000", "7500", "10000",
    "25000", "50000", "75000", "100000"
  })
  int numberOfElements;

  @Param({ARRAY_LIST, LINKED_LIST, VECTOR})
  String listType;

  private List<Integer> list;

  @Setup
  public void setUp() {
    list =
        listType.equals(ARRAY_LIST)
            ? new ArrayList<>(numberOfElements)
            : listType.equals(LINKED_LIST) ? new LinkedList<>() : new Vector<>(numberOfElements);
    IntStream.range(0, numberOfElements).forEach(list::add);
  }

  @Benchmark
  public void forIndex() {
    long sum = 0;

    for (int i = 0; i < numberOfElements; i++) {
      sum += list.get(i);
    }
  }

  @Benchmark
  public void forEach() {
    long sum = 0;

    for (int i : list) {
      sum += list.get(i);
    }
  }

  @Benchmark
  public void iterator() {
    long sum = 0;

    var iterator = list.iterator();

    while (iterator.hasNext()) {
      sum += list.get(iterator.next());
    }
  }

  @Benchmark
  public void stream() {
    long sum = list.stream().map(Integer::longValue).reduce(0L, Long::sum);
  }

  @Benchmark
  public void parallelStream() {
    long sum = list.parallelStream().map(Integer::longValue).reduce(0L, Long::sum);
  }
}
