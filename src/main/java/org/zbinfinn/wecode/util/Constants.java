package org.zbinfinn.wecode.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final List<Block> CODESPACE_BLOCKS = List.<Block>of(
            Blocks.STONE,
            Blocks.GRASS_BLOCK,
            Blocks.GLASS,
            Blocks.BLACK_STAINED_GLASS,
            Blocks.BLUE_STAINED_GLASS,
            Blocks.LIGHT_BLUE_STAINED_GLASS,
            Blocks.CYAN_STAINED_GLASS,
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.RED_STAINED_GLASS,
            Blocks.YELLOW_STAINED_GLASS,
            Blocks.GREEN_STAINED_GLASS,
            Blocks.LIME_STAINED_GLASS,
            Blocks.GRAY_STAINED_GLASS,
            Blocks.LIGHT_GRAY_STAINED_GLASS,
            Blocks.BROWN_STAINED_GLASS, // Tanks to JustAMartian for pointing out the lack of this
            Blocks.PINK_STAINED_GLASS, // Tanks to JustAMartian for pointing out the lack of this
            Blocks.PURPLE_STAINED_GLASS, // Tanks to JustAMartian for pointing out the lack of this
            Blocks.MAGENTA_STAINED_GLASS, // Tanks to JustAMartian for pointing out the lack of this
            Blocks.TINTED_GLASS,
            Blocks.WHITE_STAINED_GLASS,

            // Attempt at working with codeclient
            Blocks.AIR,

            // As per RaffyRaffy14's request
            Blocks.DIRT,

            // As per sapfii's request
            Blocks.MAGENTA_CONCRETE,
            Blocks.MAGENTA_CONCRETE_POWDER
    );
    public static void init() {



    }
}
