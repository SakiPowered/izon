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

package gg.saki.izon;

import gg.saki.izon.classloaders.IzonClassLoader;
import gg.saki.izon.libraries.Library;
import gg.saki.izon.utils.DownloadSettings;
import gg.saki.izon.utils.IzonException;
import lombok.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class Izon {

    private final @NonNull Path saveDirectory;

    private final @NonNull IzonClassLoader classLoader;
    private final IzonClassLoader isolatedClassLoader;


    public Izon(Path saveDirectory, ClassLoader classLoader) {
        this.saveDirectory = saveDirectory;

        if (!(classLoader instanceof URLClassLoader)) {
            throw new IzonException("ClassLoader must be an instance of URLClassLoader");
        }

        this.classLoader = IzonClassLoader.create((URLClassLoader) classLoader);

        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
        this.isolatedClassLoader = parent instanceof URLClassLoader ? IzonClassLoader.create((URLClassLoader) parent) : null;
    }

    public Izon(ClassLoader classLoader) {
        this(Paths.get("./libs"), classLoader);


        File libsDirectory = this.saveDirectory.toFile();
        if (libsDirectory.exists()) return;

        if (!libsDirectory.mkdirs()) {
            throw new IzonException("Failed to create libs directory");
        }
    }

    public Library.Status loadLibrary(@NonNull Library library) throws IzonException {
        return this.loadLibrary(library, false);
    }

    public Library.Status loadLibrary(@NonNull Library library, boolean isolated) throws IzonException {
        return this.loadLibrary(library, isolated, null);
    }


    public Library.Status loadLibrary(@NonNull Library library, boolean isolated, DownloadSettings settings) throws IzonException {
        Path file = this.saveDirectory.resolve(library.getFriendlyPath());

        if (Files.exists(file)) {
            return Library.Status.ALREADY_EXISTS;
        }

        if (settings == null) {
            settings = DownloadSettings.DEFAULT;
        }


        // download it
        try {
            byte[] data = downloadLibrary(library, settings);

            // check sha256
            if (library.hasChecksum() && !checkHash(library, data)) {
                throw new IzonException("SHA-256 checksum failed", library, Library.Status.CHECKSUM_MISMATCH);
            }


            // create temp file
            Path out = Files.createTempFile(this.saveDirectory, library.getFriendlyPath(), ".tmplib");
            out.toFile().deleteOnExit();

            // write and move
            Files.write(out, data);
            Files.move(out, file);
        } catch (IOException e) {
            throw new IzonException("Failed to download library", e, library, Library.Status.DOWNLOAD_FAILED);
        }

        // TODO: relocate it


        // load it
        return loadLibrary(library, file, isolated ? isolatedClassLoader : classLoader);
    }

    private byte[] downloadLibrary(Library library, DownloadSettings settings) throws IOException {
        URLConnection connection = new URL(library.getRepository().getUrl() + library.getPath()).openConnection();

        connection.setConnectTimeout(settings.getConnectionTimeout());
        connection.setReadTimeout(settings.getReadTimeout());
        connection.setRequestProperty("User-Agent", settings.getUserAgent());

        try (InputStream in = connection.getInputStream()) {
            int length;
            byte[] buffer = new byte[settings.getBufferSize()];
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            return out.toByteArray();
        }
    }

    private boolean checkHash(Library library, byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);

            return MessageDigest.isEqual(hash, library.getSha256());
        } catch (NoSuchAlgorithmException e) {
            throw new IzonException("Could not find SHA-256 algorithm", e, library, Library.Status.LOAD_FAILED);
        }
    }

    private Library.Status loadLibrary(Library library, Path file, IzonClassLoader classLoader) {
        try {
            this.classLoader.addPath(file);
        } catch (MalformedURLException e) {
            throw new IzonException("Failed to add library to class loader", e, library, Library.Status.LOAD_FAILED);
        }

        return Library.Status.SUCCESS;
    }
}