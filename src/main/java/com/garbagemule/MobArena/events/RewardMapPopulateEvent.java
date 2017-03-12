package com.garbagemule.MobArena.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class RewardMapPopulateEvent extends Event
{
	
	public static enum RewardMapType
	{
		AFTER,
		EVERY;
		
		public static RewardMapType getType(String type)
		{
			return valueOf(type.toUpperCase());
		}
		
	}
	
	// required event code
	private static final HandlerList handlerList = new HandlerList();
	
	@Override
	public HandlerList getHandlers()
	{
		return handlerList;
	}
	
	// fields
	private RewardMapType type;
	private Map<Integer, List<ItemStack>> map;
	private String arenaName;
	
	// constructor
	public RewardMapPopulateEvent(Map<Integer, List<ItemStack>> map, RewardMapType type, String arenaName)
	{
		this.map = map;
		this.type = type;
		this.arenaName = arenaName;
	}
	
	public Map<Integer, List<ItemStack>> getMap()
	{
		return map;
	}
	
	public void setMap(Map<Integer, List<ItemStack>> map)
	{
		this.map = map;
	}
	
	public RewardMapType getType()
	{
		return type;
	}
	
	public String getArenaName()
	{
		return arenaName;
	}
	
}
