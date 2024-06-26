# date-ranges

A simple HTTP endpoint that returns ranges of consecutive days for a list of dates.

## Background
I developed this mostly because I was looking to learn a bit of the [Clojure](https://clojure.org/) language and I needed a little project.  During an interview, a coworker (and old friend, [@derekgoering](https://github.com/derekgoering)) proposed a theoretical HTTP endpoint with certain behavior, so we could evaluate a candidate's approach to testing.

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

### JSON Support

If the API receives the request in JSON format, it will also respond with JSON.

**Request:**
``` JSON
[
  "2022-01-01",
  "2022-01-02",
  "2022-01-03",
  "2022-01-10"
]
```

**Response:**

``` JSON
{
  "ranges": [
    {
      "begin": "2022-01-01",
      "end": "2022-01-03"
    },
    {
      "begin": "2022-01-10",
      "end": "2022-01-10"
    }
  ]
}
```

## Goals
Building this out gave me the opportunity to figure out how to do the following in Clojure:
- Create an HTTP endpoint which would receive a POST request and return a response.
  - I began this journey with by putting together a ["Hello World" endpoint in Clojure](https://github.com/nathanchilton/hello-world-donkey) using the [Donkey](https://github.com/AppsFlyer/donkey) framework, which (as of 5 MAR 2023) is ["no longer maintained"](https://github.com/AppsFlyer/donkey/commit/e36889e3b18da17d151c6a70673653ea4d24c045) -- but that doesn't mean it has stopped working.
- Split a String of comma-separated values into an array/list
- Parse a JSON-formatted string and returns a Clojure data structure (my version can also accept and return the data in JSON format)
- Generate a JSON-formatted string from a Clojure data structure
- Compare a string to a regex to see if it matches the expected date format
- Divide a list of date strings into collections of valid and invalid dates (depending upon whether they match a regular expression)
- Build a date object using the specified year, month, and day (of month)
- Divide a list of dates into collections of valid and invalid, depending upon whether or not they are actually valid dates
- Evaluate a list of date objects and create a list of ranges of consecutive dates
  - Sort a list of date objects
  - Compare date objects to determine if they are consecutive
- Create unit tests to validate the behavior (which can be run using `lein test`)
