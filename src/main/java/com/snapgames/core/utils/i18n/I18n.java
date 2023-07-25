package com.snapgames.core.utils.i18n;

import java.util.*;

import com.snapgames.core.Application;
import com.snapgames.core.system.GSystem;

/**
 * Propose a Translation service implementation to satisfy the language and
 * country codification ISO 639.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class I18n implements GSystem {
    private static int index = 0;

    private ResourceBundle messages;

    private List<Locale> supportedLanguages;

    private static I18n instance;

    public I18n() {
    }

    public static List<Locale> getListAvailableLanguages() {
        return instance.supportedLanguages;
    }

    public static void activate(Locale localeCode) {
        instance.messages = ResourceBundle.getBundle("i18n.messages", localeCode);
    }

    public static String getMessage(String key) {
        return instance.messages.getString(key);
    }

    public static String getMessage(String key, String... varAgs) {
        return String.format(instance.messages.getString(key), varAgs);
    }

    public void roll() {
        activate(getListAvailableLanguages().get(index));
        index = index + 1 < getListAvailableLanguages().size() ? index + 1 : 0;
    }

    @Override
    public Class<? extends GSystem> getSystemName() {
        return I18n.class;
    }

    @Override
    public void initialize(Application app) {
        messages = ResourceBundle.getBundle("i18n.messages", Locale.ROOT);
        supportedLanguages = Arrays.asList(Locale.FRENCH, Locale.ENGLISH);
    }

    @Override
    public void dispose() {
        messages = null;
    }

    public static I18n get() {
        if (I18n.instance == null) {
            instance = new I18n();
        }
        return instance;
    }
}