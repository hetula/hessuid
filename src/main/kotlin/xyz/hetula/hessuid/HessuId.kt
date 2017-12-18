/*
 * Copyright (c) 2017 Tuomo Heino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.hetula.hessuid

import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.*


/**
 * HessuId class generates id from given Path/Uri.
 * Same Path/Uri will result in same id.
 * Only UNIX filesystem is supported officially.
 * Windows filesystem could work, but no tests are run for it.
 *
 * @author Tuomo Heino
 * @version 18.12.2017.
 */
class HessuId {
    private val sha1Digest = MessageDigest.getInstance("SHA-1")

    /**
     * Generates ID from Path<br>
     * Uses paths absolute path to generate the true hash.<br>
     * Paths length, name count, filename and full path are hashed to generate
     * the ID.
     *
     * @param path Path to generate ID, will be converted to absolute path
     * @throws IllegalArgumentException if provided path is directory
     */
    fun generateIdFromPath(path: Path): String {
        if (Files.isDirectory(path)) {
            throw IllegalArgumentException("Path is a directory! $path")
        }
        val absolutePath = path.toAbsolutePath()
        val pathString = absolutePath.toString()
        return String.format(Locale.ROOT, "%s-%s-%s-%s",
                Integer.toHexString(pathString.length),
                Integer.toHexString(absolutePath.nameCount),
                intTo8BitHex(hashString(absolutePath.fileName.toString())),
                hashUriOrPath(pathString)
        )
    }

    /**
     * Generates ID from http/https URI.<br></br>
     * Any other uris will result in IllegalArgumentException<br></br>
     * Note that generator does not check if URI actually exists
     * @param uri http/https uri
     * @return ID for URI
     */
    fun generateIdFromUri(uri: URI): String {
        val scheme = uri.scheme
        if (scheme != "http" && scheme != "https") {
            throw IllegalArgumentException("Invalid Uri Scheme: $scheme")
        }
        val uriString = uri.toString()

        return String.format(Locale.ROOT, "%s-%s-%s-%s-%s",
                Integer.toHexString(uriString.length),
                intTo8BitHex(hashString(scheme)),
                intTo8BitHex(hashString(uri.host)),
                intTo8BitHex(hashString(uri.path)),
                hashUriOrPath(uriString)
        )
    }

    private fun intTo8BitHex(value: Int): String {
        val hex = Integer.toHexString(value)
        if (hex.length == 8) {
            return hex
        }
        val missingZeros = 8 - hex.length
        val sb = StringBuilder()
        for (i in 0 until missingZeros) {
            sb.append(0)
        }
        return sb.append(hex).toString()
    }

    private fun hashString(str: String): Int {
        var hash = 0
        for (i in str.indices) {
            hash = 31 * hash + str[i].toInt()
        }
        return hash
    }

    private fun hashUriOrPath(uriOrPath: String): String {
        return getSha1Hash(uriOrPath)
                .map { 0xff and it.toInt() }
                .fold(StringBuilder()) { stringBuilder, hashValue ->
                    if (hashValue < 0x10) {
                        stringBuilder.append(0)
                    }
                    stringBuilder.append(Integer.toHexString(hashValue))
                }.toString()
    }

    private fun getSha1Hash(string: String): ByteArray {
        val bytes = string.toByteArray(StandardCharsets.UTF_8)
        synchronized(sha1Digest) {
            sha1Digest.reset()
            return sha1Digest.digest(bytes)
        }
    }

}