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

public interface FinalStateLockProperty extends PropertyInterface {
    public static final int BUILD_NUMBER = 1;
    public static final String VERSION = "v1.0.0";

    /**
     * This class is used to check if the class supports the
     * <code>FinalStateLock</code> property
     * 
     * @return true if the class that implements it supports the FinalStateLock,
     *         if not it returns false.
     * 
     * @apiNote
     *          <p>
     *          It is not recomended to use this for checking support, use
     *          {@link io.github.demnetwork.sjdb.dbelements.property.Properties#supports(io.github.demnetwork.sjdb.dbelements.property.Properties.DBElementPropertySupport)
     *          support(Properties.DBElementPropertySupport property)} (from
     *          {@link io.github.demnetwork.sjdb.dbelements.property.Properties
     *          Properties} class) instead. This happens because classes that
     *          implement
     *          {@link io.github.demnetwork.sjdb.dbelements.property.FinalStateLockProperty
     *          FinalStateLockProperty} can override the method
     *          {@link io.github.demnetwork.sjdb.dbelements.property.FinalStateLockProperty#supportsFSLP()
     *          supportsFSLP()}, bypassing this check. Here is an example of how to
     *          check using Properties class:
     *          </p>
     *          <p>
     * 
     *          <pre>
     * 
     *          public class Main {
     *              public static void main(String[] args) {
     *                  DBString my_DBString = new DBString("Test", "My String", false, false);
     *                  Properties properties = my_DBString.getProperties();
     *                  boolean supportsFSL = properties.supports(DBElementPropertySupport.FINAL_STATE_LOCK);
     *                  System.out.println("Supports FinalStateLock: " + supportsFSL); // Outputs 'Supports FinalStateLock: true'
     *              }
     *          }
     *          </pre>
     *          </p>
     */
    public default boolean supportsFSLP() {
        if (this instanceof FinalProperty) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean supportsFSLP(Object target) {
        return (target instanceof FinalProperty && target instanceof FinalStateLockProperty);
    }

    public boolean isFinalStateLocked();

    public void lockFinalState();
}
