package cf.wynntils.webapi.profiles;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.utils.MD5Verification;
import cf.wynntils.modules.core.config.CoreDBConfig;
import cf.wynntils.modules.core.enums.UpdateStream;
import cf.wynntils.modules.core.overlays.UpdateOverlay;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.WebReader;

public class UpdateProfile {

    boolean hasUpdate = false;
    boolean emergencyUpdate = false;
    String latestUpdate = Reference.VERSION;

    private WebReader versions;

    public UpdateProfile() {
        new Thread(() -> {
            try{
                if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.CUTTING_EDGE) {
                    MD5Verification md5Installed = new MD5Verification(ModCore.jarFile);
                    String cuttingEdgeMd5 = WebManager.getCuttingEdgeJarFileMD5();
                    if (!md5Installed.getMd5().equals(cuttingEdgeMd5)) {
                        UpdateOverlay.reset();
                        hasUpdate = true;
                        latestUpdate = "B" + WebManager.getCuttingEdgeBuildNumber();
                    }
                } else {
                    versions = new WebReader("http://api.wynntils.cf/versions");

                    try {
                        Integer latest = Integer.valueOf(versions.get(Reference.MINECRAFT_VERSIONS).replace(".", "").replace("\n", "").replace("!", ""));
                        Integer actual = Integer.valueOf(latestUpdate.replace(".", ""));

                        if (!latest.equals(actual) && versions.get(Reference.MINECRAFT_VERSIONS).contains("!")) {
                            UpdateOverlay.forceDownload();
                            hasUpdate = true;
                        }

                        if (latest > actual) {
                            UpdateOverlay.reset();
                            hasUpdate = true;
                            latestUpdate = versions.get(Reference.MINECRAFT_VERSIONS);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }catch(Exception ex) { ex.printStackTrace(); }
        }).start();
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public void forceUpdate() {
        UpdateOverlay.reset();
        hasUpdate = true;
    }

    public String getLatestUpdate() {
        return latestUpdate;
    }

}
