package com.wynndevs.modules.expansion.webapi;

import com.wynndevs.ModCore;

import java.util.Date;

public class WynnTerritory {
	
	public String Name = "";
	public int[][] Coords = {{0,0},{0,0}};
	public String Guild = "";
	public String Attacker = "";
	public Date CaptureData = null;
	
	public void FixCoords(){
		int[][] CoordsFix = {{Math.min(Coords[0][0], Coords[1][0]), Math.min(Coords[0][1], Coords[1][1])}, {Math.max(Coords[0][0], Coords[1][0]), Math.max(Coords[0][1], Coords[1][1])}};
		this.Coords = CoordsFix;
	}
	
	public String GetFormatedCoords() {
		return "[x:" + (Coords[0][0] + (Math.round((Coords[1][0] - Coords[0][0]) /2))) + ",z:" + (Coords[0][1] + (Math.round((Coords[1][1] - Coords[0][1]) /2))) + "]";
	}
	
	public String GetFormatedFullCoords() {
		return "[x:" + Coords[0][0] + ", " + Coords[1][0] + ",z:" + Coords[0][1] + ", " + Coords[1][1] + "]";
	}
	
	public boolean HasCoords() {
        return this.Coords[0][0] != 0 || this.Coords[0][1] != 0 || this.Coords[1][0] != 0 || this.Coords[1][1] != 0;
	}
	
	public boolean IsInside(){
        return ModCore.mc().player.posX >= this.Coords[0][0] && ModCore.mc().player.posX < this.Coords[1][0] + 1 && ModCore.mc().player.posZ >= this.Coords[0][1] && ModCore.mc().player.posZ < this.Coords[1][1] + 1;
	}
}