package com.quest.io;

public class Emoji {

    public static final String RUNNER = emoji(0x1F3C3);
    public static final String DRINK = emoji(0x1F379);
    public static final String PARTY = emoji(0x1F389);
    public static final String TROPHY = emoji(0x1F3C6);
    public static final String SNAIL = emoji(0x1F40C);
    public static final String TURTLE = emoji(0x1F422);
    public static final String THUMBS_UP = emoji(0x1F44D);
    public static final String THUMBS_DOWN = emoji(0x1F44E);
    public static final String CLAPPING = emoji(0x1F44F);
    public static final String HUNDRED_PERCENT = emoji(0x1F4AF);
    public static final String LOCK = emoji(0x1F512);
    public static final String UNLOCK = emoji(0x1F513);
    public static final String FIRE = emoji(0x1F525);
    public static final String CHECKERED_FLAG = emoji(0x1F3C1);
    public static final String QUESTIONER = emoji(0x1F481);
    public static final String HIGH_FIVE = emoji(0x1F64C);
    public static final String CHICKEN = emoji(0x1F414);
    public static final String COMPUTER = emoji(0x1F4BB);
    public static final String CROSSMARK = emoji(0x1274C);
    public static final String DISAPPOINTED_FACE = emoji(0x1F61E);
    public static final String POUTING_FACE = emoji(0x1F621);
    public static final String STUCK_OUT_TONGUE = emoji(0x1F61C);
    public static final String SMIRKING_FACE = emoji(0x1F60F);

    private static String emoji(final int codePoint) {
        return new String(Character.toChars(codePoint));
    }

}
