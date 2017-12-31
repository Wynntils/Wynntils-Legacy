package com.wynndevs.modules.map;

import com.wynndevs.core.Reference;
import com.wynndevs.webapi.WebManager;
import com.wynndevs.webapi.profiles.MapMarkerProfile;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.model.MapImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample factory that generates a list of MarkerOverlays.
 */
class MarkerOverlayFactory {

    static List <Waypoint> create(IClientAPI jmAPI){
        String category;

        List <Waypoint> list = new ArrayList <>();

        Waypoint MarkerWaypoint;

        MapImage icon;


        for (int i = 0; i < WebManager.getMapMarkers().size(); i++) {
            MapMarkerProfile pf = WebManager.getMapMarkers().get(i);
            BlockPos pos = new BlockPos(pf.getX(), pf.getY(), pf.getZ());

            switch (pf.getIcon()) {
                case "book":
                    category = "quests";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/book.png"), 32, 32)
                            .setColor(0xffffff);
                    continue;
//                    break;
                case "dungeon":
                    category = "dungeons";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/dungeon.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "potion":
                    category = "potion";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/potion.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "rottenflesh":
                    category = "rottenflesh";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/rottenflesh.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "paper":
                    category = "paper";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/paper.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "horse":
                    category = "horse";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/horse.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "potato":
                    category = "potato";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/potato.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "bank":
                    category = "bank";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/bank.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "xpbottle":
                    category = "xpbottle";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/xpbottle.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "egg":
                    category = "egg";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/egg.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "weapon":
                    category = "weapon";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/weapon.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "weaponstrader":
                    category = "weaponstrader";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/weaponstrader.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "key":
                    category = "key";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/key.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "name_tag":
                    category = "name_tag";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/name_tag.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "accessory":
                    category = "accessory";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/accessory.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "powder_master":
                case "Powdermaster":
                    category = "Powdermaster";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/Powdermaster.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "identifier":
                    category = "identifier";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/eyeofender.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "wool":
                    category = "wool";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/wool.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "boat":
                    category = "boat";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/boat.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "bucket":
                    category = "bucket";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/bucket.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "armor":
                    category = "armor";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/armor.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "bowl":
                    category = "bowl";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/bowl.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "item_buyer":
                    category = "item_buyer";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/item_buyer.png"), 32, 32)
                            .setColor(0xffffff);
                    break;
                case "portal":
                    category = "portal";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/portal.png"), 32, 32)
                            .setColor(0xffffff);
                    break;

                default:
                    category = "default";
                    icon = new MapImage(new ResourceLocation(Reference.MOD_ID, "textures/map/NPC.png"), 29, 37)
                            .setColor(0xffffff);
                    break;
            }

            MarkerWaypoint = new Waypoint(Reference.MOD_ID, "marker_" + category + "_" + i, pf.getName(), 0, pos)
                    .setLabelColor(-1)
                    .setIcon(icon);

            try {
                jmAPI.show(MarkerWaypoint);
                list.add(MarkerWaypoint);
            } catch (Throwable t) {
                Reference.LOGGER.error(t.getMessage(), t);
            }

        }

        return list;
    }
}
