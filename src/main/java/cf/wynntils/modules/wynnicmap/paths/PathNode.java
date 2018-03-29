package cf.wynntils.modules.wynnicmap.paths;

import java.io.Serializable;
import java.util.HashSet;

public class PathNode implements Serializable {
    public HashSet<PathNode> connectedNodes = new HashSet<>();
    public HashSet<PathNode> connectedTeleportNodes = new HashSet<>();
    public int x,y,z,dim;
    public String name, requirements;
    public int searchCounter = -1;

    public PathNode(int x, int y, int z, int dim, String name, String requirements) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.name = name;
        this.requirements = requirements;
    }

    public PathNode add(PathNode node, boolean teleport, boolean backTrack) {
        if(teleport)
            connectedTeleportNodes.add(node);
        else
            connectedNodes.add(node);
        if(backTrack) node.add(this, teleport,false);
        return node;
    }

    public void remove(PathNode node) {
        connectedNodes.remove(node);
        connectedTeleportNodes.remove(node);
    }

    public void terminate() {
        for(PathNode pn : connectedNodes)
            pn.remove(this);
        for(PathNode pn : connectedTeleportNodes)
            pn.remove(this);
    }
}
