package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.Arena;

public class SpecialWave extends AbstractWave
{
	// Recurrent
	public SpecialWave(Arena arena, String name, int wave, int frequency, int priority)
	{
		super(arena, name, wave, frequency, priority);
	}
	
	// Single
	public SpecialWave(Arena arena, String name, int wave)
	{
		super(arena, name, wave);
	}

	public void spawn(int wave)
	{
		System.out.println("WAVE SPAWN! Wave: " + wave + ", name: " + getName() + ", type: " + getType());
	}
}
