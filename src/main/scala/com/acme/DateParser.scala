package com.acme

import com.acme._
import org.parboiled2._

class DateParser(val input: ParserInput) extends Parser {

  def InputLine = rule { Expression ~ EOI }

  def Expression: Rule1[CompoundEvent] = rule {
    DateTimes                                  ~> ((t: TimeEvent) => Left(Right(t))) |
    Times                                      ~> ((t: TimeEvent) => Left(Right(t)) )|
    Dates                                      ~> ((d: DateEvent) => Left(Left(d)))  |
    TimeDurations                              ~> ((d: DurationEvent) => Right(d))
  }

  def DateTimes = rule {
    RelativeDays ~ Space ~ FuzzyTimes           ~> ((d,t) => DateTimeEvent(d,t)) |
    RelativeDays ~ Space ~ FormalTimes          ~> ((d,t) => DateTimeEvent(d,t)) |
    RelativeDatesFuture ~ Space ~ FormalTimes   ~> ((d,t) => DateTimeEvent(d,t)) |
    SpecificWeekday ~ Space ~ FuzzyTimes        ~> ((w,t) => DateTimeEvent(NextWeekdayByName(w),t)) |
    CardinalWeekdayInMonth ~Space ~ FormalTimes ~> ((d,t) => DateTimeEvent(d,t))
  }

  def Times = rule {
    FormalTimes | FuzzyTimes | RelativeTimes
  }

  def Dates = rule {
    FormalDates | RelativeDates
  }

  def FormalTimes = rule {
    TwelveHourTime                             ~> ((t) => AtTime(t)) |
    IsoTime                                    ~> ((t) => AtTime(t)) |
    At ~ Space ~ TwelveHourTime                ~> ((t) => AtTime(t)) |
    At ~ Space ~ IsoTime                       ~> ((t) => AtTime(t))
  }

  def FuzzyTimes = rule {
    Noon                                       ~> ((t) => AtTime(t)) |
    Afternoon                                  ~> ((t) => AtTime(t)) |
    Evening                                    ~> ((t) => AtTime(t)) |
    Midnight                                   ~> ((t) => AtTime(t))
  }

  def RelativeTimes = rule {
    In ~ Space ~ Number ~ Space ~ Seconds      ~> ((s) => InSeconds(s)) |
    In ~ Space ~ Number ~ Space ~ Minutes      ~> ((m) => InMinutes(m)) |
    In ~ Space ~ Number ~ Space ~ Hours        ~> ((h) => InHours(h)) |
    Number ~ Space ~ Seconds ~ Space ~ Ago     ~> ((s) => InSeconds(-s)) |
    Number ~ Space ~ Minutes ~ Space ~ Ago     ~> ((m) => InMinutes(-m)) |
    Number ~ Space ~ Hours ~ Space ~ Ago       ~> ((h) => InHours(-h)) |
    Number ~ Space ~ Seconds ~ Space ~ FromNow ~> ((s) => InSeconds(s)) |
    Number ~ Space ~ Minutes ~ Space ~ FromNow ~> ((m) => InMinutes(m)) |
    Number ~ Space ~ Hours ~ Space ~ FromNow   ~> ((h) => InHours(h))
  }

  def FormalDates = rule {
    IsoDate                                    ~> ((d) => OnDate(d)) |
    On ~ Space ~ IsoDate                       ~> ((d) => OnDate(d)) |
    LittleEndianDate                           ~> ((d) => OnDate(d)) |
    On ~ Space ~ LittleEndianDate              ~> ((d) => OnDate(d))
  }

  def RelativeDates = rule {
    RelativeDays |
    RelativeDatesFuture |
    RelativeDatesPast |
    CardinalWeekdayInMonth
  }

  def CardinalWeekdayInMonth = rule {
    Cardinal ~ Space ~ SpecificWeekday ~ Space ~ (In | Of) ~ Space ~ SpecificMonth ~> ((c,w,m) => WeekdayInMonth(c, w, m))
  }

  def RelativeDays = rule {
    Today                                      ~> (()  => InDays(0)) |
    Tomorrow                                   ~> (()  => InDays(1)) |
    Yesterday                                  ~> (()  => InDays(-1))
  }

  def RelativeDatesFuture = rule {
    SpecificWeekday                            ~> ((w) => NextWeekdayByName(w)) |
    Next ~ Space ~ Months                      ~> (()  => InMonths(1)) |
    Next ~ Space ~ SpecificWeekday             ~> ((w) => NextWeekdayByName(w)) |
    Next ~ Space ~ SpecificMonth               ~> ((m) => NextMonthByName(m)) |
    Next ~ Space ~ Weeks                       ~> (()  => InWeeks(1)) |
    Next ~ Space ~ Years                       ~> (()  => InYears(1)) |
    In ~ Space ~ Number ~ Space ~ Days         ~> ((d) => InDays(d)) |
    In ~ Space ~ Number ~ Space ~ Weeks        ~> ((w) => InWeeks(w)) |
    In ~ Space ~ Number ~ Space ~ Months       ~> ((m) => InMonths(m)) |
    In ~ Space ~ Number ~ Space ~ Years        ~> ((y) => InYears(y))
  }

  def RelativeDatesPast = rule {
    Last ~ Space ~ Months                      ~> (()  => InMonths(-1)) |
    Last ~ Space ~ SpecificWeekday             ~> ((w) => LastWeekdayByName(w)) |
    Last ~ Space ~ SpecificMonth               ~> ((m) => LastMonthByName(m)) |
    Last ~ Space ~ Weeks                       ~> (()  => InWeeks(-1)) |
    Last ~ Space ~ Years                       ~> (()  => InYears(-1)) |
    Count ~ Space ~ Days ~ Space ~ Ago         ~> ((c) => InDays(-c)) |
    Count ~ Space ~ Weeks ~ Space ~ Ago        ~> ((c) => InWeeks(-c)) |
    Count ~ Space ~ Months ~ Space ~ Ago       ~> ((c) => InMonths(-c)) |
    Count ~ Space ~ Years ~ Space ~ Ago        ~> ((c) => InYears(-c))
  }

  def TimeDurations = rule {
    For ~ Space ~ Number ~ Space ~ Seconds     ~> (ForSeconds(_)) |
    For ~ Space ~ Number ~ Space ~ Minutes     ~> (ForMinutes(_)) |
    For ~ Space ~ Number ~ Space ~ Hours       ~> (ForHours(_))
  }

  def Cardinal     = rule { First | Second | Third | Fourth | Fifth}
  def First        = rule { ignoreCase("first")  ~> (() => 1)}
  def Second       = rule { ignoreCase("second") ~> (() => 2)}
  def Third        = rule { ignoreCase("third")  ~> (() => 3)}
  def Fourth       = rule { ignoreCase("fourth") ~> (() => 4)}
  def Fifth        = rule { ignoreCase("fifth")  ~> (() => 5)}

  def Count        = rule { One | Two | Three | Four | Five }
  def One          = rule { ignoreCase("one")   ~> (() => 1)}
  def Two          = rule { ignoreCase("two")   ~> (() => 2)}
  def Three        = rule { ignoreCase("three") ~> (() => 3)}
  def Four         = rule { ignoreCase("four")  ~> (() => 4)}
  def Five         = rule { ignoreCase("five")  ~> (() => 5)}

  def Ago          = rule { ignoreCase("ago") }
  def At           = rule { ignoreCase("at") }
  def For          = rule { ignoreCase("for") }
  def FromNow      = rule { ignoreCase("from now") }
  def In           = rule { ignoreCase("in") }
  def Of           = rule { ignoreCase("of") }
  def On           = rule { ignoreCase("on") }

  def Last         = rule { ignoreCase("last") }
  def Next         = rule { ignoreCase("next") }

  def Today        = rule { ignoreCase("today") }
  def Tomorrow     = rule { ignoreCase("tomorrow") }
  def Yesterday    = rule { ignoreCase("yesterday") }

  def Seconds      = rule { ignoreCase("second") ~ optional(ignoreCase("s")) }
  def Minutes      = rule { ignoreCase("minute") ~ optional(ignoreCase("s")) }
  def Hours        = rule { ignoreCase("hour")   ~ optional(ignoreCase("s")) }
  def Days         = rule { ignoreCase("day")    ~ optional(ignoreCase("s")) }
  def Weeks        = rule { ignoreCase("week")   ~ optional(ignoreCase("s")) }
  def Months       = rule { ignoreCase("month")  ~ optional(ignoreCase("s")) }
  def Years        = rule { ignoreCase("year")   ~ optional(ignoreCase("s")) }

  def Noon         = rule { ignoreCase("noon") ~> (() => new Time(12,0))}
  def Afternoon    = rule { ignoreCase("afternoon") ~> (() => new Time(16,0))}
  def Evening      = rule { ignoreCase("evening") ~> (() => new Time(19,0))}
  def Midnight     = rule { ignoreCase("midnight") ~> (() => new Time(0,0))}

  def Number       = rule { capture(Digits) ~> (_.toInt) }
  def Digits       = rule { oneOrMore(CharPredicate.Digit) }

  def Colon        = rule { ":" }
  def Dash         = rule { "-" }
  def Dot          = rule { "." }
  def Slash        = rule { "/" }
  def Space        = rule { zeroOrMore(" ") }

  def IsoDate      = rule { YearDigits ~ optional(Dash | Slash) ~ MonthDigits ~ optional(Dash | Slash) ~ DayDigits ~> ((y,m,d) => new Date(y,m,d)) }
  def LittleEndianDate =  rule { DayDigits ~ optional(Dash | Slash) ~ MonthDigits ~ optional(Dash | Slash) ~ YearDigits ~> ((d,m,y) => new Date(y,m,d)) }
  def YearDigits   = rule { capture(4.times(CharPredicate.Digit)) ~> (_.toInt) }
  def MonthDigits  = rule { capture("0" ~ CharPredicate.Digit | "1" ~ anyOf("012" )) ~> (_.toInt) }
  def DayDigits    = rule { capture(anyOf("012") ~ CharPredicate.Digit | "3" ~ anyOf("01" )) ~> (_.toInt) }

  def IsoTime      = rule { HourDigits ~ Colon ~ MinuteDigits ~> ((h,m) => new Time(h, m)) }
  def HourDigits   = rule { capture(anyOf("01") ~ optional(CharPredicate.Digit) | ("2" ~ optional(anyOf("01234" ))) | anyOf("3456789")) ~> (_.toInt) }
  def MinuteDigits = rule { capture(anyOf("012345") ~ optional(CharPredicate.Digit)) ~> (_.toInt) }

  def TwelveHourTime = rule {
    TwelveHour ~ Colon ~ MinuteDigits ~ Space ~ Am ~> ((h,m) => new Time(h % 12,m)) |
    TwelveHour ~ Colon ~ MinuteDigits ~ Space ~ Pm ~> ((h,m) => new Time((h % 12) + 12,m)) |
    TwelveHour ~ Am                     ~> ((h) => new Time(h % 12,0)) |
    TwelveHour ~ Pm                     ~> ((h) => new Time((h % 12) + 12,0)) |
    TwelveHour ~ Space ~ Am             ~> ((h) => new Time(h % 12,0)) |
    TwelveHour ~ Space ~ Pm             ~> ((h) => new Time((h % 12) + 12,0))
  }
  def TwelveHour   = rule { capture("0" ~ CharPredicate.Digit | "1" ~ anyOf("012") | CharPredicate.Digit) ~> (_.toInt) }
  def Am           = rule { ignoreCase("a") ~ optional (Dot) ~ ignoreCase("m") ~ optional(Dot) }
  def Pm           = rule { ignoreCase("p") ~ optional (Dot) ~ ignoreCase("m") ~ optional(Dot) }

  // Absolute Weekday
  def SpecificWeekday: Rule1[Weekday] = rule {Monday | Tuesday | Wednesday | Thursday | Friday | Saturday | Sunday}
  def Monday: Rule1[Weekday]    = rule {(ignoreCase("monday")    | ignoreCase("mon")) ~> (() => Weekday.Monday )}
  def Tuesday: Rule1[Weekday]   = rule {(ignoreCase("tuesday")   | ignoreCase("tue")) ~> (() => Weekday.Tuesday )}
  def Wednesday: Rule1[Weekday] = rule {(ignoreCase("wednesday") | ignoreCase("wed")) ~> (() => Weekday.Wednesday )}
  def Thursday: Rule1[Weekday]  = rule {(ignoreCase("thursday")  | ignoreCase("thu")) ~> (() => Weekday.Thursday )}
  def Friday: Rule1[Weekday]    = rule {(ignoreCase("friday")    | ignoreCase("fri")) ~> (() => Weekday.Friday )}
  def Saturday: Rule1[Weekday]  = rule {(ignoreCase("saturday")  | ignoreCase("sat")) ~> (() => Weekday.Saturday )}
  def Sunday: Rule1[Weekday]    = rule {(ignoreCase("sunday")    | ignoreCase("sun")) ~> (() => Weekday.Sunday )}

  // Absolute Month
  def SpecificMonth: Rule1[Month] = rule {January | Febuary | March | April | May | June | July | August | September | October | November | December}
  def January: Rule1[Month]   = rule {(ignoreCase("january")   | ignoreCase("jan")) ~> (() => Month.January )}
  def Febuary: Rule1[Month]   = rule {(ignoreCase("february")  | ignoreCase("feb")) ~> (() => Month.February )}
  def March: Rule1[Month]     = rule {(ignoreCase("march")     | ignoreCase("mar")) ~> (() => Month.March )}
  def April: Rule1[Month]     = rule {(ignoreCase("april")     | ignoreCase("apr")) ~> (() => Month.April )}
  def May: Rule1[Month]       = rule {(ignoreCase("may"))                           ~> (() => Month.May )}
  def June: Rule1[Month]      = rule {(ignoreCase("june")      | ignoreCase("jun")) ~> (() => Month.June )}
  def July: Rule1[Month]      = rule {(ignoreCase("july")      | ignoreCase("jul")) ~> (() => Month.July )}
  def August: Rule1[Month]    = rule {(ignoreCase("august")    | ignoreCase("aug")) ~> (() => Month.August )}
  def September: Rule1[Month] = rule {(ignoreCase("september") | ignoreCase("sep")) ~> (() => Month.September )}
  def October: Rule1[Month]   = rule {(ignoreCase("october")   | ignoreCase("oct")) ~> (() => Month.October )}
  def November: Rule1[Month]  = rule {(ignoreCase("november")  | ignoreCase("nov")) ~> (() => Month.November )}
  def December: Rule1[Month]  = rule {(ignoreCase("december")  | ignoreCase("dec")) ~> (() => Month.December )}
}

object Weekday {
  val Monday    = Weekday(1)
  val Tuesday   = Weekday(2)
  val Wednesday = Weekday(3)
  val Thursday  = Weekday(4)
  val Friday    = Weekday(5)
  val Saturday  = Weekday(6)
  val Sunday    = Weekday(7)
}
case class Weekday(val value: Int) {
  require(value >= 1 && value <= 7)
}

object Month {
  val January   = Month(1)
  val February  = Month(2)
  val March     = Month(3)
  val April     = Month(4)
  val May       = Month(5)
  val June      = Month(6)
  val July      = Month(7)
  val August    = Month(8)
  val September = Month(9)
  val October   = Month(10)
  val November  = Month(11)
  val December  = Month(12)
}
case class Month(val value: Int) {
  require(value >= 1 && value <= 12)
}

case class DateTime(date: Date, time: Time)

case class Date(year: Int, month: Int, day: Int) {
  require(month >= 1 && month <= 12)
  // TODO stricter day checking?
  require(day >= 1 && day <= 31)
}

case class Time(hours: Int, minutes: Int) {
  require(hours >= 0 && hours <= 24)
  require(minutes >= 0 && hours <= 59)
}
