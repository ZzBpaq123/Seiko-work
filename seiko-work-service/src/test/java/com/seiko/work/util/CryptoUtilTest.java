package com.seiko.work.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CryptoUtilTest {

    @BeforeAll
    static void init() {
        CryptoUtil.init("test-passphrase");
    }

    @Test
    void roundtrip() {
        String plaintext = "abcd efgh ijkl mnop";
        assertEquals(plaintext, CryptoUtil.decrypt(CryptoUtil.encrypt(plaintext)));
    }

    @Test
    void samePlaintextProducesDifferentCiphertext() {
        String a = CryptoUtil.encrypt("same-secret");
        String b = CryptoUtil.encrypt("same-secret");
        assertNotEquals(a, b);
    }

    @Test
    void nullAndEmptyPassThrough() {
        assertNull(CryptoUtil.encrypt(null));
        assertNull(CryptoUtil.decrypt(null));
        assertEquals("", CryptoUtil.encrypt(""));
        assertEquals("", CryptoUtil.decrypt(""));
    }

    @Test
    void tamperedCiphertextFails() {
        String ciphertext = CryptoUtil.encrypt("secret");
        char[] chars = ciphertext.toCharArray();
        chars[chars.length - 2] = chars[chars.length - 2] == 'A' ? 'B' : 'A';
        String tampered = new String(chars);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> CryptoUtil.decrypt(tampered));
        assertTrue(ex.getMessage().contains("解密失败"));
    }

    @Test
    void legacyPlaintextFailsClosed() {
        assertThrows(IllegalStateException.class, () -> CryptoUtil.decrypt("plain-auth-code"));
    }

    @Test
    void blankKeyRejected() {
        assertThrows(IllegalStateException.class, () -> CryptoUtil.init("   "));
    }
}
