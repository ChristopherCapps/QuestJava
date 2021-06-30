package com.quest.baldydash;

import com.quest.io.Message;
import com.quest.model.Quest;
import com.quest.model.Waypoint;

public class BaldyDash2021 {

    final static Waypoint WHALE_HEAD_PARK = Waypoint.of(
            Message.of("Are you familiar with the TV show Survivor and its hidden idols? Find your way " +
                            "to Whale Head Park, where off the very beaten trail (and near its end) you'll find an 'idol' " + "in a typical " +
                            "hiding spot. Bring ONE idol with you, open it, and answer the question inside."),
            Waypoint.Lock.of("5", "five")); //TODO

    final static Waypoint CURRITUCK_HOPE = Waypoint.of(
            Message.of("What emotion `anchors the soul`, according to a flag at 507 Currituck Way?"),
            Waypoint.Lock.of("hope"));

    final static String LIFESAVING_SERVICE = "The US Life Saving Service was started by the government in 1848, its " +
            "mission to save the lives of shipwrecked mariners and passengers along the east coast of early American " +
            "transportation routes. ";

    final static Waypoint CHICAMACOMICO_LUCKY_DOGS = Waypoint.of(
            Message.of(LIFESAVING_SERVICE + "One of the Life Saving Service stations was at Chicamacomico along the " +
                    "Outer Banks of North Carolina. Proceed to the namesake of this station, Chicamacomico Way, " +
                    "and follow it nearly to Keeper's Landing. Just outside, find the year when Lucky Dog's was established."),
            Waypoint.Lock.of("2011"));

    final static Waypoint WASH_WOODS_MUSTARD = Waypoint.of(
            Message.of("Wash Woods, Virginia (near Virginia Beach) has been abandoned since the 1930s, except for the Life Saving station which " +
                    "was open until the mid-1950s. Along the Way named for this station, find the Sumners Crescent community. " +
                    "Once there, locate the home that Colonel Mustard of Clue would own. As you face it, on your right, which " +
                    "Clue character is the resident?"),
            Waypoint.Lock.of("green", "mr. green", "mr green", "mister green"));

    final static Waypoint KINNIKEET_TURTLE = Waypoint.of(
            Message.of("Kinnakeet is an Algonquian Indian word meaning 'mixed', referring to multiple cultures living together. This " +
                    "North Carolina town is now known as Avon, NC. Find a house under construction on the Way named for this town, " +
                    "where next to it is another home with this creature greeting visitors on the front porch."),
            Waypoint.Lock.of("turtle"));

    final static Waypoint MARKET_CARMEX = Waypoint.of(
            Message.of("While you're at the market, find this staple of Ethan's life. How many cents does it cost?"),
            Waypoint.Lock.of("239"));

    final static Waypoint MARKET_POPCORN = Waypoint.of(
            Message.of("While you're at the market, find this favorite of Finley's. How many cents does it cost?"),
            Waypoint.Lock.of("499"));

    final static Waypoint MARKET_SHARED = Waypoint.of(
            Message.of("Head to the Maritime Market and find this American classic. How many cents does it cost?",
                    "https://stizza-bongo-8997.twil.io/assets/PottedMeat.png"),
            Waypoint.Lock.of("99", "99 cents", "99 pennies", "ninety-nine", "ninetynine"));

    final static Waypoint NOONIE_DOODLE = Waypoint.of(
            Message.of("At the Noonie Doodle, what's the name of any of the superheroes adorning lunchboxes on the " +
                    "back wall?"),
            Waypoint.Lock.of("pac-man", "pacman", "heman", "he-man", "captain america"));

    final static Waypoint ALL_ABOUT_ART = Waypoint.of(
            Message.of("Along Merchant's Row, in what compass direction does the whale outside of All About Art point?"),
            Waypoint.Lock.of("north", "east", "west", "south", "northeast", "southeast", "southwest", "northwest", "nw", "sw", "ne", "se", "n", "e", "s", "w"));

    final static Waypoint PUBLIC_SAFETY_BUILDING = Waypoint.of(
            Message.of("Now, make your way to the island's public safety building and find a prominent red number. The" +
                    " key to this lock is the prime number closest to the red number."),
            Waypoint.Lock.of("31", "thirty-one", "thirty one", "thirtyone"));

    final static Waypoint LOCKER_CODE = Waypoint.of(
            Message.of("Now it's time for you to pilfer like a pirate! Head to the Island Mini Storage and you'll find " +
                    "a recently-opened locker at #41. What code was used to open the lock? (Be careful not to disturb the " +
                    "combination!) Hint: You may need to flip the light switch, but be sure to flip it off on your way out."),
            Waypoint.Lock.of("race"));

    final static Waypoint QUEEN_ANNE_A = Waypoint.of(
            Message.of("Along the road named for Blackbeard, find the house bearing his ship's name. " +
                    "What is the sum of the digits in its house number?"),
            Waypoint.Lock.of("3", "three"));

    final static Waypoint QUEEN_ANNE_B = Waypoint.of(
            Message.of("Find the Wynd named after the infamous pirate Blackbeard, and find the house bearing his " +
                    "ship's name. What is the sum of the digits in its house number?"),
            Waypoint.Lock.of("3", "three"));

    final static Waypoint THREE_FLIPPER_TRAIL_A = Waypoint.of(
            Message.of("Find the Wynd named after the infamous pirate Blackbeard, and take a diversion from pirating down " +
                    "Three Flipper Trail. When you've gone as far as you can go, " +
                    "find some unusual outdoor artwork in which a man is hoisting something over his head. What is it?"),
            Waypoint.Lock.of("moon", "crescent", "crescent moon"));

    final static Waypoint THREE_FLIPPER_TRAIL_B = Waypoint.of(
            Message.of("Take a diversion from pirating down " +
                    "Three Flipper Trail. When you've gone as far as you can go, " +
                    "find some unusual outdoor artwork in which a man is hoisting something over his head. What is it?"),
            Waypoint.Lock.of("moon", "crescent", "crescent moon"));

    final static Waypoint COLONEL_RHETT = Waypoint.of(
            Message.of("Colonel William Rhett led a naval expedition against pirates in 1718 and captured Stede Bonnet " +
                    "and his men. Bonnet wrote to the SC governor to ask for clemency, but was denied. He was hanged in " +
                    "Charleston in December of that year. Make your way to the only home on the turnaround of the Court " +
                    "celebrating Col. William Rhett. What's its accent color?"),
            Waypoint.Lock.of("yellow"));

    final static Waypoint DRAGONFILES_MARCH_A = Waypoint.of(
            Message.of("Proceed to the Wynd named for `The Gentleman Pirate` of the North Carolina coast. He was a friend of Blackbeard's " +
                    "and resided on Blackbeard's ship as a visitor in 1717. Along the Wynd, find the house number that " +
                    "corresponds with the Ides of March. What is the color of the dragonflies?"),
            Waypoint.Lock.of("blue"));

    final static Waypoint DRAGONFILES_MARCH_B = Waypoint.of(
            Message.of("Along Stede Bonnet's Wynd, proceed to the " +
                    "house number that corresponds with the Ides of March. What is the color of the dragonflies?"),
            Waypoint.Lock.of("blue"));

    final static Waypoint THISTLE_RIDGE = Waypoint.of(
            Message.of("Scramble to the very top of Thistle Ridge, locating the overgrown and cracked drive of a demolished " +
                    "home. Follow the drive to the pictured vantage point; looking down, find the brand of the abandoned " +
                    "beer bottle at your feet.",
                    "https://stizza-bongo-8997.twil.io/assets/ThistleRidgeApex.png"),
            Waypoint.Lock.of("corona"));

    final static Waypoint BEHIND_LAUGHING_GULL = Waypoint.of(
            Message.of("Make your way to this house on Laughing Gull Trail. Off in the distance behind and to the right " +
                    "can be seen another home on an adjacent road. What is the name of this nearby home?",
                    "https://stizza-bongo-8997.twil.io/assets/LaughingGullHouse.png"),
            Waypoint.Lock.of("dunes", "my 3 dunes", "my three dunes"));

    final static Waypoint BEHIND_INDIAN_BLANKET = Waypoint.of(
            Message.of("Make your way to this house on Indian Blanket Trail. Off in the distance and to the left " +
                    "of the house can be seen another home. What animal is featured next to the front door of this home" +
                    "on an adjacent road?",
                    "https://stizza-bongo-8997.twil.io/assets/IndianBlanketHouse.png"),
            Waypoint.Lock.of("whale"));

    final static Waypoint BOURNE_A = Waypoint.of(
            Message.of("Find 212 Stede Bonnet. What is the first name of the action hero character who might live here?"),
            Waypoint.Lock.of("jason"));

    final static Waypoint BOURNE_B = Waypoint.of(
            Message.of("Make your way to the Wynd named for `The Gentleman Pirate` of the North Carolina coast. " +
                    "He was a friend of Blackbeard's and resided on Blackbeard's ship as a visitor in 1717. On this Wynd, " +
                    "proceed to house 212. What is the first name of the action hero character who might live here?"),
            Waypoint.Lock.of("jason"));

    final static Waypoint LONGITUDE = Waypoint.of(
            Message.of("At 210 Writer's Way, find the degrees west longitude for BHI, next to the grill."),
            Waypoint.Lock.of("77.99", "77", "7799"));

    final static Waypoint KILLEGRAY_MATRIX = Waypoint.of(
            Message.of("Find the house number sign in Killegray Ridge Cottages that lists house #2, among others. If you draw a line " +
                    "from 4 to 2 to 8 to 10 to 12, what letter do you have?"),
            Waypoint.Lock.of("m", "letter m"));

    final static Quest.Course courseA = Quest.Course.of(
            "Course A",
            WHALE_HEAD_PARK,
            CURRITUCK_HOPE,
            THISTLE_RIDGE,
            BEHIND_INDIAN_BLANKET,
            DRAGONFILES_MARCH_A,
            COLONEL_RHETT,
            BOURNE_A,
            THREE_FLIPPER_TRAIL_A,
            QUEEN_ANNE_A,
            MARKET_SHARED,
            NOONIE_DOODLE,
            ALL_ABOUT_ART,
            PUBLIC_SAFETY_BUILDING,
            LOCKER_CODE,
            KILLEGRAY_MATRIX,
            CHICAMACOMICO_LUCKY_DOGS,
            WASH_WOODS_MUSTARD,
            LONGITUDE);

    final static Quest.Course courseB = Quest.Course.of(
            "Course B",
            WHALE_HEAD_PARK,
            KINNIKEET_TURTLE,
            KILLEGRAY_MATRIX,
            PUBLIC_SAFETY_BUILDING,
            LOCKER_CODE,
            ALL_ABOUT_ART,
            NOONIE_DOODLE,
            MARKET_SHARED,
            QUEEN_ANNE_B,
            THREE_FLIPPER_TRAIL_B,
            BOURNE_B,
            COLONEL_RHETT,
            DRAGONFILES_MARCH_B,
            BEHIND_LAUGHING_GULL,
            THISTLE_RIDGE,
            CHICAMACOMICO_LUCKY_DOGS,
            WASH_WOODS_MUSTARD,
            LONGITUDE);

    final public static Quest BALDY_DASH_QUEST = Quest.of(
            "Baldy Dash 2021",
            courseA,
            courseB);

}
