/*
 *   Copyright (c) 2024 DEMnetwork
 *   All rights reserved.

 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package io.github.demnetwork.sjdb.dbelements.property;

import java.util.ArrayList;
import io.github.demnetwork.sjdb.dbelements.DBElement;
import io.github.demnetwork.sjdb.exceptions.UnsupportedPropertyException;

/**
 * Used to handle {@link io.github.demnetwork.sjdb.dbelements.DBElement
 * DBElement}'s properties
 * 
 * @since SJDB v1.0.0
 */
public class Properties {
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";
    private final DBElement<?> element;

    public Properties(DBElement<?> e) {
        this.element = e;
    }

    public boolean supports(DBElementPropertySupport property) {
        if (property.getPropertySupportID() == 0) {
            return (this.element instanceof NameProperty);
        }
        if (property.getPropertySupportID() == 1) {
            return (this.element instanceof FinalProperty);
        }
        if (property.getPropertySupportID() == 2) {
            if (this.element instanceof FinalStateLockProperty) {
                return (((FinalStateLockProperty) element).supportsFSLP() && element instanceof FinalProperty);
            } else {
                return false;
            }
        }
        if (property.getPropertySupportID() == 3) {
            return (this.element instanceof MaxElementCountProperty);
        }
        return false;
    }

    public ArrayList<DBElementPropertySupport> PropertiesAsArrayList() {
        ArrayList<DBElementPropertySupport> sList = new ArrayList<DBElementPropertySupport>();
        for (DBElementPropertySupport s : DBElementPropertySupport.values()) {
            if (this.supports(s)) {
                sList.add(s);
            }
        }
        return sList;
    }

    public Object getPropertyValue(DBElementPropertySupport property) {
        if (!this.supports(property)) {
            throw new UnsupportedPropertyException(property);
        }
        if (property == DBElementPropertySupport.NAME) {
            return ((NameProperty) element).getName();
        }
        if (property == DBElementPropertySupport.FINAL) {
            return Boolean.valueOf(((FinalProperty) this.element).isFinal());
        }
        if (property == DBElementPropertySupport.FINAL_STATE_LOCK) {
            return Boolean.valueOf(((FinalStateLockProperty) this.element).isFinalStateLocked());
        }
        if (property == DBElementPropertySupport.MAX_ELEMENT_COUNT) {
            return Integer.valueOf(((MaxElementCountProperty) this.element).getMaxElementCount());
        }
        return null; // This will never be executed
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Properties) {
            return (this.element == (((Properties) obj).element));
        }
        return false;
    }

    public static Properties getPropertiesOf(DBElement<?> e) {
        return new Properties(e);
    }

    public enum DBElementPropertySupport {
        NAME(0, "NameProperty"),
        FINAL(1, "FinalProperty"),
        FINAL_STATE_LOCK(2, "FinalStateLockProperty"),
        MAX_ELEMENT_COUNT(3, "MaxElementCountProperty");

        private int PropertySupportID;
        private String name;

        private DBElementPropertySupport(int i, String name) {
            this.PropertySupportID = i;
            this.name = name;
        }

        public int getPropertySupportID() {
            return this.PropertySupportID;
        }

        public String getName() {
            return this.name;
        }
    }
}