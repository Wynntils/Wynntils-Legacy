/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.webapi.account;

import cf.wynntils.ModCore;
import cf.wynntils.modules.core.CoreModule;
import cf.wynntils.modules.core.config.CoreDBConfig;
import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.CryptManager;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.PublicKey;

public class WynntilsAccount {

    String token;
    boolean ready = false;

    public WynntilsAccount() {
        try{
            login();
        }catch (Exception ex) {
            ex.printStackTrace();
            token = CoreDBConfig.INSTANCE.lastToken;
        }
    }

    public String getToken() {
        return token;
    }

    private void login() throws Exception {
        URLConnection st = new URL(WebManager.apiUrls.get("UserAccount") + "/requestEncryption").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONObject result = new JSONObject(IOUtils.toString(st.getInputStream()));

        byte[] publicKeyBy = DatatypeConverter.parseHexBinary(result.getString("publicKeyIn"));

        SecretKey secretkey = CryptManager.createNewSharedKey();
        PublicKey publicKey = CryptManager.decodePublicKey(publicKeyBy);

        String s1 = (new BigInteger(CryptManager.getServerIdHash("", publicKey, secretkey))).toString(16);

        Minecraft mc = ModCore.mc();
        mc.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getToken(), s1);

        byte[] secretKeyEncrypted = CryptManager.encryptData(publicKey, secretkey.getEncoded());
        String lastKey = DatatypeConverter.printHexBinary(secretKeyEncrypted);

        URLConnection st2 = new URL(WebManager.apiUrls.get("UserAccount") + "/responseEncryption").openConnection();

        byte[] postAsBytes =  new JSONObject().put("username", mc.getSession().getUsername()).put("key", lastKey).toString().getBytes(Charsets.UTF_8);

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

        JSONObject finalResult = new JSONObject(IOUtils.toString(st2.getInputStream()));
        if(finalResult.has("error")) {
            return;
        }

        if(finalResult.has("result")) {
            token = finalResult.getString("authtoken");
            ready = true;
            CoreDBConfig.INSTANCE.lastToken = token;
            CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
        }
    }

}
