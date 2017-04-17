import java.util.concurrent.Executors

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}

object Test {

  private val singEx = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  private val globalEx =  scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    Range(0, 1000).foreach { _ =>
      run(singEx)
    }
    println("Ok 1")
    Range(0, 1000).foreach { _ =>
      run(globalEx)
    }
    println("Ok 2")
  }

  def run(ex:ExecutionContextExecutor): Unit = {
    val t = new Test
    val a = Future {
      t.executedOnCpu0()
    }(ex)
    val b = Future {
      t.executedOnCpu1()
    }(ex)
    val result1 = Await.result(a, Duration.Inf)
    val result2 = Await.result(b, Duration.Inf)
  }
}

class Test(@volatile var finished: Boolean = false,
           var value: Int = 0) {

  def executedOnCpu0() {
    value = 10
    finished = true
  }

  def executedOnCpu1() {
    while (!finished)
      assert(value == 10)
  }

}
