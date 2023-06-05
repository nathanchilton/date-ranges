# date-ranges

A simple HTTP endpoint that returns ranges of consecutive days for a list of dates.

## Background
I developed this mostly because I was looking to learn a bit of the Clojure language and I needed a little project.  During an interview, a coworker (and old friend) proposed a theoretical HTTP endpoint with certain behavior, so we could evaluate a candidate's approach to testing.

I decided that this exercise seemed like a good opportunity for building something simple, in order to challenge myself to learn a few things.

## Endpoint Description

**Input Parameter**:

>"dates"; comma-delimited dates list, in the format YYYY-MM-DD

**Expected Response**:
>"dateRanges"; comma-delimited date range list,
>
> in the format YY-MM-DD_YYYY-MM-DD

Each date range is a range of consecutive days converted by the input parameter "dates"

**Example**:

>**Input**, dates = 2022-01-01,2022-01-02,2022-01-03,2022-01-10
>
>**Expect**, dateRanges = 2022-01-01_2022-01-03,2022-01-10_2022-01-10

## Goals
Building this out enabled me to figure out how to do the following in Clojure:
- Create an HTTP endpoint which would receive a POST request and return a response.
  - I began this journey with by putting together a ["Hello World" endpoint in Clojure](https://github.com/nathanchilton/hello-world-donkey) using the [Donkey](https://github.com/AppsFlyer/donkey) framework, which I see is now "no longer maintained".
- Split a String of comma-separated values into an array/list
- Parse a JSON-formatted string and returns a Clojure data structure
- Generate a JSON-formatted string from a Clojure data structure
- Compare a string to a regex to see if it matches the expected date format
- Divide a list of date strings into collections of valid and invalid dates (depending upon whether they match a regular expression)
- Build a date object using the specified year, month, and day (of month)
- Divide a list of dates into collections of valid and invalid, depending upon whether or not they are actually valid dates
- Evaluate a list of date objects and create a list of ranges of consecutive dates
  - Sort a list of date objects
  - Compare date objects to determine if they are consecutive
- Create unit tests to validate the behavior (which can be run using `lein test`)
