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

import java.util.Objects;

public final class DownloadSettings {

    private static DownloadSettings DEFAULT;

    private final int connectionTimeout;
    private final int readTimeout;
    private final int bufferSize;

    private final @NotNull String userAgent;

    public DownloadSettings(int connectionTimeout, int readTimeout, int bufferSize, @NotNull String userAgent) {
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.bufferSize = bufferSize;
        this.userAgent = userAgent;
    }

    public static DownloadSettings getDefault() {
        if (DownloadSettings.DEFAULT == null) {
            DownloadSettings.DEFAULT = DownloadSettings.builder().build();
        }

        return DEFAULT;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public @NotNull String getUserAgent() {
        return this.userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadSettings that = (DownloadSettings) o;
        return this.connectionTimeout == that.connectionTimeout && this.readTimeout == that.readTimeout && this.bufferSize == that.bufferSize && this.userAgent.equals(that.userAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.connectionTimeout, this.readTimeout, this.bufferSize, this.userAgent);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int connectionTimeout = 5000;
        private int readTimeout = 5000;
        private int bufferSize = 2048;

        private String userAgent = "Izon";

        private Builder() {
            // seal class to prevent external instantiation
        }

        public Builder connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder userAgent(@NotNull String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public DownloadSettings build() {
            return new DownloadSettings(this.connectionTimeout, this.readTimeout, this.bufferSize, this.userAgent);
        }
    }
}
