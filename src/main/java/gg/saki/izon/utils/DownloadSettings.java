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

import lombok.Data;
import lombok.NonNull;

@Data
public class DownloadSettings {

    public static final DownloadSettings DEFAULT = DownloadSettings.builder().build();

    private final int connectionTimeout;
    private final int readTimeout;
    private final int bufferSize;

    private final @NonNull String userAgent;

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

        public Builder userAgent(@NonNull String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public DownloadSettings build() {
            return new DownloadSettings(this.connectionTimeout, this.readTimeout, this.bufferSize, this.userAgent);
        }
    }
}
