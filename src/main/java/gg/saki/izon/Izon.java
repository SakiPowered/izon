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
import gg.saki.izon.libraries.LibraryStatus;
import gg.saki.izon.utils.DownloadSettings;
import gg.saki.izon.utils.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 *
 */
public final class Izon {

    private final @NotNull Path saveDirectory;

    private final @NotNull IzonClassLoader classLoader;
    private final @Nullable IzonClassLoader isolatedClassLoader;


    public Izon(@NotNull Path saveDirectory, @NotNull ClassLoader classLoader) {
        this.saveDirectory = saveDirectory;

        if (!(classLoader instanceof URLClassLoader)) {
            throw new IllegalArgumentException("ClassLoader must be an instance of URLClassLoader");
        }

        this.classLoader = IzonClassLoader.create((URLClassLoader) classLoader);

        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
        this.isolatedClassLoader = parent instanceof URLClassLoader ? IzonClassLoader.create((URLClassLoader) parent) : null;
    }

    public Izon(@NotNull ClassLoader classLoader) {
        this(Paths.get("./libs"), classLoader);


        File libsDirectory = this.saveDirectory.toFile();
        if (libsDirectory.exists()) return;

        if (!libsDirectory.mkdirs()) {
            throw new IllegalStateException("Failed to create libs directory");
        }
    }

    public LibraryStatus loadLibrary(@NotNull Library library) {
        return this.loadLibrary(library, false);
    }

    public LibraryStatus loadLibrary(@NotNull Library library, boolean isolated) {
        return this.loadLibrary(library, isolated, null);
    }


    public LibraryStatus loadLibrary(@NotNull Library library, boolean isolated, @Nullable DownloadSettings settings) {
        Path file = this.saveDirectory.resolve(library.getPath());

        if (settings == null) {
            settings = DownloadSettings.getDefault();
        }

        if (Files.exists(file)) {
            // no checksum, just load it
            if (!library.hasChecksum()) {
                return loadLibrary(library, file, isolated);
            }

            try {
                // read file bytes
                byte[] data = FileUtil.readAllBytes(file, settings.getBufferSize());

                // check hash, if it fails, return failure
                if (!library.checkHash(data)) {
                    return LibraryStatus.failure(library, "Checksum failed");
                }

                // else, load it
                return loadLibrary(library, file, isolated);
            } catch (IOException | NoSuchAlgorithmException e) {
                return LibraryStatus.failure(library, "Failed to perform checksum", e);
            }
        }


        // download it
        try {
            byte[] data = downloadLibrary(library, settings);

            // check hash, if it fails, return failure
            if (!library.checkHash(data)) {
                return LibraryStatus.failure(library, "Checksum failed");
            }


            // create parent directories (G/A/V)
            Files.createDirectories(file.getParent());

            // create temp file
            Path out = Files.createTempFile(file.getParent(), file.toFile().getName(), ".tmplib");
            out.toFile().deleteOnExit();

            // write and move
            Files.write(out, data);
            Files.move(out, file);
        } catch (IOException | NoSuchAlgorithmException e) {
            return LibraryStatus.failure(library, "Failed to download library", e);
        }

        // TODO: relocate it


        // load it
        return loadLibrary(library, file, isolated);
    }

    private byte[] downloadLibrary(Library library, DownloadSettings settings) throws IOException {
        URLConnection connection = new URL(library.getRepository().getUrl() + library.getPath()).openConnection();

        connection.setConnectTimeout(settings.getConnectionTimeout());
        connection.setReadTimeout(settings.getReadTimeout());
        connection.setRequestProperty("User-Agent", settings.getUserAgent());

        try (InputStream in = connection.getInputStream()) {
            return FileUtil.readAllBytes(in, settings.getBufferSize());
        }
    }

    private LibraryStatus loadLibrary(Library library, Path file, boolean isolated) {
        IzonClassLoader loader = isolated ? this.isolatedClassLoader : this.classLoader;

        if (loader == null) {
            // only isolated can be null, so check if it was successfully created or not
            return LibraryStatus.failure(library, "Isolated class loader could not be created (parent is not a URLClassLoader?)");
        }

        try {
            loader.addPath(file);
        } catch (IOException e) {
            return LibraryStatus.failure(library, "Failed to add library to class loader", e);
        }

        return LibraryStatus.success(library, "Library loaded successfully");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Izon izon = (Izon) o;
        return this.saveDirectory.equals(izon.saveDirectory) && this.classLoader.equals(izon.classLoader) && Objects.equals(this.isolatedClassLoader, izon.isolatedClassLoader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.saveDirectory, this.classLoader, this.isolatedClassLoader);
    }
}