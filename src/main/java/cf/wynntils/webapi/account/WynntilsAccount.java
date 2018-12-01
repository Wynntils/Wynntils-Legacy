/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.webapi.account;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.modules.core.CoreModule;
import cf.wynntils.modules.core.config.CoreDBConfig;
import cf.wynntils.webapi.WebManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.CryptManager;
import org.apache.commons.io.IOUtils;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class WynntilsAccount {

    static String token;
    boolean ready = false;

    public WynntilsAccount() {
        try{
            login();
        }catch (Exception ex) {
            ex.printStackTrace();
            token = CoreDBConfig.INSTANCE.lastToken;
        }
    }

    public static String getToken() {
        return token;
    }

    private void login() throws Exception {
        URLConnection st = new URL(WebManager.apiUrls.get("UserAccount") + "/requestEncryption").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JsonObject result = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject();

        byte[] publicKeyBy = DatatypeConverter.parseHexBinary(result.get("publicKeyIn").getAsString());

        SecretKey secretkey = CryptManager.createNewSharedKey();
        PublicKey publicKey = CryptManager.decodePublicKey(publicKeyBy);

        String s1 = (new BigInteger(CryptManager.getServerIdHash("", publicKey, secretkey))).toString(16);

        Minecraft mc = ModCore.mc();
        mc.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getToken(), s1);

        byte[] secretKeyEncrypted = CryptManager.encryptData(publicKey, secretkey.getEncoded());
        String lastKey = DatatypeConverter.printHexBinary(secretKeyEncrypted);

        URLConnection st2 = new URL(WebManager.apiUrls.get("UserAccount") + "/responseEncryption/").openConnection();

        JsonObject object = new JsonObject();
        object.addProperty("username", mc.getSession().getUsername());
        object.addProperty("key", lastKey);
        object.addProperty("version", Reference.VERSION);

        byte[] postAsBytes = object.toString().getBytes(StandardCharsets.UTF_8);

        st2.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st2.setRequestProperty("Content-Length", "" + postAsBytes.length);
        st2.setDoOutput(true);

        OutputStream outputStream = null;
        try {
            outputStream = st2.getOutputStream();
            IOUtils.write(postAsBytes, outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        JsonObject finalResult = new JsonParser().parse(IOUtils.toString(st2.getInputStream(), "UTF-8")).getAsJsonObject();
        if(finalResult.has("error")) {
            return;
        }

        if(finalResult.has("result")) {
            token = finalResult.get("authtoken").getAsString();
            ready = true;
            CoreDBConfig.INSTANCE.lastToken = token;
            CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
        }
    }

}
