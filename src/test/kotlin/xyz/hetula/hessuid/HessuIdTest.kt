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

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.net.URI
import java.nio.file.Paths

/**
 * Needs to be run on a UNIX system.
 * /home/ needs to exist
 * @author Tuomo Heino
 * @version 18.12.2017.
 */
@RunWith(JUnit4::class)
class HessuIdTest {

    @Test
    fun validPathId() {
        val hessuId = HessuId()
        val testPath = Paths.get("/home/user1/song.mp3")
        val id = hessuId.generateIdFromPath(testPath)
        assertEquals("14-3-5b71f297-18f666e0822dc27c8fb077df47270569b9e257f2", id)
    }

    @Test
    fun validShortPathId() {
        val hessuId = HessuId()
        val testPath = Paths.get("/a.wav")
        val id = hessuId.generateIdFromPath(testPath)
        assertEquals("6-1-056d9bdf-600e239887e4a7672ce44be61875bb83ee4965a4", id)
    }

    @Test
    fun validLongPathId() {
        val hessuId = HessuId()
        val testPath = Paths.get("/home/hessu/Music/Might Album Number 9/I.flac")
        val id = hessuId.generateIdFromPath(testPath)
        assertEquals("2d-5-7f49fded-4145b30d2cb652099d357b2ca5aba4e0a3ff3c06", id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rootId() {
        val hessuId = HessuId()
        val testPath = Paths.get("/")
        hessuId.generateIdFromPath(testPath)
    }

    @Test(expected = IllegalArgumentException::class)
    fun directoryId() {
        val hessuId = HessuId()
        val testPath = Paths.get("/home/")
        hessuId.generateIdFromPath(testPath)
    }

    @Test
    fun validHttpUriId() {
        val hessuId = HessuId()
        val uri = URI.create("http://google.com/")
        val id = hessuId.generateIdFromUri(uri)
        assertEquals("12-00310888-a46e044c-0000002f-ac4cbe16220c61319d192bf9078f01de42e383e3", id)
    }

    @Test
    fun validHttpsUriId() {
        val hessuId = HessuId()
        val uri = URI.create("http://api.somesite.org/")
        val id = hessuId.generateIdFromUri(uri)
        assertEquals("18-00310888-f62fa805-0000002f-b85cca09d0877ca50d911f73eda20f37be239069", id)
    }

    @Test
    fun validHttpWithPortUriId() {
        val hessuId = HessuId()
        val uri = URI.create("http://api.somesite.org:8080/")
        val id = hessuId.generateIdFromUri(uri)
        assertEquals("1d-00310888-f62fa805-0000002f-5654c24b320ced9c0f71b5a100b53ad81f9ba811", id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidUriId() {
        val hessuId = HessuId()
        val uri = URI.create("file:///home/user1/song.mp3")
        hessuId.generateIdFromUri(uri)
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidHttpUri() {
        val hessuId = HessuId()
        val uri = URI.create("http://")
        hessuId.generateIdFromUri(uri)
    }

    @Test
    fun shortHttpUri() {
        val hessuId = HessuId()
        val uri = URI.create("http://s")
        val id = hessuId.generateIdFromUri(uri)
        assertEquals("8-00310888-00000073-00000000-b20c0c9a4c78e0e9378d95c65fa90f39684772b2", id)
    }
}