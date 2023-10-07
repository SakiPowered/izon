/*
 * MIT License
 *
 * Copyright (c) 2023 SakiPowered <https://github.com/SakiPowered>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.saki.izon.utils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileUtil {

    private FileUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static byte @NotNull [] readAllBytes(@NotNull InputStream in, int bufferSize) throws IOException {
        try (in; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int length;
            byte[] buffer = new byte[bufferSize];

            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            return out.toByteArray();
        }
    }

    public static byte @NotNull [] readAllBytes(@NotNull Path path, int bufferSize) throws IOException {
        try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
            return readAllBytes(in, bufferSize);
        }
    }



}