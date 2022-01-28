/*
 *  * Copyright © Wynntils - 2018 - 2022.
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

    @Setting(displayName = "Text Translations", description = "Should text messages be automatically translated to a foreign language?\n\n§8Please note that translations will temporarily stop working when you are being rate limited by the translation service.")
    public boolean enableTextTranslation = false;

    @Setting(displayName = "Player Chat", description = "Should messages sent by other players be translated?")
    public boolean translatePlayerChat = false;

    @Setting(displayName = "NPC Lines", description = "Should messages spoken by NPCs be translated?")
    public boolean translateNpc = true;

    @Setting(displayName = "Quest Dialogue Lines", description = "Should dialogue during quests be translated?")
    public boolean translateQuestDialogue = true;

    @Setting(displayName = "Other", description = "Should other messages, such as system information, be translated?")
    public boolean translateOther = false;

    @Setting(displayName = "Tracked Quest", description = "Should the tracked quest overlay be translated?\n\n§8The game needs to be restarted for this setting to take effect when modified.")
    public boolean translateTrackedQuest = false;

    @Setting(displayName = "Target Language Code", description = "What is the ISO two letter language code of the target language?\n\n§8You can find a list of ISO codes by searching for `List of ISO 639-1 codes` on Wikipedia.")
    public String languageName = "en";

    @Setting(displayName = "Display Original", description = "Should the original message be displayed alongside the translation?")
    public boolean keepOriginal = true;

    @Setting(displayName = "Translation Service", description = "Which translation service should be used?")
    public TranslationManager.TranslationServices translationService = TranslationManager.TranslationServices.GOOGLEAPI;

    @Setting(displayName = "Remove Accent Marks", description = "Should accent marks be removed from translations?\n\n§8Accent marks can cause display problems in some languages. Enabling this setting will avoid those issues.")
    public boolean removeAccents = false;

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
