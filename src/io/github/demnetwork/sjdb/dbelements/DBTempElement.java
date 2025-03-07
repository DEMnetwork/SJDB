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

package io.github.demnetwork.sjdb.dbelements;

public final class DBTempElement extends DBElement<String> {
    private final String message;
    protected long duration;

    private final Runnable countDown = new Runnable() {
        private long cTime;

        @Override
        public void run() {
            while (duration > 0L) {
                this.cTime = System.currentTimeMillis();
                if ((System.currentTimeMillis() - this.cTime) >= 1000) {
                    duration = duration - 1L;
                }
            }
        }
    };
    private Thread t = new Thread(countDown, "DBTempElement_Timer-" + ((int) Math.random() * 100000000));

    public DBTempElement(String message, long Duration) {
        if (message.contains("[")) {
            throw new IllegalArgumentException("The character \'[\' is not allowed!");
        }
        this.message = message;
        this.duration = Duration;
        t.setDaemon(true);
        t.start();
    }

    public String getMessage() {
        if (this.duration <= 0) {
            throw new IllegalStateException("This element is expired!");
        }
        return this.message;
    }

    @Override
    public String toString() {
        if (this.duration <= 0) {
            throw new IllegalStateException("This element is expired");
        }
        return "[TEMPE][MSG]" + message + "[/MSG][DURATION]" + duration + "[/DURATION][/TEMPE]";
    }

    @Override
    public String get() {
        return this.getMessage();
    }

    @Override
    public void set(String s) {
        throw new UnsupportedOperationException("It is not allowed to change the message of a DBTempElement");
    }

    public void destroy() {
        this.duration = 0L;
    }
}
