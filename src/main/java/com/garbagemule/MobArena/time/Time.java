package com.garbagemule.MobArena.time;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Time
{
    DAWN(23000),
    SUNRISE(23500),
    MORNING(23900),
    MIDDAY(6000),
    NOON(6000),
    DAY(8000),
    AFTERNOON(11000),
    EVENING(12000),
    SUNSET(12600),
    DUSK(13300),
    NIGHT(14000),
    MIDNIGHT(18000);

    private final int time;

}
