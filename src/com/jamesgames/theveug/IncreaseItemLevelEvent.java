package com.jamesgames.theveug;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class IncreaseItemLevelEvent extends org.bukkit.event.Event
{
  private static final HandlerList handlers = new HandlerList();
  private final LevelItem item;
  private final Player itemHolder;
  private final int previousLevel;
  private final int newLevel;
  
  public IncreaseItemLevelEvent(LevelItem item, Player itemHolder, int previousLevel, int newLevel) { this.item = item;
    this.itemHolder = itemHolder;
    this.previousLevel = previousLevel;
    this.newLevel = newLevel;
  }
  
  public LevelItem getLevelItem() {
    return item;
  }
  
  public Player getItemHolder() {
    return itemHolder;
  }
  
  public int getPreviousLevel() {
    return previousLevel;
  }
  
  public int getNewLevel() {
    return newLevel;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
