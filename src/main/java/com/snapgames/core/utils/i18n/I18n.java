package com.snapgames.core.utils.i18n;

import java.util.*;

/**
 * Propose a Translation service  implementation to satisfy the language and country codification ISO 639.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class I18n {
    private static int index = 0;

    private static ResourceBundle messages =
            ResourceBundle.getBundle("i18n.messages", Locale.ROOT);

    private static final List<Locale> supportedLanguages = Arrays.asList(Locale.FRENCH, Locale.ENGLISH);
    private static Locale currentLocale = Locale.getDefault();

    public static List<Locale> getListAvailableLanguages() {
        return supportedLanguages;
    }

    public static void activate(Locale localeCode) {
        currentLocale = localeCode;
        messages = ResourceBundle.getBundle("i18n.messages", localeCode);
    }

    public static String getMessage(String key) {
        return messages.getString(key);
    }

    public static String getMessage(String key, String... varAgs) {
        return String.format(messages.getString(key), varAgs);
    }

    public void roll() {
        activate(getListAvailableLanguages().get(index));
        index = index + 1 < getListAvailableLanguages().size() ? index + 1 : 0;
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}