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

import gg.saki.izon.classloaders.IsolatedClassLoader;
import gg.saki.izon.classloaders.IzonClassLoaderAccessor;
import gg.saki.izon.libraries.Library;
import gg.saki.izon.libraries.LibraryStatus;
import gg.saki.izon.utils.DownloadSettings;
import gg.saki.izon.utils.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public final class Izon {

    private final @NotNull Path saveDirectory;

    private final @NotNull IzonClassLoaderAccessor classLoader;
    private final @NotNull IzonClassLoaderAccessor defaultIsolatedClassLoader;

    private final @NotNull Map<Set<Library>, IzonClassLoaderAccessor> isolatedClassLoaders = new HashMap<>();


    public Izon(@NotNull Path saveDirectory, @NotNull ClassLoader classLoader) {
        this.saveDirectory = saveDirectory;

        if (!(classLoader instanceof URLClassLoader)) {
            throw new IllegalArgumentException("ClassLoader must be an instance of URLClassLoader");
        }

        this.classLoader = IzonClassLoaderAccessor.create((URLClassLoader) classLoader);
        this.defaultIsolatedClassLoader = IzonClassLoaderAccessor.create(new IsolatedClassLoader(new URL[0]));
    }

    public Izon(@NotNull ClassLoader classLoader) {
        this(Paths.get("./libs"), classLoader);


        File libsDirectory = this.saveDirectory.toFile();
        if (libsDirectory.exists()) return;

        if (!libsDirectory.mkdirs()) {
            throw new IllegalStateException("Failed to create libs directory");
        }
    }

    public IzonClassLoaderAccessor getIsolatedClassLoaderFor(@NotNull Set<Library> libraries) {
        IzonClassLoaderAccessor prevLoader = this.isolatedClassLoaders.get(libraries);

        if (prevLoader != null) {
            return prevLoader;
        }

        synchronized (this.isolatedClassLoaders) {
            URL[] urls = libraries.stream().map(l -> {
                try {
                    return new URL(l.getRepository().getUrl() + l.getPath());
                } catch (MalformedURLException e) {
                    throw new IllegalStateException(e);
                }
            }).toArray(URL[]::new);

            try (IsolatedClassLoader loader = new IsolatedClassLoader(urls)) {
                return this.isolatedClassLoaders.put(libraries, IzonClassLoaderAccessor.create(loader));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }


    public LibraryStatus loadLibrary(@NotNull Library library) {
        return this.loadLibrary(library, this.classLoader);
    }

    public LibraryStatus loadIsolatedLibrary(@NotNull Library library) {
        return this.loadLibrary(library, this.defaultIsolatedClassLoader);
    }

    public LibraryStatus loadLibrary(@NotNull Library library, @NotNull IzonClassLoaderAccessor loaderAccessor) {
        return this.loadLibrary(library, loaderAccessor, null);
    }


    public LibraryStatus loadLibrary(@NotNull Library library, @NotNull IzonClassLoaderAccessor loaderAccessor, @Nullable DownloadSettings settings) {
        Path file = this.saveDirectory.resolve(library.getPath());

        if (settings == null) {
            settings = DownloadSettings.getDefault();
        }

        if (Files.exists(file)) {
            // no checksum, just load it
            if (!library.hasChecksum()) {
                return loadLibrary(library, file, loaderAccessor);
            }

            try {
                // read file bytes
                byte[] data = FileUtil.readAllBytes(file, settings.getBufferSize());

                // check hash, if it fails, return failure
                if (!library.checkHash(data)) {
                    return LibraryStatus.failure(library, "Checksum failed");
                }

                // else, load it
                return loadLibrary(library, file, loaderAccessor);
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
        return loadLibrary(library, file, loaderAccessor);
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

    private LibraryStatus loadLibrary(Library library, Path file, IzonClassLoaderAccessor loaderAccessor) {
        try {
            loaderAccessor.addPath(file);
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
        return this.saveDirectory.equals(izon.saveDirectory) && this.classLoader.equals(izon.classLoader) && this.defaultIsolatedClassLoader.equals(izon.defaultIsolatedClassLoader) && this.isolatedClassLoaders.equals(izon.isolatedClassLoaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.saveDirectory, this.classLoader, this.defaultIsolatedClassLoader, this.isolatedClassLoaders);
    }
}