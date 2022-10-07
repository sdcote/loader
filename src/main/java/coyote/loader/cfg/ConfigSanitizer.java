/*
 * Copyright (c) 2022 Stephan D. Cote' - All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License which accompanies this distribution, and is
 * available at http://creativecommons.org/licenses/MIT/
 */
package coyote.loader.cfg;

import coyote.commons.StringUtil;
import coyote.dataframe.DataField;
import coyote.dataframe.DataFrame;

import java.util.*;
import java.util.regex.Pattern;

public class ConfigSanitizer {
    public static final String PROTECTED = "[PROTECTED]";

    private static final Set<String> protectedFieldNames = new HashSet<>();

    public static void addProtectedFieldName(String fieldName) {
        if (StringUtil.isNotEmpty(fieldName)) {
            protectedFieldNames.add(fieldName);
        }
    }

    public static boolean symbolIsProtected(String symbol) {
        boolean retval = Pattern.compile(Pattern.quote("password"), Pattern.CASE_INSENSITIVE).matcher(symbol).find();
        if (!retval) retval = Pattern.compile(Pattern.quote("secret"), Pattern.CASE_INSENSITIVE).matcher(symbol).find();
        if (!retval)
            retval = Pattern.compile(Pattern.quote("privatekey"), Pattern.CASE_INSENSITIVE).matcher(symbol).find();
        if (!retval) retval = protectedFieldNames.contains(symbol);
        return retval;
    }


    /**
     * Remove the values of sensitive configuration items.
     *
     * @param config the configuration to sanitize.
     *
     * @return a deep copy of the configuration with the sensitive values masked.
     */
    public static Config sanitize(Config config) {
        Config retval = new Config();
        HashMap<String, ConfigSlot> slots = config.getSlots();
        if (slots != null) {
            for (Map.Entry<String, ConfigSlot> entry : slots.entrySet()) {
                retval.addConfigSlot(entry.getValue());
            }
        }
        for (DataField field : config.getFields()) {
            if (field.isFrame() && field.isNotNull()) {
                Config cfg = new Config();
                cfg.populate((DataFrame) field.getObjectValue());
                cfg = sanitize(cfg); //recursive call
                retval.add(field.getName(),cfg);
            } else if (field.isNotFrame()) {
                if (symbolIsProtected(field.getName())) {
                    retval.add(field.getName(), PROTECTED);
                } else {
                    retval.add(field.getName(), field.getObjectValue());
                }
            }
        }

        return retval;
    }

}
