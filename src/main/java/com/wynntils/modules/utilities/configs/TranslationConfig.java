/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.webapi.services.TranslationManager;

@SettingsInfo(name = "chat_translation", displayPath = "Utilities/Translation")
public class TranslationConfig extends SettingsClass {
    public static TranslationConfig INSTANCE;

    @Setting(displayName = "Enable Text Translation", description = "Should text messages be automatically translated to a foreign language?")
    public boolean enableTextTranslation = false;

    @Setting(displayName = "Translate Player Chat", description = "Should messages sent by other users be translated?")
    public boolean translatePlayerChat = false;

    @Setting(displayName = "Translate NPC Lines", description = "Should messages spoken by NPCs be translated?")
    public boolean translateNpc = true;

    @Setting(displayName = "Translate Other", description = "Should other messages, like system information, be translated?")
    public boolean translateOther = false;

    @Setting(displayName = "Translate Tracked Quest", description = "Should the tracked quest overlay be translated? (needs restart)")
    public boolean translateTrackedQuest = false;

    @Setting(displayName = "Target Language Code", description = "What is the ISO two letter language code of the target language?")
    public String languageName = "en";

    @Setting(displayName = "Keep Original", description = "Should the original message be displayed alongside the translation?")
    public boolean keepOriginal = true;

    @Setting(displayName = "Translation Service", description = "Which translation service should be used?")
    public TranslationManager.TranslationServices translationService = TranslationManager.TranslationServices.GOOGLEAPI;

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("translationService")) {
            TranslationManager.resetTranslator();
        }
        if (name.equals("translateTrackedQuest") || INSTANCE.translateTrackedQuest) {
            // Re-parse quests if any value changed here can affect quest overlays
            QuestManager.clearData();
        }
    }

}