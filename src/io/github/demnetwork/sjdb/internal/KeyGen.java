/*
 *   Copyright (c) 2025 DEMnetwork
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

package io.github.demnetwork.sjdb.internal;

import java.security.*;
import java.security.spec.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class KeyGen implements Cloneable {
    static {
        double a = System.currentTimeMillis();
        a = Math.log(a);
        a = Math.pow(1.000025D, 10 * a / System.currentTimeMillis()) * 0.982 / 0.9381 - (Math.pow(1.845, 2.764524D));
        ps = a;
    }

    private final double seed;
    protected KeyContainer c = null;
    protected static final double ps;

    protected KeyGen(long seed) {
        this(seed, 64);
    }

    protected KeyGen(long seed, int iterationCountForSeedDerivation) {
        if (iterationCountForSeedDerivation <= 15) {
            throw new IllegalArgumentException("Unsercure Interation Count to derivate seed!");
        }
        double d = seed;
        double lI = 0;
        for (int i = 0; i < iterationCountForSeedDerivation; i++) {
            if (!Double.isFinite(d)) {
                d = Math.pow(Double.doubleToRawLongBits(lI) * ps, 2);
            }
            d = Math.pow(Math.log10(Math.log(Math.abs(d / 10 * -1 + 10))) * ps, 2);
            lI = d;
        }
        if (!Double.isFinite(d)) {
            d = Math.pow(Double.doubleToRawLongBits(lI) * ps, 4) * lI;
        }
        this.seed = d;
    }

    public String generateKey(String data) {
        return this.generateKey(data, 65536);
    }

    public String generateKey(String data, int iterationCount) {
        SecureRandom r = new SecureRandom(getBytes(Double.doubleToRawLongBits(this.seed)));
        SecureRandom cs = new SecureRandom();
        byte[] std = new byte[64];
        r.nextBytes(std);
        byte[] salt = new byte[64];
        cs.nextBytes(salt);
        std = KeyDerivator.derivateKey(std, salt, iterationCount, 256, 64);
        c = new KeyContainer(std);
        return new String(std);
    }

    protected static final byte[] getBytes(long l) {
        final byte[] result = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= Byte.SIZE;
        }
        return result;
    }

    protected static final String ByteArrayToString(byte[] b) {
        byte[] ba = b.clone();
        String s = "";
        for (int i = 0; i < ba.length; i += 4) {
            s = s + Integer
                    .toHexString(Byte.toUnsignedInt(ba[i]) + (Byte.toUnsignedInt(ba[i + 1]) * 256)
                            + (Byte.toUnsignedInt(ba[i + 2]) * 65536)
                            + (Byte.toUnsignedInt(ba[i + 3])) * 16777216);
        }
        return s;
    }

    protected final class KeyContainer {
        private final byte[][] data;

        public KeyContainer(byte[] key) {
            this.data = new byte[3][64];
            this.data[2] = key.clone();
            this.data[0] = HashGenerator.getHash(key).clone();
            this.data[1] = KeyGen.getBytes(Double.doubleToRawLongBits(KeyGen.this.seed));
        }

        public byte[] getData() {

            if (!java.util.Arrays.equals(this.data[0], HashGenerator.getHash(this.data[2]))) {
                throw new RuntimeException("Data Integrity Verification Failed!");
            }
            return this.data[2].clone();
        }
    }

    protected static final class HashGenerator {
        private HashGenerator() {
        }

        public static byte[] getHash(byte[] data) {
            if (data == null) {
                throw new NullPointerException("This method does not allow \'null\' data!");
            }
            if (data.length == 0) {
                throw new IllegalArgumentException("This method expected \'data\' with length > 0 but found length 0.");
            }
            try {
                MessageDigest md = MessageDigest.getInstance("SHA3-512");
                return md.digest(java.util.Arrays.copyOf(data, data.length));
            } catch (Exception e) {
                if (e instanceof NoSuchAlgorithmException) {
                    throw new UnsupportedOperationException("SHA3-512 is not supported! ", e);
                }
                throw new HashGeneratorError("Unable to generate Hash(This can be caused by system problems).", e);
            }
        }

        private static final class HashGeneratorError extends Error {
            public HashGeneratorError(String msg, Throwable cause) {
                super(msg, cause);
            }
        }
    }

    protected static final class KeyDerivator {
        public static byte[] derivateKey(byte[] k, byte[] s, int N, int p, int l) {
            double D = log2(N);
            if (D != Math.ceil(D) || D < 14) {
                throw new IllegalArgumentException("Argument N must be apart of base 2 and greather than 16383");
            }
            if (p < 256) {
                throw new IllegalArgumentException("Unsecure \'p\' value!");
            }
            final byte[] salt = s.clone();
            final byte[] key = PBKDF2(k.clone(), salt, N * 16, l);
            byte[][] b = new byte[p][l];
            b[0] = PBKDF2(key, salt.clone(), p, l);
            for (int i = 1; i < p; i++) {
                b[i] = PBKDF2(b[i - 1], salt, N * 16, l);
            }
            return getHash(b);
        }

        private static byte[] PBKDF2(byte[] a, byte[] b, int i, int l) {
            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(new String(b).toCharArray(), b, i, l);
                return factory.generateSecret(spec).getEncoded();
            } catch (Exception e) {
                return new byte[0];
            }
        }

        private static final byte[] getHash(byte[][] b) {
            double[] r = new double[b.length * b[0].length];
            for (int i = 0; i < b.length; i++) {
                for (int i2 = 1; i2 < b[i].length; i2++) {
                    r[i - 1] = StrictMath.log(b[i][i2] ^ b[i][i2 - 1]) * (b[0].length / b.length);
                }
            }
            byte[] b2 = new byte[r.length * 8];
            getHash0(r, b2);
            return b2;
        }

        private static void getHash0(double[] r, byte[] b) {
            int li = 0;
            for (int i = 0; i < r.length; i++) {
                long l = Double.doubleToRawLongBits(r[i]);
                byte[] B = getBytes(l);
                for (int i2 = 0; i2 < B.length; i2++) {
                    b[li] = b[i];
                    li++;
                }
            }
            b = HashGenerator.getHash(b);
        }
    }

    protected static final double log2(double d) {
        return (StrictMath.log(d) / StrictMath.log(2));
    }

    public final long getSeed() {
        return KeyGen.getSeed(this);
    }

    public final static long getSeed(KeyGen keyGen) {
        if (keyGen == null) {
            throw new NullPointerException("Null keyGen provided");
        }
        return Double.doubleToRawLongBits(keyGen.seed);
    }

    public abstract KeyGen clone() throws CloneNotSupportedException;

    public abstract String toString();

    public static final KeyGen getInstance(final long seed, final int iterationCount) {
        return new KeyGen(seed, iterationCount) {
            public final KeyGen clone() {
                return KeyGen.getInstance(seed, iterationCount);
            }

            public final String toString() {
                return "{KeyGen} s=" + Long.toHexString(super.getSeed());
            }
        };
    }

}