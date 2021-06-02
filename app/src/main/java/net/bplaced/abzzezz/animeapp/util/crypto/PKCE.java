/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 30.05.21, 19:25
 */

package net.bplaced.abzzezz.animeapp.util.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PKCE {

    public static String generateCodeChallenge(final String codeVerifier) throws NoSuchAlgorithmException {
        final byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(bytes, 0, bytes.length);
        final byte[] digest = messageDigest.digest();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    public static String generateCodeVerifier() {
        final SecureRandom secureRandom = new SecureRandom();
        final byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }

}
