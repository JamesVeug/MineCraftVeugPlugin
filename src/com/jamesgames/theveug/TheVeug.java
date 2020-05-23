package com.jamesgames.theveug;

import com.jamesgames.theveug.Config.ImportedData;
import com.jamesgames.theveug.LevelItem.LevelItem;
import com.jamesgames.theveug.LevelItem.LevelItemFactory;
import com.jamesgames.theveug.LevelItem.LevelItemHandler;
import com.jamesgames.theveug.util.Util;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.script.ScriptException;
import java.util.List;
import java.util.Random;

public class TheVeug {
    public static void CreateDrop(ImportedData blockData, World world, Location location, LevelItem levelItem)
    {
        // Get list of items to drop
        List<ImportedData.ImportedDataDrop> itemDrops = blockData.ItemDrops;
        if(itemDrops.size() <= 0)
        {
            // Nothing to drop
            return;
        }

        // Choose a random item to drop
        int index = new Random().nextInt(itemDrops.size());
        ImportedData.ImportedDataDrop drop = itemDrops.get(index);

        // Get chance of dropping
        double chance = Main.Instance.Config.getDropRate();
        try
        {
            double evaluated = Main.Instance.Config.solve(drop.Equation).doubleValue();
            if(levelItem != null) {
                evaluated *= levelItem.getDropRateBuff();
            }
            chance = Util.clamp(chance * evaluated, 0, 100D);
        }
        catch (ScriptException e)
        {
            Main.Instance.getLogger()
                    .info("An exception occurred while attempting to solve item percentage for the material '"
                            + drop.material + "' : '" + drop.Equation + "'.");
            return;
        }

        // Roll random change
        double random = Math.random();
        if (random * 100.0D >= chance * 100.0D)
            return;

        // Drop Item
        ItemStack dropItem = new ItemStack(drop.material, 1);
        world.dropItem(location, dropItem);
    }

    public static void AwardWeaponXP(Player player, long rewardedXP, LevelItem levelItem)
    {
        if(rewardedXP <= 0L)
        {
            return;
        }

        // Add XP
        LevelItemHandler.Instance.AddExperience(levelItem, rewardedXP, player);
    }
}
