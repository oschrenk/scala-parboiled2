package com.acme

import scala.util.{Failure, Success}
import org.scalatest._
import org.scalatest.Matchers._

import Weekday._
import Month._

class DateParserSpec extends fixture.FreeSpec with Matchers {

  type FixtureParam = TestData

  override def withFixture(test: OneArgTest): Outcome = {
    withFixture(test.toNoArgTest(test))
  }

  "today" in { td =>
    parse(td.name) shouldBe InDays(0)
  }

  "tomorrow" in { td =>
    parse(td.name) shouldBe InDays(1)
  }

  "yesterday" in { td =>
    parse(td.name) shouldBe InDays(-1)
  }

  "next monday" in { td =>
    parse(td.name) shouldBe NextWeekdayByName(Monday)
  }

  "last monday" in { td =>
    parse(td.name) shouldBe LastWeekdayByName(Monday)
  }

  "last tuesday" in { td =>
    parse(td.name) shouldBe LastWeekdayByName(Tuesday)
  }

  "next tuesday" in { td =>
    parse(td.name) shouldBe NextWeekdayByName(Tuesday)
  }

  "last january" in { td =>
    parse(td.name) shouldBe LastMonthByName(January)
  }

  "next february" in { td =>
    parse(td.name) shouldBe NextMonthByName(February)
  }

  "last week" in { td =>
    parse(td.name) shouldBe InWeeks(-1)
  }

  "next week" in { td =>
    parse(td.name) shouldBe InWeeks(1)
  }

  "last month" in { td =>
    parse(td.name) shouldBe InMonths(-1)
  }

  "next month" in { td =>
    parse(td.name) shouldBe InMonths(1)
  }

  "last year" in { td =>
    parse(td.name) shouldBe InYears(-1)
  }

  "next year" in { td =>
    parse(td.name) shouldBe InYears(1)
  }

  "second saturday in september" in { td =>
    parse(td.name) shouldBe WeekdayInMonth(2, Saturday, September)
  }

  "in 1 day" in { td =>
    parse(td.name) shouldBe InDays(1)
  }

  "in 2 weeks" in { td =>
    parse(td.name) shouldBe InWeeks(2)
  }

  "in 3 months" in { td =>
    parse(td.name) shouldBe InMonths(3)
  }

  "in 4 years" in { td =>
    parse(td.name) shouldBe InYears(4)
  }

  "one day ago" in { td =>
    parse(td.name) shouldBe InDays(-1)
  }

  "two weeks ago" in { td =>
    parse(td.name) shouldBe InWeeks(-2)
  }

  "three months ago" in { td =>
    parse(td.name) shouldBe InMonths(-3)
  }

  "four years ago" in { td =>
    parse(td.name) shouldBe InYears(-4)
  }

  def parse(line: String): Datum = {
    val parser = new DateParser(line)
    parser.InputLine.run() match {
      case Success(result) => result
      case Failure(e)      => throw new IllegalArgumentException(e)
    }
  }

}
